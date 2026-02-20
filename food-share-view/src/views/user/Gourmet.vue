<template>
    <div class="gourmet-page">
        <div class="page-header">
            <div class="header-title">
                <h2>美食集锦</h2>
                <p>发现美味，分享生活</p>
            </div>
            <div class="header-search">
                <el-input 
                    v-model="gourtmetQueryDto.title"
                    placeholder="搜索美食..."
                    prefix-icon="el-icon-search"
                    clearable 
                    @clear="handleFilterClear"
                    @keyup.enter.native="handleSearch"
                    class="search-input"
                >
                    <el-button slot="append" @click="handleSearch">搜索</el-button>
                </el-input>
            </div>
        </div>
        
        <div class="category-nav">
            <div class="group-indicators">
                <div 
                    class="group-item"
                    :class="{ active: currentGroup === 'cuisine' }"
                    @click="switchGroup('cuisine')"
                >
                    <span class="group-name">菜系</span>
                    <span class="group-arrow">▶</span>
                </div>
                <div 
                    class="group-item"
                    :class="{ active: currentGroup === 'scene' }"
                    @click="switchGroup('scene')"
                >
                    <span class="group-name">场景</span>
                    <span class="group-arrow">▶</span>
                </div>
                <div 
                    class="group-item"
                    :class="{ active: currentGroup === 'ingredient' }"
                    @click="switchGroup('ingredient')"
                >
                    <span class="group-name">食材</span>
                    <span class="group-arrow">▶</span>
                </div>
                <div 
                    class="group-item"
                    :class="{ active: currentGroup === 'method' }"
                    @click="switchGroup('method')"
                >
                    <span class="group-name">做法</span>
                    <span class="group-arrow">▶</span>
                </div>
                <div 
                    class="group-item"
                    :class="{ active: currentGroup === 'feature' }"
                    @click="switchGroup('feature')"
                >
                    <span class="group-name">特色</span>
                    <span class="group-arrow">▶</span>
                </div>
            </div>
            
            <div class="tags-wrapper">
                <div class="tags-scroll">
                    <div 
                        class="tag-item"
                        :class="{ active: currentTag === '' }"
                        @click="selectTag('')"
                    >
                        全部
                    </div>
                    <div 
                        v-for="(tag, index) in currentTags" 
                        :key="index"
                        class="tag-item"
                        :class="{ active: currentTag === tag }"
                        @click="selectTag(tag)"
                    >
                        {{ tag }}
                    </div>
                </div>
            </div>
        </div>
        
        <div class="content-layout">
            <div class="main-content">
                <div v-if="gourmetList.length === 0" class="empty-state">
                    <el-empty description="暂无美食内容">
                        <el-button type="primary" @click="route('/createGourmet')">发布美食</el-button>
                    </el-empty>
                </div>
                
                <div v-else class="gourmet-grid">
                    <div 
                        class="gourmet-card" 
                        v-for="(gourmet, index) in gourmetList" 
                        :key="index"
                        @click="readGourmet(gourmet)"
                    >
                        <div class="card-cover">
                            <img :src="gourmet.cover" :alt="gourmet.title">
                            <div class="card-overlay">
                                <span class="view-btn">
                                    <i class="el-icon-view"></i> 查看详情
                                </span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="card-author">
                                <img :src="gourmet.userAvatar" class="author-avatar">
                                <span class="author-name">{{ gourmet.userName }}</span>
                            </div>
                            <h3 class="card-title">{{ gourmet.title }}</h3>
                            <p class="card-desc">{{ gourmet.detail }}</p>
                            <div class="card-meta">
                                <span class="meta-item">
                                    <i class="el-icon-time"></i>
                                    {{ gourmet.createTime }}
                                </span>
                                <span class="meta-item">
                                    <i class="el-icon-star-off"></i>
                                    {{ gourmet.saveCount }}
                                </span>
                                <span class="meta-item">
                                    <i class="el-icon-view"></i>
                                    {{ gourmet.viewCount }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="pagination-wrapper" v-if="gourmetList.length > 0">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page="currentPage"
                        :page-sizes="[12, 24, 36]"
                        :page-size="pageSize"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="totalItems"
                        background
                    >
                    </el-pagination>
                </div>
            </div>
            
            <aside class="sidebar">
                <div class="sidebar-card">
                    <div class="sidebar-header">
                        <h3>
                            <i class="el-icon-hot-water"></i>
                            热门推荐
                        </h3>
                    </div>
                    <div class="recommend-list">
                        <div 
                            class="recommend-item" 
                            v-for="(gourmet, index) in topList" 
                            :key="index"
                            @click="readGourmet(gourmet)"
                        >
                            <div class="recommend-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
                            <img :src="gourmet.cover" class="recommend-cover">
                            <div class="recommend-info">
                                <h4 class="recommend-title">{{ gourmet.title }}</h4>
                                <div class="recommend-meta">
                                    <span>{{ gourmet.categoryName }}</span>
                                    <span>{{ timeOut(gourmet.createTime) }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </aside>
        </div>
    </div>
</template>

<script>
/**
 * 美食集锦页面
 * 功能：美食列表展示、分类筛选、搜索、分页、热门推荐
 */
import { timeAgo } from "@/utils/data"

export default {
    name: "Gourmet",
    data() {
        return {
            gourtmetQueryDto: {},      // 查询参数对象
            gourmetList: [],           // 美食列表数据
            filterText: '',            // 筛选文本
            currentPage: 1,            // 当前页码
            pageSize: 12,              // 每页数量
            totalItems: 0,             // 总数据量
            topList: [],               // 热门推荐列表
            defaultLoadItem: 5,        // 默认推荐数量
            currentGroup: 'cuisine',   // 当前分类组
            currentTag: '',            // 当前选中的标签
            categoryNameMap: {},       // 分类名称到ID的映射
            // 分类组配置
            tagGroups: {
                cuisine: ['川菜', '粤菜', '湘菜', '鲁菜', '江浙菜', '西北菜', '东北菜', '闽南菜', '云南菜', '新疆菜', '韩式', '日式', '泰式', '意式', '美式'],
                scene: ['早餐', '下午茶', '夜宵', '一人食', '家庭聚餐', '亲子友好', '朋友小聚', '节日家宴', '户外野餐'],
                ingredient: ['海鲜', '肉食', '素食', '菌菇', '豆制品', '根茎类', '叶菜类', '禽蛋类', '谷物', '坚果', '时令水果'],
                method: ['烧烤', '火锅', '煎炸', '蒸煮', '焖炖', '凉拌', '烘焙', '爆炒', '煲汤'],
                feature: ['非遗', '网红', '新手友好', '减脂轻食', '下饭神器', '下酒小菜', '复刻经典']
            }
        }
    },
    computed: {
        // 获取当前分类组的标签列表
        currentTags() {
            return this.tagGroups[this.currentGroup] || [];
        }
    },
    created() {
        // 从sessionStorage获取搜索关键词
        const savedKeyword = sessionStorage.getItem('searchKeyword');
        if (savedKeyword) {
            this.gourtmetQueryDto.title = savedKeyword;
            sessionStorage.removeItem('searchKeyword');
        }
        // 从sessionStorage获取筛选条件
        const savedFilter = sessionStorage.getItem('filterTag');
        if (savedFilter) {
            const filter = JSON.parse(savedFilter);
            this.currentGroup = filter.group;
            this.currentTag = filter.tag;
            sessionStorage.removeItem('filterTag');
        }
        // 等待分类加载完成后获取美食数据
        this.fetchCategories().then(() => {
            this.fetchGourmetData();
        });
        // 加载热门推荐
        this.loadRecommend(this.defaultLoadItem);
    },
    methods: {
        // 获取分类数据
        fetchCategories() {
            return this.$axios.post('/category/query', { current: 1, size: 100 }).then(res => {
                const { data } = res;
                if (data.code === 200 && data.data) {
                    const categories = data.data || [];
                    const map = {};
                    categories.forEach(c => {
                        map[c.name] = c.id;
                    });
                    this.categoryNameMap = map;
                }
            }).catch(error => {
                console.log('获取分类失败', error);
            });
        },
        // 路由跳转
        route(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        // 切换分类组
        switchGroup(group) {
            this.currentGroup = group;
            this.currentTag = '';
            this.gourtmetQueryDto.title = '';
            this.currentPage = 1;
            this.fetchGourmetData();
        },
        // 选择分类标签
        selectTag(tag) {
            this.currentTag = tag;
            this.gourtmetQueryDto.title = '';
            this.currentPage = 1;
            this.fetchGourmetData();
        },
        // 时间格式化
        timeOut(time) {
            return timeAgo(time);
        },
        // 加载热门推荐（随机排序）
        loadRecommend(item) {
            this.$axios.get(`/recommend/${item}`).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    const list = res.data.data || [];
                    const shuffled = list.sort(() => Math.random() - 0.5);
                    this.topList = shuffled;
                }
            }).catch(error => {
                console.log(error);
            });
        },
        // 查看美食详情
        readGourmet(gourmet) {
            sessionStorage.setItem('gourmetId', gourmet.id);
            this.$router.push('/gourmetDetail');
        },
        // 清除搜索
        handleFilterClear() {
            this.gourtmetQueryDto.title = '';
            this.currentPage = 1;
            this.fetchGourmetData();
        },
        // 搜索处理
        handleSearch() {
            this.currentPage = 1;
            this.fetchGourmetData();
        },
        // 每页数量变化
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.fetchGourmetData();
        },
        // 页码变化
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchGourmetData();
        },
        // 获取美食列表数据
        fetchGourmetData() {
            const queryDto = {
                current: this.currentPage,
                size: this.pageSize,
                isAudit: true,
                isPublish: true,
                ...this.gourtmetQueryDto
            };
            
            // 添加分类筛选条件
            if (this.currentTag) {
                const categoryId = this.categoryNameMap[this.currentTag];
                if (categoryId) {
                    queryDto.categoryId = categoryId;
                }
            }
            
            this.$axios.post('/gourmet/queryList', queryDto).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.gourmetList = res.data.data;
                    this.totalItems = data.total;
                }
            }).catch(error => {
                console.log(error);
            });
        },
    }
};
</script>

