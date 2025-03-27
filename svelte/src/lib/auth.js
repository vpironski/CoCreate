// src/lib/auth.js
export function isAuthenticated() {
    return !!localStorage.getItem('jwtToken');
}

export function getAuthHeader() {
    const token = localStorage.getItem('jwtToken');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

export function clearAuth() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
}