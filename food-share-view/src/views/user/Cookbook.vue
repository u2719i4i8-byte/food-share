<template>
    <div class="my-cookbook-page">
        <div class="page-header">
            <div class="header-left">
                <div class="back-btn" @click="goBack">
                    <i class="el-icon-arrow-left"></i>
                </div>
                <div class="title-section">
                    <h2 class="page-title">
                        <i class="el-icon-notebook-2"></i>
                        我的食谱
                    </h2>
                    <p class="page-subtitle">收藏喜爱的食谱，开启美味之旅</p>
                </div>
            </div>
            <div class="header-right">
                <div class="search-box">
                    <i class="el-icon-search"></i>
                    <input 
                        type="text" 
                        v-model="cookbookQueryDto.title" 
                        placeholder="搜索食谱..."
                        @keyup.enter="fetchGourmetData"
                        @clear="handleFilterClear"
                    />
                    <el-button type="primary" size="small" @click="fetchGourmetData">搜索</el-button>
                </div>
            </div>
        </div>

        <div class="category-section">
            <div class="group-nav">
                <div 
                    class="group-item" 
                    v-for="(group, index) in categoryGroups" 
                    :key="index"
                    :class="{ active: currentGroupIndex === index }"
                    @click="selectGroup(index)"
                >
                    {{ group.name }}
                </div>
            </div>
            
            <div class="category-nav-wrapper">
                <div class="category-nav" :style="{ transform: 'translateX(' + (-currentGroupIndex * 20) + '%)' }">
                    <div 
                        class="category-group" 
                        v-for="(group, groupIndex) in categoryGroups" 
                        :key="groupIndex"
                    >
                        <div 
                            class="category-item" 
                            v-for="(tag, tagIndex) in group.tags" 
                            :key="tagIndex"
                            :class="{ active: currentTags[groupIndex] === tagIndex }"
                            @click="selectCategoryTag(groupIndex, tagIndex, tag)"
                        >
                            {{ tag }}
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="content-section">
            <div class="section-header">
                <h3 class="section-title">
                    <i class="el-icon-food"></i>
                    {{ currentCuisine || '全部食谱' }}
                </h3>
                <span class="recipe-count">共 {{ totalItems }} 个食谱</span>
            </div>

            <div v-if="cookbookList.length === 0" class="empty-state">
                <div class="empty-icon">
                    <i class="el-icon-document"></i>
                </div>
                <h4>暂无食谱</h4>
                <p>该分类下还没有食谱，换个分类看看吧</p>
            </div>

            <div v-else class="recipe-grid">
                <div 
                    class="recipe-card"
                    v-for="(cookbook, index) in cookbookList"
                    :key="index"
                    @click="readDetail(cookbook)"
                >
                    <div class="recipe-cover">
                        <img :src="cookbook.cover" alt="" />
                        <div class="recipe-overlay">
                            <span class="view-btn">
                                <i class="el-icon-view"></i>
                                查看详情
                            </span>
                        </div>
                    </div>
                    <div class="recipe-info">
                        <h4 class="recipe-title">{{ cookbook.title }}</h4>
                        <div class="recipe-meta">
                            <span class="meta-item">
                                <i class="el-icon-time"></i>
                                {{ cookbook.createTime }}
                            </span>
                            <span class="meta-item" v-if="cookbook.categoryName">
                                <i class="el-icon-folder"></i>
                                {{ cookbook.categoryName }}
                            </span>
                        </div>
                        <div class="recipe-stats" v-if="cookbook.viewCount || cookbook.likeCount">
                            <span class="stat-item" v-if="cookbook.viewCount">
                                <i class="el-icon-view"></i>
                                {{ cookbook.viewCount }}
                            </span>
                            <span class="stat-item" v-if="cookbook.likeCount">
                                <i class="el-icon-star-off"></i>
                                {{ cookbook.likeCount }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="pagination-wrapper" v-if="cookbookList.length > 0">
                <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[12, 24, 36]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="totalItems"
                    background
                />
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Cookbook",
    data() {
        return {
            cookbookQueryDto: {},
            cookbookList: [],
            currentPage: 1,
            pageSize: 12,
            totalItems: 0,
            currentGroupIndex: 0,
            currentTags: [0, 0, 0, 0, 0],
            categoryGroups: [
                {
                    name: '菜系',
                    tags: ['推荐', '川菜', '粤菜', '湘菜', '鲁菜', '江浙菜', '西北菜', '东北菜', '闽南菜', '云南菜', '新疆菜', '韩式', '日式', '泰式', '意式', '美式']
                },
                {
                    name: '场景',
                    tags: ['推荐', '早餐', '下午茶', '夜宵', '一人食', '家庭聚餐', '亲子友好', '朋友小聚', '节日家宴', '户外野餐']
                },
                {
                    name: '食材',
                    tags: ['推荐', '海鲜', '肉食', '素食', '菌菇', '豆制品', '根茎类', '叶菜类', '禽蛋类', '谷物', '坚果', '时令水果']
                },
                {
                    name: '做法',
                    tags: ['推荐', '烧烤', '火锅', '煎炸', '蒸煮', '焖炖', '凉拌', '烘焙', '爆炒', '煲汤']
                },
                {
                    name: '特色',
                    tags: ['推荐', '非遗', '网红', '新手友好', '减脂轻食', '下饭神器', '下酒小菜', '复刻经典']
                }
            ]
        }
    },
    computed: {
        currentCuisine() {
            const groupIndex = this.currentGroupIndex;
            const tagIndex = this.currentTags[groupIndex];
            const tag = this.categoryGroups[groupIndex].tags[tagIndex];
            return tag === '推荐' ? '' : tag;
        }
    },
    created() {
        this.fetchGourmetData();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        readDetail(cookbook) {
            sessionStorage.setItem('cookbookInfo', JSON.stringify(cookbook));
            this.$router.push('/cookbookDetail');
        },
        handleFilterClear() {
            this.cookbookQueryDto.title = '';
            this.fetchGourmetData();
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.fetchGourmetData();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchGourmetData();
        },
        selectGroup(index) {
            this.currentGroupIndex = index;
        },
        selectCategoryTag(groupIndex, tagIndex, tag) {
            this.$set(this.currentTags, groupIndex, tagIndex);
            this.currentPage = 1;
            this.fetchGourmetData();
        },
        fetchGourmetData() {
            const queryDto = {
                current: this.currentPage,
                size: this.pageSize,
                key: this.cookbookQueryDto.title || '',
                group: this.categoryGroups[this.currentGroupIndex].name,
                tag: this.currentCuisine,
                ...this.cookbookQueryDto
            };
            this.$axios.post('/cookbook/queryPublish', queryDto).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.cookbookList = res.data.data || [];
                    this.totalItems = data.total || 0;
                }
            }).catch(error => {
                console.log(error);
            });
        },
    }
};
</script>

