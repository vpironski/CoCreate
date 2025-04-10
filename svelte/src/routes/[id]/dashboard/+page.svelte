<script>
    import {onMount} from 'svelte';
    import {logoutUser, isAuthenticated, getUserId, getAllProjects, deleteProject} from '$lib/api';
    import {goto} from '$app/navigation';
    import {page} from '$app/state';

    let isRedirecting = false;
    let userId = page.params.id;
    let projects = [];
    let deletingId = null;
    let error = null;

    // Modal state
    let showDeleteModal = false;
    let projectToDelete = null;
    let deleteResponse = null;

    onMount(async () => {
        await loadProjects();
    });

    async function loadProjects() {
        try {
            const auth = await isAuthenticated();
            if (!auth) {
                if (!isRedirecting) {
                    isRedirecting = true;
                    goto('/');
                }
                return;
            }

            const storedUserId = await getUserId();
            if (storedUserId !== userId) {
                if (!isRedirecting) {
                    isRedirecting = true;
                    await logoutUser();
                    goto('/');
                }
                return;
            }

            projects = await getAllProjects(userId);
            error = null;
        } catch (err) {
            console.error("Error:", err);
            error = "Failed to load projects";
        }
    }

    function openDeleteModal(project) {
        projectToDelete = project;
        showDeleteModal = true;
        deleteResponse = null;
    }

    function closeDeleteModal() {
        showDeleteModal = false;
        projectToDelete = null;
        deleteResponse = null;
    }

    async function confirmDelete() {
        if (!projectToDelete) return;

        try {
            deletingId = projectToDelete.id;
            const response = await deleteProject(userId, projectToDelete.id);
            deleteResponse = response.message || "Project deleted successfully";

            // Wait a moment before updating the UI
            await new Promise(resolve => setTimeout(resolve, 1000));

            // Remove the deleted project from the local array
            projects = projects.filter(project => project.id !== projectToDelete.id);
            closeDeleteModal();
        } catch (err) {
            console.error("Delete failed:", err);
            deleteResponse = "Failed to delete project";
        } finally {
            deletingId = null;
        }
    }
</script>

<style>
    .cursor-pointer {
        cursor: pointer;
    }
    html, body {
        height: 100%;
        margin: 0;
        padding: 0;
    }

</style>


{#if showDeleteModal}
    <div
            class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-50"
            on:click={() => closeDeleteModal()}
    >
        <div
                class="bg-white p-8 rounded-lg max-w-md w-full shadow-lg"
                on:click|stopPropagation
        >
            <h3 class="text-lg font-medium text-gray-900">Delete Project</h3>
            <p class="mt-2 text-gray-600">
                Are you sure you want to delete the project "{projectToDelete?.name}"? This action cannot be undone.
            </p>

            {#if deleteResponse}
                <div class={`mt-4 p-3 rounded-md ${deleteResponse.includes("Failed") ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'}`}>
                    {deleteResponse}
                </div>
            {/if}

            <div class="flex justify-end gap-4 mt-6">
                <button
                        class="px-4 py-2 text-gray-700 rounded-md hover:bg-gray-100"
                        on:click={closeDeleteModal}
                        disabled={deletingId === projectToDelete?.id}
                >
                    Cancel
                </button>
                <button
                        class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:bg-red-400"
                        on:click={confirmDelete}
                        disabled={deletingId === projectToDelete?.id}
                >
                    {deletingId === projectToDelete?.id ? 'Deleting...' : 'Delete'}
                </button>
            </div>
        </div>
    </div>
{/if}

<div class="min-h-screen bg-gray-50 p-8">
    <!-- Delete Confirmation Modal -->
    <!-- Header Section -->
    <div class="max-w-7xl mx-auto mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p class="mt-2 text-gray-600">Manage all your projects in one place</p>
        <div class="mt-4 flex space-x-4">
            <button class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd"
                          d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                          clip-rule="evenodd"/>
                </svg>
                New Project
            </button>
        </div>
    </div>

    <!-- Projects Table -->
    <div class="max-w-7xl mx-auto bg-white rounded-xl shadow-md overflow-hidden">
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-100">
                <tr>
                    <th scope="col"
                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Project
                        Name
                    </th>
                    <th scope="col"
                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Description
                    </th>
                    <th scope="col"
                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status
                    </th>
                    <th scope="col"
                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority
                    </th>
                    <th scope="col"
                        class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions
                    </th>
                </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                {#each projects as project}
                    <tr class="hover:bg-gray-100 active:bg-gray-200 transition cursor-pointer"
                        on:click={() => goto(`${page.url.pathname}/${project.id}`)}>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm font-medium text-gray-900">{project.name}</div>
                        </td>
                        <td class="px-6 py-4">
                            <div class="text-sm text-gray-500">{project.description}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            {#if project.status === 'DRAFT'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">Draft</span>
                            {:else if project.status === 'ACTIVE'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">Active</span>
                            {:else if project.status === 'COMPLETED'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">Completed</span>
                            {:else if project.status === 'ARCHIVED'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800">Archived</span>
                            {/if}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            {#if project.priority === 'LOW'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">Low</span>
                            {:else if project.priority === 'MEDIUM'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">Medium</span>
                            {:else if project.priority === 'HIGH'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-orange-100 text-orange-800">High</span>
                            {:else if project.priority === 'URGENT'}
                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">Urgent</span>
                            {/if}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <button class="text-blue-600 hover:text-blue-900 mr-4 group relative">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20"
                                     fill="currentColor">
                                    <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                                </svg>
                                <span class="absolute -top-8 left-1/2 transform -translate-x-1/2 bg-gray-800 text-white text-xs rounded py-1 px-2 opacity-0 group-hover:opacity-100 transition-opacity">Edit</span>
                            </button>
                            <button
                                    class="text-red-600 hover:text-red-900 group relative"
                                    on:click|preventDefault|stopPropagation={() => openDeleteModal(project)}
                                    disabled={deletingId === project.id}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20"
                                     fill="currentColor">
                                    <path fill-rule="evenodd"
                                          d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                                          clip-rule="evenodd"/>
                                </svg>
                                <span class="absolute -top-8 left-1/2 transform -translate-x-1/2 bg-gray-800 text-white text-xs rounded py-1 px-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                    {deletingId === project.id ? 'Deleting...' : 'Delete'}
                                </span>
                            </button>
                        </td>
                    </tr>
                {:else}
                    <tr>
                        <td colspan="5" class="px-6 py-4 text-center text-sm text-gray-500">
                            No projects found. Create your first project!
                        </td>
                    </tr>
                {/each}
                </tbody>
            </table>
        </div>
    </div>
</div>