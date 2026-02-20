<template>
    <div class="self-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">个人中心</h1>
            <div class="header-right"></div>
        </header>
        
        <div class="profile-container">
            <div class="profile-header">
                <div class="header-bg"></div>
                <div class="profile-avatar-section">
                    <el-upload 
                        class="avatar-uploader" 
                        action="/api/food-share-sys/v1.0/file/upload" 
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                    >
                        <div class="avatar-wrapper">
                            <img v-if="userAvatar" :src="userAvatar" class="avatar-img">
                            <div v-else class="avatar-placeholder">
                                <i class="el-icon-user"></i>
                            </div>
                            <div class="avatar-overlay">
                                <i class="el-icon-camera"></i>
                                <span>更换头像</span>
                            </div>
                        </div>
                    </el-upload>
                    <h2 class="profile-name">{{ userInfo.userName }}</h2>
                    <p class="profile-account">账号：{{ userInfo.userAccount }}</p>
                </div>
            </div>
            
            <div class="profile-body">
                <div class="profile-card">
                    <div class="card-header">
                        <h3><i class="el-icon-user"></i> 个人信息</h3>
                    </div>
                    <div class="card-body">
                        <div class="form-group">
                            <label>昵称</label>
                            <div class="input-wrapper">
                                <i class="el-icon-edit"></i>
                                <input 
                                    v-model="userInfo.userName" 
                                    placeholder="请输入昵称"
                                    class="form-input"
                                />
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label>邮箱</label>
                            <div class="input-wrapper">
                                <i class="el-icon-message"></i>
                                <input 
                                    v-model="userInfo.userEmail" 
                                    placeholder="请输入邮箱"
                                    class="form-input"
                                />
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="profile-card">
                    <div class="card-header">
                        <h3><i class="el-icon-setting"></i> 账号状态</h3>
                    </div>
                    <div class="card-body">
                        <div class="status-grid">
                            <div class="status-item">
                                <div class="status-icon" :class="{ success: !userInfo.isLogin, danger: userInfo.isLogin }">
                                    <i :class="userInfo.isLogin ? 'el-icon-warning-outline' : 'el-icon-circle-check'"></i>
                                </div>
                                <div class="status-info">
                                    <span class="status-label">登录状态</span>
                                    <span class="status-value" :class="{ danger: userInfo.isLogin }">
                                        {{ userInfo.isLogin ? '已被封禁' : '正常' }}
                                    </span>
                                </div>
                            </div>
                            
                            <div class="status-item">
                                <div class="status-icon" :class="{ success: !userInfo.isWord, danger: userInfo.isWord }">
                                    <i :class="userInfo.isWord ? 'el-icon-warning-outline' : 'el-icon-circle-check'"></i>
                                </div>
                                <div class="status-info">
                                    <span class="status-label">留言状态</span>
                                    <span class="status-value" :class="{ danger: userInfo.isWord }">
                                        {{ userInfo.isWord ? '已被禁言' : '正常' }}
                                    </span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="status-tips">
                            <el-tooltip content="一经封号，不可登录，不可使用系统功能" placement="top">
                                <span class="tip-item"><i class="el-icon-info"></i> 登录状态说明</span>
                            </el-tooltip>
                            <el-tooltip content="禁言后，互动功能受限" placement="top">
                                <span class="tip-item"><i class="el-icon-info"></i> 留言状态说明</span>
                            </el-tooltip>
                        </div>
                    </div>
                </div>
                
                <div class="action-buttons">
                    <button class="btn-save" @click="postInfo">
                        <i class="el-icon-check"></i>
                        保存修改
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Self",
    data() {
        return {
            userInfo: {},
            userAvatar: '',
        }
    },
    created() {
        this.auth();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        async postInfo() {
            const { userName, userEmail } = this.userInfo;
            const userUpdateDTO = {
                userAvatar: this.userAvatar,
                userName,
                userEmail
            }
            const { data } = await this.$axios.put('/user/update', userUpdateDTO);
            this.$notify({
                position: 'bottom-right',
                duration: 1000,
                title: '编辑个人信息',
                message: data.code === 200 ? '编辑成功' : '编辑失败',
                type: data.code === 200 ? 'success' : 'error'
            });
            if (data.code === 200) {
                this.auth();
            }
        },
        handleAvatarSuccess(res, file) {
            this.$notify({
                duration: 1500,
                title: '头像上传',
                message: res.code === 200 ? '上传成功' : '上传失败',
                type: res.code === 200 ? 'success' : 'error'
            });
            if (res.code === 200) {
                this.userAvatar = res.data;
            }
        },
        async auth() {
            const { data } = await this.$axios.get('/user/auth');
            if (data.code !== 200) {
                this.$router.push('/');
            } else {
                this.userInfo = data.data;
                this.userAvatar = data.data.userAvatar;
            }
        },
    }
};
</script>

