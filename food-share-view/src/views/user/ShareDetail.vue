<template>
    <div class="share-detail-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">分享详情</h1>
            <div class="header-right"></div>
        </header>
        
        <div class="container">
            <div v-if="authStatus">
                <div class="auth">
                    <p style="font-size: 22px;">认证信息</p>
                    <div>
                        <input class="dialog-input" style="height: 60px;padding-inline: 10px;" type="password" v-model="contentNetQueryDto.accessPassword"
                            placeholder="密码" />
                    </div>
                    <div style="margin-block: 20px;display: flex;justify-content: center;align-items: center;">
                        <span class="edit-button" style="width: 90%;text-align: center;line-height: 40px;height: 40px;" @click="addOperation()">
                            确定
                        </span>
                    </div>
                </div>
            </div>
            <div v-else
                style="box-shadow: 0 0 10px rgb(240,240,240);padding: 30px;width: 800px;margin: 0 auto;background-color: rgb(255,255,255);min-height: 100vh;">
                <div style="font-size: 30px;font-weight: bold;">
                    {{ gourmetInfo[0].title }}
                </div>
                <div style="margin-block: 10px;font-size: 14px;">
                    <span style="margin-right: 8px;">{{ gourmetInfo[0].categoryName }}</span>
                    <span style="margin-right: 8px;">由{{ gourmetInfo[0].userName }}创作</span>
                    <span style="margin-right: 8px;">{{ gourmetInfo[0].viewCount }}人观看</span>
                    <span style="margin-right: 8px;">{{ gourmetInfo[0].saveCount }}人收藏</span>
                    <span>{{ gourmetInfo[0].upvoteCount }}人点赞</span>
                </div>
                <div style="border-top: 1px solid rgb(245,245,245);">
                    <div v-html="gourmetInfo[0].content"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "ShareDetail",
    data() {
        return {
            accessCode: null,
            authStatus: false,
            contentNetQueryDto: {accessCode:''},
            gourmetInfo: []
        }
    },
    created() {
        this.accessCode = this.$route.query.accessCode;
        this.loadAuthStatus(this.accessCode);
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        addOperation() {
            if(this.contentNetQueryDto.accessCode=== ''){
                this.$notify({
                        duration: 2000,
                        title: '文档操作',
                        message: '请填写密码',
                        type: 'warning'
                    });
            }
            this.contentNetQueryDto.accessCode = this.accessCode;
            this.contentNetQueryDto.accessPassword = this.$md5(this.contentNetQueryDto.accessPassword);
            this.$axios.post('/contentNet/findContent', this.contentNetQueryDto).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.contentNetQueryDto = {};
                    this.gourmetInfo = data.data;
                    this.authStatus = false;
                } else {
                    this.contentNetQueryDto.accessPassword = '';
                    this.$notify({
                        duration: 2000,
                        title: '文档操作',
                        message: data.msg,
                        type: 'warning'
                    });
                }
            }).catch(error => {
                console.log("查询帖子信息异常", error);
            });
        },
        loadAuthStatus(accessCode) {
            this.$axios.post('/contentNet/authStatus', { accessCode }).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.authStatus = data.data;
                    if (!this.authStatus) {
                        this.loadGourmetInfo(accessCode);
                    }
                }
            }).catch(error => {
                console.log("查询帖子认证状态异常", error);
            });
        },
        loadGourmetInfo(accessCode) {
            this.$axios.post('/contentNet/findContent', { accessCode }).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.contentNetQueryDto = {};
                    this.gourmetInfo = data.data;
                } else {
                    this.$notify({
                        duration: 2000,
                        title: '文档操作',
                        message: data.msg,
                        type: 'warning'
                    });
                }
            }).catch(error => {
                console.log("查询帖子信息异常", error);
            });
        },
    }
};
</script>

<style scoped lang="scss">
.share-detail-page {
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

.container {
    width: 100%;
    min-height: 100vh;
    background-color: rgb(249, 250, 251);
    display: flex;
    justify-content: center;
    align-items: center;
    .auth{
        background-color: rgb(255,255,255);
        padding: 30px;
        width: 230px;
        border-radius: 10px;
        box-shadow: 0 0 10px rgb(240,240,240);
    }
}
</style>
