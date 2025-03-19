<script>
    import { onMount } from 'svelte';
    import { loginUser, registerUser, isAuthenticated, getUserId } from '$lib/api';
    import { goto } from '$app/navigation';

    let username = '';
    let email = '';
    let password = '';
    let isRegistering = false;
    let errorMessage = '';

    async function handleSubmit() {
        try {
            let userData;
            errorMessage = '';

            if (isRegistering) {
                userData = await registerUser(username, email, password);
            } else {
                userData = await loginUser(username, password);
            }

            if (userData?.userId) {
                username = '';
                email = '';
                password = '';
                goto(`/${userData.userId}/dashboard`);
            }
        } catch (error) {
            console.error(error.message);
            errorMessage = error.message;
        }
    }

</script>

<div class="flex flex-col items-center justify-center min-h-screen">
    <h1 class="text-3xl font-bold">{isRegistering ? 'Register' : 'Login'}</h1>

    <!-- Display error message if exists -->
    {#if errorMessage}
        <div class="text-red-500 mb-4">{errorMessage}</div>
    {/if}

    <input
            type="text"
            bind:value={username}
            placeholder="Username"
            class="border p-2 my-2"
            required
    >

    {#if isRegistering}
        <input
                type="email"
                bind:value={email}
                placeholder="Email"
                class="border p-2 my-2"
                required
        >
    {/if}

    <input
            type="password"
            bind:value={password}
            placeholder="Password"
            class="border p-2 my-2"
            required
    >

    <button
            on:click={handleSubmit}
            class="bg-blue-500 text-white px-4 py-2 rounded"
            disabled={!username || !password || (isRegistering && !email)}
    >
        {isRegistering ? 'Register' : 'Login'}
    </button>

    <button
            on:click={() => isRegistering = !isRegistering}
            class="text-blue-500 mt-4"
    >
        {isRegistering ? 'Already have an account? Login' : "Don't have an account? Register"}
    </button>
</div>
