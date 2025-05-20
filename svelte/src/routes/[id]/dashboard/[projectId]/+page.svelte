<script>
    import { onMount } from 'svelte';
    import { getProjectById, addCard, removeCard, createTask, deleteTask, updateTask} from '$lib/api';
    import { page } from '$app/state';

    let project = {};
    let loading = true;
    let errorMessage = '';
    let columns = {};

    let userId = page.params.id;
    let projectId = page.params.projectId;

    onMount(async () => {
        await loadPage()
    });

    async function loadPage() {
        try {
            project = await getProjectById(userId, projectId);
            if (project.workflow?.cards) {
                columns = Object.entries(project.workflow.cards).map(([title, tasks]) => ({
                    title,
                    tasks
                }));
            }
        } catch (error) {
            errorMessage = 'Failed to load project data';
            console.error('Error:', error);
        } finally {
            loading = false;
        }
    }

    let draggedTask = null;
    let draggedColumnIndex = null;
    let draggedIndex = null;
    let hoveredColumnIndex = null;
    let hoveredIndex = null;

    function dragStart(task, columnIndex, index) {
        draggedTask = task;
        draggedColumnIndex = columnIndex;
        draggedIndex = index;
    }

    function dragOverColumn(columnIndex, index = null) {
        hoveredColumnIndex = columnIndex;
        hoveredIndex = index;
    }

    function dragLeaveColumn() {
        hoveredColumnIndex = null;
        hoveredIndex = null;
    }

    function dropTask(columnIndex, dropIndex = null) {
        if (!draggedTask || draggedColumnIndex === null) return;

        let taskToMove = null;
        columns = columns.map((column, idx) => {
            if (idx === draggedColumnIndex) {
                taskToMove = column.tasks[draggedIndex];
                return {
                    ...column,
                    tasks: column.tasks.filter((_, i) => i !== draggedIndex)
                };
            }
            return column;
        });

        if (!taskToMove) return;

        columns = columns.map((column, idx) => {
            if (idx === columnIndex) {
                let newTasks = [...column.tasks];

                const validIndex = dropIndex !== null && dropIndex >= 0 && dropIndex <= newTasks.length
                    ? dropIndex
                    : newTasks.length;

                newTasks.splice(validIndex, 0, taskToMove);

                newTasks = newTasks.map((t, i) => ({
                    ...t,
                    order: i
                }));

                return {
                    ...column,
                    tasks: newTasks
                };
            }
            return column;
        });

        draggedTask = null;
        draggedColumnIndex = null;
        draggedIndex = null;
        hoveredColumnIndex = null;
        hoveredIndex = null;
    }

    let showAddTaskModal = false;
    let selectedColumnIndex = null;

    let newTask = {
        title: '',
        description: '',
        startDate: '',
        endDate: '',
        parentTask: '',
        priority: 'MEDIUM'
    };

    function openAddTaskModal(columnIndex) {
        selectedColumnIndex = columnIndex;
        showAddTaskModal = true;
        newTask = {
            title: '',
            description: '',
            startDate: '',
            endDate: '',
            parentTask: '',
            priority: 'MEDIUM'
        };
    }

    function closeAddTaskModal() {
        showAddTaskModal = false;
    }

    async function confirmAddTask() {
        if (!newTask.title.trim()) return;

        const columnName = columns[selectedColumnIndex]?.title;

        const task = {
            ...newTask,
            card: columnName,
        };
        console.log(columnName)
        try {
            await createTask(userId, projectId, task);
            closeAddTaskModal();

            await loadPage();
        } catch (err) {
            console.error("Failed to create task:", err);
        }

    }

    let showDeleteTaskModal = false;
    let taskToDelete = null;

    function openDeleteTaskModal(task) {
        taskToDelete = task; // Store the task to be deleted
        showDeleteTaskModal = true;
    }

    function closeDeleteTaskModal() {
        showDeleteTaskModal = false;
        taskToDelete = null;
    }

    async function confirmDeleteTask() {
        if (!taskToDelete) return;

        try {
            await deleteTask(userId, projectId, taskToDelete.id);
            closeDeleteTaskModal();

            await loadPage();
        } catch (err) {
            console.error("Failed to delete task:", err);
        }
    }

    let showAddCardModal = false;
    let newColumnName = '';

    let showDeleteCardModal = false;
    let columnToDeleteIndex = null;

    let cardDTO = {
        cardName: '',
    };

    function openAddCardModal() {
        showAddCardModal = true;
        newColumnName = '';
    }

    // Confirms Add Card
    async function confirmAddCard() {
        if (newColumnName.trim()) {
            cardDTO.cardName = newColumnName.trim();

            try {
                await addCard(userId, projectId, cardDTO);
                showAddCardModal = false;

                await loadPage();
            } catch (err) {
                console.error("Failed to add card:", err);
            }
        }
    }

    function openDeleteCardModal(index) {
        columnToDeleteIndex = index;
        showDeleteCardModal = true;
    }

    // Confirms Delete Card
    async function confirmDeleteCard() {
        const columnTitle = columns[columnToDeleteIndex]?.title;

        if (columnTitle) {
            cardDTO.cardName = columnTitle;

            try {
                await removeCard(userId, projectId, cardDTO);
                showDeleteCardModal = false;
                columnToDeleteIndex = null;

                await loadPage();
            } catch (err) {
                console.error("Failed to remove card:", err);
            }
        }
    }

    let showTaskDetailsModal = false;
    let selectedTask = null;

    function openTaskDetailsModal(task) {
        selectedTask = task;
        showTaskDetailsModal = true;
    }

    function closeTaskDetailsModal() {
        showTaskDetailsModal = false;
        selectedTask = null;
    }


    let showEditTaskModal = false;
    let editedTask = {
        title: '',
        description: '',
        startDate: '',
        endDate: '',
        priority: 'MEDIUM',
        status: 'TODO'
    };

    function openEditTaskModal(task) {
        editedTask = {
            id: task.id,
            title: task.title,
            description: task.description || '',
            startDate: task.startDate || '',
            endDate: task.endDate || '',
            priority: task.priority || 'MEDIUM',
            status: task.status || 'TODO'
        };
        showEditTaskModal = true;
    }

    function closeEditTaskModal() {
        showEditTaskModal = false;
    }

    async function saveEditedTask() {
        if (!editedTask.title.trim()) return;

        try {
            await updateTask(userId, projectId, editedTask.id, editedTask);
            closeEditTaskModal();
            closeTaskDetailsModal();
            await loadPage(); // Refresh the data
        } catch (err) {
            console.error("Failed to update task:", err);
            errorMessage = 'Failed to update task: ' + err.message;
        }
    }