<style scoped lang="scss">
.gourmet-page {
    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;
        padding: 24px;
        background: #fff;
        border-radius: 16px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
        
        .header-title {
            h2 {
                margin: 0 0 8px 0;
                font-size: 24px;
                font-weight: 700;
                background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
            }
            
            p {
                margin: 0;
                color: #999;
                font-size: 14px;
            }
        }
        
        .header-search {
            .search-input {
                width: 320px;
                
                ::v-deep .el-input__inner {
                    border-radius: 10px 0 0 10px;
                    border: 2px solid #eee;
                    background: #fafafa;
                    
                    &:focus {
                        border-color: #ff9a56;
                        background: #fff;
                    }
                }
                
                ::v-deep .el-input-group__append {
                    background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
                    border: none;
                    border-radius: 0 10px 10px 0;
                    color: #fff;
                    
                    .el-button {
                        border: none;
                        background: transparent;
                        color: #fff;
                    }
                }
            }
        }
    }
    
    .category-nav {
        background: #fff;
        border-radius: 20px;
        padding: 20px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
        margin-bottom: 24px;
        
        .group-indicators {
            display: flex;
            gap: 8px;
            margin-bottom: 16px;
            padding-bottom: 16px;
            border-bottom: 1px solid #f0f0f0;
            
            .group-item {
                display: flex;
                align-items: center;
                gap: 6px;
                padding: 8px 16px;
                border-radius: 16px;
                cursor: pointer;
                transition: all 0.3s ease;
                font-size: 12px;
                
                .group-name {
                    color: #999;
                    font-weight: 500;
                }
                
                .group-arrow {
                    color: #ccc;
                    font-size: 10px;
                    transition: transform 0.3s ease;
                }
                
                &:hover {
                    background: rgba(255, 126, 95, 0.1);
                    
                    .group-name {
                        color: #FF7E5F;
                    }
                }
                
                &.active {
                    background: linear-gradient(135deg, #FF7E5F 0%, #FEB47B 100%);
                    box-shadow: 0 4px 12px rgba(255, 126, 95, 0.3);
                    
                    .group-name {
                        color: #fff;
                        font-weight: 600;
                    }
                    
                    .group-arrow {
                        color: #fff;
                    }
                }
            }
        }
        
        .tags-wrapper {
            overflow-x: auto;
            overflow-y: hidden;
            -webkit-overflow-scrolling: touch;
            scrollbar-width: none;
            -ms-overflow-style: none;
            
            &::-webkit-scrollbar {
                display: none;
            }
            
            .tags-scroll {
                display: flex;
                gap: 12px;
                padding: 4px 0;
                transition: transform 0.3s ease;
                
                .tag-item {
                    flex-shrink: 0;
                    padding: 8px 20px;
                    border-radius: 16px;
                    background: #f8f9fa;
                    border: 1px solid #eee;
                    color: #666;
                    font-size: 14px;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    user-select: none;
                    
                    &:hover {
                        background: rgba(255, 126, 95, 0.1);
                        border-color: rgba(255, 126, 95, 0.3);
                        color: #FF7E5F;
                    }
                    
                    &:active {
                        transform: scale(0.98);
                    }
                    
                    &.active {
                        background: linear-gradient(135deg, #FF7E5F 0%, #FEB47B 100%);
                        border-color: transparent;
                        color: #fff;
                        font-weight: 500;
                        box-shadow: 0 4px 12px rgba(255, 126, 95, 0.3);
                    }
                }
            }
        }
    }
    
    .content-layout {
        display: flex;
        gap: 24px;
        
        .main-content {
            flex: 1;
            
            .empty-state {
                padding: 60px;
                background: #fff;
                border-radius: 16px;
                text-align: center;
            }
            
            .gourmet-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                gap: 20px;
                
                .gourmet-card {
                    background: #fff;
                    border-radius: 16px;
                    overflow: hidden;
                    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
                    transition: all 0.3s ease;
                    cursor: pointer;
                    
                    &:hover {
                        transform: translateY(-8px);
                        box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
                        
                        .card-cover {
                            img {
                                transform: scale(1.1);
                            }
                            
                            .card-overlay {
                                opacity: 1;
                            }
                        }
                    }
                    
                    .card-cover {
                        position: relative;
                        height: 180px;
                        overflow: hidden;
                        
                        img {
                            width: 100%;
                            height: 100%;
                            object-fit: cover;
                            transition: transform 0.5s ease;
                        }
                        
                        .card-overlay {
                            position: absolute;
                            top: 0;
                            left: 0;
                            right: 0;
                            bottom: 0;
                            background: rgba(0, 0, 0, 0.4);
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            opacity: 0;
                            transition: opacity 0.3s ease;
                            
                            .view-btn {
                                padding: 10px 20px;
                                background: rgba(255, 255, 255, 0.9);
                                border-radius: 25px;
                                color: #ff6b6b;
                                font-size: 14px;
                                font-weight: 500;
                            }
                        }
                    }
                    
                    .card-body {
                        padding: 16px;
                        
                        .card-author {
                            display: flex;
                            align-items: center;
                            gap: 8px;
                            margin-bottom: 12px;
                            
                            .author-avatar {
                                width: 28px;
                                height: 28px;
                                border-radius: 50%;
                                object-fit: cover;
                            }
                            
                            .author-name {
                                font-size: 13px;
                                color: #666;
                            }
                        }
                        
                        .card-title {
                            margin: 0 0 8px 0;
                            font-size: 16px;
                            font-weight: 600;
                            color: #333;
                            display: -webkit-box;
                            -webkit-line-clamp: 2;
                            -webkit-box-orient: vertical;
                            overflow: hidden;
                        }
                        
                        .card-desc {
                            margin: 0 0 12px 0;
                            font-size: 13px;
                            color: #999;
                            display: -webkit-box;
                            -webkit-line-clamp: 2;
                            -webkit-box-orient: vertical;
                            overflow: hidden;
                            line-height: 1.5;
                        }
                        
                        .card-meta {
                            display: flex;
                            gap: 16px;
                            font-size: 12px;
                            color: #aaa;
                            
                            .meta-item {
                                display: flex;
                                align-items: center;
                                gap: 4px;
                                
                                i {
                                    font-size: 14px;
                                }
                            }
                        }
                    }
                }
            }
            
            .pagination-wrapper {
                display: flex;
                justify-content: center;
                margin-top: 24px;
                padding: 20px;
                background: #fff;
                border-radius: 16px;
                
                ::v-deep .el-pagination.is-background {
                    .el-pager li:not(.disabled).active {
                        background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
                    }
                    
                    .el-pager li:not(.disabled):hover {
                        color: #ff6b6b;
                    }
                }
            }
        }
        
        .sidebar {
            width: 320px;
            flex-shrink: 0;
            
            .sidebar-card {
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
                overflow: hidden;
                position: sticky;
                top: 94px;
                
                .sidebar-header {
                    padding: 20px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    
                    h3 {
                        margin: 0;
                        color: #fff;
                        font-size: 16px;
                        font-weight: 600;
                        
                        i {
                            margin-right: 8px;
                        }
                    }
                }
                
                .recommend-list {
                    padding: 12px;
                    
                    .recommend-item {
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        padding: 12px;
                        border-radius: 12px;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        
                        &:hover {
                            background: #f8f9fc;
                        }
                        
                        .recommend-rank {
                            width: 24px;
                            height: 24px;
                            border-radius: 6px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 12px;
                            font-weight: 700;
                            background: #eee;
                            color: #999;
                            
                            &.rank-1 {
                                background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
                                color: #fff;
                            }
                            
                            &.rank-2 {
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                color: #fff;
                            }
                            
                            &.rank-3 {
                                background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                                color: #fff;
                            }
                        }
                        
                        .recommend-cover {
                            width: 60px;
                            height: 45px;
                            border-radius: 8px;
                            object-fit: cover;
                        }
                        
                        .recommend-info {
                            flex: 1;
                            min-width: 0;
                            
                            .recommend-title {
                                margin: 0 0 4px 0;
                                font-size: 14px;
                                font-weight: 500;
                                color: #333;
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                            }
                            
                            .recommend-meta {
                                display: flex;
                                gap: 8px;
                                font-size: 12px;
                                color: #999;
                                
                                span:first-child {
                                    padding: 2px 6px;
                                    background: rgba(102, 126, 234, 0.1);
                                    border-radius: 4px;
                                    color: #667eea;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@media (max-width: 1200px) {
    .gourmet-page {
        .content-layout {
            .sidebar {
                display: none;
            }
        }
    }
}

@media (max-width: 768px) {
    .gourmet-page {
        .page-header {
            flex-direction: column;
            gap: 16px;
            align-items: flex-start;
            
            .header-search {
                width: 100%;
                
                .search-input {
                    width: 100%;
                }
            }
        }
        
        .content-layout {
            .main-content {
                .gourmet-grid {
                    grid-template-columns: 1fr;
                }
            }
        }
    }
}
</style>
