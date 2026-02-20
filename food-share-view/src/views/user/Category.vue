<template>
    <div class="category-page">
        <header class="top-nav">
            <div class="nav-title">分类</div>
        </header>

        <div class="category-content">
            <div class="category-section" v-for="(category, index) in categories" :key="index">
                <div class="section-header">
                    <h3 class="section-title">{{ category.name }}</h3>
                    <span class="section-more" @click="expandCategory(category)">
                        {{ category.expanded ? '收起' : '展开' }}
                        <i :class="category.expanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                    </span>
                </div>
                <div class="tag-grid" :class="{ expanded: category.expanded }">
                    <div 
                        class="tag-item" 
                        v-for="(tag, tagIndex) in category.tags" 
                        :key="tagIndex"
                        @click="selectTag(category.name, tag)"
                    >
                        <div class="tag-icon">{{ tag.icon }}</div>
                        <span class="tag-name">{{ tag.name }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Category",
    data() {
        return {
            categories: [
                {
                    name: '菜系',
                    expanded: false,
                    tags: [
                        { name: '川菜', icon: '🌶️' },
                        { name: '粤菜', icon: '🥟' },
                        { name: '湘菜', icon: '🔥' },
                        { name: '鲁菜', icon: '🍖' },
                        { name: '江浙菜', icon: '🦐' },
                        { name: '西北菜', icon: '🥩' },
                        { name: '东北菜', icon: '🥘' },
                        { name: '闽南菜', icon: '🍜' },
                        { name: '云南菜', icon: '🍄' },
                        { name: '新疆菜', icon: '🍢' },
                        { name: '韩式', icon: '🥬' },
                        { name: '日式', icon: '🍣' },
                        { name: '泰式', icon: '🍛' },
                        { name: '意式', icon: '🍝' },
                        { name: '美式', icon: '🍔' }
                    ]
                },
                {
                    name: '场景',
                    expanded: false,
                    tags: [
                        { name: '早餐', icon: '🍳' },
                        { name: '午餐', icon: '🍱' },
                        { name: '晚餐', icon: '🍲' },
                        { name: '下午茶', icon: '🧁' },
                        { name: '夜宵', icon: '🌙' },
                        { name: '一人食', icon: '🥢' },
                        { name: '家庭聚餐', icon: '👨‍👩‍👧‍👦' },
                        { name: '朋友小聚', icon: '🎉' },
                        { name: '节日家宴', icon: '🎊' },
                        { name: '户外野餐', icon: '🏕️' }
                    ]
                },
                {
                    name: '食材',
                    expanded: false,
                    tags: [
                        { name: '海鲜', icon: '🦀' },
                        { name: '肉食', icon: '🥩' },
                        { name: '素食', icon: '🥬' },
                        { name: '菌菇', icon: '🍄' },
                        { name: '豆制品', icon: '🧈' },
                        { name: '根茎类', icon: '🥕' },
                        { name: '叶菜类', icon: '🥗' },
                        { name: '禽蛋类', icon: '🥚' },
                        { name: '谷物', icon: '🌾' },
                        { name: '坚果', icon: '🥜' },
                        { name: '时令水果', icon: '🍎' }
                    ]
                },
                {
                    name: '难度',
                    expanded: false,
                    tags: [
                        { name: '新手友好', icon: '🌱' },
                        { name: '简单', icon: '⭐' },
                        { name: '中等', icon: '⭐⭐' },
                        { name: '困难', icon: '⭐⭐⭐' },
                        { name: '大师级', icon: '👑' }
                    ]
                },
                {
                    name: '烹饪方式',
                    expanded: false,
                    tags: [
                        { name: '烧烤', icon: '🔥' },
                        { name: '火锅', icon: '🍲' },
                        { name: '煎炸', icon: '🍳' },
                        { name: '蒸煮', icon: '♨️' },
                        { name: '焖炖', icon: '🥘' },
                        { name: '凉拌', icon: '🥗' },
                        { name: '烘焙', icon: '🎂' },
                        { name: '爆炒', icon: '💥' },
                        { name: '煲汤', icon: '🫕' }
                    ]
                },
                {
                    name: '特色',
                    expanded: false,
                    tags: [
                        { name: '减脂轻食', icon: '🥗' },
                        { name: '下饭神器', icon: '🍚' },
                        { name: '下酒小菜', icon: '🍺' },
                        { name: '网红美食', icon: '📸' },
                        { name: '非遗美食', icon: '🏛️' },
                        { name: '复刻经典', icon: '🎬' }
                    ]
                }
            ]
        }
    },
    methods: {
        route(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        expandCategory(category) {
            category.expanded = !category.expanded;
        },
        selectTag(categoryName, tag) {
            sessionStorage.setItem('filterTag', JSON.stringify({
                group: categoryName,
                tag: tag.name
            }));
            this.$router.push('/gourmet');
        }
    }
};
</script>

<style scoped lang="scss">
.category-page {
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
    justify-content: center;
    z-index: 100;
    box-shadow: 0 1px 0 rgba(0, 0, 0, 0.05);
    
    .nav-title {
        font-size: 18px;
        font-weight: 600;
        color: #1D2129;
    }
}

.category-content {
    padding: 72px 16px 16px;
}

.category-section {
    background: #fff;
    border-radius: 16px;
    padding: 16px;
    margin-bottom: 12px;
    
    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        .section-title {
            font-size: 16px;
            font-weight: 600;
            color: #1D2129;
            margin: 0;
        }
        
        .section-more {
            font-size: 12px;
            color: #86909C;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 4px;
            
            &:hover {
                color: #FF8A00;
            }
        }
    }
    
    .tag-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 12px;
        max-height: 160px;
        overflow: hidden;
        transition: max-height 0.3s ease;
        
        &.expanded {
            max-height: 1000px;
        }
        
        .tag-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 6px;
            padding: 12px 8px;
            background: #FFF5EB;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            &:hover {
                background: #FFE8D6;
                transform: translateY(-2px);
            }
            
            .tag-icon {
                font-size: 24px;
            }
            
            .tag-name {
                font-size: 12px;
                color: #4E5969;
                text-align: center;
            }
        }
    }
}
</style>