</script>

<div class="bg-gray-50 min-h-screen flex flex-col">
    <!-- Header and controls section -->
    <div class="p-6 bg-white border-b border-gray-200">
        <div class="container mx-auto">
            <h1 class="text-3xl font-bold mb-4">Kanban Board</h1>

<!--            &lt;!&ndash; Filter and control buttons area &ndash;&gt;-->
<!--            <div class="flex flex-wrap gap-4 mb-4">-->
<!--                <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">-->
<!--                    Filter-->
<!--                </button>-->
<!--                <button class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">-->
<!--                    View Options-->
<!--                </button>-->
<!--                &lt;!&ndash; Add more controls as needed &ndash;&gt;-->
<!--            </div>-->
        </div>
    </div>

    <!-- Main content area with the Kanban board -->
    <div class="flex-1 p-6 overflow-hidden">
        {#if showTaskDetailsModal}
            <div class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-50" on:click={() => closeTaskDetailsModal()}>
                <div class="bg-white p-8 rounded-lg max-w-md w-full shadow-lg" on:click|stopPropagation>

                    <div class="flex justify-between items-start mb-4">
                        <h3 class="text-lg font-medium text-gray-900">Task Details</h3>
                        <button
                                on:click={() => openEditTaskModal(selectedTask)}
                                class="text-gray-500 hover:text-blue-600 transition"
                                aria-label="Edit task"
                        >
                            <!-- Edit SVG Icon -->
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                        </button>
                    </div>


                    <div class="space-y-4">
                        <div>
                            <h4 class="font-semibold text-gray-700">Title</h4>
                            <p class="mt-1 text-gray-600">{selectedTask.title}</p>
                        </div>

                        <div>
                            <h4 class="font-semibold text-gray-700">Description</h4>
                            <p class="mt-1 text-gray-600">{selectedTask.description || 'No description'}</p>
                        </div>

                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <h4 class="font-semibold text-gray-700">Start Date</h4>
                                <p class="mt-1 text-gray-600">{selectedTask.startDate || 'Not set'}</p>
                            </div>
                            <div>
                                <h4 class="font-semibold text-gray-700">End Date</h4>
                                <p class="mt-1 text-gray-600">{selectedTask.endDate || 'Not set'}</p>
                            </div>
                        </div>

                        <div>
                            <h4 class="font-semibold text-gray-700">Priority</h4>
                            <p class="mt-1">
                                {#if selectedTask.priority === 'LOW'}
                                    <span class="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-800">Low</span>
                                {:else if selectedTask.priority === 'MEDIUM'}
                                    <span class="text-xs px-2 py-1 rounded-full bg-yellow-100 text-yellow-800">Medium</span>
                                {:else if selectedTask.priority === 'HIGH'}
                                    <span class="text-xs px-2 py-1 rounded-full bg-orange-100 text-orange-800">High</span>
                                {:else if selectedTask.priority === 'URGENT'}
                                    <span class="text-xs px-2 py-1 rounded-full bg-red-100 text-red-800">Urgent</span>
                                {/if}
                            </p>
                        </div>

                        <div>
                            <h4 class="font-semibold text-gray-700">Status</h4>
                            <p class="mt-1">
                                {#if selectedTask.status === 'TODO'}
                                    <span class="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-800">To Do</span>
                                {:else if selectedTask.status === 'IN_PROGRESS'}
                                    <span class="text-xs px-2 py-1 rounded-full bg-blue-100 text-blue-800">In Progress</span>
                                {:else if selectedTask.status === 'DONE'}
                                    <span class="text-xs px-2 py-1 rounded-full bg-green-100 text-green-800">Done</span>
                                {:else}
                                    <span class="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-800">Not Set</span>
                                {/if}
                            </p>
                        </div>
                    </div>

                    <div class="flex justify-end mt-6">
                        <button
                                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                on:click={() => closeTaskDetailsModal()}
                        >
                            Close
                        </button>
                    </div>
                </div>
            </div>
        {/if}
        {#if showAddTaskModal}
            <div class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-200" on:click={() => closeAddTaskModal()}>
                <div class="bg-white p-8 rounded-lg max-w-md w-full shadow-lg" on:click|stopPropagation>
                    <h3 class="text-lg font-medium text-gray-900">Add New Task</h3>

                    <!-- Task Name -->
                    <input
                            type="text"
                            bind:value={newTask.title}
                            placeholder="Enter task name"
                            class="mt-4 w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
                    />

                    <!-- Description -->
                    <textarea
                            bind:value={newTask.description}
                            placeholder="Task description"
                            class="mt-3 w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
                    ></textarea>

                    <!-- Start Date (Date only) -->
                    <label class="block mt-3 text-sm text-gray-700">Start Date</label>
                    <input
                            type="date"
                            bind:value={newTask.startDate}
                            class="w-full p-2 border border-gray-300 rounded"
                    />

                    <!-- End Date (Date only) -->
                    <label class="block mt-3 text-sm text-gray-700">End Date</label>
                    <input
                            type="date"
                            bind:value={newTask.endDate}
                            class="w-full p-2 border border-gray-300 rounded"
                    />

                    <!-- Priority -->
                    <label class="block mt-3 text-sm text-gray-700">Priority</label>
                    <select
                            bind:value={newTask.priority}
                            class="w-full p-2 border border-gray-300 rounded"
                    >
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                        <option value="URGENT">Urgent</option>
                    </select>

                    <!-- Actions -->
                    <div class="flex justify-end gap-4 mt-6">
                        <button class="px-4 py-2 text-gray-700 rounded-md hover:bg-gray-100" on:click={() => closeAddTaskModal()}>
                            Cancel
                        </button>
                        <button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700" on:click={confirmAddTask}>
                            Add Task
                        </button>
                    </div>
                </div>
            </div>
        {/if}
        {#if showEditTaskModal}
            <div class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-50" on:click={() => closeEditTaskModal()}>
                <div class="bg-white p-8 rounded-lg max-w-md w-full shadow-lg" on:click|stopPropagation>
                    <h3 class="text-lg font-medium text-gray-900 mb-4">Edit Task</h3>

                    <!-- Task Name -->
                    <input
                            type="text"
                            bind:value={editedTask.title}
                            placeholder="Enter task name"
                            class="mt-4 w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
                    />

                    <!-- Description -->
                    <textarea
                            bind:value={editedTask.description}
                            placeholder="Task description"
                            class="mt-3 w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
                    ></textarea>

                    <!-- Start Date (Date only) -->
                    <label class="block mt-3 text-sm text-gray-700">Start Date</label>
                    <input
                            type="date"
                            bind:value={editedTask.startDate}
                            class="w-full p-2 border border-gray-300 rounded"
                    />

                    <!-- End Date (Date only) -->
                    <label class="block mt-3 text-sm text-gray-700">End Date</label>
                    <input
                            type="date"
                            bind:value={editedTask.endDate}
                            class="w-full p-2 border border-gray-300 rounded"
                    />

                    <!-- Priority -->
                    <label class="block mt-3 text-sm text-gray-700">Priority</label>
                    <select
                            bind:value={editedTask.priority}
                            class="w-full p-2 border border-gray-300 rounded"
                    >
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                        <option value="URGENT">Urgent</option>
                    </select>

                    <!-- Status -->
                    <label class="block mt-3 text-sm text-gray-700">Status</label>
                    <select
                            bind:value={editedTask.status}
                            class="w-full p-2 border border-gray-300 rounded"
                    >
                        <option value="TODO">To Do</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="DONE">Done</option>
                    </select>

                    <!-- Actions -->
                    <div class="flex justify-end gap-4 mt-6">
                        <button class="px-4 py-2 text-gray-700 rounded-md hover:bg-gray-100" on:click={() => closeEditTaskModal()}>
                            Cancel
                        </button>
                        <button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700" on:click={saveEditedTask}>
                            Save Changes
                        </button>
                    </div>
                </div>
            </div>
        {/if}
        {#if showDeleteTaskModal}
            <div class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-50" on:click={() => closeDeleteTaskModal()}>
                <div class="bg-white p-8 rounded-lg max-w-md w-full shadow-lg" on:click|stopPropagation>
                    <h3 class="text-lg font-medium text-gray-900">Delete Task</h3>
                    <p class="mt-2 text-gray-600">
                        Are you sure you want to delete the task "{taskToDelete?.title}"? This action cannot be undone.
                    </p>
                    <div class="flex justify-end gap-4 mt-6">
                        <button class="px-4 py-2 text-gray-700 rounded-md hover:bg-gray-100" on:click={() => closeDeleteTaskModal()}>
                            Cancel
                        </button>
                        <button class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700" on:click={() => confirmDeleteTask()}>
                            Delete
                        </button>
                    </div>
                </div>
            </div>
        {/if}
        {#if showAddCardModal}
            <div class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-200" on:click={() => showAddCardModal = false}>
                <div class="bg-white p-8 rounded-lg max-w-md w-full shadow-lg" on:click|stopPropagation>
                    <h3 class="text-lg font-medium text-gray-900">Add New Column</h3>
                    <input
                            type="text"
                            bind:value={newColumnName}
                            placeholder="Enter column name"
                            class="mt-4 w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
                    />
                    <div class="flex justify-end gap-4 mt-6">
                        <button class="px-4 py-2 text-gray-700 rounded-md hover:bg-gray-100" on:click={() => showAddCardModal = false}>
                            Cancel
                        </button>
                        <button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700" on:click={confirmAddCard}>
                            Add
                        </button>
                    </div>
                </div>
            </div>
        {/if}
        {#if showDeleteCardModal}
            <div class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-50" on:click={() => showDeleteCardModal = false}>
                <div class="bg-white p-8 rounded-lg max-w-md w-full shadow-lg" on:click|stopPropagation>
                    <h3 class="text-lg font-medium text-gray-900">Delete Column</h3>
                    <p class="mt-2 text-gray-600">
                        Are you sure you want to delete the column "{columns[columnToDeleteIndex]?.title}"? This will remove all tasks in it.
                    </p>
                    <div class="flex justify-end gap-4 mt-6">
                        <button
                                class="px-4 py-2 text-gray-700 rounded-md hover:bg-gray-100"
                                on:click={() => showDeleteCardModal = false}>
                            Cancel
                        </button>
                        <button
                                class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
                                on:click={() => confirmDeleteCard()}>
                            Delete
                        </button>
                    </div>
                </div>
            </div>
        {/if}

        <div class="container mx-auto h-full flex flex-col">
            <!-- Horizontal scroll container -->
            <div class="flex-1 overflow-x-scroll pb-4 scrollbar scrollbar-thumb-gray-400 scrollbar-track-gray-600">
            <!-- Columns container - will expand horizontally as needed -->
                <div class="inline-flex space-x-6 w-max h-full">
                    {#each columns as column, columnIndex (column.title)}
                        <div class="flex-shrink-0 w-80 min-w-[20rem] max-w-[20rem] h-full flex flex-col"
                                on:drop={() => dropTask(columnIndex)}
                                on:dragover={(e) => e.preventDefault()}
                                on:dragenter={() => dragOverColumn(columnIndex)}
                                on:dragleave={() => dragLeaveColumn()}
                                class:column-highlight={hoveredColumnIndex === columnIndex && draggedColumnIndex !== columnIndex}
                        >
                            <div class="bg-white rounded-lg shadow h-full flex flex-col">
                                <div class="p-4 border-b border-gray-200 bg-gray-200 rounded-t-sm">
                                    <div class="flex items-center justify-between">
                                        <h2 class="font-semibold text-lg truncate">{column.title}</h2>
                                        <button
                                                on:click={() => openDeleteCardModal(columnIndex)}
                                                class="text-gray-500 hover:text-red-600 transition"
                                                aria-label="Delete column"
                                        >
                                            <!-- Trash SVG Icon -->
                                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                      d="M6 18L18 6M6 6l12 12" />
                                            </svg>
                                        </button>
                                    </div>
                                </div>
                                <div class="p-4 space-y-0 flex-1 overflow-y-auto">
                                    {#each column.tasks as task, index (task.id)}
                                        <!-- Drop zone above each task -->
                                        <div
                                                class="drop-zone h-2 -my-1 transition-all"
                                                class:active-drop-zone={hoveredColumnIndex === columnIndex && hoveredIndex === index && draggedColumnIndex !== columnIndex}
                                                on:drop={() => dropTask(columnIndex, index)}
                                                on:dragover={(e) => {
                                        e.preventDefault();
                                        dragOverColumn(columnIndex, index);
                                    }}
                                                on:dragleave={() => {
                                        if (hoveredIndex === index) dragLeaveColumn();
                                    }}
                                        ></div>

                                        <div class="task bg-white p-3 rounded border border-gray-200 shadow-sm hover:shadow cursor-move transition-all"

                                             draggable="true"
                                                on:dragstart={() => dragStart(task, columnIndex, index)}
                                                on:dragend={() => {
                                                    draggedTask = null;
                                                    draggedColumnIndex = null;
                                                    draggedIndex = null;
                                                    hoveredColumnIndex = null;
                                                    hoveredIndex = null;
                                                }}
                                                class:task-dragging={draggedColumnIndex === columnIndex && draggedIndex === index}
                                                on:click={() => openTaskDetailsModal(task)}
                                        >
                                            <div class="flex items-center justify-between">
                                                <h3 class="font-medium text-gray-800 truncate">{task.title}</h3>
                                                <button
                                                        class="text-gray-500 hover:text-red-600 transition"
                                                        on:click={() => openDeleteTaskModal(task)}
                                                        aria-label="Delete task"
                                                >
                                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                                                    </svg>
                                                </button>
                                            </div>


                                            <div class="flex justify-between items-center mt-auto pt-3">
                                                {#if task.priority === 'LOW'}
                                                    <span class="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-800">Low</span>
                                                {:else if task.priority === 'MEDIUM'}
                                                    <span class="ptext-xs px-2 py-1 rounded-full bg-yellow-100 text-yellow-800">Medium</span>
                                                {:else if task.priority === 'HIGH'}
                                                    <span class="text-xs px-2 py-1 rounded-full bg-orange-100 text-orange-800">High</span>
                                                {:else if task.priority === 'URGENT'}
                                                    <span class="text-xs px-2 py-1 rounded-full bg-red-100 text-red-800">Urgent</span>
                                                {/if}
                                                <span class="text-xs text-gray-500">{task.endDate}</span>
                                            </div>
                                        </div>

                                    {/each}

                                    <!-- Drop zone at the bottom of the column -->
                                    <div
                                            class="drop-zone h-2 -my-1 transition-all"
                                            class:active-drop-zone={hoveredColumnIndex === columnIndex && hoveredIndex === column.tasks.length && draggedColumnIndex !== columnIndex}
                                            on:drop={() => dropTask(columnIndex, column.tasks.length)}
                                            on:dragover={(e) => {
                                    e.preventDefault();
                                    dragOverColumn(columnIndex, column.tasks.length);
                                }}
                                            on:dragleave={() => {
                                    if (hoveredIndex === column.tasks.length) dragLeaveColumn();
                                }}
                                    ></div>

                                    <button
                                            class="w-full py-2 text-sm text-gray-500 hover:text-gray-700 flex items-center justify-center mt-2"
                                            on:click={() => openAddTaskModal(columnIndex)}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                        </svg>
                                        Add task
                                    </button>
                                </div>
                            </div>
                        </div>
                    {/each}
                    <div class="flex-shrink-0 w-80 min-w-[20rem] max-w-[20rem] h-full flex items-center justify-center">
                        <button
                                class="w-full py-2 px-4 text-gray-600 bg-white border border-gray-300 rounded hover:bg-gray-100 transition"
                                on:click={openAddCardModal}
                        >
                            + Add Column
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<style>
    .column-highlight {
        outline: 2px dashed #3b82f6;
        outline-offset: 2px;
    }

    .active-drop-zone {
        background-color: #bfdbfe;
        height: 8px;
        margin: 4px 0;
    }

    .task-dragging {
        opacity: 0.5;
        background-color: #f3f4f6;
    }

    .drop-zone {
        transition: all 0.2s ease;
    }

</style>
