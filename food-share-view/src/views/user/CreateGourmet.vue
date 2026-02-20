<template>
    <div class="create-page">
        <header class="header">
            <i class="el-icon-arrow-left back-btn" @click="goBack"></i>
            <h1>发布美食</h1>
            <el-button type="primary" @click="publish" :loading="loading"><i class="el-icon-upload2"></i> 发布</el-button>
        </header>

        <div class="content">
            <aside class="sidebar">
                <div class="card">
                    <h3><i class="el-icon-folder-opened"></i> 选择分类</h3>
                    <el-select v-model="form.categoryName" placeholder="请选择分类" class="category-select" clearable>
                        <el-option-group v-for="group in categoryGroups" :key="group.name" :label="group.name">
                            <el-option v-for="tag in group.tags" :key="tag" :label="tag" :value="tag" />
                        </el-option-group>
                    </el-select>
                </div>

                <div class="card">
                    <h3><i class="el-icon-picture"></i> 封面图片</h3>
                    <el-upload class="uploader" action="/api/food-share-sys/v1.0/file/upload" :show-file-list="false" :on-success="onCoverSuccess" drag>
                        <div v-if="form.cover" class="preview">
                            <img :src="form.cover">
                            <div class="overlay"><i class="el-icon-refresh"></i><span>更换封面</span></div>
                        </div>
                        <div v-else class="placeholder">
                            <i class="el-icon-plus"></i>
                            <span>上传封面</span>
                            <p>建议尺寸 800x600</p>
                        </div>
                    </el-upload>
                </div>

                <div class="card">
                    <h3><i class="el-icon-view"></i> 公开权限</h3>
                    <div class="toggle">
                        <span>{{ form.isPublish ? '公开可见' : '仅自己可见' }}</span>
                        <el-switch v-model="form.isPublish" active-color="#FF6B35" />
                    </div>
                    <p class="hint">{{ form.isPublish ? '所有人都可以看到你的作品' : '只有你可以查看这篇内容' }}</p>
                </div>

                <div class="card tips">
                    <h3><i class="el-icon-info"></i> 发布提示</h3>
                    <ul>
                        <li>标题要简洁明了，突出菜品特色</li>
                        <li>详细描述食材和步骤，方便他人学习</li>
                        <li>上传精美的封面图能获得更多关注</li>
                    </ul>
                </div>
            </aside>

            <main class="main">
                <div class="card title-card">
                    <input v-model="form.title" placeholder="请输入标题，让更多人看到你的美食..." maxlength="50">
                    <span class="count">{{ (form.title || '').length }}/50</span>
                </div>
                <div class="card editor-card">
                    <Editor height="calc(100vh - 280px)" :receiveContent="form.content" @on-receive="onContentChange" />
                </div>
            </main>
        </div>
    </div>
</template>

<script>
import Editor from "@/components/Editor"
import { isLoggedIn } from "@/utils/storage.js"

export default {
    name: "CreateGourmet",
    components: { Editor },
    data: () => ({
        form: { title: '', content: '', categoryName: '', cover: '', isPublish: true },
        categoryGroups: [
            { name: '菜系', tags: ['川菜', '粤菜', '湘菜', '鲁菜', '江浙菜', '西北菜', '东北菜', '闽南菜', '云南菜', '新疆菜', '韩式', '日式', '泰式', '意式', '美式'] },
            { name: '场景', tags: ['早餐', '下午茶', '夜宵', '一人食', '家庭聚餐', '亲子友好', '朋友小聚', '节日家宴', '户外野餐'] },
            { name: '食材', tags: ['海鲜', '肉食', '素食', '菌菇', '豆制品', '根茎类', '叶菜类', '禽蛋类', '谷物', '坚果', '时令水果'] },
            { name: '做法', tags: ['烧烤', '火锅', '煎炸', '蒸煮', '焖炖', '凉拌', '烘焙', '爆炒', '煲汤'] },
            { name: '特色', tags: ['非遗', '网红', '新手友好', '减脂轻食', '下饭神器', '下酒小菜', '复刻经典'] }
        ],
        loading: false
    }),
    created() {
        if (!isLoggedIn()) {
            this.$message.warning('请先登录')
            return this.$router.push('/login')
        }
    },
    methods: {
        goBack() { this.$router.back() },
        
        onCoverSuccess(res) {
            if (res.code === 200) {
                this.form.cover = res.data
                this.$message.success('封面上传成功')
            } else {
                this.$message.error('上传失败')
            }
        },
        
        onContentChange(content) { this.form.content = content },
        
        async publish() {
            if (!this.form.title || !this.form.title.trim()) return this.$message.warning('请输入标题')
            if (!this.form.cover) return this.$message.warning('请上传封面图片')
            if (!this.form.content || !this.form.content.trim()) return this.$message.warning('请输入内容')
            
            this.loading = true
            try {
                const { data } = await this.$axios.post('/gourmet/save', this.form)
                if (data.code === 200) {
                    this.$message.success('发布成功！')
                    setTimeout(() => this.$router.push('/mine'), 1000)
                } else {
                    this.$message.error(data.msg || '发布失败')
                }
            } catch (e) {
                this.$message.error('发布失败，请重试')
            } finally {
                this.loading = false
            }
        }
    }
}
</script>

