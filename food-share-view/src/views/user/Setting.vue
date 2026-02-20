<template>
    <div class="setting-page">
        <div class="page-bg"></div>
        
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">账号设置</h1>
            <div class="header-right"></div>
        </header>

        <div class="setting-content">
            <div class="profile-card">
                <div class="card-bg"></div>
                <div class="profile-main">
                    <div class="avatar-section" @click="openAvatarUpload">
                        <div class="avatar-ring">
                            <img :src="displayAvatar" class="avatar-img">
                            <div class="avatar-glow"></div>
                        </div>
                        <div class="avatar-edit-icon">
                            <i class="el-icon-camera"></i>
                        </div>
                    </div>
                    <input 
                        type="file" 
                        ref="avatarInput" 
                        accept="image/*" 
                        style="display: none" 
                        @change="handleAvatarChange"
                    >
                    
                    <div class="profile-info">
                        <h2 class="profile-name">{{ userInfo.userName || '美食爱好者' }}</h2>
                    </div>
                </div>
            </div>

            <div class="section-card">
                <div class="section-header">
                    <div class="section-icon">
                        <i class="el-icon-user"></i>
                    </div>
                    <span class="section-title">基本信息</span>
                </div>
                <div class="section-body">
                    <div class="info-item" @click="showEditDialog('userName')">
                        <div class="info-label">
                            <i class="el-icon-edit-outline"></i>
                            <span>用户名</span>
                        </div>
                        <div class="info-value">
                            <span>{{ userInfo.userName || '未设置' }}</span>
                            <i class="el-icon-arrow-right"></i>
                        </div>
                    </div>
                    <div class="info-item" @click="showEditDialog('userEmail')">
                        <div class="info-label">
                            <i class="el-icon-message"></i>
                            <span>邮箱</span>
                        </div>
                        <div class="info-value">
                            <span>{{ userInfo.userEmail || '未绑定' }}</span>
                            <i class="el-icon-arrow-right"></i>
                        </div>
                    </div>
                </div>
            </div>

            <div class="section-card">
                <div class="section-header">
                    <div class="section-icon security">
                        <i class="el-icon-lock"></i>
                    </div>
                    <span class="section-title">账号安全</span>
                </div>
                <div class="section-body">
                    <div class="info-item" @click="showPasswordDialog">
                        <div class="info-label">
                            <i class="el-icon-key"></i>
                            <span>修改密码</span>
                        </div>
                        <div class="info-value">
                            <span class="security-status">已设置</span>
                            <i class="el-icon-arrow-right"></i>
                        </div>
                    </div>
                </div>
            </div>

            <div class="action-section">
                <div class="action-btn logout-btn" @click="handleLogout">
                    <i class="el-icon-switch-button"></i>
                    <span>退出登录</span>
                </div>
            </div>
        </div>

        <el-dialog 
            :visible.sync="editDialogVisible" 
            width="85%"
            custom-class="modern-dialog"
            :close-on-click-modal="false"
            :show-close="false"
        >
            <div class="dialog-header">
                <div class="dialog-icon">
                    <i class="el-icon-edit"></i>
                </div>
                <h3>{{ editTitle }}</h3>
            </div>
            <div class="dialog-body">
                <el-input 
                    v-model="editValue" 
                    :placeholder="'请输入' + editTitle"
                    maxlength="30"
                    show-word-limit
                ></el-input>
            </div>
            <div class="dialog-footer">
                <div class="btn-cancel" @click="editDialogVisible = false">取消</div>
                <div class="btn-confirm" @click="saveEdit" :class="{ loading: saving }">
                    <span v-if="!saving">保存</span>
                    <i v-else class="el-icon-loading"></i>
                </div>
            </div>
        </el-dialog>

        <el-dialog 
            :visible.sync="passwordDialogVisible" 
            width="85%"
            custom-class="modern-dialog"
            :close-on-click-modal="false"
            :show-close="false"
        >
            <div class="dialog-header">
                <div class="dialog-icon security">
                    <i class="el-icon-lock"></i>
                </div>
                <h3>修改密码</h3>
            </div>
            <div class="dialog-body password-body">
                <div class="input-group">
                    <label>当前密码</label>
                    <el-input 
                        v-model="passwordForm.oldPwd" 
                        type="password" 
                        placeholder="请输入当前密码"
                        show-password
                    ></el-input>
                </div>
                <div class="input-group">
                    <label>新密码</label>
                    <el-input 
                        v-model="passwordForm.newPwd" 
                        type="password" 
                        placeholder="请输入新密码（6-20位）"
                        show-password
                    ></el-input>
                </div>
                <div class="input-group">
                    <label>确认密码</label>
                    <el-input 
                        v-model="passwordForm.confirmPwd" 
                        type="password" 
                        placeholder="请再次输入新密码"
                        show-password
                    ></el-input>
                </div>
            </div>
            <div class="dialog-footer">
                <div class="btn-cancel" @click="passwordDialogVisible = false">取消</div>
                <div class="btn-confirm" @click="changePassword" :class="{ loading: saving }">
                    <span v-if="!saving">确认修改</span>
                    <i v-else class="el-icon-loading"></i>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { isLoggedIn, getUserInfo, setUserInfo, logout } from '@/utils/storage.js';
