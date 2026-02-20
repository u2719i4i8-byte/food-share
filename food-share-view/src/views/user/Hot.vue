<template>
    <div class="hot-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">热度统计</h1>
            <div class="header-right"></div>
        </header>
        
        <div class="hot-content">
            <div class="item-type">
            <span :style="{
                backgroundColor: item.id === typeItem.id ? 'rgb(91, 142, 248)' : '',
                color: item.id === typeItem.id ? 'rgb(245,245,245)' : ''
            }" @click="typeSelected(typeItem)" v-for="(typeItem, index) in interactionTypes" :key="index">{{
                typeItem.name
            }}</span>
        </div>
        <LineChart @on-selected="onSelected" :tag="tag" height="500px" :values="values" :date="dates" />
        </div>
    </div>
</template>
<script>
import LineChart from "@/components/LineChart"
export default {
    components: { LineChart },
    name: "Hot",
    data() {
        return {
            interactionQueryDto: {
                day: 365, // 默认查一年
            },
            values: [],
            dates: [],
            interactionTypes: [],
            item: {},
            tag: '流量状况'
        }
    },
    created() {
        this.fetchInteractionTypes();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        changeTag(type) {
            if (type.id === 1) {
                this.tag = '点赞量';
            } else if (type.id === 2) {
                this.tag = '收藏人数';
            } else if (type.id === 3) {
                this.tag = '阅读量';
            } else if (type.id === 4) {
                this.tag = '评分人数';
            }
        },
        typeSelected(typeItem) {
            this.changeTag(typeItem);
            this.item = typeItem;
            this.interactionQueryDto.type = typeItem.id;
            this.fetchHotData();
        },
        fetchInteractionTypes() {
            this.$axios.get('/interaction/types').then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.interactionTypes = data.data;
                    this.typeSelected(this.interactionTypes[0]);
                }
            }).catch(error => {
                console.log("查询互动类型异常", error);
            });
        },
        onSelected(day) {
            this.interactionQueryDto.day = day;
            this.fetchHotData();
        },
        fetchHotData() {
            this.$axios.post('/interaction/daysQuery', this.interactionQueryDto).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    const datas = data.data;
                    this.values = datas.map(entity => entity.count);
                    this.dates = datas.map(entity => entity.name);
                }
            }).catch(error => {
                console.log("查询流量状况数据异常", error);

            });
        }
    }
};
</script>
<style scoped lang="scss">
.hot-page {
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

.hot-content {
    padding: 16px;
}

.item-type {
    display: flex;
    justify-content: left;
    align-items: center;
    background-color: rgb(246, 246, 246);
    gap: 16px;
    padding: 18px 10px;

    span {
        cursor: pointer;
        font-size: 12px;
        display: inline-block;
        padding: 4px 20px;
        border-radius: 15px;
        color: rgb(51, 51, 51);
    }
}
</style>
