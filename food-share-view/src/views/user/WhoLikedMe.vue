<template>
    <div class="likes-page">
        <div class="page-header">
            <div class="header-bg"></div>
            <div class="header-content">
                <div class="nav-bar">
                    <div class="back-btn" @click="goBack">
                        <i class="el-icon-arrow-left"></i>
                    </div>
                    <h1 class="page-title">收到的赞</h1>
                    <div class="placeholder"></div>
                </div>
                <div class="stats-summary">
                    <div class="stats-icon">
                        <i class="el-icon-star-on"></i>
                    </div>
                    <div class="stats-info">
                        <span class="stats-count">{{ totalCount }}</span>
                        <span class="stats-label">人赞过你</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="likes-content" v-loading="loading">
            <div class="empty-state" v-if="!loading && likeList.length === 0">
                <div class="empty-visual">
                    <div class="empty-circle">
                        <i class="el-icon-star-on"></i>
                    </div>
                    <div class="empty-dots">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
                <h3 class="empty-title">还没有收到赞</h3>
                <p class="empty-desc">发布更多内容，让更多人看到你的作品</p>
                <div class="empty-action" @click="route('/createGourmet')">
                    <i class="el-icon-plus"></i>
                    <span>发布内容</span>
                </div>
            </div>

            <div class="likes-list" v-else>
                <div 
                    class="like-card" 
                    v-for="(item, index) in likeList" 
                    :key="index"
                    @click="viewContent(item)"
                >
                    <div class="user-section">
                        <div class="user-avatar">
                            <img :src="item.userAvatar || defaultAvatar" :alt="item.userName">
                            <div class="avatar-ring"></div>
                        </div>
                        <div class="user-info">
                            <span class="user-name">{{ item.userName || '美食爱好者' }}</span>
                            <span class="like-time">{{ formatTime(item.createTime) }}</span>
                        </div>
                    </div>
                    
                    <div class="like-indicator">
                        <div class="like-icon">
                            <i class="el-icon-star-on"></i>
                        </div>
                    </div>

                    <div class="content-preview">
                        <div class="content-cover" v-if="item.contentCover">
                            <img :src="item.contentCover" :alt="item.contentTitle">
                        </div>
                        <div class="content-info">
                            <span class="content-type">{{ item.contentType === 'gourmet' ? '美食做法' : '内容分享' }}</span>
                            <h4 class="content-title">{{ item.contentTitle || '未知内容' }}</h4>
                        </div>
                    </div>
                </div>

                <div class="load-more" v-if="hasMore" @click="loadMore">
                    <span v-if="!loadingMore">加载更多</span>
                    <span v-else>加载中...</span>
                </div>
                
                <div class="no-more" v-if="!hasMore && likeList.length > 0">
                    <span>没有更多了</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { isLoggedIn } from '@/utils/storage.js';

const DEFAULT_AVATAR = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+';

