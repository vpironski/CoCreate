<script>
    import { goto } from '$app/navigation';
    import {getUserId, getAllProjects, getProjectCustomFields, createProject, isAuthenticated} from '$lib/api.js';
    import { onMount } from "svelte";

    let project = {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        parentProjectId: '',
        customFields: {},
        priority: 'MEDIUM'
    };

    let customFieldDefinitions = [];

    let projects = [];
    let error = '';
    let userId = getUserId();
    let isLoading = true;

    onMount(async () => {
        await loadInitialData();
        if (!project.startDate) {
            project.startDate = new Date().toISOString().split('T')[0];
        }
        if (!project.endDate) {
            let defaultEndDate = new Date();
            defaultEndDate.setMonth(defaultEndDate.getMonth() + 1);
            project.endDate = defaultEndDate.toISOString().split('T')[0];
        }
    });

    async function loadInitialData() {
        isLoading = true;
        error = '';
        customFieldDefinitions = [];
        let formCustomFieldsMap = {};
        try {
            projects = await getAllProjects(userId);
            const customFieldsResponse = await getProjectCustomFields(userId);
            const fetchedFields = Array.isArray(customFieldsResponse?.customFields) ? customFieldsResponse.customFields : [];

            fetchedFields.forEach(field => {
                const fieldType = determineFieldType(field.value);
                let initialValue = field.value;

                if (fieldType === 'date' && initialValue && typeof initialValue === 'string') {
                    initialValue = initialValue.split('T')[0];
                } else if (fieldType === 'datetime-local' && initialValue && typeof initialValue === 'string') {
                    initialValue = initialValue.substring(0, 16);
                }

                customFieldDefinitions.push({
                    name: field.name,
                    type: fieldType,
                    originalValue: initialValue
                });

                formCustomFieldsMap[field.name] = initialValue;
            });

            project.customFields = formCustomFieldsMap;
            project = project;
            customFieldDefinitions = customFieldDefinitions;

        } catch (err) {
            console.error("Initialization failed:", err);
            error = "Failed to load project settings. Please try again.";
            if (err?.response?.data?.message) { error = err.response.data.message; } else if (err.message) { error = err.message; }
        } finally {
            isLoading = false;
        }
    }

    function determineFieldType(value) {
        if (typeof value === 'boolean') return 'checkbox';
        if (typeof value === 'number') return 'number';
        if (typeof value === 'string') {
            if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(:\d{2}(\.\d+)?)?/.test(value)) return 'datetime-local';
            if (/^\d{4}-\d{2}-\d{2}$/.test(value)) return 'date';
        }
        return 'text';
    }

    function formatDateTime(value) {
        if (typeof value === 'string' && value.includes('T')) {
            return value.substring(0, 16).replace('T', ' ');
        }
        return value;
    }

    async function handleSubmit() {
        error = '';
        isLoading = true;
        try {

            const processedCustomFieldsArray = [];
            for (const fieldDef of customFieldDefinitions) {
                const name = fieldDef.name;
                const type = fieldDef.type;
                let currentValue = project.customFields[name];

                if (type === 'number') {
                    currentValue = (currentValue === '' || currentValue === null || isNaN(parseFloat(currentValue))) ? 0 : parseFloat(currentValue);
                } else if (type === 'checkbox') {
                    currentValue = Boolean(currentValue);
                } else if (type === 'date') {
                    currentValue = currentValue || null;
                } else if (type === 'datetime-local') {
                    currentValue = currentValue || null;
                } else {
                    currentValue = (currentValue === null || currentValue === undefined) ? "" : String(currentValue);
                }

                processedCustomFieldsArray.push({
                    name: name,
                    value: currentValue
                });
            }

            const formattedProject = {
                name: project.name,
                description: project.description,
                startDate: project.startDate || null,
                endDate: project.endDate || null,
                parentProjectId: project.parentProjectId || null,
                priority: project.priority,
                customFields: {
                    customFields: processedCustomFieldsArray
                }
            };

            await createProject(userId, formattedProject);
            console.log("Project creation successful, navigating to dashboard");

            await goto(`/${userId}/dashboard`);

        } catch (err) {
            console.error("Project creation failed:", err);
            error = err.message || "An unexpected error occurred.";

            if (!isAuthenticated()) {
                console.error("Lost authentication during request");
                error += " Session expired. Please log in again.";
            }
        } finally {
            isLoading = false;
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

            <!-- Parent Project Selection -->
            <div>
                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2">
                    Parent Project
                </label>
                <select
                        bind:value={project.parentProjectId}
                        class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                >
                    <option value="">None</option>
                    {#each projects as p}
                        <option value={p.id}>{p.name}</option>
                    {/each}
                </select>
            </div>


            <!-- Custom Fields -->
            <div>
                {#if customFieldDefinitions.length > 0}
                    <div id="custom_fields" class="space-y-4">
                        <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Custom Fields</h3>

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
                                            placeholder={formatDateTime(field.value)}
                                            class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded-lg py-2 px-3 leading-tight focus:outline-none focus:bg-white focus:border-teal-500 dark:bg-gray-700 dark:text-white"
                                    />
                                {/if}
                            </div>
                        {/each}
                    </div>
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