<template>
    <div class="login-container">
        <div class="bg-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
            <div class="shape shape-4"></div>
        </div>
        
        <div class="login-card">
            <div class="card-left">
                <div class="welcome-content">
                    <div class="logo-wrapper">
                        <Logo :bag="colorLogo" bag="#fff" sysName="橘子美食网" />
                    </div>
                    <div class="welcome-text">
                        <h1>欢迎回来</h1>
                        <p>探索美食，分享生活</p>
                    </div>
                    <div class="feature-list">
                        <div class="feature-item">
                            <i class="el-icon-food"></i>
                            <span>发现美味食谱</span>
                        </div>
                        <div class="feature-item">
                            <i class="el-icon-share"></i>
                            <span>分享烹饪心得</span>
                        </div>
                        <div class="feature-item">
                            <i class="el-icon-star-on"></i>
                            <span>收藏喜欢的美食</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="card-right">
                <div class="form-header">
                    <h2>账号登录</h2>
                    <p>登录您的账号，开启美食之旅</p>
                </div>
                
                <div class="form-body">
                    <div class="input-group" :class="{ 'has-value': act }">
                        <i class="el-icon-user"></i>
                        <input 
                            v-model="act" 
                            type="text" 
                            @focus="focusInput('act')"
                            @blur="blurInput('act')"
                        />
                        <label :class="{ 'active': act || focusedInput === 'act' }">输入账号</label>
                        <div class="input-line"></div>
                    </div>
                    
                    <div class="input-group" :class="{ 'has-value': pwd }">
                        <i class="el-icon-lock"></i>
                        <input 
                            v-model="pwd" 
                            type="password"
                            @focus="focusInput('pwd')"
                            @blur="blurInput('pwd')"
                            @keyup.enter="login"
                        />
                        <label :class="{ 'active': pwd || focusedInput === 'pwd' }">输入密码</label>
                        <div class="input-line"></div>
                    </div>
                    
                    <button class="login-btn" @click="login" :disabled="isLoading">
                        <span v-if="!isLoading">立即登录</span>
                        <span v-else class="loading-spinner">
                            <i class="el-icon-loading"></i> 登录中...
                        </span>
                    </button>
                    
                    <div class="divider">
                        <span>或</span>
                    </div>
                    
                    <div class="register-link">
                        <p>还没有账号？<span @click="toDoRegister">立即注册</span></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
const DELAY_TIME = 1300;
const DEFAULT_AVATAR = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+';
import request from "@/utils/request.js";
import { setToken, setUserInfo } from "@/utils/storage.js";
import md5 from 'js-md5';
import Logo from '@/components/Logo.vue';

