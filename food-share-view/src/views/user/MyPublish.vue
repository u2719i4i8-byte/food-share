<template>
    <div class="my-publish-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">我的发布</h1>
            <div class="header-right"></div>
        </header>

        <div class="tabs-bar">
            <div 
                class="tab-item" 
                :class="{ active: currentTab === 'gourmet' }" 
                @click="switchTab('gourmet')"
            >
                <span>美食做法</span>
                <div class="tab-count">{{ gourmetCount }}</div>
            </div>
            <div 
                class="tab-item" 
                :class="{ active: currentTab === 'content' }" 
                @click="switchTab('content')"
            >
                <span>内容分享</span>
                <div class="tab-count">{{ contentCount }}</div>
            </div>
        </div>

        <div class="content-area" v-loading="loading">
            <template v-if="currentTab === 'gourmet'">
                <div class="empty-state" v-if="!loading && gourmetList.length === 0">
                    <div class="empty-icon">
                        <i class="el-icon-document"></i>
                    </div>
                    <p class="empty-text">还没有发布过美食做法</p>
                    <div class="empty-btn" @click="route('/createGourmet')">
                        去发布
                    </div>
                </div>
                <div class="publish-list" v-else>
                    <div 
                        class="publish-card" 
                        v-for="(item, index) in gourmetList" 
                        :key="index"
                        @click="viewGourmet(item)"
                    >
                        <div class="card-cover">
                            <img :src="item.cover || defaultCover" :alt="item.title">
                        </div>
                        <div class="card-content">
                            <h3 class="card-title">{{ item.title }}</h3>
                            <div class="card-meta">
                                <span class="meta-item">
                                    <i class="el-icon-view"></i>
                                    {{ item.viewCount || 0 }}
                                </span>
                                <span class="meta-item">
                                    <i class="el-icon-star-on"></i>
                                    {{ item.saveCount || 0 }}
                                </span>
                                <span class="meta-item">
                                    <i class="el-icon-chat-dot-round"></i>
                                    {{ item.commentCount || 0 }}
                                </span>
                            </div>
                            <div class="card-time">{{ formatTime(item.createTime) }}</div>
                        </div>
                        <div class="card-actions">
                            <div class="action-btn" @click.stop="editGourmet(item)">
                                <i class="el-icon-edit"></i>
                            </div>
                            <div class="action-btn delete" @click.stop="deleteGourmet(item)">
                                <i class="el-icon-delete"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </template>

            <template v-if="currentTab === 'content'">
                <div class="empty-state" v-if="!loading && contentList.length === 0">
                    <div class="empty-icon">
                        <i class="el-icon-picture"></i>
                    </div>
                    <p class="empty-text">还没有发布过内容分享</p>
                    <div class="empty-btn" @click="route('/createContent')">
                        去发布
                    </div>
                </div>
                <div class="content-grid" v-else>
                    <div 
                        class="content-card" 
                        v-for="(item, index) in contentList" 
                        :key="index"
                        @click="viewContent(item)"
                    >
                        <div class="content-cover">
                            <img :src="item.cover || defaultCover" :alt="item.title">
                        </div>
                        <div class="content-info">
                            <p class="content-title">{{ item.title || '内容分享' }}</p>
                            <div class="content-stats">
                                <span><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </div>

        <div class="fab-btn" @click="route('/createGourmet')">
            <i class="el-icon-plus"></i>
        </div>
    </div>
</template>

<script>
import { isLoggedIn } from '@/utils/storage.js';

const DEFAULT_COVER = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyMDAgMTUwIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjE1MCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjEwMCIgeT0iNzUiIGZpbGw9IiNjY2MiIGZvbnQtZmFtaWx5PSJzYW5zLXNlcmlmIiBmb250LXNpemU9IjE0IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7ml6Dlm77niYc8L3RleHQ+PC9zdmc+';