export default {
    name: "WhoLikedMe",
    data() {
        return {
            likeList: [],
            loading: false,
            loadingMore: false,
            totalCount: 0,
            currentPage: 1,
            pageSize: 10,
            defaultAvatar: DEFAULT_AVATAR
        }
    },
    computed: {
        isLogin() {
            return isLoggedIn();
        },
        hasMore() {
            return this.likeList.length < this.totalCount;
        }
    },
    mounted() {
        if (!this.isLogin) {
            this.$router.push('/login');
            return;
        }
        this.fetchLikes();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        route(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        async fetchLikes() {
            this.loading = true;
            try {
                const params = {
                    current: (this.currentPage - 1) * this.pageSize,
                    size: this.pageSize
                };
                const res = await this.$axios.post('/interaction/queryWhoLikedMe', params);
                const { data } = res;
                if (data.code === 200) {
                    this.likeList = data.data || [];
                    this.totalCount = data.count || 0;
                }
            } catch (error) {
                console.log('获取点赞列表失败', error);
            } finally {
                this.loading = false;
            }
        },
        async loadMore() {
            if (this.loadingMore) return;
            this.loadingMore = true;
            this.currentPage++;
            try {
                const params = {
                    current: (this.currentPage - 1) * this.pageSize,
                    size: this.pageSize
                };
                const res = await this.$axios.post('/interaction/queryWhoLikedMe', params);
                const { data } = res;
                if (data.code === 200) {
                    this.likeList = [...this.likeList, ...(data.data || [])];
                }
            } catch (error) {
                console.log('加载更多失败', error);
                this.currentPage--;
            } finally {
                this.loadingMore = false;
            }
        },
        viewContent(item) {
            if (item.contentType === 'gourmet') {
                sessionStorage.setItem('gourmetId', item.contentId);
                this.$router.push('/gourmetDetail');
            } else if (item.contentType === 'content_net') {
                sessionStorage.setItem('shareId', item.contentId);
                this.$router.push('/shareDetail');
            }
        },
        formatTime(time) {
            if (!time) return '';
            const date = new Date(time);
            const now = new Date();
            const diff = now - date;
            const days = Math.floor(diff / (1000 * 60 * 60 * 24));
            
            if (days === 0) {
                const hours = Math.floor(diff / (1000 * 60 * 60));
                if (hours === 0) {
                    const minutes = Math.floor(diff / (1000 * 60));
                    return minutes <= 1 ? '刚刚' : minutes + '分钟前';
                }
                return hours + '小时前';
            } else if (days === 1) {
                return '昨天';
            } else if (days < 7) {
                return days + '天前';
            } else {
                return date.toLocaleDateString();
            }
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

.likes-page {
    min-height: 100vh;
    background: $bg-primary;
    padding-bottom: 80px;
}

.page-header {
    position: relative;
    padding-bottom: 24px;
    
    .header-bg {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 200px;
        background: $primary-gradient;
        border-radius: 0 0 32px 32px;
        overflow: hidden;
        
        &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image: 
                radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
                radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 40%);
        }
    }
    
    .header-content {
        position: relative;
        z-index: 10;
        
        .nav-bar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 16px 20px;
            
            .back-btn {
                width: 40px;
                height: 40px;
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
            }
            
            .page-title {
                font-size: 18px;
                font-weight: 600;
                color: #fff;
                margin: 0;
            }
            
            .placeholder {
                width: 40px;
            }
        }
        
        .stats-summary {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 16px;
            padding: 24px 20px;
            
            .stats-icon {
                width: 56px;
                height: 56px;
                background: rgba(255, 255, 255, 0.2);
                backdrop-filter: blur(10px);
                border-radius: 16px;
                display: flex;
                align-items: center;
                justify-content: center;
                
                i {
                    font-size: 28px;
                    color: #fff;
                }
            }
            
            .stats-info {
                display: flex;
                flex-direction: column;
                
                .stats-count {
                    font-size: 32px;
                    font-weight: 700;
                    color: #fff;
                    line-height: 1.2;
                }
                
                .stats-label {
                    font-size: 14px;
                    color: rgba(255, 255, 255, 0.85);
                }
            }
        }
    }
}

.likes-content {
    padding: 0 16px;
    
    .empty-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 60px 20px;
        
        .empty-visual {
            position: relative;
            margin-bottom: 24px;
            
            .empty-circle {
                width: 100px;
                height: 100px;
                background: $primary-soft;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                
                i {
                    font-size: 48px;
                    color: $primary-color;
                    opacity: 0.6;
                }
            }
            
            .empty-dots {
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                width: 140px;
                height: 140px;
                
                span {
                    position: absolute;
                    width: 8px;
                    height: 8px;
                    background: $primary-color;
                    border-radius: 50%;
                    opacity: 0.3;
                    animation: float 3s ease-in-out infinite;
                    
                    &:nth-child(1) {
                        top: 0;
                        left: 50%;
                        transform: translateX(-50%);
                        animation-delay: 0s;
                    }
                    
                    &:nth-child(2) {
                        bottom: 20%;
                        right: 0;
                        animation-delay: 0.5s;
                    }
                    
                    &:nth-child(3) {
                        bottom: 20%;
                        left: 0;
                        animation-delay: 1s;
                    }
                }
            }
        }
        
        .empty-title {
            font-size: 18px;
            font-weight: 600;
            color: $text-primary;
            margin: 0 0 8px;
        }
        
        .empty-desc {
            font-size: 14px;
            color: $text-light;
            margin: 0 0 24px;
        }
        
        .empty-action {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 12px 24px;
            background: $primary-gradient;
            border-radius: 24px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            i {
                font-size: 18px;
                color: #fff;
            }
            
            span {
                font-size: 14px;
                font-weight: 500;
                color: #fff;
            }
            
            &:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 24px rgba(255, 107, 53, 0.3);
            }
        }
    }
    
    .likes-list {
        .like-card {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 16px;
            background: $bg-card;
            border-radius: 16px;
            box-shadow: $shadow-sm;
            margin-bottom: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            &:hover {
                transform: translateY(-2px);
                box-shadow: $shadow-md;
            }
            
            .user-section {
                display: flex;
                align-items: center;
                gap: 12px;
                flex: 1;
                min-width: 0;
                
                .user-avatar {
                    position: relative;
                    width: 48px;
                    height: 48px;
                    flex-shrink: 0;
                    
                    img {
                        width: 100%;
                        height: 100%;
                        border-radius: 50%;
                        object-fit: cover;
                        background: #f0f0f0;
                    }
                    
                    .avatar-ring {
                        position: absolute;
                        top: -2px;
                        left: -2px;
                        right: -2px;
                        bottom: -2px;
                        border: 2px solid $primary-color;
                        border-radius: 50%;
                        opacity: 0.3;
                    }
                }
                
                .user-info {
                    display: flex;
                    flex-direction: column;
                    min-width: 0;
                    
                    .user-name {
                        font-size: 15px;
                        font-weight: 600;
                        color: $text-primary;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                    }
                    
                    .like-time {
                        font-size: 12px;
                        color: $text-light;
                        margin-top: 2px;
                    }
                }
            }
            
            .like-indicator {
                flex-shrink: 0;
                
                .like-icon {
                    width: 36px;
                    height: 36px;
                    background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    
                    i {
                        font-size: 18px;
                        color: #fff;
                    }
                }
            }
            
            .content-preview {
                display: flex;
                align-items: center;
                gap: 12px;
                flex-shrink: 0;
                max-width: 140px;
                
                .content-cover {
                    width: 48px;
                    height: 48px;
                    border-radius: 10px;
                    overflow: hidden;
                    flex-shrink: 0;
                    
                    img {
                        width: 100%;
                        height: 100%;
                        object-fit: cover;
                        background: #f0f0f0;
                    }
                }
                
                .content-info {
                    display: flex;
                    flex-direction: column;
                    min-width: 0;
                    
                    .content-type {
                        font-size: 10px;
                        color: $primary-color;
                        background: $primary-soft;
                        padding: 2px 6px;
                        border-radius: 4px;
                        width: fit-content;
                        margin-bottom: 4px;
                    }
                    
                    .content-title {
                        font-size: 13px;
                        font-weight: 500;
                        color: $text-secondary;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        margin: 0;
                        max-width: 80px;
                    }
                }
            }
        }
        
        .load-more {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 16px;
            background: $bg-card;
            border-radius: 16px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            span {
                font-size: 14px;
                color: $primary-color;
                font-weight: 500;
            }
            
            &:hover {
                background: $primary-soft;
            }
        }
        
        .no-more {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            
            span {
                font-size: 13px;
                color: $text-light;
            }
        }
    }
}

@keyframes float {
    0%, 100% {
        transform: translateY(0);
        opacity: 0.3;
    }
    50% {
        transform: translateY(-10px);
        opacity: 0.6;
    }
}
</style>