<style scoped lang="scss">
.self-page {
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

.self-page {
    min-height: calc(100vh - 118px);
    display: flex;
    justify-content: center;
    padding: 40px 0;
    
    .profile-container {
        width: 100%;
        max-width: 600px;
        
        .profile-header {
            position: relative;
            background: #fff;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
            margin-bottom: 24px;
            
            .header-bg {
                height: 120px;
                background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 50%, #667eea 100%);
            }
            
            .profile-avatar-section {
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 0 0 30px;
                margin-top: -50px;
                
                .avatar-uploader {
                    .avatar-wrapper {
                        position: relative;
                        width: 100px;
                        height: 100px;
                        border-radius: 50%;
                        overflow: hidden;
                        border: 4px solid #fff;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
                        cursor: pointer;
                        
                        .avatar-img {
                            width: 100%;
                            height: 100%;
                            object-fit: cover;
                        }
                        
                        .avatar-placeholder {
                            width: 100%;
                            height: 100%;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            
                            i {
                                font-size: 40px;
                                color: #fff;
                            }
                        }
                        
                        .avatar-overlay {
                            position: absolute;
                            top: 0;
                            left: 0;
                            right: 0;
                            bottom: 0;
                            background: rgba(0, 0, 0, 0.5);
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            opacity: 0;
                            transition: opacity 0.3s ease;
                            
                            i {
                                font-size: 20px;
                                color: #fff;
                                margin-bottom: 4px;
                            }
                            
                            span {
                                font-size: 12px;
                                color: #fff;
                            }
                        }
                        
                        &:hover .avatar-overlay {
                            opacity: 1;
                        }
                    }
                }
                
                .profile-name {
                    margin: 16px 0 8px;
                    font-size: 22px;
                    font-weight: 700;
                    color: #333;
                }
                
                .profile-account {
                    margin: 0;
                    font-size: 14px;
                    color: #999;
                }
            }
        }
        
        .profile-body {
            .profile-card {
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
                margin-bottom: 20px;
                overflow: hidden;
                
                .card-header {
                    padding: 20px 24px;
                    border-bottom: 1px solid #f0f0f0;
                    
                    h3 {
                        margin: 0;
                        font-size: 16px;
                        font-weight: 600;
                        color: #333;
                        
                        i {
                            margin-right: 8px;
                            color: #667eea;
                        }
                    }
                }
                
                .card-body {
                    padding: 24px;
                    
                    .form-group {
                        margin-bottom: 20px;
                        
                        &:last-child {
                            margin-bottom: 0;
                        }
                        
                        label {
                            display: block;
                            margin-bottom: 10px;
                            font-size: 14px;
                            font-weight: 500;
                            color: #666;
                        }
                        
                        .input-wrapper {
                            position: relative;
                            display: flex;
                            align-items: center;
                            
                            i {
                                position: absolute;
                                left: 16px;
                                color: #aaa;
                                font-size: 16px;
                            }
                            
                            .form-input {
                                width: 100%;
                                height: 48px;
                                padding: 0 16px 0 44px;
                                border: 2px solid #eee;
                                border-radius: 12px;
                                font-size: 14px;
                                background: #fafafa;
                                transition: all 0.3s ease;
                                
                                &:focus {
                                    outline: none;
                                    border-color: #ff9a56;
                                    background: #fff;
                                    box-shadow: 0 0 0 4px rgba(255, 154, 86, 0.1);
                                }
                            }
                        }
                    }
                    
                    .status-grid {
                        display: grid;
                        grid-template-columns: repeat(2, 1fr);
                        gap: 16px;
                        margin-bottom: 20px;
                        
                        .status-item {
                            display: flex;
                            align-items: center;
                            gap: 12px;
                            padding: 16px;
                            background: #f8f9fc;
                            border-radius: 12px;
                            
                            .status-icon {
                                width: 40px;
                                height: 40px;
                                border-radius: 10px;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                
                                &.success {
                                    background: rgba(56, 239, 125, 0.15);
                                    
                                    i {
                                        color: #38ef7d;
                                        font-size: 20px;
                                    }
                                }
                                
                                &.danger {
                                    background: rgba(255, 107, 107, 0.15);
                                    
                                    i {
                                        color: #ff6b6b;
                                        font-size: 20px;
                                    }
                                }
                            }
                            
                            .status-info {
                                display: flex;
                                flex-direction: column;
                                gap: 2px;
                                
                                .status-label {
                                    font-size: 13px;
                                    color: #999;
                                }
                                
                                .status-value {
                                    font-size: 14px;
                                    font-weight: 600;
                                    color: #38ef7d;
                                    
                                    &.danger {
                                        color: #ff6b6b;
                                    }
                                }
                            }
                        }
                    }
                    
                    .status-tips {
                        display: flex;
                        gap: 16px;
                        
                        .tip-item {
                            display: inline-flex;
                            align-items: center;
                            gap: 4px;
                            font-size: 12px;
                            color: #999;
                            cursor: help;
                            
                            i {
                                font-size: 14px;
                            }
                        }
                    }
                }
            }
            
            .action-buttons {
                display: flex;
                justify-content: center;
                padding-top: 10px;
                
                .btn-save {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    padding: 14px 48px;
                    background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
                    border: none;
                    border-radius: 30px;
                    color: #fff;
                    font-size: 15px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    box-shadow: 0 4px 20px rgba(255, 154, 86, 0.4);
                    
                    i {
                        font-size: 18px;
                    }
                    
                    &:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 25px rgba(255, 154, 86, 0.5);
                    }
                    
                    &:active {
                        transform: translateY(0);
                    }
                }
            }
        }
    }
}

@media (max-width: 600px) {
    .self-page {
        padding: 20px;
        
        .profile-container {
            .profile-body {
                .profile-card {
                    .card-body {
                        .status-grid {
                            grid-template-columns: 1fr;
                        }
                    }
                }
            }
        }
    }
}
</style>
