<template>
    <div class="cookbook-operation-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">食谱管理</h1>
            <div class="header-right"></div>
        </header>
        
        <el-tabs type="border-card">
        <el-tab-pane label="我的食谱">
            <MyCookbook />
        </el-tab-pane>
        <el-tab-pane label="我的营养素">
            <MyNutrimentManage />
        </el-tab-pane>
        <el-tab-pane label="食谱配置">
            <MyCookbookNutrimentManage/>
        </el-tab-pane>
    </el-tabs>
    </div>
</template>
<script>
import MyCookbook from "@/views/user/MyCookbook"
import MyNutrimentManage from "@/views/user/MyNutrimentManage"
import MyCookbookNutrimentManage from "@/views/user/MyCookbookNutrimentManage"
export default {
    components: { MyCookbook, MyNutrimentManage, MyCookbookNutrimentManage },
    name: "CookbookOperation",
    data() {
        return {

        }
    },
    created() {

    },
    methods: {
        goBack() {
            this.$router.back();
        },

    }
};
</script>
<style scoped lang="scss">
.cookbook-operation-page {
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
</style>
