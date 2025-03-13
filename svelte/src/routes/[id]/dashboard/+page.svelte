<script>
    import { onMount } from 'svelte';
    import { logoutUser, isAuthenticated, getUserId } from '$lib/api';
    import { goto } from '$app/navigation';
    import {page} from "$app/state";

    let userId;
    let isRedirecting = false;

    onMount(() => {
        // Access the $page store directly
        const { id } = $page.params; // Destructuring directly from $page
        userId = id;

        // If not authenticated, redirect to login page
        if (!isAuthenticated()) {
            if (!isRedirecting) {
                isRedirecting = true;
                goto('/'); // Redirect to login page
            }
        } else {
            // If the logged-in user's ID doesn't match the URL, redirect to login page
            const storedUserId = getUserId();
            if (storedUserId !== userId) {
                if (!isRedirecting) {
                    isRedirecting = true;
                    logoutUser(); // Log the user out
                    goto('/'); // Redirect to login page
                }
            }
        }
    });

    // Logout function that clears the session and redirects to login page
    function handleLogout() {
        logoutUser();
        goto('/'); // Redirect to login page after logout
    }
</script>

<div class="flex flex-col items-center justify-center min-h-screen">
    <h1 class="text-3xl font-bold">Welcome to Your Dashboard, User {userId}!</h1>

    <button on:click={handleLogout} class="bg-red-500 text-white px-4 py-2 mt-4 rounded hover:bg-red-600 transition">
        Logout
    </button>
</div>
