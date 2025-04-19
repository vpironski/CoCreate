<script>
	import '../app.css';
    import { page } from '$app/stores';
    import {getUsername, getUserId, logoutUser, getRole} from '$lib/api';
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';
    import { browser } from '$app/environment';

    let userId = '';
    let username = '';
    let message = '';
    let role = '';

    let showDropdown = false;
    const toggleDropdown = () => showDropdown = !showDropdown;

    $: layoutClasses = $page.data.showNavbar ?? true ? 'min-h-screen overflow-y-auto ml-0 md:ml-64 pt-16' : '';

    onMount(() => {
        if (browser) {
            userId = getUserId();
            username = getUsername();
            role = getRole();
            console.log(role)
        }
    });


    async function handleSignOut() {
        try {
            await logoutUser();
            if (browser) {
                localStorage.removeItem('username');
                setTimeout(() => goto('/'), 1000);
            }
        } catch (error) {
            message = error.message;
            if (browser) {
                localStorage.removeItem('userId');
                localStorage.removeItem('username');
                setTimeout(() => goto('/'), 1000);
            }
        }
    }
</script>
<svelte:head>
    <meta charSet="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta http-equiv="X-UA-Compatible" content="ie=edge"/>
    <title>CoCreate</title>
    <link href="https://fonts.googleapis.com/css2?family=Readex+Pro:wght@200..700&display=swap" rel="stylesheet">
    <style>
        button { cursor: pointer; }
        * {
            font-family: 'Readex Pro', sans-serif;
            font-weight: 350;
        }

        /* Optional: Create weight classes for specific elements */
        .font-semibold { font-weight: 450; }
        /*.font-bold { font-weight: 600; }*/
        /*.font-extrabold { font-weight: 700; }*/
    </style>

</svelte:head>


