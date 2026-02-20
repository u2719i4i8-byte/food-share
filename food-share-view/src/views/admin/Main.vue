<template>
    <div class="admin-layout">
        <aside class="sidebar" :class="{ collapsed: isCollapsed }">
            <div class="sidebar-header">
                <Logo sysName="美食后台" :flag="isCollapsed" :bag="colorLogo" />
            </div>
            <nav class="sidebar-nav">
                <div 
                    class="nav-item"
                    :class="{ active: currentRoute === route.path }"
                    v-for="(route, index) in adminRoutes"
                    :key="index"
                    @click="handleRouteSelect(route.path)"
                >
                    <i :class="getRouteIcon(route.path)"></i>
                    <span v-if="!isCollapsed" class="nav-text">{{ route.name }}</span>
                </div>
            </nav>
        </aside>
        
        <main class="main-content">
            <header class="top-header">
                <div class="header-left">
                    <button class="collapse-btn" @click="toggleSidebar">
                        <i :class="isCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
                    </button>
                    <div class="breadcrumb">
                        <span class="current-tag">{{ tag }}</span>
                    </div>
                </div>
                <div class="header-right">
                    <el-dropdown @command="handleCommand">
                        <div class="user-info">
                            <el-avatar :size="36" :src="userInfo.url"></el-avatar>
                            <span class="user-name">{{ userInfo.name }}</span>
                            <i class="el-icon-arrow-down"></i>
                        </div>
                        <el-dropdown-menu slot="dropdown" class="user-dropdown">
                            <el-dropdown-item command="center">
                                <i class="el-icon-user"></i> 个人中心
                            </el-dropdown-item>
                            <el-dropdown-item command="switchAccount">
                                <i class="el-icon-switch"></i> 切换账户
                            </el-dropdown-item>
                            <el-dropdown-item command="loginOut" divided>
                                <i class="el-icon-switch-button"></i> 退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>
            </header>
            
            <div class="content-wrapper">
                <router-view></router-view>
            </div>
        </main>
        
        <el-dialog 
            :visible.sync="dialogOperaion" 
            width="480px"
            custom-class="profile-dialog"
            title="个人中心"
        >
            <div class="profile-form">
                <div class="form-item avatar-item">
                    <label>头像</label>
                    <el-upload 
                        class="avatar-uploader"
                        action="/api/food-share-sys/v1.0/file/upload" 
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                    >
                        <img v-if="userInfo.url" :src="userInfo.url" class="avatar-preview">
                        <div v-else class="avatar-placeholder">
                            <i class="el-icon-plus"></i>
                        </div>
                    </el-upload>
                </div>
                <div class="form-item">
                    <label>用户名</label>
                    <input v-model="userInfo.name" placeholder="请输入用户名">
                </div>
                <div class="form-item">
                    <label>邮箱</label>
                    <input v-model="userInfo.email" placeholder="请输入邮箱">
                </div>
            </div>
            <div slot="footer" class="dialog-footer">
                <button class="btn-cancel" @click="dialogOperaion = false">取消</button>
                <button class="btn-confirm" @click="updateUserInfo">确定修改</button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request.js";
import router from "@/router/index";
import { clearToken } from "@/utils/storage"
import Logo from '@/components/Logo.vue';

