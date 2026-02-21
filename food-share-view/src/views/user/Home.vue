<template>
    <div class="home-page">
        <aside class="sidebar">
            <div class="logo">
                <Logo :sysName="'橘子美食网'" />
            </div>
            
            <nav class="sidebar-nav">
                <div class="nav-item" :class="{ active: currentNav === 'home' }" @click="switchNav('home')">
                    <i class="el-icon-house"></i>
                    <span>发现</span>
                </div>
                <div class="nav-item" @click="route('/save')">
                    <i class="el-icon-star-on"></i>
                    <span>收藏夹</span>
                </div>
                <div class="nav-item" @click="route('/dietHistory')">
                    <i class="el-icon-data-line"></i>
                    <span>饮食管理</span>
                </div>
                <div class="nav-item" @click="route('/nutriment')">
                    <i class="el-icon-pie-chart"></i>
                    <span>营养分析</span>
                </div>
                <div class="nav-item" @click="route('/createGourmet')">
                    <i class="el-icon-edit-outline"></i>
                    <span>发布美食</span>
                </div>
                <div class="nav-item" @click="route('/message')">
                    <i class="el-icon-bell"></i>
                    <span>消息通知</span>
                    <div class="badge" v-if="unreadCount > 0">{{ unreadCount }}</div>
                </div>
            </nav>
            
            <div class="user-profile" @click="handleUserClick">
                <img :src="displayAvatar" class="profile-avatar">
                <span class="profile-name">{{ displayName }}</span>
            </div>
        </aside>

        <main class="main-content">
            <div class="content-header">
                <div class="search-bar">
                    <div class="search-box">
                        <input type="text" placeholder="搜索" v-model="searchKeyword" @keyup.enter="handleSearch">
                        <i class="el-icon-search" @click="handleSearch"></i>
                    </div>
                </div>
                
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
                                @click="selectTag(groupIndex, tagIndex)"
                            >
                                {{ tag }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="note-grid" ref="noteGrid">
                <div 
                    class="note-card" 
                    v-for="(gourmet, index) in gourmetList" 
                    :key="index"
                    @click="readGourmet(gourmet)"
                >
                    <div class="card-cover">
                        <img :src="getSafeImageUrl(gourmet.cover, index)" :alt="gourmet.title" loading="lazy" @error="handleImageError">
                    </div>
                    <div class="card-content">
                        <h3 class="card-title">{{ gourmet.title }}</h3>
                        <div class="card-footer">
                            <img :src="gourmet.userAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + index" class="author-avatar">
                            <div class="like-info">
                                <i class="el-icon-star-on"></i>
                                <span>{{ gourmet.saveCount || 0 }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="empty-state" v-if="!loading && gourmetList.length === 0">
                <div class="empty-icon">
                    <i class="el-icon-document"></i>
                </div>
                <h4>暂无相关美食</h4>
                <p>该分类下还没有美食内容，换个分类看看吧</p>
            </div>

            <div class="loading-more" v-if="loading">
                <div class="loading-spinner"></div>
                <span>加载中...</span>
            </div>

            <div class="no-more" v-if="!hasMore && gourmetList.length > 0">
                <span>已经到底啦~</span>
            </div>
        </main>
    </div>
</template>

<script>
/**
 * 首页组件 - 美食发现页面
 * 功能：分类导航、美食推荐、搜索、无限滚动加载
 */
import { getToken, isLoggedIn, getUserInfo, setUserInfo } from '@/utils/storage.js';
import Logo from '@/components/Logo.vue';

// 定时器轮询间隔配置
const UNREAD_POLL_INTERVAL = 15000;    // 未读消息轮询间隔（毫秒）
const USER_INFO_POLL_INTERVAL = 60000; // 用户信息轮询间隔（毫秒）

export default {
    name: "Home",
    components: { Logo },
    data() {
        return {
            currentNav: 'home',        // 当前导航项
            searchKeyword: '',         // 搜索关键词
            gourmetList: [],           // 美食列表数据
            loading: false,            // 加载状态
            current: 1,                // 当前页码
            size: 20,                  // 每页数量
            hasMore: true,             // 是否有更多数据
            unreadCount: 0,            // 未读消息数量
            unreadCountTimer: null,    // 未读消息定时器
            userInfoTimer: null,       // 用户信息定时器
            currentGroupIndex: 0,      // 当前分类组索引
            currentTags: [0, 0, 0, 0, 0], // 各分类组当前选中的标签索引
            categoryNameMap: {},       // 分类名称到ID的映射
            defaultAvatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+',
            defaultCover: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MDAgMzAwIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2ZmZjVmMCIvPjxjaXJjbGUgY3g9IjIwMCIgY3k9IjE1MCIgcj0iNjAiIGZpbGw9IiNmZjZiMzUiIG9wYWNpdHk9IjAuMyIvPjx0ZXh0IHg9IjIwMCIgeT0iMTUwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LXNpemU9IjI0IiBmaWxsPSIjZmY2YjM1IiBvcGFjaXR5PSIwLjYiPuObluWutDwvdGV4dD48L3N2Zz4=',
            // 分类组配置：菜系、场景、食材、做法、特色
            categoryGroups: [
                {
                    name: '菜系',
                    tags: ['推荐', '家常菜', '川菜', '粤菜', '湘菜', '鲁菜', '江浙菜', '西北菜', '东北菜', '闽南菜', '云南菜', '新疆菜', '韩式', '日式', '泰式', '意式', '美式']
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
            ],
            lastFetchTime: 0,          // 上次获取用户信息的时间
            cachedUserInfo: null       // 缓存的用户信息
        }
    },
    computed: {
        // 判断用户是否已登录
        isLogin() {
            return isLoggedIn();
        },
        // 显示用户头像
        displayAvatar() {
            if (!this.isLogin) {
                return this.defaultAvatar;
            }
            if (this.cachedUserInfo && this.cachedUserInfo.userAvatar) {
                return this.cachedUserInfo.userAvatar;
            }
            const { userAvatar } = getUserInfo();
            return userAvatar || this.defaultAvatar;
        },
        // 显示用户名称
        displayName() {
            if (!this.isLogin) {
                return '登录';
            }
            if (this.cachedUserInfo && this.cachedUserInfo.userName) {
                return this.cachedUserInfo.userName;
            }
            const { userName } = getUserInfo();
            return userName || '我';
        }
    },
    mounted() {
        // 初始化用户状态
        this.initUserState();
        // 等待分类加载完成后获取美食数据
        this.fetchCategories().then(() => {
            this.fetchGourmets();
        });
        // 已登录用户启动定时器
        if (this.isLogin) {
            this.startTimers();
        }
        // 监听滚动事件实现无限加载
        window.addEventListener('scroll', this.handleScroll);
        // 监听页面可见性变化
        document.addEventListener('visibilitychange', this.handleVisibilityChange);
    },
    beforeDestroy() {
        // 移除事件监听
        window.removeEventListener('scroll', this.handleScroll);
        document.removeEventListener('visibilitychange', this.handleVisibilityChange);
        // 停止定时器
        this.stopTimers();
    },
    methods: {
        // 图片加载失败处理
        handleImageError(e) {
            const img = e.target;
            if (img.dataset.error === 'true') return;
            img.dataset.error = 'true';
            img.src = this.defaultCover;
        },
        // 获取安全的图片URL
        getSafeImageUrl(url, seed) {
            if (!url) return this.defaultCover;
            if (url.includes('unsplash')) {
                return `https://picsum.photos/seed/${seed || 'food'}/400/300`;
            }
            return url;
        },
        // 初始化用户状态
        initUserState() {
            if (this.isLogin) {
                this.loadCachedUserInfo();
                this.getUnreadCount();
            }
        },
        // 从sessionStorage加载缓存的用户信息
        loadCachedUserInfo() {
            const savedUserName = sessionStorage.getItem('userName');
            const savedUserAvatar = sessionStorage.getItem('userAvatar');
            if (savedUserName || savedUserAvatar) {
                this.cachedUserInfo = {
                    userName: savedUserName,
                    userAvatar: savedUserAvatar
                };
            }
        },
        // 页面可见性变化处理
        handleVisibilityChange() {
            if (!document.hidden && this.isLogin) {
                this.refreshAllData();
            }
        },
        // 刷新所有数据
        refreshAllData() {
            this.getUnreadCount();
            this.fetchUserInfo();
        },
        // 启动定时器
        startTimers() {
            this.getUnreadCount();
            this.fetchUserInfo();
            
            // 定时获取未读消息数量
            this.unreadCountTimer = setInterval(() => {
                if (!document.hidden) {
                    this.getUnreadCount();
                }
            }, UNREAD_POLL_INTERVAL);
            
            // 定时获取用户信息
            this.userInfoTimer = setInterval(() => {
                if (!document.hidden) {
                    this.fetchUserInfo();
                }
            }, USER_INFO_POLL_INTERVAL);
        },
        // 停止定时器
        stopTimers() {
            if (this.unreadCountTimer) {
                clearInterval(this.unreadCountTimer);
                this.unreadCountTimer = null;
            }
            if (this.userInfoTimer) {
                clearInterval(this.userInfoTimer);
                this.userInfoTimer = null;
            }
        },
        // 获取用户信息
        fetchUserInfo() {
            if (!getToken()) return;
            
            const now = Date.now();
            if (now - this.lastFetchTime < 5000) return;
            this.lastFetchTime = now;
            
            this.$axios.get('/user/auth').then(res => {
                const { data } = res;
                if (data.code === 200 && data.data) {
                    const userName = data.data.userName || data.data.userAccount || '我';
                    const userAvatar = data.data.userAvatar || this.defaultAvatar;
                    const userId = data.data.id;
                    
                    this.cachedUserInfo = { userName, userAvatar };
                    setUserInfo(userName, userAvatar, userId);
                }
            }).catch(err => {
                console.log('获取用户信息失败', err);
            });
        },
        // 用户头像点击处理
        handleUserClick() {
            if (this.isLogin) {
                this.route('/mine');
            } else {
                this.route('/login');
            }
        },
        // 路由跳转
        route(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        // 切换导航项
        switchNav(nav) {
            this.currentNav = nav;
            if (nav === 'home') {
                this.current = 1;
                this.gourmetList = [];
                this.hasMore = true;
                this.fetchGourmets();
            }
        },
        // 选择分类组
        selectGroup(index) {
            this.currentGroupIndex = index;
            this.currentTags.splice(index, 1, 0);
            this.current = 1;
            this.gourmetList = [];
            this.hasMore = true;
            this.fetchGourmets();
        },
        // 选择分类标签
        selectTag(groupIndex, tagIndex) {
            this.currentTags.splice(groupIndex, 1, tagIndex);
            this.current = 1;
            this.gourmetList = [];
            this.hasMore = true;
            this.fetchGourmets();
        },
        // 搜索处理
        handleSearch() {
            if (this.searchKeyword.trim()) {
                sessionStorage.setItem('searchKeyword', this.searchKeyword);
                this.$router.push('/gourmet');
            }
        },
        // 获取当前选中的标签名称
        getCurrentTagName() {
            const groupIndex = this.currentGroupIndex;
            const tagIndex = this.currentTags[groupIndex];
            return this.categoryGroups[groupIndex].tags[tagIndex];
        },
        // 获取美食列表数据
        fetchGourmets() {
            if (this.loading || !this.hasMore) return;
            
            const tagName = this.getCurrentTagName();
            if (tagName && tagName !== '推荐') {
                const categoryId = this.categoryNameMap[tagName];
                if (!categoryId) {
                    this.hasMore = false;
                    this.gourmetList = [];
                    this.loading = false;
                    return;
                }
            }
            
            this.loading = true;
            
            // 推荐标签：获取当前分类组下所有分类的随机美食
            if (tagName === '推荐') {
                const groupIndex = this.currentGroupIndex;
                const groupTags = this.categoryGroups[groupIndex].tags.filter(t => t !== '推荐');
                const categoryIds = groupTags.map(tag => this.categoryNameMap[tag]).filter(id => id);
                
                const queryDto = {
                    current: this.current,
                    size: this.size,
                    isAudit: true,
                    isPublish: true,
                    orderBy: 'random'
                };
                
                if (categoryIds.length > 0) {
                    queryDto.categoryIds = categoryIds;
                }
                
                this.$axios.post('/gourmet/queryList', queryDto).then(res => {
                    const { data } = res;
                    if (data.code === 200) {
                        const list = res.data.data || [];
                        if (list.length < this.size) {
                            this.hasMore = false;
                        }
                        this.gourmetList = [...this.gourmetList, ...list];
                        this.current++;
                    }
                }).catch(error => {
                    console.log(error);
                }).finally(() => {
                    this.loading = false;
                });
            } else {
                // 普通分类标签：获取该分类下的美食
                const queryDto = {
                    current: this.current,
                    size: this.size,
                    isAudit: true,
                    isPublish: true
                };
                
                const categoryId = this.categoryNameMap[tagName];
                if (categoryId) {
                    queryDto.categoryId = categoryId;
                }
                
                this.$axios.post('/gourmet/queryList', queryDto).then(res => {
                    const { data } = res;
                    if (data.code === 200) {
                        const list = res.data.data || [];
                        if (list.length < this.size) {
                            this.hasMore = false;
                        }
                        this.gourmetList = [...this.gourmetList, ...list];
                        this.current++;
                    }
                }).catch(error => {
                    console.log(error);
                }).finally(() => {
                    this.loading = false;
                });
            }
        },
        // 查看美食详情
        readGourmet(gourmet) {
            sessionStorage.setItem('gourmetId', gourmet.id);
            this.$router.push('/gourmetDetail');
        },
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
        // 滚动加载更多
        handleScroll() {
            const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
            const windowHeight = window.innerHeight;
            const documentHeight = document.documentElement.scrollHeight;
            
            // 距离底部300px时触发加载
            if (scrollTop + windowHeight >= documentHeight - 300) {
                this.fetchGourmets();
            }
        },
        // 获取未读消息数量
        getUnreadCount() {
            if (!this.isLogin) {
                this.unreadCount = 0;
                return;
            }
            this.$axios.get('/user/unreadCount').then(res => {
                const { data } = res;
                if (data.code === 200 && data.data) {
                    this.unreadCount = data.data.total || 0;
                }
            }).catch(err => {
                console.log('获取未读消息数量失败', err);
            });
        }
    }
};
</script>

<style scoped lang="scss">
$primary: #FF6B35;
$primary-light: #FF8C5A;
$primary-dark: #E55A2B;
$bg-white: #FFFFFF;
$text-primary: #333333;
$text-secondary: #666666;
$border-color: #E5E5E5;
$search-bg: #F6F6F6;

.home-page {
    min-height: 100vh;
    background: $bg-white;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', sans-serif;
}

.sidebar {
    position: fixed;
    left: 0;
    top: 0;
    width: 260px;
    height: 100vh;
    background: linear-gradient(180deg, #fff9f5 0%, #ffffff 50%, #fff9f5 100%);
    display: flex;
    flex-direction: column;
    z-index: 100;
    padding: 20px 16px;
    border-right: 1px solid rgba(255, 107, 53, 0.1);
    box-shadow: 4px 0 20px rgba(255, 107, 53, 0.05);
    
    .logo {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 16px 0;
        margin-bottom: 8px;
    }
    
    .sidebar-nav {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 6px;
        padding: 8px 0;
        
        .nav-item {
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 14px 18px;
            border-radius: 16px;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;
            
            i {
                font-size: 22px;
                color: $text-secondary;
                transition: all 0.3s ease;
            }
            
            span {
                font-size: 15px;
                font-weight: 500;
                color: $text-secondary;
                transition: all 0.3s ease;
            }
            
            .badge {
                position: absolute;
                right: 14px;
                min-width: 20px;
                height: 20px;
                background: linear-gradient(135deg, $primary, $primary-light);
                border-radius: 10px;
                font-size: 11px;
                color: #fff;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 0 6px;
                font-weight: 600;
                box-shadow: 0 2px 8px rgba(255, 107, 53, 0.4);
                animation: pulse 2s infinite;
            }
            
            @keyframes pulse {
                0%, 100% { transform: scale(1) }
                50% { transform: scale(1.1) }
            }
            
            &::before {
                content: '';
                position: absolute;
                left: 0;
                top: 50%;
                transform: translateY(-50%);
                width: 4px;
                height: 0;
                background: linear-gradient(180deg, $primary, $primary-light);
                border-radius: 0 4px 4px 0;
                transition: height 0.3s ease;
            }
            
            &:hover {
                background: linear-gradient(90deg, rgba(255, 107, 53, 0.1), transparent);
                
                i, span { color: $primary }
                
                &::before { height: 60% }
            }
            
            &.active {
                background: linear-gradient(90deg, rgba(255, 107, 53, 0.15), rgba(255, 107, 53, 0.05));
                
                i { color: $primary }
                span { color: $primary; font-weight: 600 }
                
                &::before { height: 70% }
            }
        }
    }
    
    .user-profile {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        margin-top: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        border-radius: 16px;
        background: linear-gradient(135deg, rgba(255, 107, 53, 0.08), rgba(255, 140, 90, 0.05));
        border: 1px solid rgba(255, 107, 53, 0.15);
        
        &:hover {
            background: linear-gradient(135deg, rgba(255, 107, 53, 0.15), rgba(255, 140, 90, 0.1));
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(255, 107, 53, 0.2);
        }
        
        .profile-avatar {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid rgba(255, 107, 53, 0.3);
            transition: all 0.3s ease;
        }
        
        .profile-name {
            font-size: 15px;
            font-weight: 600;
            color: $text-primary;
        }
        
        &:hover .profile-avatar {
            border-color: $primary;
            box-shadow: 0 0 0 4px rgba(255, 107, 53, 0.15);
        }
    }
}

.main-content {
    margin-left: 260px;
    min-height: 100vh;
    padding: 20px 0;
}

.content-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0 24px 16px;
    
    .search-bar {
        width: 100%;
        max-width: 500px;
        margin-bottom: 16px;
        
        .search-box {
            display: flex;
            align-items: center;
            width: 100%;
            height: 40px;
            background: $search-bg;
            border-radius: 20px;
            padding: 0 16px;
            
            input {
                flex: 1;
                border: none;
                background: transparent;
                outline: none;
                font-size: 15px;
                color: $text-primary;
                
                &::placeholder {
                    color: #999;
                }
            }
            
            i {
                font-size: 18px;
                color: #999;
                cursor: pointer;
                margin-left: 8px;
            }
        }
    }
    
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
                color: $primary;
                background: rgba(255, 107, 53, 0.08);
            }
            
            &.active {
                color: #FFF;
                font-weight: 600;
                background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
                box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
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
                    background: $bg-white;
                    border: 1px solid $border-color;
                    font-size: 14px;
                    color: $text-secondary;
                    cursor: pointer;
                    white-space: nowrap;
                    transition: all 0.2s ease;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    
                    &:hover {
                        border-color: $primary;
                        color: $primary;
                    }
                    
                    &:active {
                        transform: scale(0.98);
                    }
                    
                    &.active {
                        background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
                        border: none;
                        color: #FFF;
                        box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
                    }
                }
            }
        }
    }
}

