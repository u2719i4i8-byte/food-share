<template>
    <div class="gourmet-manage-page">
        <div class="filter-bar">
            <el-select style="width: 120px;" @change="fetchFreshData" size="small"
                v-model="gourmetQueryDto.categoryId" placeholder="菜系">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
            </el-select>
            <el-select style="width: 120px;" @change="fetchFreshData" size="small" v-model="gourmetQueryDto.isAudit"
                placeholder="审核状态">
                <el-option v-for="item in auditStatus" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-select style="width: 120px;" @change="fetchFreshData" size="small"
                v-model="gourmetQueryDto.isPublish" placeholder="公开状态">
                <el-option v-for="item in publishStatus" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-date-picker style="width: 240px;" @change="fetchFreshData" size="small" v-model="searchTime"
                type="daterange" range-separator="至" start-placeholder="发布开始" end-placeholder="发布结束">
            </el-date-picker>
            <el-input size="small" style="width: 200px;" v-model="gourmetQueryDto.title" placeholder="搜索美食标题"
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
                <el-table-column prop="title" label="美食标题" min-width="150">
                    <template slot-scope="scope">
                        <span class="title-text">{{ scope.row.title }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" label="发布者" width="100">
                    <template slot-scope="scope">
                        <span class="user-name">{{ scope.row.userName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName" label="菜系" width="90">
                    <template slot-scope="scope">
                        <span class="category-tag">{{ scope.row.categoryName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" :sortable="true" label="发布时间" width="160">
                    <template slot-scope="scope">
                        <span class="time-text">{{ scope.row.createTime }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="isAudit" label="审核状态" width="100">
                    <template slot-scope="scope">
                        <span :class="['status-tag', scope.row.isAudit ? 'success' : 'warning']">
                            <i :class="scope.row.isAudit ? 'el-icon-success' : 'el-icon-warning'"></i>
                            {{ scope.row.isAudit ? '已审核' : '待审核' }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="isPublish" label="公开状态" width="100">
                    <template slot-scope="scope">
                        <span :class="['status-tag', scope.row.isPublish ? 'info' : 'danger']">
                            <i :class="scope.row.isPublish ? 'el-icon-unlock' : 'el-icon-lock'"></i>
                            {{ scope.row.isPublish ? '公开' : '私有' }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="140" fixed="right">
                    <template slot-scope="scope">
                        <div class="action-buttons">
                            <span v-if="!scope.row.isAudit" class="action-btn edit" @click="handleEdit(scope.row)">
                                <i class="el-icon-check"></i> 审核
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
        
        <el-dialog :show-close="true" :visible.sync="dialogGourmetOperaion" width="400px" custom-class="confirm-dialog">
            <div class="dialog-content">
                <div class="dialog-icon warning">
                    <i class="el-icon-warning-outline"></i>
                </div>
                <div class="dialog-title">审核确认</div>
                <div class="dialog-message">确定审核通过【{{ data.title }}】这篇美食帖子吗？</div>
            </div>
            <span slot="footer" class="dialog-footer">
                <button class="btn-cancel" @click="cannel()">取消操作</button>
                <button class="btn-confirm" @click="auditOpetaion()">确定审核</button>
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
            dialogGourmetOperaion: false,
            isOperation: false,
            tableData: [],
            delectedRows: [],
            searchTime: [],
            gourmetQueryDto: {},
            categories: [],
            auditStatus: [{ value: null, label: '全部' }, { value: 0, label: '未审核' }, { value: 1, label: '已审核' }],
            publishStatus: [{ value: null, label: '全部' }, { value: 0, label: '私有' }, { value: 1, label: '公开' }],
        };
    },
    created() {
        this.fetchFreshData();
        this.fetchFreshCategories();
    },
    methods: {
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
        cannel() {
            this.data = {};
            this.dialogGourmetOperaion = false;
            this.isOperation = false;
        },
        async batchDelete() {
            if (!this.delectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除美食做法数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.delectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/gourmet/batchDelete`, ids);
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
                    this.$message.error("美食做法信息删除异常：", error);
                    console.error(`美食做法信息删除异常：`, error);
                }
            }
        },
        async auditOpetaion() {
            try {
                const response = await this.$axios.put(`/gourmet/audit/${this.data.id}`);
                if (response.data.code === 200) {
                    this.$notify({
                        duration: 1000,
                        title: '审核成功',
                        message: response.data.msg,
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
                    ...this.gourmetQueryDto
                };
                const response = await this.$axios.post('/gourmet/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                this.$message.error("查询美食做法信息异常:", error);
                console.error('查询美食做法信息异常:', error);
            }
        },
        add() {
            this.dialogGourmetOperaion = true;
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
            this.dialogGourmetOperaion = true;
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
.gourmet-manage-page {
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
        
        ::v-deep .el-select, ::v-deep .el-input, ::v-deep .el-date-editor {
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
            border-radius: 8px;
            object-fit: cover;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .title-text {
            font-weight: 500;
            color: #333;
        }
        
        .user-name {
            color: #666;
        }
        
        .category-tag {
            display: inline-block;
            padding: 4px 10px;
            background: rgba(255, 107, 53, 0.1);
            color: #FF6B35;
            border-radius: 20px;
            font-size: 12px;
        }
        
        .time-text {
            color: #999;
            font-size: 13px;
        }
        
        .status-tag {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            
            i {
                font-size: 14px;
            }
            
            &.success {
                background: rgba(52, 199, 89, 0.1);
                color: #34c759;
            }
            
            &.warning {
                background: rgba(255, 149, 0, 0.1);
                color: #ff9500;
            }
            
            &.danger {
                background: rgba(255, 59, 48, 0.1);
                color: #ff3b30;
            }
            
            &.info {
                background: rgba(0, 122, 255, 0.1);
                color: #007aff;
            }
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

::v-deep .confirm-dialog {
    border-radius: 16px;
    overflow: hidden;
    
    .el-dialog__header {
        display: none;
    }
    
    .el-dialog__body {
        padding: 0;
        
        .dialog-content {
            text-align: center;
            padding: 30px 20px;
            
            .dialog-icon {
                width: 64px;
                height: 64px;
                margin: 0 auto 20px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                
                &.warning {
                    background: rgba(255, 149, 0, 0.1);
                    
                    i {
                        font-size: 32px;
                        color: #ff9500;
                    }
                }
            }
            
            .dialog-title {
                font-size: 18px;
                font-weight: 600;
                color: #333;
                margin-bottom: 8px;
            }
            
            .dialog-message {
                font-size: 14px;
                color: #666;
            }
        }
    }
    
    .el-dialog__footer {
        padding: 0 20px 24px;
        
        .dialog-footer {
            display: flex;
            justify-content: center;
            gap: 16px;
            
            .btn-cancel, .btn-confirm {
                padding: 10px 32px;
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
