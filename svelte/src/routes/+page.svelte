<script>
    import {onMount} from 'svelte';
    import {loginUser, registerUser, isAuthenticated, getUserId} from '$lib/api';
    import {goto} from '$app/navigation';

    let username = '';
    let email = '';
    let password = '';
    let isRegistering = false; // Toggle between login and register

    async function handleSubmit() {
        try {
            let userData;
            if (isRegistering) {
                // Register the user and store token and userId
                userData = await registerUser(username, email, password);
            } else {
                // Log the user in and store token and userId
                userData = await loginUser(username, password);
            }

            // After successful login or register, redirect to the dashboard
            if (userData?.userId) {
                goto(`/${userData.userId}/dashboard`);
            }
        } catch (error) {
            console.error(error.message);
        }
    }

    onMount(() => {
        // If the user is already authenticated, redirect to their dashboard
        if (isAuthenticated()) {
            const userId = getUserId();
            if (userId) {
                goto(`/${userId}/dashboard`);
            }
        }
    });
</script>

<div class="flex flex-col items-center justify-center min-h-screen">
    <h1 class="text-3xl font-bold">{isRegistering ? 'Register' : 'Login'}</h1>

    <input type="text" bind:value={username} placeholder="Username" class="border p-2 my-2">
    {#if isRegistering}
        <input type="email" bind:value={email} placeholder="Email" class="border p-2 my-2">
    {/if}
    <input type="password" bind:value={password} placeholder="Password" class="border p-2 my-2">

    <button on:click={handleSubmit} class="bg-blue-500 text-white px-4 py-2 rounded">
        {isRegistering ? 'Register' : 'Login'}
    </button>

    <button on:click={() => isRegistering = !isRegistering} class="text-blue-500 mt-4">
        {isRegistering ? 'Already have an account? Login' : "Don't have an account? Register"}
    </button>
</div>