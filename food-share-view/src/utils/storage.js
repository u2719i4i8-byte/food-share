/**
 * 本地存储工具类
 * 功能：管理用户token和用户信息的存储、获取、清除
 */

const TOKEN_KEY = "token"

/**
 * 获取用户token
 * @returns {string|null} 用户token
 */
export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * 设置用户token
 * @param {string} token - 用户token
 */
export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 清除用户token
 */
export function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

/**
 * 判断用户是否已登录
 * @returns {boolean} 是否已登录
 */
export function isLoggedIn() {
    return !!getToken();
}

/**
 * 获取用户信息
 * @returns {Object} 用户信息对象 {userName, userAvatar, userId}
 */
export function getUserInfo() {
    const userName = sessionStorage.getItem('userName');
    const userAvatar = sessionStorage.getItem('userAvatar');
    const userId = sessionStorage.getItem('userId');
    return { userName, userAvatar, userId };
}

/**
 * 设置用户信息
 * @param {string} userName - 用户名称
 * @param {string} userAvatar - 用户头像URL
 * @param {number} userId - 用户ID
 */
export function setUserInfo(userName, userAvatar, userId) {
    if (userName) sessionStorage.setItem('userName', userName);
    if (userAvatar) sessionStorage.setItem('userAvatar', userAvatar);
    if (userId) sessionStorage.setItem('userId', userId);
}

/**
 * 清除用户信息
 */
export function clearUserInfo() {
    sessionStorage.removeItem('userName');
    sessionStorage.removeItem('userAvatar');
    sessionStorage.removeItem('userId');
    sessionStorage.removeItem('role');
}

/**
 * 退出登录（清除token和用户信息）
 */
export function logout() {
    clearToken();
    clearUserInfo();
}
