<template>
    <div class="category-manage-page">
        <div class="filter-bar">
            <el-input size="small" style="width: 220px;" v-model="categoryQueryDto.name" placeholder="搜索类别名" clearable
                @clear="handleFilterClear">
                <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
            </el-input>
            <button class="add-btn" @click="add()">
                <i class="el-icon-plus"></i> 新增美食类别
            </button>
        </div>
        
        <div class="table-container">
            <el-table :stripe="true" :data="tableData" style="width: 100%">
                <el-table-column prop="id" label="ID" width="80">
                    <template slot-scope="scope">
                        <span class="id-text">#{{ scope.row.id }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="美食类别名" min-width="200">
                    <template slot-scope="scope">
                        <span class="category-name">{{ scope.row.name }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
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
        
        <el-dialog :show-close="true" :visible.sync="dialogCategoryOperaion" width="400px" custom-class="category-dialog">
            <div class="dialog-header">
                <span class="dialog-title">{{ isOperation ? '编辑类别' : '新增类别' }}</span>
            </div>
            <div class="dialog-body">
                <div class="form-item">
                    <label>类别名</label>
                    <input class="form-input" v-model="data.name" placeholder="请输入美食类别名" />
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
export default {
    data() {
        return {
            data: {},
            filterText: '',
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            dialogCategoryOperaion: false,
            isOperation: false,
            tableData: [],
            delectedRows: [],
            categoryQueryDto: {},
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        cannel() {
            this.data = {};
            this.dialogCategoryOperaion = false;
            this.isOperation = false;
        },
        async batchDelete() {
            if (!this.delectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除美食类别数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.delectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/category/batchDelete`, ids);
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
                    this.$message.error("美食类别信息删除异常：", error);
                    console.error(`美食类别信息删除异常：`, error);
                }
            }
        },
        async updateOperation() {
            try {
                const response = await this.$axios.put('/category/update', this.data);
                if (response.data.code === 200) {
                    this.$notify({
                        duration: 1000,
                        title: '信息修改',
                        message: '修改成功',
                        type: 'success'
                    });
                    this.cannel();
                    this.fetchFreshData();
                }
            } catch (error) {
                console.error('修改出错:', error);
            }
        },
        async addOperation() {
            try {
                const response = await this.$axios.post('/category/save', this.data);
                if (response.data.code === 200) {
                    this.$notify({
                        duration: 1000,
                        title: '信息新增',
                        message: '新增成功',
                        type: 'success'
                    });
                    this.cannel();
                    this.fetchFreshData();
                }
            } catch (error) {
                console.error('信息新增出错:', error);
                this.$message.error('提交失败，请稍后再试！');
            }
        },
        async fetchFreshData() {
            try {
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    key: this.filterText,
                    ...this.categoryQueryDto
                };
                const response = await this.$axios.post('/category/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                this.$message.error("查询美食类别信息异常:", error);
                console.error('查询美食类别信息异常:', error);
            }
        },
        add() {
            this.dialogCategoryOperaion = true;
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
            this.dialogCategoryOperaion = true;
            this.isOperation = true;
            this.data = { ...row }
        },
        handleDelete(row) {
            this.delectedRows.push(row);
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
.category-manage-page {
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
        
        ::v-deep .el-input {
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
        
        .add-btn {
            padding: 8px 16px;
            background: linear-gradient(135deg, #FF6B35 0%, #f7931e 100%);
            color: #fff;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            
            &:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
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
        
        .id-text {
            color: #999;
            font-size: 13px;
        }
        
        .category-name {
            font-weight: 500;
            color: #333;
            padding: 6px 12px;
            background: rgba(255, 107, 53, 0.1);
            border-radius: 8px;
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

::v-deep .category-dialog {
    border-radius: 16px;
    overflow: hidden;
    
    .el-dialog__header {
        display: none;
    }
    
    .el-dialog__body {
        padding: 0;
        
        .dialog-header {
            padding: 20px 24px;
            background: linear-gradient(135deg, #FF6B35 0%, #f7931e 100%);
            
            .dialog-title {
                font-size: 18px;
                font-weight: 600;
                color: #fff;
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
            }
        }
    }
    
    .el-dialog__footer {
        padding: 0 24px 24px;
        
        .dialog-footer {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            
            .btn-cancel, .btn-confirm {
                padding: 10px 24px;
                border-radius: 10px;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                border: none;
            }
            
            .btn-cancel {
                background: #f5f5f5;
                color: #666;
                
                &:hover {
                    background: #eee;
                }
            }
            
            .btn-confirm {
                background: linear-gradient(135deg, #FF6B35 0%, #f7931e 100%);
                color: #fff;
                
                &:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
                }
            }
        }
    }
}
</style>
