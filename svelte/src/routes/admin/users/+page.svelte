<script>
    import { onMount } from 'svelte';
    import { getUsers } from '$lib/api.js';
    let users = [];
    let error = '';

    onMount(async () => {
        try {
            users = await getUsers();
        } catch (err) {
            error = err.message;
        }
    });
</script>

<div class="min-h-screen flex bg-gray-50">

    <!-- Main Content -->
    <main class="flex-1 p-8">
        <h2 class="text-3xl font-semibold mb-4">Admin Panel</h2>
        {#if error}
            <div class="text-red-600">{error}</div>
        {:else}
            <table class="min-w-full bg-white shadow rounded-lg overflow-hidden">
                <thead class="bg-gray-100 text-left">
                <tr>
                    <th class="px-6 py-3 text-sm font-semibold text-gray-600 uppercase">Username</th>
                    <th class="px-6 py-3 text-sm font-semibold text-gray-600 uppercase">Actions</th>
                </tr>
                </thead>
                <tbody>
                {#each users as user}
                    <tr class="border-b hover:bg-gray-50">
                        <td class="px-6 py-4">{user.username}</td>
                        <td class="px-6 py-4">
                            <a class="text-blue-600 hover:text-blue-800 font-medium"
                               href={`/admin/audit-logs/${user.id}`}>
                                View Audit Logs
                            </a>
                        </td>
                    </tr>
                {/each}
                </tbody>
            </table>
        {/if}
    </main>
</div>
