<template>
    <div class="save-page">
        <header class="header">
            <i class="el-icon-arrow-left back-btn" @click="goBack"></i>
            <h1>我的收藏</h1>
            <span class="count-badge" v-if="list.length">{{ list.length }}</span>
        </header>

        <div class="content">
            <div v-if="list.length === 0" class="empty">
                <div class="empty-icon"><i class="el-icon-star-off"></i></div>
                <h3>暂无收藏</h3>
                <p>去发现更多美食，收藏你喜欢的菜谱吧</p>
                <div class="empty-btn" @click="$router.push('/home')">去发现</div>
            </div>

            <div v-else class="grid">
                <div class="card" v-for="(item, i) in list" :key="i" @click="viewDetail(item)">
                    <div class="cover">
                        <img :src="item.cover" loading="lazy">
                        <div class="overlay">
                            <span><i class="el-icon-view"></i> 查看详情</span>
                        </div>
                    </div>
                    <div class="info">
                        <h3>{{ item.title }}</h3>
                        <div class="author">
                            <img :src="item.userAvatar || defaultAvatar">
                            <span>{{ item.userName }}</span>
                        </div>
                        <div class="meta">
                            <span><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span>
                            <span><i class="el-icon-star-on"></i> {{ item.saveCount || 0 }}</span>
                            <span><i class="el-icon-discount"></i> {{ item.upvoteCount || 0 }}</span>
                        </div>
                    </div>
                    <div class="date-tag">{{ formatDate(item.createTime) }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Save",
    data: () => ({
        list: [],
        defaultAvatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+'
    }),
    created() { this.fetch() },
    methods: {
        goBack() { this.$router.back() },
        viewDetail(item) { sessionStorage.setItem('gourmetId', item.id); this.$router.push('/gourmetDetail') },
        formatDate(date) { if (!date) return ''; return date.substring(0, 10) },
        async fetch() {
            const { data } = await this.$axios.post('/interaction/queryCollectionList', {})
            if (data.code === 200) this.list = data.data || []
        }
    }
}
</script>

<style scoped lang="scss">
$primary: #FF6B35;

.save-page { min-height: 100vh; background: linear-gradient(180deg, #fff5f0 0%, #f8f8f8 100%) }

.header {
    display: flex; align-items: center; justify-content: center; padding: 20px;
    background: linear-gradient(135deg, #ff9a56, $primary); position: relative;
    h1 { color: #fff; font-size: 20px; margin: 0; letter-spacing: 2px }
    .back-btn { position: absolute; left: 16px; color: #fff; font-size: 22px; padding: 8px; cursor: pointer }
    .count-badge { position: absolute; right: 16px; background: rgba(255,255,255,0.3); color: #fff; padding: 4px 12px; border-radius: 20px; font-size: 13px }
}

.content { padding: 20px; max-width: 1200px; margin: 0 auto }

.empty { text-align: center; padding: 80px 20px;
    .empty-icon { width: 100px; height: 100px; margin: 0 auto 24px; background: linear-gradient(135deg, rgba(255,107,53,0.1), rgba(255,107,53,0.05)); border-radius: 50%; display: flex; align-items: center; justify-content: center;
        i { font-size: 48px; color: $primary; opacity: 0.6 }
    }
    h3 { font-size: 20px; color: #333; margin: 0 0 12px }
    p { color: #999; margin: 0 0 24px }
    .empty-btn { display: inline-block; padding: 12px 32px; background: linear-gradient(135deg, #ff9a56, $primary); color: #fff; border-radius: 25px; cursor: pointer; font-weight: 500; box-shadow: 0 4px 15px rgba(255,107,53,0.3);
        &:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255,107,53,0.4) }
    }
}

.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px }

.card { background: #fff; border-radius: 20px; overflow: hidden; cursor: pointer; box-shadow: 0 4px 20px rgba(0,0,0,0.06); transition: 0.4s; position: relative;
    &:hover { transform: translateY(-8px); box-shadow: 0 12px 40px rgba(255,107,53,0.15);
        .cover .overlay { opacity: 1 }
        .cover img { transform: scale(1.1) }
    }
    .cover { position: relative; height: 180px; overflow: hidden;
        img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s }
        .overlay { position: absolute; inset: 0; background: linear-gradient(180deg, transparent 0%, rgba(0,0,0,0.7) 100%); display: flex; align-items: flex-end; justify-content: center; padding-bottom: 20px; opacity: 0; transition: 0.3s;
            span { color: #fff; font-size: 14px; padding: 8px 20px; background: $primary; border-radius: 20px }
        }
    }
    .info { padding: 16px;
        h3 { margin: 0 0 12px; font-size: 16px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap }
        .author { display: flex; align-items: center; gap: 8px; margin-bottom: 12px;
            img { width: 28px; height: 28px; border-radius: 50%; border: 2px solid rgba(255,107,53,0.3) }
            span { font-size: 13px; color: #666 }
        }
        .meta { display: flex; gap: 16px;
            span { font-size: 12px; color: #999; display: flex; align-items: center; gap: 4px; i { color: $primary } }
        }
    }
    .date-tag { position: absolute; top: 12px; right: 12px; background: rgba(255,255,255,0.95); padding: 4px 12px; border-radius: 12px; font-size: 11px; color: #666; backdrop-filter: blur(10px) }
}

@media (max-width: 768px) {
    .grid { grid-template-columns: repeat(2, 1fr); gap: 12px }
    .card .cover { height: 140px }
}
</style>
