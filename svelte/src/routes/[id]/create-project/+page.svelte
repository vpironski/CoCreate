<script>
    import { goto } from '$app/navigation';
    import { getUserId, getAllProjects, getProjectCustomFields } from '$lib/api.js';

    let project = {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        parentProjectId: '',
        customFields: {},
        priority: 'MEDIUM'
    };

    let projects = []; // For parent project selection
    let error = '';
    let userId = getUserId();
    let isLoading = true;
    let customFieldDefinitions = [];

    async function loadInitialData() {
        isLoading = true;
        error = '';
        try {
            // Load projects for parent selection
            projects = await getAllProjects(userId);

            // Load custom fields - now using correct endpoint
            const customFieldsResponse = await getProjectCustomFields(userId);

            // Transform the response if needed (based on your Postman screenshot)
            customFieldDefinitions = Object.entries(customFieldsResponse).map(([name, value]) => ({
                name,
                type: determineFieldType(value) // You'll need this helper function
            }));

            // Initialize custom fields
            project.customFields = customFieldDefinitions.reduce((acc, field) => {
                acc[field.name] = field.type === 'checkbox' ? false :
                    field.type === 'number' ? 0 : '';
                return acc;
            }, {});

        } catch (err) {
            console.error("Initialization failed:", err);
            error = "Failed to load project settings. Please try again.";
            if (err.response?.data?.message) {
                error = err.response.data.message;
            }
        } finally {
            isLoading = false;
        }
    }

    // Add this helper function to your script
    function determineFieldType(value) {
        if (typeof value === 'boolean') return 'checkbox';
        if (typeof value === 'number') return 'number';
        if (!isNaN(Date.parse(value))) return 'date';
        return 'text';
    }

    // Load data when component mounts
    loadInitialData();

    function formatDate(dateStr) {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        const pad = num => num.toString().padStart(2, '0');
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
    }

    async function handleSubmit() {
        error = '';
        try {
            const formattedProject = {
                ...project,
                startDate: formatDate(project.startDate),
                endDate: formatDate(project.endDate),
                parentProjectId: project.parentProjectId || null,
                customFields: project.customFields
            };

            const response = await fetch(`http://localhost:8080/api/${userId}/dashboard/create-project`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
                },
                body: JSON.stringify(formattedProject)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create project');
            }

            await goto(`/${userId}/dashboard`);
        } catch (err) {
            console.error("Project creation failed:", err);
            error = err.message || 'Project creation failed';
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
                        End Date
                    </label>
                    <input
                            id="end-date"
                            type="date"
                            bind:value={project.endDate}
                            class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                    />
                </div>
            </div>

            <!-- Custom Fields -->
            <div>
                <label for="custom_fields" class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2">
                    Custom Fields
                </label>
                <div id="custom_fields" class="space-y-4">
                    {#if customFieldDefinitions.length > 0}
                        {#each customFieldDefinitions as field, index}
                            <div>
                                <label class="block text-gray-700 text-sm mb-1">
                                    {field.name}
                                </label>
                                {#if field.type === 'checkbox'}
                                    <input
                                            data-field={`custom-field-${index}`}
                                            type="checkbox"
                                            bind:checked={project.customFields[field.name]}
                                            class="h-4 w-4 text-teal-600 focus:ring-teal-500 border-gray-300 rounded"
                                    />
                                {:else}
                                    <input
                                            data-field={`custom-field-${index}`}
                                            type={field.type}
                                            bind:value={project.customFields[field.name]}
                                            placeholder={field.placeholder}
                                            class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-2 px-3 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                                    />
                                {/if}
                            </div>
                        {/each}
                    {/if}
                </div>
            </div>

            <!-- Priority -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2">
                    Priority
                </label>
                <div class="flex space-x-4">
                    {#each ['LOW', 'MEDIUM', 'HIGH', 'URGENT'] as priority}
                        <label for="priority" class="inline-flex items-center">
                            <input
                                    id="priority"
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