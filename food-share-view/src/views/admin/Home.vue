<template>
    <div class="dashboard">
        <div class="stats-section">
            <div class="stat-card" v-for="(item, index) in staticData" :key="index">
                <div class="stat-icon" :style="{ background: getStatColor(index) }">
                    <i :class="getStatIcon(index)"></i>
                </div>
                <div class="stat-info">
                    <span class="stat-value">{{ item.count }}</span>
                    <span class="stat-label">{{ item.name }}</span>
                </div>
            </div>
        </div>
        
        <div class="charts-section">
            <div class="chart-card">
                <div class="chart-header">
                    <h3>内容分享趋势</h3>
                    <div class="chart-actions">
                        <select v-model="shareDays" @change="netItemSelected(shareDays)">
                            <option :value="7">近7天</option>
                            <option :value="30">近30天</option>
                            <option :value="90">近90天</option>
                            <option :value="365">近一年</option>
                        </select>
                    </div>
                </div>
                <div class="chart-body">
                    <LineChart 
                        height="280px" 
                        tag="内容分享趋势" 
                        :values="shareValues"
                        :date="shareDates" 
                    />
                </div>
            </div>
            
            <div class="chart-card">
                <div class="chart-header">
                    <h3>收录美食做法</h3>
                    <div class="chart-actions">
                        <select v-model="gourmetDays" @change="gourmetItemSelected(gourmetDays)">
                            <option :value="7">近7天</option>
                            <option :value="30">近30天</option>
                            <option :value="90">近90天</option>
                            <option :value="365">近一年</option>
                        </select>
                    </div>
                </div>
                <div class="chart-body">
                    <LineChart 
                        height="280px" 
                        tag="收录美食做法" 
                        :values="gourmetValues" 
                        :date="gourmetDates" 
                    />
                </div>
            </div>
        </div>
        
        <div class="content-section">
            <div class="hot-posts">
                <div class="section-header">
                    <h3><i class="el-icon-trophy"></i> 最热帖子</h3>
                    <span class="more-link">查看更多</span>
                </div>
                <div class="posts-list">
                    <div class="post-item" v-for="(gourmet, index) in gourmetList" :key="index" @click="readGourmet(gourmet)">
                        <div class="post-cover">
                            <img :src="gourmet.cover" :alt="gourmet.title">
                        </div>
                        <div class="post-content">
                            <div class="post-author">
                                <img :src="gourmet.userAvatar" class="author-avatar">
                                <span class="author-name">{{ gourmet.userName }}</span>
                            </div>
                            <h4 class="post-title">{{ gourmet.title }}</h4>
                            <div class="post-meta">
                                <span class="category-tag">{{ gourmet.categoryName }}</span>
                                <div class="post-stats">
                                    <span><i class="el-icon-thumb"></i> {{ gourmet.upvoteCount }}</span>
                                    <span><i class="el-icon-view"></i> {{ gourmet.viewCount }}</span>
                                    <span><i class="el-icon-star-off"></i> {{ gourmet.saveCount }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import LineChart from "@/components/LineChart"
import { timeAgo } from "@/utils/data"

export default {
    components: { LineChart },
    data() {
        return {
            staticData: [],
            gourmetList: [],
            shareValues: [],
            shareDates: [],
            gourmetValues: [],
            gourmetDates: [],
            shareDays: 365,
            gourmetDays: 365,
        }
    },
    created() {
        this.fentchStaticCount();
        this.fentchgourmet();
        this.netItemSelected(this.shareDays);
        this.gourmetItemSelected(this.gourmetDays);
    },
    methods: {
        timeOut(time) {
            return timeAgo(time);
        },
        getStatColor(index) {
            const colors = [
                'linear-gradient(135deg, #FF6B35 0%, #f7931e 100%)',
                'linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%)',
                'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
                'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
            ];
            return colors[index % colors.length];
        },
        getStatIcon(index) {
            const icons = ['el-icon-user', 'el-icon-document', 'el-icon-star-on', 'el-icon-chat-dot-round'];
            return icons[index % icons.length];
        },
        gourmetItemSelected(day) {
            this.$axios.get(`/gourmet/daysQuery/${day}`).then(res => {
                if (res.data.code === 200) {
                    this.gourmetValues = res.data.data.map(money => money.count);
                    this.gourmetDates = res.data.data.map(money => money.name);
                }
            });
        },
        netItemSelected(day) {
            this.$axios.post(`/contentNet/daysQuery`, { day }).then(res => {
                if (res.data.code === 200) {
                    this.shareValues = res.data.data.map(money => money.count);
                    this.shareDates = res.data.data.map(money => money.name);
                }
            });
        },
        readGourmet(gourmet) {
            sessionStorage.setItem('gourmetId', gourmet.id);
            this.$router.push('/seeGourmetDetail');
        },
        fentchgourmet() {
            const gourmetQueryDto = {
                current: 1,
                size: 5
            }
            this.$axios.post('/gourmet/queryByView', gourmetQueryDto).then(res => {
                if (res.data.code === 200) {
                    this.gourmetList = res.data.data;
                }
            });
        },
        fentchStaticCount() {
            this.$axios.get('/views/staticControls').then(res => {
                if (res.data.code === 200) {
                    this.staticData = res.data.data;
                }
            });
        },
    },
};
</script>

<style scoped lang="scss">
.dashboard {
    .stats-section {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 20px;
        margin-bottom: 24px;
        
        .stat-card {
            background: #fff;
            border-radius: 16px;
            padding: 24px;
            display: flex;
            align-items: center;
            gap: 16px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
            transition: all 0.3s ease;
            
            &:hover {
                transform: translateY(-4px);
                box-shadow: 0 8px 24px rgba(255, 107, 53, 0.15);
            }
            
            .stat-icon {
                width: 56px;
                height: 56px;
                border-radius: 14px;
                display: flex;
                align-items: center;
                justify-content: center;
                
                i {
                    font-size: 24px;
                    color: #fff;
                }
            }
            
            .stat-info {
                display: flex;
                flex-direction: column;
                gap: 4px;
                
                .stat-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: #333;
                }
                
                .stat-label {
                    font-size: 14px;
                    color: #999;
                }
            }
        }
    }
    
    .charts-section {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
        margin-bottom: 24px;
        
        .chart-card {
            background: #fff;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
            
            .chart-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 20px 24px;
                border-bottom: 1px solid #f0f0f0;
                
                h3 {
                    margin: 0;
                    font-size: 16px;
                    font-weight: 600;
                    color: #333;
                }
                
                .chart-actions {
                    select {
                        padding: 8px 16px;
                        border: 1px solid #eee;
                        border-radius: 8px;
                        font-size: 13px;
                        color: #666;
                        background: #fafafa;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        
                        &:focus {
                            outline: none;
                            border-color: #FF6B35;
                        }
                    }
                }
            }
            
            .chart-body {
                padding: 20px 24px;
            }
        }
    }
    
    .content-section {
        .hot-posts {
            background: #fff;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
            
            .section-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 20px 24px;
                border-bottom: 1px solid #f0f0f0;
                
                h3 {
                    margin: 0;
                    font-size: 16px;
                    font-weight: 600;
                    color: #333;
                    
                    i {
                        margin-right: 8px;
                        color: #FF6B35;
                    }
                }
                
                .more-link {
                    font-size: 13px;
                    color: #FF6B35;
                    cursor: pointer;
                    
                    &:hover {
                        text-decoration: underline;
                    }
                }
            }
            
            .posts-list {
                padding: 16px 24px;
                
                .post-item {
                    display: flex;
                    gap: 16px;
                    padding: 16px 0;
                    border-bottom: 1px solid #f5f5f5;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    
                    &:last-child {
                        border-bottom: none;
                    }
                    
                    &:hover {
                        background: #fffbf8;
                        margin: 0 -24px;
                        padding: 16px 24px;
                        border-radius: 10px;
                        
                        .post-title {
                            color: #FF6B35;
                        }
                    }
                    
                    .post-cover {
                        flex-shrink: 0;
                        
                        img {
                            width: 120px;
                            height: 80px;
                            object-fit: cover;
                            border-radius: 10px;
                        }
                    }
                    
                    .post-content {
                        flex: 1;
                        min-width: 0;
                        
                        .post-author {
                            display: flex;
                            align-items: center;
                            gap: 8px;
                            margin-bottom: 8px;
                            
                            .author-avatar {
                                width: 24px;
                                height: 24px;
                                border-radius: 50%;
                                object-fit: cover;
                            }
                            
                            .author-name {
                                font-size: 13px;
                                color: #666;
                            }
                        }
                        
                        .post-title {
                            font-size: 15px;
                            font-weight: 600;
                            color: #333;
                            margin: 0 0 10px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            transition: color 0.3s ease;
                        }
                        
                        .post-meta {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            
                            .category-tag {
                                display: inline-block;
                                padding: 4px 10px;
                                background: rgba(255, 107, 53, 0.1);
                                color: #FF6B35;
                                font-size: 12px;
                                border-radius: 6px;
                            }
                            
                            .post-stats {
                                display: flex;
                                gap: 12px;
                                
                                span {
                                    font-size: 12px;
                                    color: #999;
                                    
                                    i {
                                        margin-right: 4px;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@media (max-width: 1200px) {
    .dashboard {
        .stats-section {
            grid-template-columns: repeat(2, 1fr);
        }
        
        .charts-section {
            grid-template-columns: 1fr;
        }
    }
}

@media (max-width: 768px) {
    .dashboard {
        .stats-section {
            grid-template-columns: 1fr;
        }
    }
}
</style>
