/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./src/**/*.{html,js,svelte,ts}",
        "./src/**/**/*.{html,js,svelte,ts}"
    ],
    theme: {
        extend: {
            colors: {
                teal: {
                    900: '#134e4a',
                },
                gray: {
                    200: '#e5e7eb',
                }
            },
            fontFamily: {
                'sans': ['Readex Pro', 'sans-serif']
            }
        },
    },
    plugins: [],
    darkMode: 'class'
}