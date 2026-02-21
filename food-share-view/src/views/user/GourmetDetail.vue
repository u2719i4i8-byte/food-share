<template>
    <div class="detail-page">
        <header class="header">
            <div class="header-content">
                <div class="back-btn" @click="goBack">
                    <i class="el-icon-arrow-left"></i>
                    <span>返回</span>
                </div>
                <Logo :sysName="'橘子美食网'" />
                <div class="header-right">
                    <img :src="userAvatar" class="user-avatar" @click="goToMine">
                </div>
            </div>
        </header>

        <div class="main-container">
            <article class="article-wrapper">
                <div class="article-header">
                    <div class="author-row">
                        <div class="author-info">
                            <img :src="gourmet.userAvatar || defaultAvatar" class="author-avatar">
                            <div class="author-text">
                                <span class="author-name">{{ gourmet.userName }}</span>
                                <span class="publish-time">{{ formatDate(gourmet.createTime) }}</span>
                            </div>
                        </div>
                    </div>
                    <h1 class="article-title">{{ gourmet.title }}</h1>
                    <div class="article-meta">
                        <span class="category-tag" v-if="gourmet.categoryName">{{ gourmet.categoryName }}</span>
                        <span class="meta-item"><i class="el-icon-view"></i> {{ gourmet.viewCount || 0 }} 浏览</span>
                        <span class="meta-item"><i class="el-icon-star-on"></i> {{ gourmet.saveCount || 0 }} 收藏</span>
                    </div>
                </div>

                <div class="article-content">
                    <div class="content-html" v-html="gourmet.content"></div>
                </div>

                <div class="article-actions">
                    <div class="action-btn" @click="opeationSave" :class="{ active: saveStatus }">
                        <i :class="saveStatus ? 'el-icon-collection' : 'el-icon-collection-tag'"></i>
                        <span>{{ gourmet.saveCount || 0 }}</span>
                        <label>收藏</label>
                    </div>
                </div>

                <div class="comments-section">
                    <div class="section-header">
                        <h3><i class="el-icon-chat-dot-round"></i> 评论 <span class="count">{{ commentCount }}</span></h3>
                    </div>

                    <div class="comment-input-box">
                        <img :src="userAvatar || defaultAvatar" class="input-avatar">
                        <div class="input-wrapper">
                            <input type="text" :placeholder="userName ? '说点什么，发表你的看法...' : '登录后发表评论...'" v-model="newComment" @keyup.enter="sendComment" :disabled="!userName">
                            <button class="send-btn" @click="sendComment" :disabled="!newComment.trim() || !userName">
                                <i class="el-icon-s-promotion"></i> 发送
                            </button>
                        </div>
                    </div>

                    <div class="comment-list">
                        <div class="comment-item" v-for="(comment, index) in comments" :key="index">
                            <img :src="comment.avatar" class="comment-avatar">
                            <div class="comment-body">
                                <div class="comment-header">
                                    <span class="comment-author">{{ comment.author }}</span>
                                    <span class="comment-time">{{ comment.time }}</span>
                                </div>
                                <p class="comment-text">{{ comment.text }}</p>
                                <div class="comment-actions">
                                    <span class="action-btn" @click="toggleReply(comment)">
                                        <i class="el-icon-chat-dot-round"></i> 回复
                                    </span>
                                    <span class="action-btn delete-btn" v-if="comment.userId === userId" @click="deleteComment(comment.id)">
                                        <i class="el-icon-delete"></i> 删除
                                    </span>
                                </div>
                                <div class="reply-input-box" v-if="comment.showReply">
                                    <input type="text" v-model="comment.replyContent" placeholder="输入回复..." @keyup.enter="sendReply(comment)">
                                    <button @click="sendReply(comment)" :disabled="!comment.replyContent || !comment.replyContent.trim()">发送</button>
                                </div>
                                <div class="child-comments" v-if="comment.children && comment.children.length > 0">
                                    <div class="child-item" v-for="(child, cIndex) in comment.children" :key="cIndex">
                                        <img :src="child.userAvatar || defaultAvatar" class="child-avatar">
                                        <div class="child-body">
                                            <div class="child-header">
                                                <span class="child-author">{{ child.userName }}</span>
                                                <span class="reply-arrow" v-if="child.replierName">回复 <em>{{ child.replierName }}</em></span>
                                                <span class="child-time">{{ child.createTime ? child.createTime.substring(0, 16) : '' }}</span>
                                            </div>
                                            <p class="child-text">{{ child.content }}</p>
                                            <div class="child-actions">
                                                <span class="action-btn" @click="toggleChildReply(comment, child)">
                                                    <i class="el-icon-chat-dot-round"></i> 回复
                                                </span>
                                                <span class="action-btn delete-btn" v-if="child.userId === userId" @click="deleteComment(child.id)">
                                                    <i class="el-icon-delete"></i> 删除
                                                </span>
                                            </div>
                                            <div class="reply-input-box" v-if="child.showReply">
                                                <input type="text" v-model="child.replyContent" placeholder="输入回复..." @keyup.enter="sendChildReply(comment, child)">
                                                <button @click="sendChildReply(comment, child)" :disabled="!child.replyContent || !child.replyContent.trim()">发送</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="empty-comments" v-if="comments.length === 0">
                            <div class="empty-icon"><i class="el-icon-chat-line-round"></i></div>
                            <p>暂无评论，快来抢沙发吧~</p>
                        </div>
                    </div>
                </div>
            </article>

            <aside class="sidebar" v-if="showRecommend">
                <div class="sidebar-card">
                    <div class="card-header">
                        <span class="card-title">相关推荐</span>
                    </div>
                    <div class="recommend-list">
                        <div class="recommend-item" v-for="(note, index) in relatedNotes" :key="index" @click="readGourmet(note)">
                            <img :src="note.cover" class="recommend-cover">
                            <div class="recommend-info">
                                <h4>{{ note.title }}</h4>
                                <div class="recommend-meta">
                                    <span><i class="el-icon-star-on"></i> {{ note.upvoteCount || 0 }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </aside>
        </div>
    </div>
</template>

<script>
/**
 * 美食详情页面
 * 功能：美食详情展示、收藏、评论、相关推荐
 */
import Logo from '@/components/Logo.vue';
import { getUserInfo, isLoggedIn } from '@/utils/storage.js';

export default {
    name: "GourmetDetail",
    components: { Logo },
    data() {
        return {
            gourmetId: null,        // 当前美食ID
            gourmet: {},            // 美食详情数据
            saveStatus: false,      // 收藏状态
            commentCount: 0,        // 评论数量
            newComment: '',         // 新评论内容
            userAvatar: '',         // 用户头像
            userName: '',           // 用户名称
            userId: null,           // 用户ID
            defaultAvatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI0ZGNkIzNSIvPjxjaXJjbGUgY3g9IjUwIiBjeT0iMzgiIHI9IjE4IiBmaWxsPSIjZmZmIi8+PGVsbGlwc2UgY3g9IjUwIiBjeT0iODUiIHJ4PSIyNSIgcnk9IjIwIiBmaWxsPSIjZmZmIi8+PC9zdmc+',
            comments: [],           // 评论列表
            relatedNotes: [],       // 相关推荐列表
            showRecommend: true     // 是否显示推荐
        }
    },
    created() {
        this.loadUserInfo();
        this.loadGourmetId();
        this.checkScreenWidth();
        window.addEventListener('resize', this.checkScreenWidth);
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.checkScreenWidth);
    },
    methods: {
        // 加载用户信息
        loadUserInfo() {
            const { userName, userAvatar, userId } = getUserInfo();
            this.userName = userName || '游客';
            this.userAvatar = userAvatar || this.defaultAvatar;
            this.userId = userId;
        },
        // 检查屏幕宽度，控制推荐显示
        checkScreenWidth() {
            this.showRecommend = window.innerWidth >= 1200;
        },
        // 日期格式化
        formatDate(date) {
            if (!date) return '';
            return date.substring(0, 10);
        },
        // 加载美食ID并获取相关数据
        loadGourmetId() {
            this.gourmetId = Number(sessionStorage.getItem('gourmetId'));
            this.fetchGourmetById(this.gourmetId);
            this.viewOperation(this.gourmetId);
            this.fetchSaveOperation(this.gourmetId);
            this.fetchComments(this.gourmetId);
        },
        // 根据ID获取美食详情
        fetchGourmetById(gourmetId) {
            this.$axios.get(`/gourmet/${gourmetId}`).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.gourmet = data.data[0];
                    this.fetchRelatedNotes();
                }
            }).catch(error => {
                console.log("通过ID查询美食做法异常：", error);
            })
        },
        // 获取评论列表
        fetchComments(contentId) {
            this.$axios.get(`/evaluations/list/${contentId}/gourmet`).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    const result = data.data || { count: 0, data: [] };
                    this.commentCount = result.count || 0;
                    this.comments = (result.data || []).map(item => ({
                        id: item.id,
                        userId: item.userId,
                        avatar: item.userAvatar || this.defaultAvatar,
                        author: item.userName || '匿名用户',
                        text: item.content,
                        time: item.createTime ? item.createTime.substring(0, 16) : '',
                        likes: item.upvoteCount || 0,
                        upvoteFlag: item.upvoteFlag || false,
                        showReply: false,
                        replyContent: '',
                        children: (item.commentChildVOS || []).map(child => ({
                            id: child.id,
                            userId: child.userId,
                            parentId: child.parentId,
                            userAvatar: child.userAvatar,
                            userName: child.userName,
                            replierName: child.replierName,
                            content: child.content,
                            createTime: child.createTime,
                            showReply: false,
                            replyContent: ''
                        }))
                    }));
                }
            }).catch(error => {
                console.log("获取评论异常：", error);
            })
        },
        // 记录浏览操作
        viewOperation(contentId) {
            this.$axios.post(`/interaction/viewOperation/${contentId}`).then(() => {}).catch(error => {
                console.log("浏览操作异常：", error);
            })
        },
        // 获取收藏状态
        fetchSaveOperation(contentId) {
            this.$axios.get(`/interaction/saveStatus/${contentId}`).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.saveStatus = data.data > 0;
                }
            }).catch(error => {
                console.log("通过收藏状态异常：", error);
            })
        },
        // 收藏/取消收藏操作
        opeationSave() {
            this.$axios.post(`/interaction/saveOperation/${this.gourmetId}`).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    if (data.data > 0) {
                        this.gourmet.saveCount = (this.gourmet.saveCount || 0) + 1;
                    } else {
                        this.gourmet.saveCount = Math.max(0, (this.gourmet.saveCount || 0) - 1);
                    }
                    this.saveStatus = data.data > 0;
                }
            }).catch(error => {
                console.log("收藏操作异常：", error);
            })
        },
        // 发送评论
        sendComment() {
            if (!isLoggedIn()) {
                this.$message.warning('请先登录');
                return this.$router.push('/login');
            }
            if (!this.newComment.trim()) return;
            const params = { 
                contentId: this.gourmetId, 
                content: this.newComment,
                contentType: 'gourmet'
            };
            this.$axios.post('/evaluations/insert', params).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.newComment = '';
                    this.$message.success('评论发布成功！');
                    this.fetchComments(this.gourmetId);
                } else {
                    this.$message.error(data.msg || '评论发布失败');
                }
            }).catch(error => {
                console.log("评论发布异常：", error);
                this.$message.error('评论发布失败，请重试');
            })
        },
        // 删除评论
        deleteComment(commentId) {
            this.$confirm('确定要删除这条评论吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$axios.delete(`/evaluations/delete/${commentId}`).then(res => {
                    const { data } = res;
                    if (data.code === 200) {
                        this.$message.success('删除成功');
                        this.fetchComments(this.gourmetId);
                    } else {
                        this.$message.error(data.msg || '删除失败');
                    }
                }).catch(error => {
                    console.log("删除评论异常：", error);
                    this.$message.error('删除失败，请重试');
                });
            }).catch(() => {});
        },
        // 切换回复框显示
        toggleReply(comment) {
            if (!isLoggedIn()) {
                this.$message.warning('请先登录');
                return this.$router.push('/login');
            }
            this.$set(comment, 'showReply', !comment.showReply);
            if (!comment.replyContent) {
                this.$set(comment, 'replyContent', '');
            }
        },
        // 切换子评论回复框显示
        toggleChildReply(parentComment, childComment) {
            if (!isLoggedIn()) {
                this.$message.warning('请先登录');
                return this.$router.push('/login');
            }
            this.$set(childComment, 'showReply', !childComment.showReply);
            if (!childComment.replyContent) {
                this.$set(childComment, 'replyContent', '');
            }
        },
        // 发送回复（父级评论）
        sendReply(comment) {
            if (!comment.replyContent || !comment.replyContent.trim()) return;
            const params = {
                contentId: this.gourmetId,
                content: comment.replyContent,
                contentType: 'gourmet',
                parentId: comment.id
            };
            this.$axios.post('/evaluations/insert', params).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.$message.success('回复成功');
                    this.fetchComments(this.gourmetId);
                } else {
                    this.$message.error(data.msg || '回复失败');
                }
            }).catch(error => {
                console.log("回复异常：", error);
                this.$message.error('回复失败，请重试');
            });
        },
        // 发送子评论回复
        sendChildReply(parentComment, childComment) {
            if (!childComment.replyContent || !childComment.replyContent.trim()) return;
            const params = {
                contentId: this.gourmetId,
                content: childComment.replyContent,
                contentType: 'gourmet',
                parentId: parentComment.id,
                replierId: childComment.userId
            };
            this.$axios.post('/evaluations/insert', params).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.$message.success('回复成功');
                    this.fetchComments(this.gourmetId);
                } else {
                    this.$message.error(data.msg || '回复失败');
                }
            }).catch(error => {
                console.log("回复异常：", error);
                this.$message.error('回复失败，请重试');
            });
        },
        // 返回上一页
        goBack() {
            this.$router.back();
        },
        // 跳转到个人中心
        goToMine() {
            this.$router.push('/mine');
        },
        // 获取相关推荐美食
        fetchRelatedNotes() {
            this.$axios.post(`/gourmet/queryList`, { size: 4, current: 1 }).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.relatedNotes = (data.data || []).filter(item => item.id !== this.gourmetId);
                }
            }).catch(error => {
                console.log("查询推荐美食异常：", error);
            })
        },
        // 查看其他美食详情
        readGourmet(gourmet) {
            sessionStorage.setItem('gourmetId', gourmet.id);
            this.$router.go(0);
        }
    }
};
</script>

