<template>
    <div class="cookbook-manage-page">
        <div class="filter-bar">
            <button class="add-btn" @click="add()">
                <i class="el-icon-plus"></i> 新增食谱
            </button>
            <el-select style="width: 120px;" @change="fetchFreshData" size="small"
                v-model="cookbookQueryDto.categoryId" placeholder="菜系">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
            </el-select>
            <el-select style="width: 120px;" @change="fetchFreshData" size="small"
                v-model="cookbookQueryDto.isPublish" placeholder="公开状态">
                <el-option v-for="item in publishStatus" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-date-picker style="width: 240px;" @change="fetchFreshData" size="small" v-model="searchTime"
                type="daterange" range-separator="至" start-placeholder="发布开始" end-placeholder="发布结束">
            </el-date-picker>
            <el-input size="small" style="width: 200px;" v-model="cookbookQueryDto.title" placeholder="食谱名称"
                clearable @clear="handleFilterClear">
                <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
            </el-input>
        </div>
        
        <div class="table-container">
            <el-table :stripe="true" :data="tableData" style="width: 100%">
                <el-table-column prop="cover" label="封面" width="100">
                    <template slot-scope="scope">
                        <img :src="scope.row.cover" class="cover-image" />
                    </template>
                </el-table-column>
                <el-table-column prop="title" label="食谱名" min-width="150">
                    <template slot-scope="scope">
                        <span class="title-text">{{ scope.row.title }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" label="发布者" width="100">
                    <template slot-scope="scope">
                        <span class="user-name">{{ scope.row.userName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName" label="所属类别" width="100">
                    <template slot-scope="scope">
                        <span class="category-tag">{{ scope.row.categoryName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="isPublish" label="公开状态" width="100">
                    <template slot-scope="scope">
                        <span :class="['status-tag', scope.row.isPublish ? 'public' : 'private']">
                            <i :class="scope.row.isPublish ? 'el-icon-unlock' : 'el-icon-lock'"></i>
                            {{ scope.row.isPublish ? '公开' : '私人' }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" :sortable="true" width="160">
                    <template slot-scope="scope">
                        <span class="time-text">{{ scope.row.createTime }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="140" fixed="right">
                    <template slot-scope="scope">
                        <div class="action-buttons">
                            <span class="action-btn edit" @click="handleEdit(scope.row)">
                                <i class="el-icon-edit"></i> 编辑
                            </span>
                            <span class="action-btn delete" @click="handleDelete(scope.row)">
                                <i class="el-icon-delete"></i> 删除
                            </span>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin:16px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[20, 50]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </div>
        
        <el-dialog :show-close="true" :visible.sync="dialogCookbookOperaion" width="600px" custom-class="cookbook-dialog">
            <div class="dialog-header">
                <span class="dialog-title">{{ isOperation ? '编辑食谱' : '新增食谱' }}</span>
            </div>
            <div class="dialog-body">
                <div class="form-item">
                    <label>食谱封面</label>
                    <el-upload class="avatar-uploader" action="/api/food-share-sys/v1.0/file/upload" :show-file-list="false"
                        :on-success="handleCoverSuccess">
                        <img v-if="cover" :src="cover" class="cover-preview">
                        <div v-else class="cover-placeholder">
                            <i class="el-icon-plus"></i>
                        </div>
                    </el-upload>
                </div>
                <div class="form-item">
                    <label>食谱名称</label>
                    <input class="form-input" v-model="data.title" placeholder="请输入食谱名称" />
                </div>
                <div class="form-item">
                    <label>所属菜系</label>
                    <el-select style="width: 100%;" size="small" v-model="data.categoryId" placeholder="选择菜系">
                        <el-option v-for="item in categories.filter(c => c.id)" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </div>
                <div class="form-item">
                    <label>食谱内容</label>
                    <Editor height="250px" :receiveContent="data.content" @on-receive="onReceive" />
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <button class="btn-cancel" @click="cannel()">取消操作</button>
                <button v-if="!isOperation" class="btn-confirm" @click="addOperation()">确定新增</button>
                <button v-else class="btn-confirm" @click="updateOperation()">确定修改</button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import Editor from "@/components/Editor"
export default {
    components: { Editor },
    data() {
        return {
            data: {},
            filterText: '',
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            tableData: [],
            delectedRows: [],
            searchTime: [],
            cover: null,
            dialogCookbookOperaion: false,
            isOperation: false,
            cookbookQueryDto: {},
            publishStatus: [{ value: null, label: '全部' }, { value: 0, label: '私人' }, { value: 1, label: '公开' }],
            categories: [],
        };
    },
    created() {
        this.fetchFreshData();
        this.fetchFreshCategories();
    },
    methods: {
        handleCoverSuccess(res, file) {
            this.$notify({
                duration: 1500,
                title: '封面上传',
                message: res.code === 200 ? '上传成功' : '上传失败',
                type: res.code === 200 ? 'success' : 'error'
            });
            if (res.code === 200) {
                this.cover = res.data;
            }
        },
        cannel() {
            this.data = {};
            this.cover = null;
            this.dialogCookbookOperaion = false;
            this.isOperation = false;
        },
        addOperation() {
            this.data.cover = this.cover;
            this.$axios.post('/cookbook/backSave', this.data).then(res => {
                if (res.data.code === 200) {
                    this.$notify({
                        duration: 1000,
                        title: '食谱操作',
                        message: '新增成功',
                        type: 'success'
                    });
                    this.cannel();
                    this.fetchFreshData();
                }
            }).catch(error => {
                console.log("新增食谱异常：", error);
            });
        },
        updateOperation() {
            this.data.cover = this.cover;
            this.$axios.put('/cookbook/update', this.data).then(res => {
                if (res.data.code === 200) {
                    this.$notify({
                        duration: 1000,
                        title: '食谱操作',
                        message: '修改成功',
                        type: 'success'
                    });
                    this.cannel();
                    this.fetchFreshData();
                }
            }).catch(error => {
                console.log("修改食谱异常：", error);
            });
        },
        fetchFreshCategories() {
            this.$axios.post('/category/query', {}).then(res => {
                if (res.data.code === 200) {
                    this.categories = res.data.data;
                    const allCategory = { id: null, name: '全部' };
                    this.categories.unshift(allCategory);
                }
            }).catch(error => {
                console.log("查询美食做法异常：", error);
            });
        },
        onReceive(content) {
            this.data.content = content;
        },
        add() {
            this.dialogCookbookOperaion = true;
        },
        async batchDelete() {
            if (!this.delectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除食谱数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.delectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/cookbook/batchDelete`, ids);
                    if (response.data.code === 200) {
                        this.$notify({
                            duration: 1000,
                            title: '信息删除',
                            message: '删除成功',
                            type: 'success'
                        });
                        this.fetchFreshData();
                        return;
                    }
                } catch (error) {
                    this.$message.error("食谱删除异常：", error);
                    console.error(`食谱删除异常：`, error);
                }
            }
        },
        async fetchFreshData() {
            try {
                this.tableData = [];
                let startTime = null;
                let endTime = null;
                if (this.searchTime != null && this.searchTime.length === 2) {
                    const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
                    startTime = `${startDate.split('T')[0]}T00:00:00`;
                    endTime = `${endDate.split('T')[0]}T23:59:59`;
                }
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    key: this.filterText,
                    startTime: startTime,
                    endTime: endTime,
                    ...this.cookbookQueryDto
                };
                const response = await this.$axios.post('/cookbook/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                this.$message.error("查询食谱异常:", error);
                console.error('查询食谱异常:', error);
            }
        },
        handleFilter() {
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleFilterClear() {
            this.filterText = '';
            this.handleFilter();
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchFreshData();
        },
        handleEdit(row) {
            this.data = row;
            this.cover = row.cover;
            this.dialogCookbookOperaion = true;
            this.isOperation = true;
        },
        handleDelete(row) {
            this.delectedRows.push(row);
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
.cookbook-manage-page {
    background: #fff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    
    .filter-bar {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px 20px;
        background: #fafafa;
        border-radius: 12px;
        margin-bottom: 20px;
        flex-wrap: wrap;
        
        .add-btn {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 10px 20px;
            background: linear-gradient(135deg, #FF6B35 0%, #f7931e 100%);
            color: #fff;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            border: none;
            box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
            
            &:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 16px rgba(255, 107, 53, 0.4);
            }
        }
        
        ::v-deep .el-input, ::v-deep .el-date-editor, ::v-deep .el-select {
            .el-input__inner {
                border-radius: 8px;
                border-color: #eee;
                
                &:focus {
                    border-color: #FF6B35;
                }
            }
        }
        
        ::v-deep .el-input-group__append {
            background: linear-gradient(135deg, #FF6B35 0%, #f7931e 100%);
            border-color: #FF6B35;
            
            .el-button {
                color: #fff;
            }
        }
    }
    
    .table-container {
        ::v-deep .el-table {
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
            
            th {
                background: #fff5f0 !important;
                color: #333;
                font-weight: 600;
                font-size: 14px;
            }
            
            td {
                padding: 16px 12px;
            }
            
            tr:hover > td {
                background: #fffbf8 !important;
            }
        }
        
        .cover-image {
            width: 60px;
            height: 40px;
            object-fit: cover;
            border-radius: 6px;
        }
        
        .title-text {
            color: #333;
            font-weight: 500;
        }
        
        .user-name {
            font-weight: 500;
            color: #333;
        }
        
        .category-tag {
            display: inline-block;
            padding: 4px 10px;
            background: rgba(255, 107, 53, 0.1);
            color: #FF6B35;
            font-size: 12px;
            border-radius: 6px;
        }
        
        .status-tag {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            
            &.public {
                background: rgba(17, 153, 142, 0.1);
                color: #11998e;
            }
            
            &.private {
                background: rgba(255, 149, 0, 0.1);
                color: #ff9500;
            }
        }
        
        .time-text {
            color: #999;
            font-size: 13px;
        }
        
        .action-buttons {
            display: flex;
            gap: 8px;
            
            .action-btn {
                display: inline-flex;
                align-items: center;
                gap: 4px;
                padding: 6px 12px;
                border-radius: 6px;
                font-size: 13px;
                cursor: pointer;
                transition: all 0.3s ease;
                border: none;
                
                &.edit {
                    background: rgba(255, 107, 53, 0.1);
                    color: #FF6B35;
                    
                    &:hover {
                        background: rgba(255, 107, 53, 0.2);
                    }
                }
                
                &.delete {
                    background: rgba(255, 59, 48, 0.1);
                    color: #ff3b30;
                    
                    &:hover {
                        background: rgba(255, 59, 48, 0.2);
                    }
                }
            }
        }
        
        ::v-deep .el-pagination {
            margin-top: 20px;
            padding: 12px 0;
            
            .el-pager li {
                border-radius: 8px;
                margin: 0 4px;
                
                &.active {
                    background: linear-gradient(135deg, #FF6B35 0%, #f7931e 100%);
                    color: #fff;
                }
                
                &:hover {
                    color: #FF6B35;
                }
            }
        }
    }
}

.cookbook-dialog {
    border-radius: 16px;
    
    .dialog-header {
        padding: 20px 24px;
        border-bottom: 1px solid #f0f0f0;
        
        .dialog-title {
            font-size: 18px;
            font-weight: 600;
            color: #333;
        }
    }
    
    .dialog-body {
        padding: 24px;
        
        .form-item {
            margin-bottom: 20px;
            
            label {
                display: block;
                margin-bottom: 8px;
                font-size: 14px;
                font-weight: 500;
                color: #666;
            }
            
            .form-input {
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
            
            .avatar-uploader {
                .cover-preview {
                    width: 120px;
                    height: 80px;
                    object-fit: cover;
                    border-radius: 10px;
                }
                
                .cover-placeholder {
                    width: 120px;
                    height: 80px;
                    border-radius: 10px;
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
    
    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        padding: 16px 24px;
        
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
