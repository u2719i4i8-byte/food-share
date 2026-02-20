<template>
    <div class="draft-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">草稿箱</h1>
            <div class="header-right" v-if="draftList.length > 0" @click="clearAllDrafts">
                <span>清空</span>
            </div>
        </header>

        <div class="content-area" v-loading="loading">
            <div class="empty-state" v-if="!loading && draftList.length === 0">
                <div class="empty-icon">
                    <i class="el-icon-edit-outline"></i>
                </div>
                <p class="empty-text">草稿箱空空如也</p>
                <p class="empty-desc">创作的内容会自动保存在这里</p>
            </div>

            <div class="draft-list" v-else>
                <div 
                    class="draft-card" 
                    v-for="(item, index) in draftList" 
                    :key="index"
                    @click="continueEdit(item)"
                >
                    <div class="draft-cover" v-if="item.cover">
                        <img :src="item.cover" alt="">
                    </div>
                    <div class="draft-content">
                        <h3 class="draft-title">{{ item.title || '无标题' }}</h3>
                        <p class="draft-preview">{{ item.content || '暂无内容' }}</p>
                        <div class="draft-meta">
                            <span class="meta-time">{{ formatTime(item.updateTime) }}</span>
                            <span class="meta-type">{{ getTypeName(item.type) }}</span>
                        </div>
                    </div>
                    <div class="draft-actions">
                        <div class="action-btn" @click.stop="continueEdit(item)">
                            <i class="el-icon-edit"></i>
                        </div>
                        <div class="action-btn delete" @click.stop="deleteDraft(item, index)">
                            <i class="el-icon-delete"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="tips-card" v-if="draftList.length > 0">
            <i class="el-icon-info"></i>
            <span>草稿保存在本地，清除浏览器数据会丢失</span>
        </div>
    </div>
</template>

<script>
const DRAFT_STORAGE_KEY = 'food_share_drafts';

export default {
    name: "Draft",
    data() {
        return {
            loading: false,
            draftList: []
        }
    },
    mounted() {
        this.loadDrafts();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        loadDrafts() {
            this.loading = true;
            try {
                const drafts = localStorage.getItem(DRAFT_STORAGE_KEY);
                if (drafts) {
                    this.draftList = JSON.parse(drafts);
                }
            } catch (error) {
                console.log('加载草稿失败', error);
            } finally {
                this.loading = false;
            }
        },
        saveDrafts() {
            localStorage.setItem(DRAFT_STORAGE_KEY, JSON.stringify(this.draftList));
        },
        continueEdit(item) {
            if (item.type === 'gourmet') {
                sessionStorage.setItem('draftData', JSON.stringify(item));
                this.$router.push('/createGourmet?draft=' + item.id);
            } else if (item.type === 'content') {
                sessionStorage.setItem('draftData', JSON.stringify(item));
                this.$router.push('/createContent?draft=' + item.id);
            }
        },
        deleteDraft(item, index) {
            this.$confirm('确定要删除这个草稿吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.draftList.splice(index, 1);
                this.saveDrafts();
                this.$message.success('删除成功');
            }).catch(() => {});
        },
        clearAllDrafts() {
            this.$confirm('确定要清空所有草稿吗？此操作不可恢复', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.draftList = [];
                this.saveDrafts();
                this.$message.success('已清空草稿箱');
            }).catch(() => {});
        },
        getTypeName(type) {
            const types = {
                'gourmet': '美食做法',
                'content': '内容分享'
            };
            return types[type] || '未知类型';
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

.draft-page {
    min-height: 100vh;
    background: $bg-primary;
    padding-bottom: 80px;
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
        min-width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        
        i {
            font-size: 22px;
            color: $text-primary;
        }
        
        span {
            font-size: 14px;
            color: $text-secondary;
        }
    }
    
    .header-title {
        font-size: 17px;
        font-weight: 600;
        color: $text-primary;
        margin: 0;
    }
}

.content-area {
    margin-top: 56px;
    padding: 16px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 20px;
    
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
        font-size: 16px;
        font-weight: 500;
        color: $text-primary;
        margin: 0 0 8px;
    }
    
    .empty-desc {
        font-size: 14px;
        color: $text-light;
        margin: 0;
    }
}

.draft-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.draft-card {
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
    
    .draft-cover {
        width: 80px;
        height: 80px;
        flex-shrink: 0;
        
        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
    }
    
    .draft-content {
        flex: 1;
        padding: 12px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        min-width: 0;
        
        .draft-title {
            font-size: 15px;
            font-weight: 600;
            color: $text-primary;
            margin: 0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .draft-preview {
            font-size: 13px;
            color: $text-secondary;
            margin: 4px 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .draft-meta {
            display: flex;
            gap: 12px;
            font-size: 12px;
            color: $text-light;
            
            .meta-type {
                color: $primary-color;
            }
        }
    }
    
    .draft-actions {
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

.tips-card {
    position: fixed;
    bottom: 20px;
    left: 16px;
    right: 16px;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(10px);
    border-radius: 12px;
    padding: 12px 16px;
    display: flex;
    align-items: center;
    gap: 8px;
    
    i {
        font-size: 16px;
        color: #fff;
    }
    
    span {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.9);
    }
}
</style>