import md5 from 'js-md5';

const DEFAULT_AVATAR = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+';

export default {
    name: "Setting",
    data() {
        return {
            userInfo: {
                userName: '',
                userAvatar: '',
                userEmail: ''
            },
            editDialogVisible: false,
            editField: '',
            editValue: '',
            editTitle: '',
            passwordDialogVisible: false,
            passwordForm: {
                oldPwd: '',
                newPwd: '',
                confirmPwd: ''
            },
            saving: false
        }
    },
    computed: {
        isLogin() {
            return isLoggedIn();
        },
        displayAvatar() {
            return this.userInfo.userAvatar || DEFAULT_AVATAR;
        }
    },
    mounted() {
        if (!this.isLogin) {
            this.$router.push('/login');
            return;
        }
        this.loadUserInfo();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        loadUserInfo() {
            const { userName, userAvatar } = getUserInfo();
            this.userInfo.userName = userName || '';
            this.userInfo.userAvatar = userAvatar || '';
            this.fetchUserInfo();
        },
        async fetchUserInfo() {
            try {
                const res = await this.$axios.get('/user/auth');
                const { data } = res;
                if (data.code === 200 && data.data) {
                    this.userInfo.userName = data.data.userName || '';
                    this.userInfo.userAvatar = data.data.userAvatar || '';
                    this.userInfo.userEmail = data.data.userEmail || '';
                }
            } catch (error) {
                console.log('获取用户信息失败', error);
            }
        },
        openAvatarUpload() {
            this.$refs.avatarInput.click();
        },
        async handleAvatarChange(event) {
            const file = event.target.files[0];
            if (!file) return;
            
            if (!file.type.startsWith('image/')) {
                this.$message.error('请选择图片文件');
                return;
            }
            
            if (file.size > 5 * 1024 * 1024) {
                this.$message.error('图片大小不能超过5MB');
                return;
            }
            
            this.saving = true;
            
            try {
                const formData = new FormData();
                formData.append('file', file);
                
                const res = await this.$axios.post('/file/upload', formData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
                
                if (res.data && res.data.code === 200 && res.data.data) {
                    const avatarUrl = res.data.data;
                    await this.updateUserInfo({ userAvatar: avatarUrl });
                    this.userInfo.userAvatar = avatarUrl;
                    setUserInfo(this.userInfo.userName, avatarUrl, null);
                } else {
                    this.$message.error('上传头像失败');
                }
            } catch (error) {
                console.log('上传头像失败', error);
                this.$message.error('上传头像失败');
            } finally {
                this.saving = false;
                event.target.value = '';
            }
        },
        showEditDialog(field) {
            this.editField = field;
            const titles = {
                userName: '用户名',
                userEmail: '邮箱'
            };
            this.editTitle = titles[field];
            this.editValue = this.userInfo[field] || '';
            this.editDialogVisible = true;
        },
        async saveEdit() {
            if (!this.editValue.trim()) {
                this.$message.warning('内容不能为空');
                return;
            }
            
            if (this.editField === 'userEmail') {
                const emailReg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailReg.test(this.editValue)) {
                    this.$message.warning('请输入正确的邮箱格式');
                    return;
                }
            }
            
            this.saving = true;
            
            try {
                const updateData = {};
                updateData[this.editField] = this.editValue.trim();
                await this.updateUserInfo(updateData);
                this.userInfo[this.editField] = this.editValue.trim();
                
                if (this.editField === 'userName') {
                    setUserInfo(this.editValue.trim(), this.userInfo.userAvatar, null);
                }
                
                this.editDialogVisible = false;
                this.$message.success('修改成功');
            } catch (error) {
                console.log('修改失败', error);
            } finally {
                this.saving = false;
            }
        },
        async updateUserInfo(data) {
            try {
                const res = await this.$axios.put('/user/update', data);
                const { data: resData } = res;
                if (resData.code !== 200) {
                    const msg = (resData && resData.msg) || '修改失败';
                    this.$message.error(msg);
                    throw new Error(msg);
                }
            } catch (error) {
                throw error;
            }
        },
        showPasswordDialog() {
            this.passwordForm = {
                oldPwd: '',
                newPwd: '',
                confirmPwd: ''
            };
            this.passwordDialogVisible = true;
        },
        async changePassword() {
            const { oldPwd, newPwd, confirmPwd } = this.passwordForm;
            
            if (!oldPwd) {
                this.$message.warning('请输入当前密码');
                return;
            }
            
            if (!newPwd || newPwd.length < 6 || newPwd.length > 20) {
                this.$message.warning('新密码长度需为6-20位');
                return;
            }
            
            if (newPwd !== confirmPwd) {
                this.$message.warning('两次输入的密码不一致');
                return;
            }
            
            this.saving = true;
            
            try {
                const hashedOldPwd = md5(md5(oldPwd));
                const hashedNewPwd = md5(md5(newPwd));
                const res = await this.$axios.put('/user/updatePwd', {
                    oldPwd: hashedOldPwd,
                    newPwd: hashedNewPwd,
                    againPwd: hashedNewPwd
                });
                
                const { data } = res;
                if (data.code === 200) {
                    this.$message.success('密码修改成功，请重新登录');
                    this.passwordDialogVisible = false;
                    setTimeout(() => {
                        logout();
                        this.$router.push('/login');
                    }, 1500);
                } else {
                    const msg = (data && data.msg) || '修改失败';
                    this.$message.error(msg);
                }
            } catch (error) {
                console.log('修改密码失败', error);
                this.$message.error('修改密码失败');
            } finally {
                this.saving = false;
            }
        },
        handleLogout() {
            this.$confirm('确定要退出登录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                logout();
                this.$router.push('/login');
            }).catch(() => {});
        }
    }
};
</script>

