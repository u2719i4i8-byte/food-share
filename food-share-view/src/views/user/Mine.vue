<template>
    <div class="mine-page">
        <div class="profile-header">
            <div class="header-bg">
                <div class="bg-pattern"></div>
            </div>
            
            <div class="header-nav">
                <div class="nav-btn back" @click="goBack">
                    <i class="el-icon-arrow-left"></i>
                </div>
                <div class="nav-actions">
                    <div class="nav-btn edit-btn" @click="route('/setting')">
                        <span>编辑资料</span>
                    </div>
                </div>
            </div>
            
            <div class="user-profile">
                <div class="avatar-section">
                    <div class="avatar-ring">
                        <img :src="displayAvatar" class="avatar-img">
                        <div class="avatar-glow"></div>
                    </div>
                    <div class="online-badge"></div>
                </div>
                
                <h1 class="user-name">{{ displayName }}</h1>
                <p class="user-bio">{{ userBio }}</p>
            </div>
        </div>

        <div class="quick-menu">
            <div class="menu-grid">
                <div class="menu-card" @click="route('/dietHistory')">
                    <div class="card-icon diet">
                        <i class="el-icon-food"></i>
                    </div>
                    <div class="card-content">
                        <span class="card-title">饮食记录</span>
                        <span class="card-desc">追踪每日饮食</span>
                    </div>
                    <i class="el-icon-arrow-right card-arrow"></i>
                </div>
                
                <div class="menu-card" @click="route('/nutriment')">
                    <div class="card-icon nutrition">
                        <i class="el-icon-data-analysis"></i>
                    </div>
                    <div class="card-content">
                        <span class="card-title">营养分析</span>
                        <span class="card-desc">智能营养报告</span>
                    </div>
                    <i class="el-icon-arrow-right card-arrow"></i>
                </div>
                
                <div class="menu-card" @click="route('/cookbook')">
                    <div class="card-icon cookbook">
                        <i class="el-icon-notebook-2"></i>
                    </div>
                    <div class="card-content">
                        <span class="card-title">我的食谱</span>
                        <span class="card-desc">专属美食清单</span>
                    </div>
                    <i class="el-icon-arrow-right card-arrow"></i>
                </div>
                
                <div class="menu-card" @click="route('/draft')">
                    <div class="card-icon draft">
                        <i class="el-icon-edit-outline"></i>
                    </div>
                    <div class="card-content">
                        <span class="card-title">草稿箱</span>
                        <span class="card-desc">未完成的内容</span>
                    </div>
                    <i class="el-icon-arrow-right card-arrow"></i>
                </div>
            </div>
        </div>

        <div class="bottom-section">
            <div class="logout-btn" @click="handleLogout">
                <i class="el-icon-switch-button"></i>
                <span>退出登录</span>
            </div>
        </div>
    </div>
</template>

<script>
/**
 * 个人中心页面
 * 功能：用户信息展示、发布管理、收藏管理、点赞记录、编辑资料
 */
import { isLoggedIn, getUserInfo, logout } from '@/utils/storage.js';

// 默认头像（SVG格式）
const DEFAULT_AVATAR = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+';
// 轮询间隔（毫秒）
const POLL_INTERVAL = 30000;

