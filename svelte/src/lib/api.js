import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
    withCredentials: true, // Ensures cookies (JWT) are sent with requests
    headers: {
        'Content-Type': 'application/json'
    }
});

function handleApiError(error) {
    return error.response?.data?.error || 'An unexpected error occurred';
}


export async function registerUser(username, email, password) {
    try {
        const response = await api.post('/user/register', { username, email, password });
        localStorage.setItem('userId', response.data.userId);
        return { message: response.data.message, userId: response.data.userId };
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function loginUser(username, password) {
    try {
        const response = await api.post('/user/login', { username, password });
        localStorage.setItem('userId', response.data.userId);
        return { message: response.data.message, userId: response.data.userId };
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function logoutUser() {
    try {
        await api.post('/user/logout');
        localStorage.removeItem('userId');
    } catch (error) {
        console.error("Logout API error:", error.response ? error.response.data : error.message);
        throw new Error(handleApiError(error));
    }
}

export function getUserId() {
    return localStorage.getItem('userId');
}

export function isAuthenticated() {
    return !!getUserId();
}