export default {
    name: "Login",
    components: { Logo },
    data() {
        return {
            act: '',
            pwd: '',
            colorLogo: '#fff',
            focusedInput: '',
            isLoading: false
        }
    },
    methods: {
        focusInput(input) {
            this.focusedInput = input;
        },
        blurInput() {
            this.focusedInput = '';
        },
        toDoRegister() {
            this.$router.push('/register');
        },
        async login() {
            if (!this.act || !this.pwd) {
                this.$swal.fire({
                    title: '提示',
                    text: '账号或密码不能为空',
                    icon: 'warning',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                return;
            }
            
            this.isLoading = true;
            const hashedPwd = md5(md5(this.pwd));
            const paramDTO = { userAccount: this.act, userPwd: hashedPwd };
            
            try {
                const { data } = await request.post(`user/login`, paramDTO);
                if (data.code !== 200) {
                    this.isLoading = false;
                    this.$swal.fire({
                        title: '登录失败',
                        text: data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: DELAY_TIME,
                    });
                    return;
                }
                
                setToken(data.data.token);
                
                this.$swal.fire({
                    title: '登录成功',
                    text: '欢迎回来！',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                
                setTimeout(() => {
                    const { role, userInfo } = data.data;
                    sessionStorage.setItem('role', role);
                    if (userInfo) {
                        const userName = userInfo.userName || userInfo.userAccount;
                        const userAvatar = userInfo.userAvatar || DEFAULT_AVATAR;
                        const userId = userInfo.id;
                        setUserInfo(userName, userAvatar, userId);
                    }
                    this.navigateToRole(role);
                }, DELAY_TIME);
            } catch (error) {
                this.isLoading = false;
                console.error('登录请求错误:', error);
                this.$message.error('登录请求出错，请重试！');
            }
        },
        navigateToRole(role) {
            switch (role) {
                case 1:
                    this.$router.push('/admin');
                    break;
                case 2:
                    this.$router.push('/user');
                    break;
                default:
                    console.warn('未知的角色类型:', role);
                    break;
            }
        },
    }
};
</script>

<style lang="scss" scoped>
* {
    user-select: none;
}

.login-container {
    width: 100%;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    position: relative;
    overflow: hidden;
    
    .bg-shapes {
        position: absolute;
        width: 100%;
        height: 100%;
        overflow: hidden;
        z-index: 0;
        
        .shape {
            position: absolute;
            border-radius: 50%;
            opacity: 0.1;
            animation: float 20s infinite ease-in-out;
        }
        
        .shape-1 {
            width: 400px;
            height: 400px;
            background: #fff;
            top: -100px;
            left: -100px;
            animation-delay: 0s;
        }
        
        .shape-2 {
            width: 300px;
            height: 300px;
            background: #fff;
            top: 50%;
            right: -50px;
            animation-delay: -5s;
        }
        
        .shape-3 {
            width: 200px;
            height: 200px;
            background: #fff;
            bottom: -50px;
            left: 30%;
            animation-delay: -10s;
        }
        
        .shape-4 {
            width: 150px;
            height: 150px;
            background: #fff;
            top: 20%;
            left: 60%;
            animation-delay: -15s;
        }
    }
    
    @keyframes float {
        0%, 100% {
            transform: translateY(0) rotate(0deg);
        }
        25% {
            transform: translateY(-30px) rotate(5deg);
        }
        50% {
            transform: translateY(0) rotate(0deg);
        }
        75% {
            transform: translateY(30px) rotate(-5deg);
        }
    }
    
    .login-card {
        display: flex;
        width: 900px;
        min-height: 550px;
        background: rgba(255, 255, 255, 0.95);
        border-radius: 20px;
        box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
        overflow: hidden;
        z-index: 1;
        backdrop-filter: blur(10px);
        
        .card-left {
            width: 380px;
            background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
            padding: 50px 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            
            &::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
            }
            
            .welcome-content {
                position: relative;
                z-index: 1;
                color: #fff;
                text-align: center;
                
                .logo-wrapper {
                    margin-bottom: 30px;
                }
                
                .welcome-text {
                    margin-bottom: 40px;
                    
                    h1 {
                        font-size: 32px;
                        font-weight: 700;
                        margin: 0 0 10px 0;
                        text-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    
                    p {
                        font-size: 16px;
                        opacity: 0.9;
                        margin: 0;
                    }
                }
                
                .feature-list {
                    text-align: left;
                    
                    .feature-item {
                        display: flex;
                        align-items: center;
                        margin-bottom: 15px;
                        padding: 12px 20px;
                        background: rgba(255, 255, 255, 0.15);
                        border-radius: 10px;
                        backdrop-filter: blur(5px);
                        transition: all 0.3s ease;
                        
                        &:hover {
                            background: rgba(255, 255, 255, 0.25);
                            transform: translateX(5px);
                        }
                        
                        i {
                            font-size: 20px;
                            margin-right: 12px;
                        }
                        
                        span {
                            font-size: 14px;
                            font-weight: 500;
                        }
                    }
                }
            }
        }
        
        .card-right {
            flex: 1;
            padding: 50px 45px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            
            .form-header {
                margin-bottom: 35px;
                
                h2 {
                    font-size: 28px;
                    color: #333;
                    margin: 0 0 8px 0;
                    font-weight: 700;
                }
                
                p {
                    color: #888;
                    margin: 0;
                    font-size: 14px;
                }
            }
            
            .form-body {
                .input-group {
                    position: relative;
                    margin-bottom: 30px;
                    
                    i {
                        position: absolute;
                        left: 15px;
                        top: 50%;
                        transform: translateY(-50%);
                        color: #aaa;
                        font-size: 18px;
                        transition: all 0.3s ease;
                        z-index: 1;
                    }
                    
                    input {
                        width: 100%;
                        height: 55px;
                        padding: 0 15px 0 45px;
                        border: 2px solid #eee;
                        border-radius: 12px;
                        font-size: 16px;
                        background: #fafafa;
                        transition: all 0.3s ease;
                        box-sizing: border-box;
                        
                        &:focus {
                            outline: none;
                            border-color: #ff9a56;
                            background: #fff;
                            box-shadow: 0 0 0 4px rgba(255, 154, 86, 0.1);
                        }
                        
                        &:focus + label,
                        &:valid + label {
                            top: -10px;
                            font-size: 12px;
                            color: #ff9a56;
                            background: #fff;
                            padding: 0 8px;
                        }
                    }
                    
                    label {
                        position: absolute;
                        left: 45px;
                        top: 50%;
                        transform: translateY(-50%);
                        color: #999;
                        font-size: 14px;
                        pointer-events: none;
                        transition: all 0.3s ease;
                        
                        &.active {
                            top: -10px;
                            font-size: 12px;
                            color: #ff9a56;
                            background: #fff;
                            padding: 0 8px;
                            left: 15px;
                        }
                    }
                    
                    .input-line {
                        position: absolute;
                        bottom: 0;
                        left: 50%;
                        width: 0;
                        height: 2px;
                        background: linear-gradient(90deg, #ff9a56, #ff6b6b);
                        transition: all 0.3s ease;
                    }
                    
                    &:focus-within {
                        i {
                            color: #ff9a56;
                        }
                        
                        .input-line {
                            left: 0;
                            width: 100%;
                        }
                    }
                }
                
                .login-btn {
                    width: 100%;
                    height: 50px;
                    border: none;
                    border-radius: 12px;
                    background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
                    color: #fff;
                    font-size: 16px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    box-shadow: 0 4px 15px rgba(255, 154, 86, 0.4);
                    margin-top: 10px;
                    
                    &:hover:not(:disabled) {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(255, 154, 86, 0.5);
                    }
                    
                    &:active:not(:disabled) {
                        transform: translateY(0);
                    }
                    
                    &:disabled {
                        opacity: 0.7;
                        cursor: not-allowed;
                    }
                    
                    .loading-spinner {
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                    }
                }
                
                .divider {
                    display: flex;
                    align-items: center;
                    margin: 25px 0;
                    
                    &::before,
                    &::after {
                        content: '';
                        flex: 1;
                        height: 1px;
                        background: #eee;
                    }
                    
                    span {
                        padding: 0 15px;
                        color: #aaa;
                        font-size: 13px;
                    }
                }
                
                .register-link {
                    text-align: center;
                    
                    p {
                        color: #888;
                        font-size: 14px;
                        margin: 0;
                        
                        span {
                            color: #ff6b6b;
                            font-weight: 600;
                            cursor: pointer;
                            transition: all 0.3s ease;
                            
                            &:hover {
                                color: #ff9a56;
                                text-decoration: underline;
                            }
                        }
                    }
                }
            }
        }
    }
}

@media (max-width: 900px) {
    .login-container {
        .login-card {
            width: 90%;
            max-width: 400px;
            flex-direction: column;
            
            .card-left {
                width: 100%;
                padding: 30px;
                
                .welcome-content {
                    .welcome-text {
                        margin-bottom: 20px;
                        
                        h1 {
                            font-size: 24px;
                        }
                    }
                    
                    .feature-list {
                        display: none;
                    }
                }
            }
            
            .card-right {
                padding: 30px;
            }
        }
    }
}
</style>