export default {
    name: "MyPublish",
    data() {
        return {
            currentTab: 'gourmet',
            loading: false,
            gourmetList: [],
            contentList: [],
            gourmetCount: 0,
            contentCount: 0,
            defaultCover: DEFAULT_COVER
        }
    },
    computed: {
        isLogin() {
            return isLoggedIn();
        }
    },
    mounted() {
        if (!this.isLogin) {
            this.$router.push('/login');
            return;
        }
        this.fetchGourmetList();
        this.fetchContentList();
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
        switchTab(tab) {
            this.currentTab = tab;
        },
        async fetchGourmetList() {
            this.loading = true;
            try {
                const res = await this.$axios.get('/gourmet/queryUser');
                const { data } = res;
                if (data.code === 200 && data.data) {
                    this.gourmetList = data.data;
                    this.gourmetCount = data.data.length;
                }
            } catch (error) {
                console.log('获取美食列表失败', error);
            } finally {
                this.loading = false;
            }
        },
        async fetchContentList() {
            try {
                const res = await this.$axios.get('/content-net/queryUser');
                const { data } = res;
                if (data.code === 200 && data.data) {
                    this.contentList = data.data;
                    this.contentCount = data.data.length;
                }
            } catch (error) {
                console.log('获取内容分享列表失败', error);
            }
        },
        viewGourmet(item) {
            sessionStorage.setItem('gourmetId', item.id);
            this.$router.push('/gourmetDetail');
        },
        viewContent(item) {
            sessionStorage.setItem('contentNetId', item.id);
            this.$router.push('/shareDetail');
        },
        editGourmet(item) {
            sessionStorage.setItem('gourmetId', item.id);
            this.$router.push('/editGourmet');
        },
        deleteGourmet(item) {
            this.$confirm('确定要删除这篇美食做法吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.doDeleteGourmet(item.id);
            }).catch(() => {});
        },
        async doDeleteGourmet(id) {
            try {
                const res = await this.$axios.post('/gourmet/batchDelete', [id]);
                const { data } = res;
                if (data.code === 200) {
                    this.$message.success('删除成功');
                    this.fetchGourmetList();
                } else {
                    this.$message.error(data.msg || '删除失败');
                }
            } catch (error) {
                console.log('删除失败', error);
                this.$message.error('删除失败');
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
$primary-gradient: linear-gradient(135deg, #FF6B35 0%, #FF8F5A 100%);
$text-primary: #1a1a2e;
$text-secondary: #6b7280;
$text-light: #9ca3af;
$bg-primary: #fafafa;
$bg-card: #ffffff;
$border-color: #f0f0f0;

.my-publish-page {
    min-height: 100vh;
    background: $bg-primary;
    padding-bottom: 100px;
}

.page-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 56px;
    background: $bg-card;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    z-index: 100;
    box-shadow: 0 1px 0 $border-color;
    
    .header-left, .header-right {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        
        i {
            font-size: 22px;
            color: $text-primary;
        }
    }
    
    .header-title {
        font-size: 17px;
        font-weight: 600;
        color: $text-primary;
        margin: 0;
    }
}

.tabs-bar {
    position: fixed;
    top: 56px;
    left: 0;
    right: 0;
    height: 50px;
    background: $bg-card;
    display: flex;
    padding: 0 16px;
    z-index: 99;
    box-shadow: 0 1px 0 $border-color;
    
    .tab-item {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        cursor: pointer;
        position: relative;
        transition: all 0.3s ease;
        
        span {
            font-size: 15px;
            color: $text-secondary;
            transition: color 0.3s ease;
        }
        
        .tab-count {
            min-width: 20px;
            height: 20px;
            background: $border-color;
            border-radius: 10px;
            font-size: 12px;
            color: $text-secondary;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 0 6px;
            transition: all 0.3s ease;
        }
        
        &.active {
            span {
                color: $primary-color;
                font-weight: 600;
            }
            
            .tab-count {
                background: $primary-gradient;
                color: #fff;
            }
            
            &::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 50%;
                transform: translateX(-50%);
                width: 24px;
                height: 3px;
                background: $primary-gradient;
                border-radius: 2px;
            }
        }
    }
}

.content-area {
    margin-top: 106px;
    padding: 16px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    
    .empty-icon {
        width: 80px;
        height: 80px;
        background: $border-color;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 16px;
        
        i {
            font-size: 36px;
            color: $text-light;
        }
    }
    
    .empty-text {
        font-size: 15px;
        color: $text-secondary;
        margin: 0 0 20px;
    }
    
    .empty-btn {
        padding: 10px 24px;
        background: $primary-gradient;
        border-radius: 20px;
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
        }
    }
}

.publish-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.publish-card {
    display: flex;
    background: $bg-card;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    }
    
    .card-cover {
        width: 100px;
        height: 100px;
        flex-shrink: 0;
        
        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
    }
    
    .card-content {
        flex: 1;
        padding: 12px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        
        .card-title {
            font-size: 15px;
            font-weight: 600;
            color: $text-primary;
            margin: 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .card-meta {
            display: flex;
            gap: 12px;
            margin-top: 8px;
            
            .meta-item {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                color: $text-light;
                
                i {
                    font-size: 14px;
                }
            }
        }
        
        .card-time {
            font-size: 11px;
            color: $text-light;
            margin-top: 4px;
        }
    }
    
    .card-actions {
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 8px;
        padding: 12px;
        
        .action-btn {
            width: 32px;
            height: 32px;
            background: $bg-primary;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            
            i {
                font-size: 16px;
                color: $text-secondary;
            }
            
            &:hover {
                background: rgba(255, 107, 53, 0.1);
                
                i {
                    color: $primary-color;
                }
            }
            
            &.delete:hover {
                background: rgba(239, 68, 68, 0.1);
                
                i {
                    color: #ef4444;
                }
            }
        }
    }
}

.content-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
}

.content-card {
    background: $bg-card;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    }
    
    .content-cover {
        aspect-ratio: 1;
        
        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
    }
    
    .content-info {
        padding: 12px;
        
        .content-title {
            font-size: 14px;
            font-weight: 500;
            color: $text-primary;
            margin: 0 0 6px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .content-stats {
            font-size: 12px;
            color: $text-light;
            
            i {
                font-size: 14px;
                margin-right: 4px;
            }
        }
    }
}

.fab-btn {
    position: fixed;
    bottom: 100px;
    right: 20px;
    width: 56px;
    height: 56px;
    background: $primary-gradient;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 16px rgba(255, 107, 53, 0.4);
    cursor: pointer;
    transition: all 0.3s ease;
    z-index: 50;
    
    i {
        font-size: 24px;
        color: #fff;
    }
    
    &:hover {
        transform: scale(1.1);
        box-shadow: 0 6px 20px rgba(255, 107, 53, 0.5);
    }
}
</style>
