import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(config => {
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
            }

            config.headers.Authorization = `Bearer ${token}`;
        } catch (e) {
            console.error('Invalid token:', e);

            localStorage.removeItem('jwtToken');
        }
    }
    return config;
}, error => {
    return Promise.reject(error);
});

api.interceptors.response.use(response => response, error => {
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
        localStorage.removeItem('jwtToken');

        const response = await api.post('/user/login', { username, password });

        localStorage.setItem('jwtToken', response.data.token);
        localStorage.setItem('userId', response.data.userId);
        localStorage.setItem('username', username);

        return response.data;
    } catch (error) {
        localStorage.removeItem('jwtToken');
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
    return localStorage.getItem('userId');
}

export function getUsername() {
    return localStorage.getItem('username');
}

export function isAuthenticated() {
    return !!getUserId();
}
// Fetch all projects for a specific user
export async function getAllProjects(userId) {
    try {
        const response = await api.get(`/${userId}/dashboard`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

// Fetch a specific project by its ID
export async function getProjectById(userId, projectId) {
    try {
        const response = await api.get(`/${userId}/dashboard/${projectId}`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

// Create a new project
export async function createProject(userId, project) {
    try {
        const response = await api.post(`/${userId}/dashboard/createProject`, project);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}


// Edit an existing project
export async function updateProject(userId, projectId, updatedProject) {
    try {
        const response = await api.put(`/${userId}/dashboard/editProject/${projectId}`, updatedProject);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

// Delete a project
export async function deleteProject(userId, projectId) {
    try {
        const response = await api.delete(`/${userId}/dashboard/deleteProject/${projectId}`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

// Fetch all tasks in a project
export async function getTasksForProject(userId, projectId) {
    try {
        const response = await api.get(`/${userId}/dashboard/${projectId}`);
        return response.data.tasks || [];
    } catch (error) {
        console.error("Logout error:", error);
        throw new Error(handleApiError(error));
    }
}

// Create a new task in a project
export async function createTask(userId, projectId, task) {
    try {
        const response = await api.post(`/${userId}/dashboard/${projectId}/task`, task);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

// Edit an existing task
export async function updateTask(userId, projectId, taskId, updatedTask) {
    try {
        const response = await api.put(`/${userId}/dashboard/${projectId}/${taskId}/edit`, updatedTask);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}

// Delete a task
export async function deleteTask(userId, projectId, taskId) {
    try {
        const response = await api.delete(`/${userId}/dashboard/${projectId}/${taskId}/delete`);
        return response.data;
    } catch (error) {
        throw new Error(handleApiError(error));
    }
}
