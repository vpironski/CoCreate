<script>
    import { onMount } from 'svelte';
    import { logoutUser, isAuthenticated, getUserId, getAllProjects } from '$lib/api';
    import { goto } from '$app/navigation';
    import { page } from '$app/stores';

    let isRedirecting = false;
    let userId = $page.params.id;
    let projects = [];

    onMount(async () => {
        try {
            const auth = await isAuthenticated();
            if (!auth) {
                if (!isRedirecting) {
                    isRedirecting = true;
                    goto('/');
                }
                return;
            }

            const storedUserId = await getUserId(); // Ensure it's awaited
            if (storedUserId !== userId) {
                if (!isRedirecting) {
                    isRedirecting = true;
                    await logoutUser();
                    goto('/');
                }
                return;
            }

            // Fetch and assign projects
            projects = await getAllProjects(userId);
        } catch (error) {
            console.error("Authentication or fetching projects failed:", error);
            goto('/');
        }
    });

    async function handleLogout() {
        try {
            await logoutUser();
            goto('/');
        } catch (error) {
            console.error("Logout failed", error);
        }
    }
</script>


<div class="flex flex-col items-center justify-center min-h-screen">
    <!-- Display Projects -->
    <div class="w-full max-w-4xl">
        {#if projects.length > 0}
            <ul class="space-y-4">
                {#each projects as project}
                    <li class="border p-4 rounded shadow">
                        <h2 class="text-xl font-semibold">{project.name}</h2>
                        <p>{project.description}</p>

                        <!-- Edit and Delete buttons -->
                        <div class="mt-4">
                            <button class="bg-blue-500 text-white px-4 py-2 mr-2 rounded hover:bg-blue-600">
                                Edit
                            </button>
                            <button class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">
                                Delete
                            </button>
                        </div>
                    </li>
                {/each}
            </ul>
        {:else}
            <p>You have no projects yet. Create a new project!</p>
        {/if}
    </div>

    <!-- Logout Button -->
    <button on:click={handleLogout} class="bg-red-500 text-white px-4 py-2 mt-4 rounded hover:bg-red-600 transition">
        Logout
    </button>
</div>
