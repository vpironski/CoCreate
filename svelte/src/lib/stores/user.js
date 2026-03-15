import { writable } from 'svelte/store';
import { browser } from '$app/environment';

function createUserStore() {
    const { subscribe, set } = writable({
        userId: browser ? localStorage.getItem('userId') || '' : '',
        username: browser ? localStorage.getItem('username') || '' : '',
        role: browser ? localStorage.getItem('role') || '' : ''
    });

    return {
        subscribe,
        setUser: (userData) => {
            if (browser) {
                localStorage.setItem('userId', userData.userId);
                localStorage.setItem('username', userData.username);
                localStorage.setItem('role', userData.role);
            }
            set(userData);
        },
        clearUser: () => {
            if (browser) {
                localStorage.removeItem('userId');
                localStorage.removeItem('username');
                localStorage.removeItem('role');
            }
            set({ userId: '', username: '', role: '' });
        }
    };
}

export const userStore = createUserStore();