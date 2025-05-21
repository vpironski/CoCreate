<script>
    import { onMount } from 'svelte';
    import { getAuditLogs, restoreProject } from '$lib/api.js';
    import {page} from '$app/state';

    let auditLogs = [];
    let userId = page.params.userId;
    let error = null;

    onMount(async () => {
        try {
            auditLogs = await getAuditLogs(userId);
        } catch (err) {
            error = err.message;
        }
    });

    async function handleRestore(entityId) {
        try {
            const result = await restoreProject(userId, entityId);
            alert(result.message || 'Project restored!');
            auditLogs = await getAuditLogs(userId);
        } catch (err) {
            alert('Failed to restore project: ' + err.message);
        }
    }
</script>

<section class="p-6 max-w-4xl mx-auto">
    <h1 class="text-2xl font-bold mb-4">Deleted Projects - Audit Logs</h1>

    {#if error}
        <p class="text-red-500">{error}</p>
    {/if}

    {#if auditLogs.length === 0}
        <p class="text-gray-500">No deleted projects found.</p>
    {:else}
        <ul class="space-y-4">
            {#each auditLogs as log}
                <li class="p-4 border rounded-md shadow-sm bg-white dark:bg-gray-800 dark:text-white flex justify-between items-center">
                    <div>
                        <p><strong>Entity ID:</strong> {log.entityId}</p>
                        <p><strong>Project: </strong>{log.originalData.name}</p>
                        <p><strong>Deleted At:</strong> {new Date(log.deletedAt).toLocaleString()}</p>
                        <p><strong>Type:</strong> {log.type}</p>
                    </div>
                    <button
                            on:click={() => handleRestore(log.entityId)}
                            class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Restore
                    </button>
                </li>
            {/each}
        </ul>
    {/if}
</section>
