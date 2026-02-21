<template>
    <div class="message-page">
        <header class="top-nav">
            <div class="nav-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <div class="nav-title">消息</div>
            <div class="nav-right"></div>
        </header>

        <div class="message-content">
            <div class="message-tabs">
                <div 
                    class="tab-item" 
                    :class="{ active: currentTab === 'comment' }"
                    @click="switchTab('comment')"
                >
                    <i class="el-icon-chat-dot-round"></i>
                    <span>评论</span>
                    <div class="badge" v-if="commentCount > 0">{{ commentCount }}</div>
                </div>
                <div 
                    class="tab-item" 
                    :class="{ active: currentTab === 'like' }"
                    @click="switchTab('like')"
                >
                    <i class="el-icon-star-on"></i>
                    <span>点赞</span>
                    <div class="badge" v-if="likeCount > 0">{{ likeCount }}</div>
                </div>
                <div 
                    class="tab-item" 
                    :class="{ active: currentTab === 'follow' }"
                    @click="switchTab('follow')"
                >
                    <i class="el-icon-user"></i>
                    <span>关注</span>
                    <div class="badge" v-if="followCount > 0">{{ followCount }}</div>
                </div>
                <div 
                    class="tab-item" 
                    :class="{ active: currentTab === 'system' }"
                    @click="switchTab('system')"
                >
                    <i class="el-icon-bell"></i>
                    <span>系统</span>
                    <div class="badge" v-if="systemCount > 0">{{ systemCount }}</div>
                </div>
            </div>

            <div class="message-list">
                <div class="message-item" v-for="(msg, index) in messageList" :key="index" @click="readMessage(msg)">
                    <img :src="msg.avatar" class="msg-avatar">
                    <div class="msg-content">
                        <div class="msg-header">
                            <span class="msg-name">{{ msg.name }}</span>
                            <span class="msg-time">{{ msg.time }}</span>
                        </div>
                        <div class="msg-text">{{ msg.text }}</div>
                        <div class="msg-preview" v-if="msg.preview">
                            <img :src="msg.preview" class="preview-img">
                        </div>
                    </div>
                </div>

                <div class="empty-state" v-if="messageList.length === 0">
                    <i class="el-icon-chat-dot-round"></i>
                    <p>暂无消息</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Message",
    data() {
        return {
            currentTab: 'comment',
            commentCount: 0,
            likeCount: 0,
            followCount: 0,
            systemCount: 0,
            messageList: [],
            defaultAvatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+',
            defaultCover: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MDAgMzAwIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2ZmZjVmMCIvPjxjaXJjbGUgY3g9IjIwMCIgY3k9IjE1MCIgcj0iNjAiIGZpbGw9IiNmZjZiMzUiIG9wYWNpdHk9IjAuMyIvPjx0ZXh0IHg9IjIwMCIgeT0iMTUwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LXNpemU9IjI0IiBmaWxsPSIjZmY2YjM1IiBvcGFjaXR5PSIwLjYiPuObluWutDwvdGV4dD48L3N2Zz4='
        }
    },
    mounted() {
        this.getUnreadCount();
        this.loadMessages('comment');
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        getUnreadCount() {
            this.$axios.get('/user/unreadCount').then(res => {
                const { data } = res;
                if (data.code === 200 && data.data) {
                    this.commentCount = data.data.commentCount || 0;
                    this.likeCount = data.data.likeCount || 0;
                }
            }).catch(err => {
                console.log('获取未读消息数量失败', err);
            });
        },
        route(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        switchTab(tab) {
            this.currentTab = tab;
            this.loadMessages(tab);
        },
        loadMessages(tab) {
            this.messageList = [];
            if (tab === 'comment') {
                this.$axios.get('/notification/comments').then(res => {
                    const { data } = res;
                    if (data.code === 200) {
                        this.messageList = (data.data || []).map(item => ({
                            id: item.id,
                            avatar: item.userAvatar || this.defaultAvatar,
                            name: item.userName || '匿名用户',
                            time: this.formatTime(item.createTime),
                            text: item.content || '评论了你的内容',
                            preview: item.contentCover || this.defaultCover,
                            contentId: item.contentId,
                            contentType: item.contentType
                        }));
                        this.commentCount = 0;
                    }
                }).catch(err => {
                    console.log('获取评论消息失败', err);
                });
            } else if (tab === 'like') {
                this.$axios.get('/notification/likes').then(res => {
                    const { data } = res;
                    if (data.code === 200) {
                        this.messageList = (data.data || []).map(item => ({
                            id: item.id,
                            avatar: item.userAvatar || this.defaultAvatar,
                            name: item.userName || '匿名用户',
                            time: this.formatTime(item.createTime),
                            text: '赞了你的「' + (item.contentTitle || '内容') + '」',
                            preview: item.contentCover || this.defaultCover,
                            contentId: item.contentId,
                            contentType: item.contentType
                        }));
                        this.likeCount = 0;
                    }
                }).catch(err => {
                    console.log('获取点赞消息失败', err);
                });
            } else if (tab === 'follow') {
                this.followCount = 0;
            } else if (tab === 'system') {
                this.systemCount = 0;
            }
        },
        formatTime(timeStr) {
            if (!timeStr) return '';
            const date = new Date(timeStr);
            const now = new Date();
            const diff = now - date;
            const minutes = Math.floor(diff / 60000);
            const hours = Math.floor(diff / 3600000);
            const days = Math.floor(diff / 86400000);
            
            if (minutes < 1) return '刚刚';
            if (minutes < 60) return minutes + '分钟前';
            if (hours < 24) return hours + '小时前';
            if (days < 7) return days + '天前';
            return timeStr.substring(0, 10);
        },
        readMessage(msg) {
            if (msg.contentId && msg.contentType === 'gourmet') {
                sessionStorage.setItem('gourmetId', msg.contentId);
                this.$router.push('/gourmetDetail');
            }
        }
    }
};
</script>