{#if $page.data.showNavbar ?? true}


    <nav class="fixed top-0 z-50 w-full bg-gray-100 border-b border-gray-200 dark:bg-gray-800 dark:border-gray-900">
        <div class="px-3 py-3 lg:px-5 lg:pl-3">
            <div class="flex items-center justify-between">
                <div class="flex items-center justify-start rtl:justify-end">
<!--                    <button data-drawer-target="logo-sidebar" data-drawer-toggle="logo-sidebar" aria-controls="logo-sidebar" type="button" class="inline-flex items-center p-2 text-sm text-gray-900 rounded-lg sm:hidden hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:text-gray-400 dark:hover:bg-gray-700 dark:focus:ring-gray-600">-->
<!--                        <span class="sr-only">Open sidebar</span>-->
<!--                        <svg class="w-6 h-6" aria-hidden="true" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">-->
<!--                            <path clip-rule="evenodd" fill-rule="evenodd" d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 4.75zm0 10.5a.75.75 0 01.75-.75h7.5a.75.75 0 010 1.5h-7.5a.75.75 0 01-.75-.75zM2 10a.75.75 0 01.75-.75h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 10z"></path>-->
<!--                        </svg>-->
<!--                    </button>-->
                    <a href={userId ? `/${userId}/dashboard` : '/'} class="flex ms-2 md:me-24">
                        <span class="self-center text-xl font-semibold sm:text-4xl whitespace-nowrap text-teal-900 dark:text-teal-500">
                            CoCreate
                        </span>
                    </a>
                </div>
                <div class="flex items-center">
                    <div class="flex items-center ms-3 group relative">
                        <div class="relative flex items-center gap-2">
                            {#if username}
                                <span class="text-xl font-medium">{username}</span>
                            {/if}

                            <button on:click={toggleDropdown} class="focus:outline-none">
                                <img
                                        class="w-8 h-8 rounded-full bg-gray-200"
                                        src={`https://ui-avatars.com/api/?name=${username || 'U'}&background=random`}
                                        alt="User avatar"
                                />
                            </button>

                            <!-- Dropdown -->
                            {#if showDropdown}
                                <div class="absolute right-0 top-full mt-2 w-48 z-50 bg-white divide-y divide-gray-100 rounded-sm shadow-lg dark:bg-gray-700 dark:divide-gray-600">
                                    <ul class="py-1">
                                        <li class="block md:hidden">
                                            <a
                                                    href={userId ? `/${userId}/dashboard` : '/'}
                                                    class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-600 dark:hover:text-white"
                                            >
                                                Dashboard
                                            </a>
                                        </li>
                                        <li>
                                            <a
                                                    href={userId ? `/${userId}` : '/'}
                                                    class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-600 dark:hover:text-white"
                                            >
                                                Your profile
                                            </a>
                                        </li>
                                        <li>
                                            <button
                                                    on:click={handleSignOut}
                                                    class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-600 dark:hover:text-white"
                                            >
                                                Sign out
                                            </button>
                                        </li>
                                    </ul>
                                </div>
                            {/if}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <aside id="logo-sidebar" class="fixed top-0 left-0 z-40 w-64 h-screen pt-20 transition-transform -translate-x-full bg-gray-100 border-r border-gray-200 sm:translate-x-0 dark:bg-gray-800 dark:border-gray-700" aria-label="Sidebar">
        <div class="h-full px-3 pb-4 overflow-y-auto dark:bg-gray-800">
            <ul class="space-y-2 font-medium">
                <li>
                    <a href={userId ? `/${userId}/create-project` : '/'} class="flex items-center p-2 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
                        <svg class="shrink-0 w-5 h-5 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 18">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m3.75 9v6m3-3H9m1.5-12H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
                            </svg>
                        </svg>
                        <span class="ms-3">New Project</span>
                    </a>
                </li>
                <li>
                    <a href={userId ? `/${userId}/dashboard` : '/'} class="flex items-center p-2 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
                        <svg class="shrink-0 w-5 h-5 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 18">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M6 6.878V6a2.25 2.25 0 0 1 2.25-2.25h7.5A2.25 2.25 0 0 1 18 6v.878m-12 0c.235-.083.487-.128.75-.128h10.5c.263 0 .515.045.75.128m-12 0A2.25 2.25 0 0 0 4.5 9v.878m13.5-3A2.25 2.25 0 0 1 19.5 9v.878m0 0a2.246 2.246 0 0 0-.75-.128H5.25c-.263 0-.515.045-.75.128m15 0A2.25 2.25 0 0 1 21 12v6a2.25 2.25 0 0 1-2.25 2.25H5.25A2.25 2.25 0 0 1 3 18v-6c0-.98.626-1.813 1.5-2.122" />
                            </svg>
                        </svg>
                        <span class="ms-3">Dashboard</span>
                    </a>
                </li>
                {#if role === 'ADMIN'}
                    <li>
                        <a href="/admin/users" class="flex items-center p-2 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
                            <svg class="shrink-0 w-5 h-5 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M15 19.5V21m-6-1.5V21m6-1.5a3 3 0 0 0-6 0M21 7.5c0 2.485-2.239 4.5-5 4.5s-5-2.015-5-4.5S13.239 3 16 3s5 2.015 5 4.5Zm-13 6A4.5 4.5 0 0 1 3 9c0-2.485 2.015-5 4.5-5S12 6.515 12 9a4.5 4.5 0 0 1-4.5 4.5Zm0 0A6.975 6.975 0 0 0 3 18.75V21h9v-2.25a6.975 6.975 0 0 0-4.5-6.75Z" />
                            </svg>
                            <span class="ms-3">Users</span>
                        </a>
                    </li>
                {/if}
<!--                <li>-->
<!--                    <a href="#" class="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">-->
            <!--                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">-->
            <!--                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 13.5h3.86a2.25 2.25 0 0 1 2.012 1.244l.256.512a2.25 2.25 0 0 0 2.013 1.244h3.218a2.25 2.25 0 0 0 2.013-1.244l.256-.512a2.25 2.25 0 0 1 2.013-1.244h3.859m-19.5.338V18a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18v-4.162c0-.224-.034-.447-.1-.661L19.24 5.338a2.25 2.25 0 0 0-2.15-1.588H6.911a2.25 2.25 0 0 0-2.15 1.588L2.35 13.177a2.25 2.25 0 0 0-.1.661Z" />-->
            <!--                </svg>-->

                <!--                        <span class="flex-1 ms-3 whitespace-nowrap">Inbox</span>-->
<!--                        <span class="inline-flex items-center justify-center w-3 h-3 p-3 ms-3 text-sm font-medium text-blue-800 bg-blue-100 rounded-full dark:bg-blue-900 dark:text-blue-300">3</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--                <li>-->
<!--                    <a href="#" class="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">-->
<!--                        <svg class="shrink-0 w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 18">-->
<!--                            <path d="M14 2a3.963 3.963 0 0 0-1.4.267 6.439 6.439 0 0 1-1.331 6.638A4 4 0 1 0 14 2Zm1 9h-1.264A6.957 6.957 0 0 1 15 15v2a2.97 2.97 0 0 1-.184 1H19a1 1 0 0 0 1-1v-1a5.006 5.006 0 0 0-5-5ZM6.5 9a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9ZM8 10H5a5.006 5.006 0 0 0-5 5v2a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-2a5.006 5.006 0 0 0-5-5Z"/>-->
<!--                        </svg>-->
<!--                        <span class="flex-1 ms-3 whitespace-nowrap">Users</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--                <li>-->
<!--                    <a href="#" class="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">-->
<!--                        <svg class="shrink-0 w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 20">-->
<!--                            <path d="M17 5.923A1 1 0 0 0 16 5h-3V4a4 4 0 1 0-8 0v1H2a1 1 0 0 0-1 .923L.086 17.846A2 2 0 0 0 2.08 20h13.84a2 2 0 0 0 1.994-2.153L17 5.923ZM7 9a1 1 0 0 1-2 0V7h2v2Zm0-5a2 2 0 1 1 4 0v1H7V4Zm6 5a1 1 0 1 1-2 0V7h2v2Z"/>-->
<!--                        </svg>-->
<!--                        <span class="flex-1 ms-3 whitespace-nowrap">Products</span>-->
<!--                    </a>-->
<!--                </li>-->
            </ul>
        </div>
    </aside>

{/if}

<main class="{layoutClasses}">
    <slot/>
</main>