<style scoped lang="scss">
$primary-color: #FF6B35;
$primary-gradient: linear-gradient(135deg, #ff9a56 0%, #FF6B35 100%);
$text-primary: #333;
$text-secondary: #666;
$text-light: #999;
$bg-primary: #f8f8f8;
$bg-card: #fff;
$border-color: #f0f0f0;

.setting-page {
    min-height: 100vh;
    background: $bg-primary;
    position: relative;
    
    .page-bg {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 180px;
        background: $primary-gradient;
        border-radius: 0 0 30px 30px;
    }
}

.page-header {
    position: relative;
    z-index: 10;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    padding-top: 20px;
    
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
            color: #fff;
        }
    }
    
    .header-title {
        font-size: 18px;
        font-weight: 600;
        color: #fff;
        margin: 0;
    }
}

.setting-content {
    position: relative;
    z-index: 10;
    padding: 0 16px;
}

.profile-card {
    position: relative;
    background: $bg-card;
    border-radius: 20px;
    padding: 24px;
    margin-bottom: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    
    .card-bg {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 60px;
        background: $primary-gradient;
        opacity: 0.1;
    }
    
    .profile-main {
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
    }
    
    .avatar-section {
        position: relative;
        cursor: pointer;
        margin-bottom: 16px;
        
        .avatar-ring {
            position: relative;
            width: 88px;
            height: 88px;
            border-radius: 50%;
            padding: 3px;
            background: $primary-gradient;
            
            .avatar-img {
                width: 100%;
                height: 100%;
                border-radius: 50%;
                object-fit: cover;
                border: 3px solid #fff;
            }
            
            .avatar-glow {
                position: absolute;
                top: -4px;
                left: -4px;
                right: -4px;
                bottom: -4px;
                border-radius: 50%;
                background: $primary-gradient;
                opacity: 0.3;
                filter: blur(8px);
                z-index: -1;
            }
        }
        
        .avatar-edit-icon {
            position: absolute;
            bottom: 0;
            right: 0;
            width: 28px;
            height: 28px;
            background: $primary-gradient;
            border-radius: 50%;
            border: 3px solid #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            
            i {
                font-size: 12px;
                color: #fff;
            }
        }
    }
    
    .profile-info {
        text-align: center;
        
        .profile-name {
            font-size: 20px;
            font-weight: 600;
            color: $text-primary;
            margin: 0;
        }
    }
}

