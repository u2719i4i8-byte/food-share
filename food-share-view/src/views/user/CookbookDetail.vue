<template>
    <div class="cookbook-detail-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">食谱详情</h1>
            <div class="header-right"></div>
        </header>
        
        <div class="detail-content">
        <div style="position: sticky;top: 0;z-index: 1000;background-color: rgb(255,255,255);">
            <div style="font-size: 40px;font-weight: bold;padding-block: 10px;">
                {{ cookbook.title }}
            </div>
            <div style="font-size: 14px;border-bottom: 1px solid rgb(246,246,246);padding-block: 6px;">
                <span style="margin-right: 15px;">所属菜系：{{ cookbook.categoryName }}</span>
                <span>收录于：{{ cookbook.createTime }}</span>
            </div>
        </div>

        <div>
            <div v-html="cookbook.content"></div>
        </div>
        <div style="border-top: 1px solid rgb(245,245,245);">
            <Evaluations :contentId="cookbook.id" contentType="COOKBOOK" />
        </div>
        </div>
    </div>
</template>
<script>
import Evaluations from "@/components/Evaluations"
export default {
    components: { Evaluations },
    name: "CookbookDetail",
    data() {
        return {
            cookbook: {}
        }
    },
    created() {
        this.loadCookbookDetail();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        loadCookbookDetail() {
            const jsonCookbook = sessionStorage.getItem('cookbookInfo');
            this.cookbook = JSON.parse(jsonCookbook);
        },
    }
};
</script>
<style scoped lang="scss">
.cookbook-detail-page {
    min-height: 100vh;
    background: #f5f5f5;
}

.page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    background: #fff;
    border-bottom: 1px solid #eee;
    
    .header-left, .header-right {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .header-left {
        cursor: pointer;
        
        i {
            font-size: 22px;
            color: #333;
        }
    }
    
    .header-title {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin: 0;
    }
}

.detail-content {
    margin: 0 auto;
    max-width: 1000px;
    background-color: rgb(255,255,255);
    border-radius: 2px;
    padding: 40px;
}
</style>
