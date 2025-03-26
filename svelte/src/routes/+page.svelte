<script>
    import { loginUser, registerUser} from '$lib/api';
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


<div class="min-h-screen bg-teal-900 flex flex-col md:flex-row">
    <!-- Left Section - Mobile (Full Width) / Desktop (Half Width) -->
    <div class="w-full md:w-1/2 bg-gray-200 dark:bg-gray-900 p-6 md:p-8 flex items-center justify-center">
        <div class="max-w-md space-y-6 md:space-y-8">
                <!-- CoCreate Title -->
                <h1 class="text-5xl md:text-8xl font-bold text-orange-500 dark:text-orange-300 text-center md:text-left">
                    CoCreate
                </h1>

                <!-- Main Quote -->
                <blockquote class="text-3xl md:text-6xl font-bold text-teal-900 dark:text-teal-500 leading-tight text-center md:text-left">
                    "Be the cause something to <span class="text-orange-500 dark:text-orange-300">exist</span>."
                </blockquote>


                <!-- Why Choose Us Section -->
                <div class="hidden md:block space-y-6">
                    <h2 class="text-xl font-semibold text-teal-700 dark:text-teal-200">
                        Why choose us?
                    </h2>

                    <div class="space-y-6">
                        <!-- Feature 1 -->
                        <div class="space-y-1">
                            <h3 class="text-lg font-medium text-teal-900 dark:text-teal-500">
                                - Intuitive and User-friendly interface
                            </h3>
                            <p class="text-teal-600 dark:text-teal-200/80 text-sm">
                                //designed with simplicity in mind
                            </p>
                        </div>

                        <!-- Feature 2 -->
                        <div class="space-y-1">
                            <h3 class="text-lg font-medium text-teal-900 dark:text-teal-500">
                                - Customizable workflows for every team
                            </h3>
                            <p class="text-teal-600 dark:text-teal-200/80 text-sm">
                                //your unique project needs
                            </p>
                        </div>

                        <!-- Feature 3 -->
                        <div class="space-y-1">
                            <h3 class="text-lg font-medium text-teal-900 dark:text-teal-500">
                                - Real-time collaboration and insights
                            </h3>
                            <p class="text-teal-600 dark:text-teal-200/80 text-sm">
                                //productivity with seamless communication
                            </p>
                        </div>
                    </div>
                </div>
            </div>
    </div>

    <!-- Right Section - Login Form (Grey Text) -->
    <div class="w-full md:w-1/2 bg-teal-900 dark:bg-teal-50">
        <div class="flex flex-col items-center justify-center px-4 md:px-6 py-8 md:py-0 mx-auto h-full">
            <div class="w-full max-w-md bg-white rounded-lg shadow dark:border p-6 md:p-8 dark:bg-gray-800 dark:border-gray-700">
                <div class="p-6 space-y-4 md:space-y-6 sm:p-8">
                    <h1 class="text-xl font-bold leading-tight tracking-tight text-gray-700 md:text-2xl dark:text-gray-300">
                        {isRegistering ? 'Create Account' : 'Welcome Back'}
                    </h1>
                    {#if errorMessage}
                        <div class="text-red-500 mb-4">{errorMessage}</div>
                    {/if}
                    <form class="space-y-4 md:space-y-6" action="#">
                        <!-- Username Input -->
                        <div>
                            <label for="username" class="block mb-2 text-sm font-medium text-gray-700 dark:text-gray-300">
                                Username
                            </label>
                            <input type="text" bind:value={username}
                                   id="username"
                                   class="bg-gray-50 border border-gray-300 text-gray-700 rounded-lg focus:ring-teal-500 focus:border-teal-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-teal-500 dark:focus:border-teal-500"
                                   placeholder="john.doe" required>
                        </div>

                        <!-- Conditional Email Input -->
                        {#if isRegistering}
                            <div>
                                <label for="email" class="block mb-2 text-sm font-medium text-gray-700 dark:text-gray-300">
                                    Email
                                </label>
                                <input type="email" bind:value={email}
                                       id="email"
                                       class="bg-gray-50 border border-gray-300 text-gray-700 rounded-lg focus:ring-teal-500 focus:border-teal-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-teal-500 dark:focus:border-teal-500"
                                       placeholder="name@company.com" required>
                            </div>
                        {/if}

                        <!-- Password Input -->
                        <div>
                            <label for="password" class="block mb-2 text-sm font-medium text-gray-700 dark:text-gray-300">
                                Password
                            </label>
                            <input type="password" bind:value={password}
                                   id="password"
                                   class="bg-gray-50 border border-gray-300 text-gray-700 rounded-lg focus:ring-teal-500 focus:border-teal-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-teal-500 dark:focus:border-teal-500"
                                   placeholder="••••••••" required>
                        </div>

                        <!-- Toggle Button -->
                        <p class="text-sm font-light text-gray-600 dark:text-gray-400">
                            <button type="button" on:click={() => isRegistering = !isRegistering}
                                    class="hover:text-teal-600 dark:hover:text-teal-400 transition-colors">
                                {isRegistering ? 'Already have an account? Login' : "Don't have an account? Register"}
                            </button>
                        </p>

                        <!-- Submit Button -->
                        <button on:click={handleSubmit}
                                class="w-full bg-teal-900 hover:bg-teal-700 text-white font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-teal-500 dark:hover:bg-teal-600 transition-colors"
                                disabled={!username || !password || (isRegistering && !email)}>
                            {isRegistering ? 'Create Account' : 'Sign In'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="block md:hidden w-full bg-gray-200 dark:bg-teal-50">
        <div class="flex flex-col items-center justify-center px-4 py-8 mx-auto h-full">
            <div class="w-full max-w-md space-y-6 p-6">
                <h2 class="text-xl font-semibold text-teal-700 dark:text-teal-200">
                    Why choose us?
                </h2>

                <div class="space-y-6">
                    <!-- Feature 1 -->
                    <div class="space-y-1">
                        <h3 class="text-lg font-medium text-teal-900 dark:text-teal-500">
                            - Intuitive and User-friendly interface
                        </h3>
                        <p class="text-teal-600 dark:text-teal-200/80 text-sm">
                            //designed with simplicity in mind
                        </p>
                    </div>

                    <!-- Feature 2 -->
                    <div class="space-y-1">
                        <h3 class="text-lg font-medium text-teal-900 dark:text-teal-500">
                            - Customizable workflows for every team
                        </h3>
                        <p class="text-teal-600 dark:text-teal-200/80 text-sm">
                            //your unique project needs
                        </p>
                    </div>

                    <!-- Feature 3 -->
                    <div class="space-y-1">
                        <h3 class="text-lg font-medium text-teal-900 dark:text-teal-500">
                            - Real-time collaboration and insights
                        </h3>
                        <p class="text-teal-600 dark:text-teal-200/80 text-sm">
                            //productivity with seamless communication
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>