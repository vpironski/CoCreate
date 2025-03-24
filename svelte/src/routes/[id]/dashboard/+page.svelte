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
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>

</body>
</html>


<h2>Section title</h2>
<div class="table-responsive small">
    <table class="table table-striped table-sm">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Header</th>
            <th scope="col">Header</th>
            <th scope="col">Header</th>
            <th scope="col">Header</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>1,001</td>
            <td>random</td>
            <td>data</td>
            <td>placeholder</td>
            <td>text</td>
        </tr>
        <tr>
            <td>1,002</td>
            <td>placeholder</td>
            <td>irrelevant</td>
            <td>visual</td>
            <td>layout</td>
        </tr>
        <tr>
            <td>1,003</td>
            <td>data</td>
            <td>rich</td>
            <td>dashboard</td>
            <td>tabular</td>
        </tr>
        <tr>
            <td>1,003</td>
            <td>information</td>
            <td>placeholder</td>
            <td>illustrative</td>
            <td>data</td>
        </tr>
        <tr>
            <td>1,004</td>
            <td>text</td>
            <td>random</td>
            <td>layout</td>
            <td>dashboard</td>
        </tr>
        <tr>
            <td>1,005</td>
            <td>dashboard</td>
            <td>irrelevant</td>
            <td>text</td>
            <td>placeholder</td>
        </tr>
        <tr>
            <td>1,006</td>
            <td>dashboard</td>
            <td>illustrative</td>
            <td>rich</td>
            <td>data</td>
        </tr>
        <tr>
            <td>1,007</td>
            <td>placeholder</td>
            <td>tabular</td>
            <td>information</td>
            <td>irrelevant</td>
        </tr>
        <tr>
            <td>1,008</td>
            <td>random</td>
            <td>data</td>
            <td>placeholder</td>
            <td>text</td>
        </tr>
        <tr>
            <td>1,009</td>
            <td>placeholder</td>
            <td>irrelevant</td>
            <td>visual</td>
            <td>layout</td>
        </tr>
        <tr>
            <td>1,010</td>
            <td>data</td>
            <td>rich</td>
            <td>dashboard</td>
            <td>tabular</td>
        </tr>
        <tr>
            <td>1,011</td>
            <td>information</td>
            <td>placeholder</td>
            <td>illustrative</td>
            <td>data</td>
        </tr>
        <tr>
            <td>1,012</td>
            <td>text</td>
            <td>placeholder</td>
            <td>layout</td>
            <td>dashboard</td>
        </tr>
        <tr>
            <td>1,013</td>
            <td>dashboard</td>
            <td>irrelevant</td>
            <td>text</td>
            <td>visual</td>
        </tr>
        <tr>
            <td>1,014</td>
            <td>dashboard</td>
            <td>illustrative</td>
            <td>rich</td>
            <td>data</td>
        </tr>
        <tr>
            <td>1,015</td>
            <td>random</td>
            <td>tabular</td>
            <td>information</td>
            <td>text</td>
        </tr>
        </tbody>
    </table>
</div>