export default {
    name: "Admin",
    components: { Logo },
    data() {
        return {
            adminRoutes: [],
            userInfo: {
                id: null,
                url: '',
                name: '',
                role: null,
                email: ''
            },
            isCollapsed: false,
            tag: '可视化',
            colorLogo: '#fff',
            dialogOperaion: false
        };
    },
    computed: {
        currentRoute() {
            return this.$route.path;
        }
    },
    created() {
        let menus = router.options.routes.filter(route => route.path == '/admin')[0];
        this.adminRoutes = menus.children;
        this.tokenCheckLoad();
        if (this.$route.path === '/admin' || this.$route.path === '/admin/') {
            this.$router.push('/admin/home');
        }
    },
    methods: {
        getRouteIcon(path) {
            const icons = {
                '/admin/home': 'el-icon-data-line',
                '/admin/userManage': 'el-icon-user',
                '/admin/categoryManage': 'el-icon-menu',
                '/admin/gourmetManage': 'el-icon-food',
                '/admin/cookbookManage': 'el-icon-notebook-2',
                '/admin/nutrimentManage': 'el-icon-coin',
                '/admin/dietHistoryManage': 'el-icon-date',
                '/admin/evaluationsManage': 'el-icon-star-on'
            };
            return icons[path] || 'el-icon-document';
        },
        toggleSidebar() {
            this.isCollapsed = !this.isCollapsed;
        },
        handleCommand(command) {
            if (command === 'center') {
                this.dialogOperaion = true;
            } else if (command === 'switchAccount') {
                this.switchAccount();
            } else if (command === 'loginOut') {
                this.loginOut();
            }
        },
        async switchAccount() {
            const confirmed = await this.$swalConfirm({
                title: '切换账户',
                text: '切换后需要重新登录',
                icon: 'question',
            });
            if (confirmed) {
                clearToken();
                sessionStorage.clear();
                this.$router.push('/login');
            }
        },
        async updateUserInfo() {
            try {
                const userUpdateDTO = {
                    userAvatar: this.userInfo.url,
                    userName: this.userInfo.name,
                    userEmail: this.userInfo.email
                }
                const resposne = await this.$axios.put(`/user/update`, userUpdateDTO);
                const { data } = resposne;
                if (data.code === 200) {
                    this.dialogOperaion = false;
                    this.tokenCheckLoad();
                    this.$swal.fire({
                        title: '修改成功',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    });
                }
            } catch (e) {
                this.dialogOperaion = false;
                this.$swal.fire({
                    title: '修改失败',
                    text: e,
                    icon: 'error',
                    showConfirmButton: false,
                    timer: 2000,
                });
            }
        },
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error(`头像上传异常`);
                return;
            }
            this.$message.success(`头像上传成功`);
            this.userInfo.url = res.data;
        },
        async loginOut() {
            const confirmed = await this.$swalConfirm({
                title: '退出登录？',
                text: '退出后需重新登录',
                icon: 'warning',
            });
            if (confirmed) {
                this.$swal.fire({
                    title: '退出成功',
                    text: '即将返回登录页面',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: 1000,
                });
                setTimeout(() => {
                    clearToken();
                    this.$router.push("/login");
                }, 1000)
            }
        },
        handleRouteSelect(path) {
            let ary = this.adminRoutes.filter(entity => entity.path == path);
            this.tag = ary[0].name;
            if (this.$router.currentRoute.fullPath == path) {
                return;
            }
            this.$router.push(path);
        },
        async tokenCheckLoad() {
            try {
                const res = await request.get('user/auth');
                if (res.data.code === 400) {
                    this.$message.error(res.data.msg);
                    this.$router.push('/login');
                    return;
                }
                const { id, userAvatar: url, userName: name, userRole: role, userEmail: email } = res.data.data;
                this.userInfo = { id, url, name, role, email };
            } catch (error) {
                console.error('获取用户认证信息时发生错误:', error);
                this.$message.error('认证信息加载失败，请重试！');
            }
        },
    }
};
</script>