<style scoped lang="scss">
$primary: #FF6B35;
$primary-light: #FF8C5A;

.detail-page { min-height: 100vh; background: linear-gradient(180deg, #fff9f5 0%, #fff 100%) }

.header {
    position: sticky; top: 0; z-index: 100;
    background: rgba(255,255,255,0.95); backdrop-filter: blur(20px);
    border-bottom: 1px solid rgba(255, 107, 53, 0.1);
    .header-content {
        display: flex; align-items: center; justify-content: space-between;
        max-width: 1200px; margin: 0 auto; padding: 12px 24px;
    }
    .back-btn {
        display: flex; align-items: center; gap: 6px;
        padding: 8px 16px; border-radius: 20px;
        background: rgba(255, 107, 53, 0.1); color: $primary;
        cursor: pointer; transition: all 0.3s;
        &:hover { background: $primary; color: #fff }
    }
    .user-avatar { width: 40px; height: 40px; border-radius: 50%; cursor: pointer; border: 2px solid rgba(255,107,53,0.3) }
}

.main-container { display: flex; gap: 24px; max-width: 1200px; margin: 0 auto; padding: 24px }

.article-wrapper { flex: 1; min-width: 0 }

.article-header {
    background: #fff; border-radius: 20px; padding: 24px; margin-bottom: 20px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
    .author-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px }
    .author-info { display: flex; align-items: center; gap: 12px }
    .author-avatar { width: 48px; height: 48px; border-radius: 50%; border: 3px solid rgba(255,107,53,0.3) }
    .author-text { display: flex; flex-direction: column; gap: 4px }
    .author-name { font-size: 16px; font-weight: 600; color: #333 }
    .publish-time { font-size: 12px; color: #999 }
    .article-title { font-size: 28px; font-weight: 700; color: #333; margin: 0 0 16px 0; line-height: 1.4 }
    .article-meta { display: flex; align-items: center; gap: 16px; flex-wrap: wrap }
    .category-tag {
        padding: 4px 12px; border-radius: 12px;
        background: linear-gradient(135deg, rgba(255,107,53,0.15), rgba(255,107,53,0.08));
        font-size: 13px; color: $primary; font-weight: 500;
    }
    .meta-item { font-size: 13px; color: #999; i { color: $primary; margin-right: 4px } }
}

.article-content {
    background: #fff; border-radius: 20px; padding: 24px; margin-bottom: 20px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
    .content-html {
        font-size: 16px; line-height: 2; color: #333;
        ::v-deep p { margin: 0 0 16px 0 }
        ::v-deep img { max-width: 100%; border-radius: 12px; margin: 16px 0; box-shadow: 0 4px 12px rgba(0,0,0,0.1) }
        ::v-deep h1, ::v-deep h2, ::v-deep h3 { margin: 24px 0 12px 0; color: #333 }
        ::v-deep ul, ::v-deep ol { padding-left: 24px; margin: 12px 0 }
        ::v-deep li { margin-bottom: 8px }
        ::v-deep blockquote {
            border-left: 4px solid $primary; padding-left: 16px;
            margin: 16px 0; color: #666; background: #fafafa; padding: 12px 16px; border-radius: 0 8px 8px 0
        }
    }
}

.article-actions {
    display: flex; justify-content: center; gap: 40px;
    background: #fff; border-radius: 20px; padding: 20px; margin-bottom: 20px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
    .action-btn {
        display: flex; flex-direction: column; align-items: center; gap: 4px;
        padding: 12px 32px; border-radius: 16px; cursor: pointer;
        transition: all 0.3s;
        i { font-size: 28px; color: #999; transition: all 0.3s }
        span { font-size: 20px; font-weight: 600; color: #333 }
        label { font-size: 13px; color: #999 }
        &:hover { background: rgba(255,107,53,0.08); i { color: $primary } }
        &.active { 
            i { color: $primary }
            span { color: $primary }
            label { color: $primary }
        }
    }
}

.comments-section {
    background: #fff; border-radius: 20px; padding: 24px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
    .section-header { margin-bottom: 20px;
        h3 { font-size: 18px; font-weight: 600; color: #333; margin: 0; display: flex; align-items: center; gap: 8px;
            i { color: $primary }
            .count { color: $primary }
        }
    }
}

.comment-input-box {
    display: flex; gap: 12px; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #f0f0f0;
    .input-avatar { width: 40px; height: 40px; border-radius: 50%; flex-shrink: 0 }
    .input-wrapper { flex: 1; display: flex; gap: 8px;
        input { flex: 1; height: 44px; border: 2px solid #eee; border-radius: 22px; padding: 0 16px; font-size: 14px; outline: none; transition: all 0.3s;
            &:focus { border-color: $primary; box-shadow: 0 0 0 4px rgba(255,107,53,0.1) }
        }
        .send-btn { padding: 0 20px; border: none; border-radius: 22px; background: linear-gradient(135deg, $primary, $primary-light); color: #fff; font-size: 14px; cursor: pointer; transition: all 0.3s; display: flex; align-items: center; gap: 4px;
            &:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(255,107,53,0.3) }
            &:disabled { opacity: 0.5; cursor: not-allowed }
        }
    }
}

.comment-list {
    .comment-item { display: flex; gap: 12px; padding: 16px; border-radius: 12px; margin-bottom: 12px; transition: all 0.3s;
        &:hover { background: #fafafa }
        .comment-avatar { width: 40px; height: 40px; border-radius: 50%; flex-shrink: 0 }
        .comment-body { flex: 1;
            .comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;
                .comment-author { font-size: 14px; font-weight: 600; color: #333 }
                .comment-time { font-size: 12px; color: #999 }
            }
            .comment-text { font-size: 14px; color: #333; line-height: 1.6; margin: 0 0 8px 0 }
            .comment-actions { display: flex; gap: 16px;
                .action-btn { font-size: 12px; color: #999; cursor: pointer; display: flex; align-items: center; gap: 4px; transition: color 0.3s;
                    &:hover { color: $primary }
                    &.delete-btn:hover { color: #f56c6c }
                }
            }
            .reply-input-box { display: flex; gap: 8px; margin-top: 12px;
                input { flex: 1; height: 36px; border: 1px solid #eee; border-radius: 18px; padding: 0 14px; font-size: 13px; outline: none;
                    &:focus { border-color: $primary }
                }
                button { padding: 0 16px; border: none; border-radius: 18px; background: $primary; color: #fff; font-size: 13px; cursor: pointer;
                    &:hover:not(:disabled) { background: $primary-light }
                    &:disabled { opacity: 0.5; cursor: not-allowed }
                }
            }
            .child-comments { margin-top: 12px; padding-left: 16px; border-left: 2px solid #f0f0f0;
                .child-item { display: flex; gap: 10px; padding: 10px 0;
                    .child-avatar { width: 32px; height: 32px; border-radius: 50%; flex-shrink: 0 }
                    .child-body { flex: 1;
                        .child-header { display: flex; align-items: center; gap: 6px; margin-bottom: 4px;
                            .child-author { font-size: 13px; font-weight: 500; color: #333 }
                            .reply-arrow { font-size: 12px; color: #999;
                                em { font-style: normal; color: $primary }
                            }
                            .child-time { font-size: 11px; color: #bbb; margin-left: auto }
                        }
                        .child-text { font-size: 13px; color: #555; line-height: 1.5; margin: 0 0 6px 0 }
                        .child-actions { display: flex; gap: 12px;
                            .action-btn { font-size: 11px; color: #aaa; cursor: pointer; display: flex; align-items: center; gap: 3px;
                                &:hover { color: $primary }
                                &.delete-btn:hover { color: #f56c6c }
                            }
                        }
                        .reply-input-box { display: flex; gap: 8px; margin-top: 8px;
                            input { flex: 1; height: 32px; border: 1px solid #eee; border-radius: 16px; padding: 0 12px; font-size: 12px; outline: none;
                                &:focus { border-color: $primary }
                            }
                            button { padding: 0 14px; border: none; border-radius: 16px; background: $primary; color: #fff; font-size: 12px; cursor: pointer;
                                &:hover:not(:disabled) { background: $primary-light }
                                &:disabled { opacity: 0.5; cursor: not-allowed }
                            }
                        }
                    }
                }
            }
        }
    }
    .empty-comments { text-align: center; padding: 40px 20px;
        .empty-icon { width: 60px; height: 60px; margin: 0 auto 16px; background: rgba(255,107,53,0.1); border-radius: 50%; display: flex; align-items: center; justify-content: center;
            i { font-size: 28px; color: $primary }
        }
        p { color: #999; margin: 0 }
    }
}

.sidebar { width: 300px; flex-shrink: 0 }

.sidebar-card {
    background: #fff; border-radius: 20px; padding: 20px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05); position: sticky; top: 80px;
    .card-header { margin-bottom: 16px;
        .card-title { font-size: 16px; font-weight: 600; color: #333 }
    }
}

.recommend-list {
    .recommend-item { display: flex; gap: 12px; padding: 12px; border-radius: 12px; cursor: pointer; transition: all 0.3s;
        &:hover { background: #fafafa }
        .recommend-cover { width: 80px; height: 60px; border-radius: 8px; object-fit: cover; flex-shrink: 0 }
        .recommend-info { flex: 1; min-width: 0;
            h4 { font-size: 14px; color: #333; margin: 0 0 8px 0; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical }
            .recommend-meta { font-size: 12px; color: #999; i { color: $primary } }
        }
    }
}

@media (max-width: 1200px) {
    .sidebar { display: none }
}

@media (max-width: 768px) {
    .main-container { padding: 16px }
    .article-header { padding: 16px;
        .article-title { font-size: 22px }
    }
    .article-content { padding: 16px }
    .article-actions { gap: 20px; padding: 16px }
}
</style>
