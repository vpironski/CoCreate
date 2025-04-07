<script>
    import { onMount } from 'svelte';
    import { getProjectById } from '$lib/api';
    import { page } from '$app/state';

    let project = {};
    let loading = true;
    let errorMessage = '';
    let columns = {};

    let projectId = page.params.projectId;  // From the [projectId] part

    onMount(async () => {
        try {
            project = await getProjectById(projectId);
            console.log("Project data:", project);
            mapWorkflowToColumns();
        } catch (error) {
            errorMessage = 'Failed to load project data';
            console.error('Error:', error);
        } finally {
            loading = false;
        }
    });

    function mapWorkflowToColumns() {
        columns = {};

        for (const [columnName, taskIds] of Object.entries(project.workflow)) {
            columns[columnName] = taskIds.map(taskId => {
                return project.tasks.find(task => task.id === taskId);
            });
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

        // 1. Remove task from original column
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

        // 2. Insert into new column
        columns = columns.map((column, idx) => {
            if (idx === columnIndex) {
                let newTasks = [...column.tasks];

                // Default dropIndex to end if null or invalid
                const validIndex = dropIndex !== null && dropIndex >= 0 && dropIndex <= newTasks.length
                    ? dropIndex
                    : newTasks.length;

                newTasks.splice(validIndex, 0, taskToMove);

                // 3. Reassign order values to all tasks in the column
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

        // 4. Reset drag state
        draggedTask = null;
        draggedColumnIndex = null;
        draggedIndex = null;
        hoveredColumnIndex = null;
        hoveredIndex = null;
    }

    // Add new task
    function addTask(columnIndex) {
        columns = columns.map((column, idx) => {
            if (idx === columnIndex) {
                const newOrder = column.tasks.length > 0
                    ? column.tasks[column.tasks.length - 1].order + 1
                    : 0;

                const newTask = {
                    id: Date.now(),
                    title: "New Task",
                    description: "Task description",
                    tag: "General",
                    due: "No due date",
                    order: newOrder
                };

                return {
                    ...column,
                    tasks: [...column.tasks, newTask]
                };
            }
            return column;
        });
    }
</script>
<div class="bg-gray-50 min-h-screen flex flex-col">
    <!-- Header and controls section -->
    <div class="p-6 bg-white border-b border-gray-200">
        <div class="container mx-auto">
            <h1 class="text-3xl font-bold mb-4">Kanban Board</h1>

            <!-- Filter and control buttons area -->
            <div class="flex flex-wrap gap-4 mb-4">
                <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                    Filter
                </button>
                <button class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                    View Options
                </button>
                <!-- Add more controls as needed -->
            </div>
        </div>
    </div>

    <!-- Main content area with the Kanban board -->
    <div class="flex-1 p-6 overflow-hidden">
        <div class="container mx-auto h-full flex flex-col">
            <!-- Horizontal scroll container -->
            <div class="flex-1 overflow-x-scroll pb-4 scrollbar scrollbar-thumb-gray-400 scrollbar-track-gray-600">
            <!-- Columns container - will expand horizontally as needed -->
                <div class="inline-flex space-x-6 w-max h-full">
                    {#each columns as column, columnIndex (column.title)}
                        <div
                                class="flex-shrink-0 w-1/5 h-full flex flex-col"
                                on:drop={() => dropTask(columnIndex)}
                                on:dragover={(e) => e.preventDefault()}
                                on:dragenter={() => dragOverColumn(columnIndex)}
                                on:dragleave={() => dragLeaveColumn()}
                                class:column-highlight={hoveredColumnIndex === columnIndex && draggedColumnIndex !== columnIndex}
                        >
                            <div class="bg-white rounded-lg shadow h-full flex flex-col">
                                <div class="p-4 border-b border-gray-200 bg-gray-200 rounded-t-sm">
                                    <h2 class="font-semibold text-lg">{column.title}</h2>
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

                                        <div
                                                class="task bg-white p-3 rounded border border-gray-200 shadow-sm hover:shadow cursor-move transition-all"
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
                                        >
                                            <h3 class="font-medium text-gray-800">{task.title}</h3>
                                            <p class="text-sm text-gray-600 mt-2">{task.description}</p>
                                            <div class="flex justify-between items-center mt-3">
                                                <span class="text-xs px-2 py-1 bg-blue-100 text-blue-800 rounded">{task.tag}</span>
                                                <span class="text-xs text-gray-500">{task.due}</span>
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
                                            on:click={() => addTask(columnIndex)}
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
