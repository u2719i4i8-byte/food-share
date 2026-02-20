<template>
    <div class="user-layout">
        <main class="main-content">
            <div class="content-wrapper">
                <router-view></router-view>
            </div>
        </main>
    </div>
</template>

<script>
export default {
    name: "User",
    data() {
        return {
            key: '',
            defaultPath: '/home',
            userInfo: {}
        }
    },
    computed: {
        currentRoute() {
            return this.$route.path;
        }
    },
    created() {
        this.auth();
    },
    methods: {
        route(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        async auth() {
            const { data } = await this.$axios.get('/user/auth');
            if (data.code !== 200) {
                this.$router.push('/');
            } else {
                this.userInfo = data.data;
                sessionStorage.setItem('userInfo', JSON.stringify(this.userInfo));
                sessionStorage.setItem('userName', this.userInfo.userName || this.userInfo.userAccount);
                sessionStorage.setItem('userAvatar', this.userInfo.userAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=user');
                sessionStorage.setItem('userId', this.userInfo.id);
            }
        },
    }
};
</script>

<style scoped lang="scss">
.user-layout {
    min-height: 100vh;
    background: #FFFFFF;
}

.main-content {
    min-height: 100vh;
    
    .content-wrapper {
        width: 100%;
    }
}
</style>