.note-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 16px;
    padding: 0 24px;
    max-width: 1400px;
    margin: 0 auto;
}

.note-card {
    background: $bg-white;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    }
    
    .card-cover {
        width: 100%;
        position: relative;
        aspect-ratio: 3/4;
        
        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
            border-radius: 8px;
        }
    }
    
    .card-content {
        padding: 8px 0 6px;
        
        .card-title {
            font-size: 14px;
            font-weight: 500;
            color: $text-primary;
            margin: 0 0 6px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            line-height: 1.4;
        }
        
        .card-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            
            .author-avatar {
                width: 20px;
                height: 20px;
                border-radius: 50%;
                object-fit: cover;
            }
            
            .like-info {
                display: flex;
                align-items: center;
                gap: 3px;
                
                i {
                    font-size: 14px;
                    color: $text-secondary;
                }
                
                span {
                    font-size: 12px;
                    color: $text-secondary;
                }
            }
        }
    }
}

.loading-more {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 32px;
    color: $text-secondary;
    font-size: 14px;
    
    .loading-spinner {
        width: 24px;
        height: 24px;
        border: 2px solid $border-color;
        border-top-color: $text-secondary;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
    }
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.no-more {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 32px;
    color: $text-secondary;
    font-size: 14px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 20px;
    
    .empty-icon {
        width: 100px;
        height: 100px;
        margin-bottom: 24px;
        background: linear-gradient(135deg, rgba(255, 107, 53, 0.1), rgba(255, 107, 53, 0.05));
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        
        i {
            font-size: 48px;
            color: $primary;
            opacity: 0.6;
        }
    }
    
    h4 {
        margin: 0 0 12px 0;
        font-size: 20px;
        color: $text-primary;
        font-weight: 500;
    }
    
    p {
        margin: 0;
        font-size: 14px;
        color: $text-secondary;
    }
}

.mobile-nav {
    display: none;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 56px;
    background: $bg-white;
    justify-content: space-around;
    align-items: center;
    border-top: 1px solid $border-color;
    z-index: 100;
    
    .nav-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2px;
        cursor: pointer;
        padding: 6px 16px;
        
        i {
            font-size: 22px;
            color: $text-secondary;
        }
        
        span {
            font-size: 10px;
            color: $text-secondary;
        }
        
        &.active {
            i, span {
                color: $primary;
            }
        }
        
        &.publish {
            margin-top: -16px;
            
            .publish-circle {
                width: 44px;
                height: 44px;
                background: $primary;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                
                i {
                    font-size: 24px;
                    color: #fff;
                }
            }
        }
    }
}

@media (max-width: 1280px) {
    .note-grid {
        grid-template-columns: repeat(4, 1fr);
        gap: 14px;
    }
}

@media (max-width: 1024px) {
    .sidebar {
        display: none;
    }
    
    .main-content {
        margin-left: 0;
    }
    
    .mobile-nav {
        display: flex;
    }
    
    .note-grid {
        grid-template-columns: repeat(3, 1fr);
        gap: 12px;
        padding: 0 12px;
    }
}

@media (max-width: 768px) {
    .note-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
    }
}

@media (max-width: 480px) {
    .note-grid {
        grid-template-columns: 1fr;
        gap: 16px;
    }
}
</style>