<style scoped lang="scss">
.admin-layout {
    display: flex;
    min-height: 100vh;
    background: #f0f2f8;
    
    .sidebar {
        width: 240px;
        min-height: 100vh;
        background: linear-gradient(180deg, #FF6B35 0%, #f7931e 100%);
        transition: width 0.3s ease;
        position: fixed;
        left: 0;
        top: 0;
        z-index: 100;
        
        &.collapsed {
            width: 80px;
            
            .nav-item {
                justify-content: center;
                padding: 0;
                
                i {
                    margin-right: 0;
                }
            }
        }
        
        .sidebar-header {
            padding: 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        .sidebar-nav {
            padding: 16px 12px;
            
            .nav-item {
                display: flex;
                align-items: center;
                padding: 14px 16px;
                margin-bottom: 4px;
                border-radius: 10px;
                cursor: pointer;
                color: rgba(255, 255, 255, 0.7);
                transition: all 0.3s ease;
                
                i {
                    font-size: 18px;
                    margin-right: 12px;
                }
                
                .nav-text {
                    font-size: 14px;
                    font-weight: 500;
                }
                
                &:hover {
                    background: rgba(255, 255, 255, 0.1);
                    color: #fff;
                }
                
                &.active {
                    background: rgba(255, 255, 255, 0.2);
                    color: #fff;
                }
            }
        }
    }
    
    .main-content {
        flex: 1;
        margin-left: 240px;
        transition: margin-left 0.3s ease;
        
        .top-header {
            position: sticky;
            top: 0;
            z-index: 50;
            display: flex;
            justify-content: space-between;
            align-items: center;
            height: 64px;
            padding: 0 24px;
            background: #fff;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
            
            .header-left {
                display: flex;
                align-items: center;
                gap: 16px;
                
                .collapse-btn {
                    width: 36px;
                    height: 36px;
                    border: none;
                    border-radius: 8px;
                    background: #f5f6f7;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    
                    i {
                        font-size: 18px;
                        color: #666;
                    }
                    
                    &:hover {
                        background: #e8e9eb;
                    }
                }
                
                .breadcrumb {
                    .current-tag {
                        font-size: 16px;
                        font-weight: 600;
                        color: #333;
                    }
                }
            }
            
            .header-right {
                .user-info {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    padding: 6px 12px 6px 6px;
                    border-radius: 25px;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    
                    &:hover {
                        background: #f5f6f7;
                    }
                    
                    .user-name {
                        font-size: 14px;
                        font-weight: 500;
                        color: #333;
                    }
                    
                    .el-icon-arrow-down {
                        font-size: 12px;
                        color: #999;
                    }
                }
            }
        }
        
        .content-wrapper {
            padding: 24px;
            min-height: calc(100vh - 64px);
        }
    }
}

.user-dropdown {
    margin-top: 8px;
    border-radius: 12px;
    border: none;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    padding: 8px;
    
    .el-dropdown-item {
        border-radius: 8px;
        padding: 10px 16px;
        
        i {
            margin-right: 8px;
            color: #FF6B35;
        }
        
        &:hover {
            background: rgba(255, 107, 53, 0.1);
            color: #FF6B35;
        }
    }
}

.profile-dialog {
    border-radius: 16px;
    
    .profile-form {
        padding: 20px 0;
        
        .form-item {
            margin-bottom: 20px;
            
            label {
                display: block;
                margin-bottom: 8px;
                font-size: 14px;
                font-weight: 500;
                color: #666;
            }
            
            input {
                width: 100%;
                height: 44px;
                padding: 0 16px;
                border: 2px solid #eee;
                border-radius: 10px;
                font-size: 14px;
                transition: all 0.3s ease;
                
                &:focus {
                    outline: none;
                    border-color: #FF6B35;
                }
            }
            
            &.avatar-item {
                .avatar-uploader {
                    .avatar-preview {
                        width: 80px;
                        height: 80px;
                        border-radius: 50%;
                        object-fit: cover;
                    }
                    
                    .avatar-placeholder {
                        width: 80px;
                        height: 80px;
                        border-radius: 50%;
                        background: #f5f6f7;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        border: 2px dashed #ddd;
                        
                        i {
                            font-size: 24px;
                            color: #999;
                        }
                    }
                }
            }
        }
    }
    
    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        
        .btn-cancel, .btn-confirm {
            padding: 10px 24px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .btn-cancel {
            background: #f5f6f7;
            border: none;
            color: #666;
            
            &:hover {
                background: #e8e9eb;
            }
        }
        
        .btn-confirm {
            background: linear-gradient(135deg, #FF6B35 0%, #f7931e 100%);
            border: none;
            color: #fff;
            
            &:hover {
                transform: translateY(-1px);
            }
        }
    }
}
</style>