<style scoped lang="scss">
.my-cookbook-page {
    min-height: calc(100vh - 120px);
    background: #f5f7fa;
    padding: 24px;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    flex-wrap: wrap;
    gap: 16px;
    
    .header-left {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .back-btn {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            background: #f5f5f5;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            
            i {
                font-size: 20px;
                color: #333;
            }
            
            &:hover {
                background: #eee;
            }
        }
        
        .title-section {
            .page-title {
                display: flex;
                align-items: center;
                gap: 10px;
                margin: 0 0 8px 0;
                font-size: 24px;
                font-weight: 600;
                color: #333;
                
                i {
                    color: #11998e;
                    font-size: 28px;
                }
            }
            
            .page-subtitle {
                margin: 0;
                font-size: 14px;
                color: #999;
            }
        }
    }
    
    .header-right {
        .search-box {
            display: flex;
            align-items: center;
            background: #fff;
            border-radius: 12px;
            padding: 4px 4px 4px 16px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
            
            i {
                color: #999;
                font-size: 18px;
            }
            
            input {
                border: none;
                outline: none;
                padding: 10px 12px;
                font-size: 14px;
                width: 200px;
                
                &::placeholder {
                    color: #ccc;
                }
            }
            
            .el-button {
                border-radius: 8px;
            }
        }
    }
}

