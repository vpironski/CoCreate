<script>
    import { onMount } from 'svelte';
    import { registerUser, loginUser, getToken, isAuthenticated, logoutUser } from '../lib/api.js';

    let username = "";
    let email = "";
    let password = "";
    let isRegistering = false; // toggle between login and register
    let message = "";
    let token = getToken();

    async function handleSubmit() {
        try {
            if (isRegistering) {
                token = await registerUser(username, email, password);
                message = "Registration successful! Redirecting...";
            } else {
                token = await loginUser(username, password);
                message = "Login successful! Redirecting...";
            }
            // Redirect or handle auth state after successful auth
            setTimeout(() => {
                window.location.href = "/dashboard";
            }, 1500);
        } catch (error) {
            message = error.message;
        }
    }

    function toggleForm() {
        isRegistering = !isRegistering;
    }

    onMount(() => {
        if (isAuthenticated()) {
            window.location.href = "/dashboard";
        }
    });
</script>
<h1 class="text-4xl font-bold text-center mt-10">Hello, Tailwind!</h1>
<div class="container">
    {#if token}
        <h2>Welcome!</h2>
        <p>You are logged in.</p>
        <button on:click={logoutUser}>Logout</button>
    {:else}
        <h2>{isRegistering ? "Register" : "Login"}</h2>
        <form on:submit|preventDefault={handleSubmit}>
            {#if isRegistering}
                <input type="text" bind:value={username} placeholder="Username" required />
                <input type="email" bind:value={email} placeholder="Email" required />
            {:else}
                <input type="text" bind:value={username} placeholder="Username" required />
            {/if}
            <input type="password" bind:value={password} placeholder="Password" required />
            <button type="submit">{isRegistering ? "Register" : "Login"}</button>
        </form>
        <button on:click={toggleForm}>
            {isRegistering ? "Already have an account? Login" : "New user? Register"}
        </button>
        {#if message}
            <p>{message}</p>
        {/if}
    {/if}
</div>