<style scoped lang="scss">
.message-page {
    min-height: 100vh;
    background: #F5F7FA;
}

.top-nav {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 56px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    z-index: 100;
    box-shadow: 0 1px 0 rgba(0, 0, 0, 0.05);
    
    .nav-left, .nav-right {
        width: 56px;
        height: 56px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .nav-left {
        cursor: pointer;
        
        i {
            font-size: 22px;
            color: #333;
        }
    }
    
    .nav-title {
        font-size: 18px;
        font-weight: 600;
        color: #1D2129;
    }
}

.message-content {
    padding: 72px 0 16px;
}

.message-tabs {
    display: flex;
    background: #fff;
    padding: 12px 16px;
    gap: 8px;
    
    .tab-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        padding: 12px;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
        
        i {
            font-size: 24px;
            color: #86909C;
        }
        
        span {
            font-size: 12px;
            color: #86909C;
        }
        
        .badge {
            position: absolute;
            top: 6px;
            right: 12px;
            min-width: 16px;
            height: 16px;
            background: #FF8A00;
            border-radius: 8px;
            font-size: 10px;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 0 4px;
        }
        
        &.active {
            background: #FFF5EB;
            
            i, span {
                color: #FF8A00;
            }
        }
        
        &:hover {
            background: #FFF5EB;
        }
    }
}

.message-list {
    padding: 12px 16px;
    
    .message-item {
        display: flex;
        gap: 12px;
        padding: 16px;
        background: #fff;
        border-radius: 12px;
        margin-bottom: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
            background: #FFF5EB;
        }
        
        .msg-avatar {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            object-fit: cover;
            flex-shrink: 0;
        }
        
        .msg-content {
            flex: 1;
            min-width: 0;
            
            .msg-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 6px;
                
                .msg-name {
                    font-size: 14px;
                    font-weight: 600;
                    color: #1D2129;
                }
                
                .msg-time {
                    font-size: 12px;
                    color: #86909C;
                }
            }
            
            .msg-text {
                font-size: 14px;
                color: #4E5969;
                line-height: 1.5;
            }
            
            .msg-preview {
                margin-top: 8px;
                
                .preview-img {
                    width: 48px;
                    height: 48px;
                    border-radius: 8px;
                    object-fit: cover;
                }
            }
        }
    }
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: #86909C;
    
    i {
        font-size: 48px;
        margin-bottom: 16px;
    }
    
    p {
        font-size: 14px;
        margin: 0;
    }
}
</style>