<style scoped lang="scss">
$primary: #FF6B35;

.create-page { min-height: 100vh; background: #f5f7fa }

.header {
    display: flex; justify-content: space-between; align-items: center; padding: 16px 24px;
    background: #fff; border-bottom: 1px solid #eee; position: sticky; top: 0; z-index: 100;
    h1 { font-size: 20px; margin: 0 }
    .back-btn { font-size: 22px; padding: 8px; cursor: pointer }
    .el-button { padding: 10px 24px; border-radius: 20px; i { margin-right: 6px } }
}

.content { display: flex; max-width: 1400px; margin: 0 auto; padding: 24px; gap: 24px }

.sidebar { width: 320px; flex-shrink: 0 }
.main { flex: 1; min-width: 0 }

.card { background: #fff; border-radius: 16px; padding: 20px; margin-bottom: 16px; box-shadow: 0 2px 12px rgba(0,0,0,0.04);
    h3 { display: flex; align-items: center; gap: 8px; margin: 0 0 16px; font-size: 16px; i { color: $primary } }
}

.category-select { width: 100% }
::v-deep .el-select-group__title { 
    color: #FF6B35 !important; 
    font-weight: 600; 
    font-size: 14px;
    padding-left: 10px;
    border-bottom: 1px solid rgba(255, 107, 53, 0.2);
    background: linear-gradient(90deg, rgba(255, 107, 53, 0.08), transparent);
}
::v-deep .el-select-dropdown__item:hover { background-color: rgba(255, 107, 53, 0.1) }
::v-deep .el-select-dropdown__item.selected { color: #FF6B35; font-weight: 600 }

.uploader { width: 100%;
    ::v-deep .el-upload { width: 100% }
    ::v-deep .el-upload-dragger { width: 100%; height: auto; border: 2px dashed #eee; border-radius: 12px; background: #f5f7fa;
        &:hover { border-color: $primary }
    }
    .preview { position: relative; width: 100%; height: 180px; border-radius: 12px; overflow: hidden;
        img { width: 100%; height: 100%; object-fit: cover }
        .overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.5); display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; opacity: 0; transition: 0.3s;
            i, span { color: #fff }
        }
        &:hover .overlay { opacity: 1 }
    }
    .placeholder { display: flex; flex-direction: column; align-items: center; padding: 40px 20px;
        i { font-size: 36px; color: #999; margin-bottom: 12px }
        span { color: #666; margin-bottom: 8px }
        p { color: #999; font-size: 12px; margin: 0 }
    }
}

.toggle { display: flex; justify-content: space-between; align-items: center }
.hint { font-size: 12px; color: #999; margin: 12px 0 0 }

.tips { background: linear-gradient(135deg, #fff5f0, #fff); border: 1px solid rgba(255,107,53,0.1);
    ul { margin: 0; padding-left: 20px; li { font-size: 13px; color: #666; line-height: 1.8 } }
}

.title-card { position: relative;
    input { width: 100%; border: none; outline: none; font-size: 24px; font-weight: 600; padding-right: 60px;
        &::placeholder { color: #999; font-weight: 400 }
    }
    .count { position: absolute; right: 24px; bottom: 24px; font-size: 12px; color: #999 }
}

.editor-card { overflow: hidden }

@media (max-width: 1024px) {
    .content { flex-direction: column }
    .sidebar { width: 100% }
}
</style>