export default {
    name: "Mine",
    data() {
        return {
            // 用户信息对象
            userInfo: {
                userName: '',       // 用户名称
                userAvatar: '',     // 用户头像
                publishCount: 0,    // 发布数量
                upvoteCount: 0,     // 点赞数量
                saveCount: 0,       // 收藏数量
                viewCount: 0        // 浏览数量
            },
            loading: false,         // 加载状态
            pollTimer: null,        // 轮询定时器
            defaultAvatar: DEFAULT_AVATAR
        }
    },
    computed: {
        // 判断用户是否已登录
        isLogin() {
            return isLoggedIn();
        },
        // 显示用户头像
        displayAvatar() {
            if (this.userInfo.userAvatar) {
                return this.userInfo.userAvatar;
            }
            const { userAvatar } = getUserInfo();
            return userAvatar || DEFAULT_AVATAR;
        },
        // 显示用户名称
        displayName() {
            if (this.userInfo.userName) {
                return this.userInfo.userName;
            }
            const { userName } = getUserInfo();
            return userName || '美食爱好者';
        },
        // 用户简介
        userBio() {
            return '分享美食，记录生活';
        }
    },
    mounted() {
        this.loadCachedInfo();
        this.fetchUserCenter();
        this.startPolling();
        document.addEventListener('visibilitychange', this.handleVisibilityChange);
    },
    beforeDestroy() {
        this.stopPolling();
        document.removeEventListener('visibilitychange', this.handleVisibilityChange);
    },
    methods: {
        // 返回上一页
        goBack() {
            this.$router.back();
        },
        // 从缓存加载用户信息
        loadCachedInfo() {
            const { userName, userAvatar } = getUserInfo();
            if (userName) this.userInfo.userName = userName;
            if (userAvatar) this.userInfo.userAvatar = userAvatar;
        },
        // 页面可见性变化处理
        handleVisibilityChange() {
            if (!document.hidden && this.isLogin) {
                this.fetchUserCenter();
            }
        },
        // 启动轮询
        startPolling() {
            this.pollTimer = setInterval(() => {
                if (!document.hidden && this.isLogin) {
                    this.fetchUserCenter();
                }
            }, POLL_INTERVAL);
        },
        // 停止轮询
        stopPolling() {
            if (this.pollTimer) {
                clearInterval(this.pollTimer);
                this.pollTimer = null;
            }
        },
        // 路由跳转
        route(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        // 获取用户中心数据
        async fetchUserCenter() {
            if (!this.isLogin) {
                this.$router.push('/login');
                return;
            }
            
            try {
                const res = await this.$axios.get('/user/center');
                const { data } = res;
                console.log('用户中心数据:', data);
                if (data.code === 200 && data.data) {
                    console.log('用户头像URL:', data.data.userAvatar);
                    this.userInfo = {
                        userName: data.data.userName || '',
                        userAvatar: data.data.userAvatar || '',
                        publishCount: data.data.publishCount || 0,
                        upvoteCount: data.data.upvoteCount || 0,
                        saveCount: data.data.saveCount || 0,
                        viewCount: data.data.viewCount || 0
                    };
                }
            } catch (error) {
                console.log('获取用户中心信息失败', error);
            }
        },
        // 退出登录
        handleLogout() {
            this.$confirm('确定要退出登录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                logout();
                this.$router.push('/login');
            }).catch(() => {});
        }
    }
};
</script>

<style scoped lang="scss">
$primary-color: #FF6B35;
$primary-light: #FF8F5A;
$primary-gradient: linear-gradient(135deg, #FF6B35 0%, #FF8F5A 100%);
$primary-soft: linear-gradient(135deg, rgba(255, 107, 53, 0.1) 0%, rgba(255, 143, 90, 0.1) 100%);
$text-primary: #1a1a2e;
$text-secondary: #6b7280;
$text-light: #9ca3af;
$bg-primary: #f8f9fa;
$bg-card: #ffffff;
$border-color: #f0f0f0;
$shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.04);
$shadow-md: 0 4px 16px rgba(0, 0, 0, 0.08);
$shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.12);

.mine-page {
    min-height: 100vh;
    background: $bg-primary;
    padding-bottom: 120px;
}

