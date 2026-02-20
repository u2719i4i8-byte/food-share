<template>
    <div class="modern-logo" @click="handleClick">
        <div class="logo-icon" :class="{ 'on-dark': isOnDark }">
            <div class="icon-inner">
                <span class="icon-emoji">🍊</span>
            </div>
            <div class="icon-ring"></div>
        </div>
        <div class="logo-text" v-if="!flag" :class="{ 'on-dark': isOnDark }">
            <span class="text-main">{{ sysName }}</span>
            <span class="text-dot">.</span>
        </div>
    </div>
</template>

<script>
export default {
    name: "Logo",
    data() {
        return {}
    },
    props: {
        sysName: {
            type: String,
            required: true,
            default: '橘子美食网'
        },
        flag: {
            type: Boolean,
            required: false
        },
        bag: {
            type: String,
            default: '#1c1c1c'
        },
    },
    computed: {
        isOnDark() {
            const darkColors = ['#fff', '#ffffff', 'white', '#FF6B35', '#f7931e'];
            return darkColors.includes(this.bag);
        }
    },
    methods: {
        handleClick() {
            this.$router.push('/home');
        }
    }
};
</script>

<style scoped lang="scss">
.modern-logo {
    display: flex;
    align-items: center;
    gap: 14px;
    cursor: pointer;
    user-select: none;
    
    .logo-icon {
        position: relative;
        width: 50px;
        height: 50px;
        
        .icon-inner {
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, #FF8A00 0%, #FF6B00 50%, #FFA500 100%);
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 20px rgba(255, 138, 0, 0.5);
            transition: all 0.3s ease;
            
            .icon-emoji {
                font-size: 28px;
                filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.2));
            }
        }
        
        .icon-ring {
            position: absolute;
            inset: -4px;
            border: 3px solid transparent;
            border-radius: 18px;
            background: linear-gradient(135deg, #FFD700, #FF8A00, #FFD700) border-box;
            -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
            mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
            -webkit-mask-composite: xor;
            mask-composite: exclude;
            opacity: 0;
            transition: all 0.3s ease;
        }
        
        &.on-dark {
            .icon-inner {
                background: rgba(255, 255, 255, 0.98);
                box-shadow: 0 6px 24px rgba(0, 0, 0, 0.25), 0 0 0 2px rgba(255, 255, 255, 0.3);
                
                .icon-emoji {
                    font-size: 30px;
                    filter: drop-shadow(0 2px 6px rgba(255, 107, 0, 0.4));
                }
            }
            
            .icon-ring {
                background: linear-gradient(135deg, #ffffff, #FFD700, #ffffff) border-box;
                opacity: 0.6;
            }
        }
        
        &:hover {
            .icon-inner {
                transform: scale(1.1);
                box-shadow: 0 8px 30px rgba(255, 138, 0, 0.6);
            }
            
            &.on-dark .icon-inner {
                box-shadow: 0 10px 35px rgba(0, 0, 0, 0.3), 0 0 0 3px rgba(255, 255, 255, 0.4);
            }
            
            .icon-ring {
                opacity: 1;
                animation: ring-rotate 2s linear infinite;
            }
        }
    }
    
    .logo-text {
        display: flex;
        align-items: baseline;
        
        .text-main {
            font-size: 26px;
            font-weight: 800;
            background: linear-gradient(135deg, #FF8A00 0%, #FF6B00 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: 2px;
            font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
        }
        
        .text-dot {
            font-size: 26px;
            font-weight: 800;
            color: #FF8A00;
            animation: dot-blink 1.5s ease-in-out infinite;
        }
        
        &.on-dark {
            .text-main {
                background: linear-gradient(135deg, #ffffff 0%, #FFE4B5 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                text-shadow: 0 2px 10px rgba(255, 255, 255, 0.3);
                filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
            }
            
            .text-dot {
                color: #fff;
                text-shadow: 0 0 15px rgba(255, 255, 255, 0.6), 0 2px 4px rgba(0, 0, 0, 0.2);
            }
        }
    }
    
    &:hover {
        .logo-text .text-main {
            background: linear-gradient(135deg, #FF6B00 0%, #FF8A00 100%);
            -webkit-background-clip: text;
            background-clip: text;
        }
        
        .logo-text.on-dark .text-main {
            background: linear-gradient(135deg, #FFE4B5 0%, #ffffff 100%);
            -webkit-background-clip: text;
            background-clip: text;
        }
    }
}

@keyframes ring-rotate {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

@keyframes dot-blink {
    0%, 100% {
        opacity: 1;
    }
    50% {
        opacity: 0.4;
    }
}

@media (max-width: 768px) {
    .modern-logo {
        .logo-icon {
            width: 40px;
            height: 40px;
            
            .icon-inner {
                border-radius: 11px;
                
                .icon-emoji {
                    font-size: 22px;
                }
            }
        }
        
        .logo-text .text-main {
            font-size: 20px;
        }
    }
}
</style>