.category-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px 24px;
    background: #fff;
    border-radius: 16px;
    margin-bottom: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    
    .group-nav {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 24px;
        height: 48px;
        margin-bottom: 12px;
        
        .group-item {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 8px 20px;
            font-size: 16px;
            font-weight: 500;
            color: #666;
            cursor: pointer;
            border-radius: 20px;
            transition: all 0.3s ease;
            
            &:hover {
                color: #FF6B35;
                background: rgba(255, 126, 95, 0.08);
            }
            
            &.active {
                color: #FFF;
                font-weight: 600;
                background: linear-gradient(135deg, #ff9a56 0%, #FF6B35 100%);
                box-shadow: 0 4px 12px rgba(255, 126, 95, 0.3);
            }
        }
    }
    
    .category-nav-wrapper {
        width: 100%;
        max-width: 1400px;
        height: 48px;
        overflow: hidden;
        
        .category-nav {
            display: flex;
            width: 500%;
            height: 100%;
            transition: transform 300ms ease-out;
            
            .category-group {
                display: flex;
                gap: 12px;
                align-items: center;
                padding: 0 8px;
                width: 20%;
                overflow-x: auto;
                -ms-overflow-style: none;
                scrollbar-width: none;
                
                &::-webkit-scrollbar {
                    display: none;
                }
                
                .category-item {
                    flex-shrink: 0;
                    height: 32px;
                    padding: 0 16px;
                    border-radius: 16px;
                    background: #fff;
                    border: 1px solid #eee;
                    font-size: 14px;
                    color: #666;
                    cursor: pointer;
                    white-space: nowrap;
                    transition: all 0.2s ease;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    
                    &:hover {
                        border-color: #FF6B35;
                        color: #FF6B35;
                    }
                    
                    &:active {
                        transform: scale(0.98);
                    }
                    
                    &.active {
                        background: linear-gradient(135deg, #ff9a56 0%, #FF6B35 100%);
                        border: none;
                        color: #FFF;
                        box-shadow: 0 4px 12px rgba(255, 126, 95, 0.3);
                    }
                }
            }
        }
    }
}

.content-section {
    background: #fff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    
    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        .section-title {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            color: #333;
            
            i {
                color: #11998e;
            }
        }
        
        .recipe-count {
            font-size: 14px;
            color: #999;
        }
    }
}

.empty-state {
    text-align: center;
    padding: 60px 20px;
    
    .empty-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto 20px;
        background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        
        i {
            font-size: 36px;
            color: #ccc;
        }
    }
    
    h4 {
        margin: 0 0 8px 0;
        font-size: 18px;
        color: #333;
    }
    
    p {
        margin: 0;
        font-size: 14px;
        color: #999;
    }
}

.recipe-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}

.recipe-card {
    background: #fff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        
        .recipe-cover {
            .recipe-overlay {
                opacity: 1;
            }
        }
    }
    
    .recipe-cover {
        position: relative;
        height: 180px;
        overflow: hidden;
        
        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.3s ease;
        }
        
        .recipe-overlay {
            position: absolute;
            inset: 0;
            background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.6) 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s ease;
            
            .view-btn {
                display: flex;
                align-items: center;
                gap: 6px;
                padding: 10px 20px;
                background: rgba(255, 255, 255, 0.95);
                border-radius: 20px;
                font-size: 14px;
                color: #333;
                font-weight: 500;
                
                i {
                    color: #11998e;
                }
            }
        }
    }
    
    .recipe-info {
        padding: 16px;
        
        .recipe-title {
            margin: 0 0 10px 0;
            font-size: 16px;
            font-weight: 600;
            color: #333;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .recipe-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            margin-bottom: 12px;
            
            .meta-item {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                color: #999;
                
                i {
                    font-size: 14px;
                }
            }
        }
        
        .recipe-stats {
            display: flex;
            gap: 16px;
            padding-top: 12px;
            border-top: 1px solid #f0f0f0;
            
            .stat-item {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                color: #666;
                
                i {
                    color: #11998e;
                }
            }
        }
    }
}

.pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
    .my-cookbook-page {
        padding: 16px;
    }
    
    .page-header {
        flex-direction: column;
        align-items: flex-start;
        
        .header-right {
            width: 100%;
            
            .search-box {
                width: 100%;
                
                input {
                    flex: 1;
                    width: auto;
                }
            }
        }
    }
    
    .recipe-grid {
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    }
}
</style>
