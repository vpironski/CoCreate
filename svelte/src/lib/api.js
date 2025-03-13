import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

function isBrowser() {
    return typeof window !== 'undefined';
}

export async function registerUser(username, email, password) {
    try {
        const response = await axios.post(`${API_URL}/user/register`, { username, email, password });
        const { token, userId } = response.data;

        if (isBrowser()) {
            localStorage.setItem('jwtToken', token);
            localStorage.setItem('userId', userId);
        }

        return { token, userId };
    } catch (error) {
        console.error('Error registering user:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Registration failed');
    }
}

export async function loginUser(username, password) {
    try {
        const response = await axios.post(`${API_URL}/user/login`, { username, password });
        const { token, userId } = response.data;

        if (isBrowser()) {
            localStorage.setItem('jwtToken', token);
            localStorage.setItem('userId', userId);
        }

        return { token, userId };
    } catch (error) {
        console.error('Error logging in:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Login failed');
    }
}

export function logoutUser() {
    if (isBrowser()) {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userId');
    }
}

export function getToken() {
    return isBrowser() ? localStorage.getItem('jwtToken') : null;
}

export function getUserId() {
    return isBrowser() ? localStorage.getItem('userId') : null;
}

export function isAuthenticated() {
    return !!getToken();
}
