<script>
    import { onMount } from 'svelte';
    import {getUser, getAllProjects, addField, removeField} from '$lib/api.js';
    import { page } from '$app/state';
    import { goto } from '$app/navigation';

    let userId = page.params.id;
    let user = null;
    let projects = [];
    let error = null;
    let showAddModal = false;
    let showRemoveModal = false;
    let field = {
        name: '',
        type: '',
    };
    let fieldName = '';
    let fieldType = 'String';
    const typeOptions = [
        { label: 'Text', value: 'String' },
        { label: 'Integer', value: 'Integer' },
        { label: 'Decimal', value: 'Double' },
        { label: 'Date', value: 'LocalDate' },
        { label: 'Date + time', value: 'LocalDateTime' },
        { label: 'Checkbox', value: 'Boolean' }
    ];

    onMount(async () => {
        try {
            user = await getUser(userId);
            projects = await getAllProjects(userId);
        } catch (err) {
            error = err.message;
        }
    });

    $: firstProjects = projects?.slice(0, 4);
    $: extraProjects = projects?.slice(4);

    function handleProjectSelect(event) {
        const selectedProjectId = event.target.value;
        if (selectedProjectId) {
            goto(`/${userId}/dashboard/${selectedProjectId}`);
        }
    }

    async function handleAddField() {
        if (fieldName && fieldType) {
            field.name = fieldName;
            field.type = fieldType;
            console.log(field)

            await addField(userId, field)
            fieldName = '';
            fieldType = 'String';
            showAddModal = false;

            location.reload()
        }
    }

    async function handleRemoveField(name) {
        if(name){
            field.name = name;
            await removeField(userId, field)
            showRemoveModal = false;

            location.reload()
        }
    }
</script>

{#if error}
    <div class="text-red-500 p-4">{error}</div>
{:else if !user}
    <div class="p-4">Loading...</div>
{:else}

    <div class="min-h-screen p-6 bg-gray-50">
        <!-- Top right user info -->
        <div class="flex justify-end mb-6">
            <div class="flex items-center gap-4">
                <div class="text-right">
                    <div class="text-xl font-bold">{user.username}</div>
                    <div class="text-gray-600">{user.email}</div>
                </div>
                <img
                        class="w-16 h-16 rounded-full bg-gray-200"
                        src={`https://ui-avatars.com/api/?name=${user.username || 'U'}&background=random`}
                        alt="User avatar"
                />
            </div>
        </div>


        <!-- Projects Section -->
        <div class="mb-8">
            <h2 class="text-2xl font-semibold mb-4">Personal Projects</h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
                {#each firstProjects as project}
                    <a
                            href={`/${userId}/dashboard/${project.id}`}
                            class="bg-white shadow-md rounded-lg p-4 border hover:shadow-lg transition"
                    >
                        <div class="text-lg font-medium">{project.name}</div>
                    </a>
                {/each}
            </div>

            {#if extraProjects.length > 0}
                <div class="mt-4">
                    <label class="block mb-2 text-sm font-medium text-gray-700">More Projects</label>
                    <select
                            class="w-full border rounded p-2"
                            change={handleProjectSelect}
                        >
                        <option value="">View more projects</option>
                        {#each extraProjects as project}
                            <option value={project.id}>{project.name}</option>
                        {/each}
                    </select>
                </div>
            {/if}
        </div>
        {#if showAddModal}
            <div class="fixed inset-0 bg-opacity-50 flex items-center justify-center">
                <div class="bg-white p-6 rounded shadow-md w-96">
                    <h2 class="text-xl font-semibold mb-4">Add New Field</h2>
                    <div class="mb-4">
                        <label class="block mb-1">Field Name</label>
                        <input
                                type="text"
                                bind:value={fieldName}
                                class="w-full border rounded p-2"
                        />
                    </div>
                    <div class="mb-4">
                        <label class="block mb-1">Field Type</label>
                        <select
                                bind:value={fieldType}
                                class="w-full border rounded p-2"
                        >
                            {#each typeOptions as option}
                                <option value={option.value}>{option.label}</option>
                            {/each}
                        </select>
                    </div>
                    <div class="flex justify-end space-x-2">
                        <button
                                onclick={() => (showAddModal = false)}
                                class="px-4 py-2 bg-gray-300 rounded"
                        >
                            Cancel
                        </button>
                        <button
                                onclick={handleAddField}
                                class="px-4 py-2 bg-blue-500 text-white rounded"
                        >
                            Add
                        </button>
                    </div>
                </div>
            </div>
        {/if}

        <!-- Remove Field Modal -->
        {#if showRemoveModal}
            <div class="fixed inset-0 bg-opacity-50 flex items-center justify-center">
                <div class="bg-white p-6 rounded shadow-md w-96">
                    <h2 class="text-xl font-semibold mb-4">Remove Field</h2>
                    <p class="mb-4">Are you sure you want to remove the field "{fieldName}"?</p>
                    <div class="flex justify-end space-x-2">
                        <button
                                onclick={() => (showRemoveModal = false)}
                                class="px-4 py-2 bg-gray-300 rounded"
                        >
                            Cancel
                        </button>
                        <button
                                onclick={() => handleRemoveField(fieldName)}
                                class="px-4 py-2 bg-red-500 text-white rounded"
                        >
                            Remove
                        </button>
                    </div>
                </div>
            </div>
        {/if}

        <!-- Field Settings -->
        <div>
            <div class="flex items-center justify-between mb-4">
                <h2 class="text-2xl font-semibold">Field Settings</h2>
                <button
                        onclick={() => (showAddModal = true)}
                        class="px-4 py-2 bg-green-500 text-white rounded"
                >
                    Add Field
                </button>
            </div>
            <div class="bg-white shadow-sm rounded-lg p-4 border space-y-2">
                {#each Object.entries(user.fieldSettings) as [key, value]}
                    <div class="flex justify-between items-center">
                        <span class="font-medium text-gray-700">{key}</span>
                        <div class="flex items-center gap-2">
                            <span class="text-gray-600">{value}</span>
                            <button
                                    onclick={() => {
                  fieldName = key;
                  fieldType = value;
                  showRemoveModal = true;
                }}
                                    class="text-red-500"
                            >
                                &times;
                            </button>
                        </div>
                    </div>
                {/each}
            </div>
        </div>
    </div>
{/if}
