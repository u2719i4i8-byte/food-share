<template>
    <div class="reset-pwd-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">重置密码</h1>
            <div class="header-right"></div>
        </header>
        
        <div class="container">
            <div>
                <p>*原始密码</p>
                <input style="line-height: 40px;" class="dialog-input" type="password" v-model="oldPwd" placeholder="请输入" />
            </div>
            <div>
                <p>*新密码</p>
                <input style="line-height: 40px;" class="dialog-input" type="password" v-model="newPwd" placeholder="请输入" />
            </div>
            <div>
                <p>*确认密码</p>
                <input style="line-height: 40px;" class="dialog-input" type="password" v-model="againPwd" placeholder="请输入" />
            </div>
            <div>
                <div style="margin-top: 20px;text-align: center;">
                    <el-button type="primary" class="customer" size="mini" @click="postInfo" round>立即修改</el-button>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { clearToken } from "@/utils/storage"
export default {
    name: "ResetPwd",
    data() {
        return {
            oldPwd: '',
            newPwd: '',
            againPwd: ''
        }
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        async postInfo() {
            const userUpdatePwdDTO = {
                oldPwd: this.$md5(this.$md5(this.oldPwd)),
                newPwd: this.$md5(this.$md5(this.newPwd)),
                againPwd: this.$md5(this.$md5(this.againPwd))
            }
            const { data } = await this.$axios.put('/user/updatePwd', userUpdatePwdDTO);
            this.$notify({
                duration: 1000,
                title: '修改密码',
                message: data.code === 200 ? '修改成功，请重新登录' : data.msg,
                type: data.code === 200 ? 'success' : 'error'
            });
            if (data.code === 200) {
                clearToken();
                this.$router.push('/');
            }
        },
    }
};
</script>
<style scoped lang="scss">
.reset-pwd-page {
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
    width: 500px;
    margin: 0 auto;
    padding: 20px;
}
</style>
