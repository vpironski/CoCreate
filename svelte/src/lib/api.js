import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { browser } from '$app/environment';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(config => {
    if (!browser) return config;

    const token = localStorage.getItem('jwtToken');

    if (config.url.includes('/login') || config.url.includes('/register')) {
        return config;
    }

    if (token) {
        try {
            const decoded = jwtDecode(token);
            if (decoded.exp * 1000 < Date.now()) {
                localStorage.removeItem('jwtToken');
                localStorage.removeItem('userId');
                localStorage.removeItem('username');
                if (window.location.pathname !== '/') {
                    window.location.href = '/';
                }
                return Promise.reject(new Error('Token expired'));
            }

            config.headers.Authorization = `Bearer ${token}`;
        } catch (e) {
            console.error('Invalid token:', e);
            localStorage.removeItem('jwtToken');
            return Promise.reject(e);
        }
    }
    return config;
}, error => {
    return Promise.reject(error);
});

api.interceptors.response.use(response => response, error => {
    if (!browser) return Promise.reject(error);

    if (error.response?.status === 401 || error.response?.status === 403) {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('username');
        if (window.location.pathname !== '/') {
            window.location.href = '/';
        }
    }
    return Promise.reject(error);
});

function handleApiError(error) {
    return error.response?.data?.error || error.message || 'An unexpected error occurred';
}

export async function registerUser(username, email, password) {
    try {
        const response = await api.post('/user/register', { username, email, password });
        localStorage.setItem('jwtToken', response.data.token);
        localStorage.setItem('userId', response.data.userId);
        localStorage.setItem('username', username);
        return {
            message: response.data.message,
            userId: response.data.userId,
            token: response.data.token
        };
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function loginUser(username, password) {
    try {
        if (browser) {
            localStorage.removeItem('jwtToken');
        }

        const response = await api.post('/user/login', { username, password });

        if (browser) {
            localStorage.setItem('jwtToken', response.data.token);
            localStorage.setItem('userId', response.data.userId);
            localStorage.setItem('username', username);
        }

        return response.data;
    } catch (error) {
        if (browser) {
            localStorage.removeItem('jwtToken');
        }
        throw new Error(handleApiError(error));
    }
}

export async function logoutUser() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    window.location.href = '/';
}

export function getUserId() {
    return browser ? localStorage.getItem('userId') : null;
}

export function getUsername() {
    return localStorage.getItem('username');
}

export function isAuthenticated() {
    const token = localStorage.getItem('jwtToken');
    if (!token) return false;
    try {
        const decoded = jwtDecode(token);
        return decoded.exp * 1000 > Date.now();
    } catch (e) {
        return false;
    }
}

export async function getAllProjects(userId) {
    try {
        const response = await api.get(`/${userId}/dashboard`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function getProjectById(userId, projectId) {
    try {
        const response = await api.get(`/${userId}/dashboard/${projectId}`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function getProjectCustomFields(userId) {
    try {
        const response = await api.get(`/${userId}/dashboard/create-project`);
        return response.data;

    } catch (error) {
        throw new Error(handleApiError(error));
    }
}


export async function createProject(userId, project) {
    try {
        console.log("Making API call to create project:", `/${userId}/dashboard/create-project`);
        const response = await api.post(`/${userId}/dashboard/create-project`, project);
        console.log("Project creation successful:", response.data);
        return response.data;
    } catch (error) {
        console.error("API error details:", error.response?.status, error.response?.data);
        throw new Error(handleApiError(error));
    }
}


export async function updateProject(userId, projectId, updatedProject) {
    try {
        const response = await api.put(`/${userId}/edit-project/${projectId}`, updatedProject);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function deleteProject(userId, projectId) {
    try {
        const response = await api.delete(`/${userId}/dashboard/delete-project/${projectId}`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function getTasksForProject(userId, projectId) {
    try {
        const response = await api.get(`/${userId}/${projectId}`);
        return response.data.tasks || [];
    } catch (error) {
        console.error("Logout error:", error);
        throw new Error(handleApiError(error));
    }
}

export async function createTask(userId, projectId, task) {
    try {
        const response = await api.post(`/${userId}/${projectId}/task`, task);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function updateTask(userId, projectId, taskId, updatedTask) {
    try {
        const response = await api.put(`/${userId}/${projectId}/${taskId}/edit`, updatedTask);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

export async function deleteTask(userId, projectId, taskId) {
    try {
        const response = await api.delete(`/${userId}/${projectId}/${taskId}/delete`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}