.section-card {
    background: $bg-card;
    border-radius: 16px;
    margin-bottom: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    
    .section-header {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 16px;
        border-bottom: 1px solid $border-color;
        
        .section-icon {
            width: 32px;
            height: 32px;
            background: linear-gradient(135deg, #FFF5EB 0%, #FFE8D6 100%);
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            
            i {
                font-size: 16px;
                color: $primary-color;
            }
            
            &.security {
                background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
                
                i {
                    color: #4CAF50;
                }
            }
        }
        
        .section-title {
            font-size: 15px;
            font-weight: 600;
            color: $text-primary;
        }
    }
    
    .section-body {
        padding: 8px 0;
    }
}

.info-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 16px;
    cursor: pointer;
    transition: background 0.3s ease;
    
    &:hover {
        background: rgba(255, 36, 66, 0.04);
    }
    
    .info-label {
        display: flex;
        align-items: center;
        gap: 12px;
        
        i {
            font-size: 18px;
            color: $text-light;
        }
        
        span {
            font-size: 15px;
            color: $text-primary;
        }
    }
    
    .info-value {
        display: flex;
        align-items: center;
        gap: 8px;
        
        span {
            font-size: 14px;
            color: $text-light;
            max-width: 140px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            
            &.security-status {
                color: #4CAF50;
            }
        }
        
        i {
            font-size: 14px;
            color: $text-light;
        }
    }
}

.action-section {
    margin-top: 24px;
    
    .logout-btn {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 14px;
        background: $bg-card;
        border-radius: 16px;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
        
        i {
            font-size: 18px;
            color: #ff4d4f;
        }
        
        span {
            font-size: 15px;
            color: #ff4d4f;
        }
        
        &:hover {
            background: rgba(255, 77, 79, 0.08);
        }
    }
}
</style>

<style lang="scss">
.modern-dialog {
    border-radius: 20px !important;
    overflow: hidden;
    
    .dialog-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 24px 24px 16px;
        
        .dialog-icon {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, #FFF5EB 0%, #FFE8D6 100%);
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 12px;
            
            i {
                font-size: 22px;
                color: #FF6B35;
            }
            
            &.security {
                background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
                
                i {
                    color: #4CAF50;
                }
            }
        }
        
        h3 {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin: 0;
        }
    }
    
    .dialog-body {
        padding: 0 24px 16px;
        
        &.password-body {
            .input-group {
                margin-bottom: 16px;
                
                &:last-child {
                    margin-bottom: 0;
                }
                
                label {
                    display: block;
                    font-size: 13px;
                    color: #666;
                    margin-bottom: 8px;
                }
            }
        }
        
        .el-input__inner {
            border-radius: 12px;
            border: 1px solid #f0f0f0;
            padding: 12px 16px;
            
            &:focus {
                border-color: #FF6B35;
            }
        }
    }
    
    .dialog-footer {
        display: flex;
        gap: 12px;
        padding: 16px 24px 24px;
        
        .btn-cancel, .btn-confirm {
            flex: 1;
            padding: 14px;
            border-radius: 12px;
            text-align: center;
            font-size: 15px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .btn-cancel {
            background: #f5f5f5;
            color: #666;
            
            &:hover {
                background: #eee;
            }
        }
        
        .btn-confirm {
            background: linear-gradient(135deg, #ff9a56 0%, #FF6B35 100%);
            color: #fff;
            
            &:hover {
                opacity: 0.9;
            }
            
            &.loading {
                opacity: 0.7;
                pointer-events: none;
            }
        }
    }
}
</style>