.profile-header {
    position: relative;
    padding-bottom: 20px;
    
    .header-bg {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 280px;
        background: $primary-gradient;
        border-radius: 0 0 40px 40px;
        overflow: hidden;
        
        .bg-pattern {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image: 
                radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
                radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.15) 0%, transparent 40%),
                radial-gradient(circle at 40% 40%, rgba(255, 255, 255, 0.08) 0%, transparent 30%);
        }
    }
    
    .header-nav {
        position: relative;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        z-index: 10;
        
        .nav-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            i {
                font-size: 20px;
                color: #fff;
            }
            
            &:hover {
                background: rgba(255, 255, 255, 0.3);
                transform: scale(1.05);
            }
            
            &.edit-btn {
                width: auto;
                padding: 8px 16px;
                
                span {
                    font-size: 14px;
                    color: #fff;
                    font-weight: 500;
                }
            }
        }
        
        .nav-actions {
            display: flex;
            gap: 12px;
        }
    }
    
    .user-profile {
        position: relative;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20px 20px 24px;
        z-index: 10;
        
        .avatar-section {
            position: relative;
            margin-bottom: 16px;
            
            .avatar-ring {
                position: relative;
                width: 90px;
                height: 90px;
                padding: 3px;
                background: rgba(255, 255, 255, 0.3);
                border-radius: 50%;
                
                .avatar-img {
                    width: 100%;
                    height: 100%;
                    border-radius: 50%;
                    object-fit: cover;
                    background: #f0f0f0;
                }
                
                .avatar-glow {
                    position: absolute;
                    top: -5px;
                    left: -5px;
                    right: -5px;
                    bottom: -5px;
                    border: 2px solid rgba(255, 255, 255, 0.4);
                    border-radius: 50%;
                    animation: pulse 2s ease-in-out infinite;
                }
            }
            
            .online-badge {
                position: absolute;
                bottom: 5px;
                right: 5px;
                width: 14px;
                height: 14px;
                background: #52c41a;
                border: 3px solid #fff;
                border-radius: 50%;
            }
        }
        
        .user-name {
            font-size: 22px;
            font-weight: 700;
            color: #fff;
            margin: 0 0 6px;
            text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .user-bio {
            font-size: 14px;
            color: rgba(255, 255, 255, 0.85);
            margin: 0;
        }
    }
}

.quick-menu {
    padding: 20px 16px 0;
    
    .menu-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
    }
    
    .menu-card {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: $bg-card;
        border-radius: 16px;
        box-shadow: $shadow-sm;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
            transform: translateY(-2px);
            box-shadow: $shadow-md;
            
            .card-icon {
                transform: scale(1.1);
            }
            
            .card-arrow {
                transform: translateX(4px);
            }
        }
        
        .card-icon {
            width: 44px;
            height: 44px;
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.3s ease;
            
            i {
                font-size: 22px;
                color: #fff;
            }
            
            &.diet {
                background: linear-gradient(135deg, #FF6B35 0%, #FF8F5A 100%);
            }
            
            &.nutrition {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            }
            
            &.cookbook {
                background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            }
            
            &.draft {
                background: linear-gradient(135deg, #fc6076 0%, #ff9a44 100%);
            }
        }
        
        .card-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            
            .card-title {
                font-size: 15px;
                font-weight: 600;
                color: $text-primary;
            }
            
            .card-desc {
                font-size: 12px;
                color: $text-light;
                margin-top: 2px;
            }
        }
        
        .card-arrow {
            font-size: 14px;
            color: $text-light;
            transition: transform 0.3s ease;
        }
    }
}

.bottom-section {
    position: fixed;
    bottom: 70px;
    left: 16px;
    right: 16px;
    z-index: 50;
    
    .logout-btn {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 14px;
        background: $bg-card;
        border-radius: 16px;
        box-shadow: $shadow-md;
        cursor: pointer;
        transition: all 0.3s ease;
        
        i {
            font-size: 18px;
            color: #ff4d4f;
        }
        
        span {
            font-size: 14px;
            font-weight: 500;
            color: #ff4d4f;
        }
        
        &:hover {
            transform: translateY(-2px);
            box-shadow: $shadow-lg;
            background: rgba(255, 77, 79, 0.05);
        }
    }
}

@keyframes pulse {
    0%, 100% {
        transform: scale(1);
        opacity: 0.4;
    }
    50% {
        transform: scale(1.05);
        opacity: 0.2;
    }
}
</style>
