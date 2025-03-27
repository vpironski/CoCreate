<script>
    import {onMount} from 'svelte';
    import {goto} from '$app/navigation';
    import {getUserId, getAllProjects} from '$lib/api';

    let project = {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        settings: {},
        budget: 0.0,
        parentProjectId: '',
        customFields: {},
        priority: 'MEDIUM'
    };

    let projects = [];
    let error = '';
    let userId = '';
    let isLoading = true;

    onMount(async () => {
        userId = getUserId();
        try {
            const response = await getAllProjects(userId);
            projects = response.data || [];
        } catch (err) {
            error = 'Failed to load projects';
        } finally {
            isLoading = false;
        }
    });

    function formatDate(dateStr) {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        const pad = num => num.toString().padStart(2, '0');
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
    }

    async function handleSubmit() {
        try {
            const formattedProject = {
                ...project,
                startDate: formatDate(project.startDate),
                endDate: formatDate(project.endDate),
                settings: {
                    ...project.settings,
                    priority: project.priority
                },
                budget: Number(project.budget),
                parentProjectId: project.parentProjectId || "",
                customFields: project.customFields || {}
            };

            const response = await fetch(`localhost:8080/api/${userId}/dashboard/create-project`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formattedProject)
            });

            if (!response.ok) {
                throw new Error('Failed to create project');
            }

            await goto(`/${userId}/dashboard`);
        } catch (err) {
            error = err.message;
        }
    }
</script>

<div class="min-h-screen flex items-start justify-center pt-16 md:pt-24 px-4 md:px-8">
    <div class="w-full max-w-3xl bg-white rounded-3xl shadow-xl p-6 md:p-10 dark:bg-gray-800 transition-all duration-300 mt-8 md:mt-16">
        <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-6">Create New Project</h2>

        {#if error}
            <div class="mb-4 p-3 bg-red-100 text-red-700 rounded-lg">{error}</div>
        {/if}

        <form on:submit|preventDefault={handleSubmit} class="w-full mx-auto space-y-6">
            <!-- Project Name -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2" for="project-name">
                    Project Name*
                </label>
                <input
                        id="project-name"
                        type="text"
                        bind:value={project.name}
                        class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                        required
                />
            </div>

            <!-- Description -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2" for="description">
                    Description
                </label>
                <textarea
                        id="description"
                        bind:value={project.description}
                        rows="3"
                        class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                ></textarea>
            </div>

            <!-- Date Range -->
            <div class="flex flex-wrap -mx-3 mb-6">
                <div class="w-full md:w-1/2 px-3 mb-6 md:mb-0">
                    <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2" for="start-date">
                        Start Date*
                    </label>
                    <input
                            id="start-date"
                            type="date"
                            bind:value={project.startDate}
                            class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                            required
                    />
                </div>
                <div class="w-full md:w-1/2 px-3">
                    <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2" for="end-date">
                        End Date*
                    </label>
                    <input
                            id="end-date"
                            type="date"
                            bind:value={project.endDate}
                            class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                            required
                    />
                </div>
            </div>

            <!-- Budget -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2" for="budget">
                    Budget ($)
                </label>
                <input
                        id="budget"
                        type="number"
                        step="0.01"
                        min="0"
                        bind:value={project.budget}
                        class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                />
            </div>

            <!-- Priority -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2">
                    Priority
                </label>
                <div class="flex space-x-4">
                    {#each ['LOW', 'MEDIUM', 'HIGH'] as priority}
                        <label class="inline-flex items-center">
                            <input
                                    type="radio"
                                    bind:group={project.priority}
                                    value={priority}
                                    class="form-radio h-5 w-5 text-teal-600 dark:text-teal-400"
                            />
                            <span class="ml-2 text-gray-700 dark:text-gray-300">{priority}</span>
                        </label>
                    {/each}
                </div>
            </div>

            <!-- Parent Project -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2" for="parent-project">
                    Related Project (optional)
                </label>
                {#if isLoading}
                    <select disabled class="block w-full bg-gray-300 text-gray-500 rounded-lg py-3 px-4">
                        <option>Loading projects...</option>
                    </select>
                {:else}
                    <select
                            id="parent-project"
                            bind:value={project.parentProjectId}
                            class="block appearance-none w-full bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded-lg leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                    >
                        <option value="">None (create new standalone project)</option>
                        {#each projects as p}
                            <option value={p.id}>{p.name}</option>
                        {/each}
                    </select>
                {/if}
            </div>

            <!-- Custom Fields -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2">
                    Custom Fields
                </label>
                <div class="space-y-4">
                    <div>
                        <label class="block text-gray-700 text-sm mb-1" for="client-name">
                            Client Name
                        </label>
                        <input
                                id="client-name"
                                type="text"
                                bind:value={project.customFields.clientName}
                                class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-2 px-3 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                        />
                    </div>
                    <div>
                        <label class="block text-gray-700 text-sm mb-1" for="website-type">
                            Website Type
                        </label>
                        <input
                                id="website-type"
                                type="text"
                                bind:value={project.customFields.websiteType}
                                class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-2 px-3 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                        />
                    </div>
                </div>
            </div>

            <!-- Submit Button -->
            <div class="flex justify-end pt-4">
                <button
                        type="submit"
                        class="bg-teal-600 hover:bg-teal-700 text-white font-bold py-2 px-6 rounded-lg focus:outline-none focus:shadow-outline transition-colors disabled:opacity-50"
                        disabled={isLoading}
                >
                    {isLoading ? 'Creating...' : 'Create Project'}
                </button>
            </div>
        </form>
    </div>
</div>