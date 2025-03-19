<script>
    import { onMount } from 'svelte';
    import { logoutUser, isAuthenticated, getUserId } from '$lib/api';
    import { goto } from '$app/navigation';
    import { page } from '$app/stores';

    let isRedirecting = false;
    let userId = $page.params.id;

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
            }
        } catch (error) {
            console.error("Authentication check failed:", error);
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
    <h1 class="text-3xl font-bold">Welcome to Your Dashboard, User {userId}!</h1>

    <button on:click={handleLogout}
            class="bg-red-500 text-white px-4 py-2 mt-4 rounded hover:bg-red-600 transition">
        Logout
    </button>
</div>
