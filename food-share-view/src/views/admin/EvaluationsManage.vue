<template>
    <div class="evaluations-manage-page">
        <div class="filter-bar">
            <el-date-picker size="small" style="width: 240px;" v-model="searchTime" type="daterange"
                range-separator="至" start-placeholder="评论开始" end-placeholder="评论结束">
            </el-date-picker>
            <el-input size="small" style="width: 200px;" v-model="evalustionsQueryDto.content" placeholder="评论内容"
                clearable @clear="handleFilterClear">
                <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
            </el-input>
        </div>
        
        <div class="table-container">
            <el-table :stripe="true" :data="tableData" style="width: 100%">
                <el-table-column prop="content" label="评论内容" min-width="200">
                    <template slot-scope="scope">
                        <el-tooltip class="item" effect="dark" :content="scope.row.content" placement="bottom">
                            <span class="content-text">{{ scope.row.content }}</span>
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column prop="upvoteList" width="80" label="点赞数">
                    <template slot-scope="scope">
                        <span class="like-count">
                            <i class="el-icon-star-on"></i>
                            {{ scope.row.upvoteCount || 0 }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="contentType" width="100" label="挂载源">
                    <template slot-scope="scope">
                        <span class="source-tag">{{ scope.row.contentType }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" width="100" label="评论者">
                    <template slot-scope="scope">
                        <span class="user-name">{{ scope.row.userName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="replierName" width="100" label="被回复">
                    <template slot-scope="scope">
                        <span class="reply-name" v-if="scope.row.replierName">{{ scope.row.replierName }}</span>
                        <span class="no-reply" v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column prop="parentId" width="80" label="层级">
                    <template slot-scope="scope">
                        <span :class="['level-tag', scope.row.parentId ? 'child' : 'parent']">
                            {{ scope.row.parentId ? '子级' : '父级' }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" width="160" label="评论时间" :sortable="true">
                    <template slot-scope="scope">
                        <span class="time-text">{{ scope.row.createTime }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right">
                    <template slot-scope="scope">
                        <div class="action-buttons">
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
        
        <el-dialog :show-close="true" :visible.sync="reportDialog" width="500px" custom-class="report-dialog">
            <div class="dialog-header">
                <span class="dialog-title">举报详情</span>
            </div>
            <div class="dialog-body">
                <div class="chart-section">
                    <PieChart :types="types" :values="values" />
                </div>
                <div class="stats-section" v-if="reportsDate.length">
                    <div class="stat-item" v-for="(entity, index) in reportsDate" :key="index">
                        <span class="stat-count">{{ entity.count }}</span>
                        <span class="stat-name">{{ entity.name }}</span>
                    </div>
                </div>
                <div class="empty-state" v-else>
                    <i class="el-icon-document"></i>
                    <span>暂无举报数据</span>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <button class="btn-cancel" @click="reportDialog = false">关闭</button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import PieChart from "@/components/PieChart"
export default {
    components: {
        PieChart
    },
    data() {
        return {
            data: { cover: '' },
            reportsDate: [],
            filterText: '',
            tableData: [],
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            dialogOperaion: false,
            isOperation: false,
            searchTime: [],
            selectedRows: [],
            reportDialog: false,
            types: [],
            values: [],
            evalustionsQueryDto: {},
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        reportsList(row) {
            this.reportDialog = true;
            this.reportList(row.id);
        },
        async reportList(id) {
            const response = await this.$axios(`/evaluations-reports/reportCount/${id}`);
            const { data } = response;
            this.reportsDate = data.data;
            this.types = this.reportsDate.map(entity => entity.name);
            this.values = this.reportsDate.map(entity => entity.count);
        },
        handleSelectionChange(selection) {
            this.selectedRows = selection;
        },
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除评论数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/evaluations/batchDelete`, ids);
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
                } catch (e) {
                    this.$message.error("评论信息删除异常：", e);
                    console.error(`评论信息删除异常：`, e);
                }
            }
        },
        highlightKeyword(text) {
            if (text == null) {
                return;
            }
            if (this.filterText != '') {
                const regex = new RegExp(this.filterText, 'gi');
                return text.replace(regex, '<span class="highlight" style="background-color: #bec936;color:#373834;">$&</span>');
            }
            return text;
        },
        async fetchFreshData() {
            try {
                let startTime = '';
                let endTime = '';
                if (this.searchTime !== null && this.searchTime.length === 2) {
                    const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
                    startTime = `${startDate.split('T')[0]}T00:00:00`;
                    endTime = `${endDate.split('T')[0]}T23:59:59`;
                }
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    startTime: startTime,
                    endTime: endTime,
                    ...this.evalustionsQueryDto,
                };
                let response = await this.$axios.post('/evaluations/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('Fetch data failed:', error);
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
        async handleDelete(row) {
            this.selectedRows.push(row);
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
.evaluations-manage-page {
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
        
        ::v-deep .el-input, ::v-deep .el-date-editor {
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
        
        .content-text {
            color: #333;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .like-count {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            font-weight: 600;
            color: #FF6B35;
            
            i {
                font-size: 16px;
            }
        }
        
        .source-tag {
            display: inline-block;
            padding: 4px 10px;
            background: rgba(102, 126, 234, 0.1);
            color: #667eea;
            font-size: 12px;
            border-radius: 6px;
        }
        
        .user-name {
            font-weight: 500;
            color: #333;
        }
        
        .reply-name {
            color: #666;
        }
        
        .no-reply {
            color: #ccc;
        }
        
        .level-tag {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 500;
            
            &.parent {
                background: rgba(255, 107, 53, 0.1);
                color: #FF6B35;
            }
            
            &.child {
                background: rgba(17, 153, 142, 0.1);
                color: #11998e;
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

.report-dialog {
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
        
        .chart-section {
            display: flex;
            justify-content: center;
            margin-bottom: 24px;
        }
        
        .stats-section {
            display: flex;
            justify-content: center;
            gap: 24px;
            flex-wrap: wrap;
            
            .stat-item {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 8px;
                
                .stat-count {
                    font-size: 24px;
                    font-weight: 700;
                    color: #FF6B35;
                }
                
                .stat-name {
                    font-size: 13px;
                    color: #666;
                }
            }
        }
        
        .empty-state {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 12px;
            padding: 40px 0;
            color: #999;
            
            i {
                font-size: 48px;
                opacity: 0.5;
            }
        }
    }
    
    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        padding: 16px 24px;
        
        .btn-cancel {
            padding: 10px 24px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            background: #f5f6f7;
            border: none;
            color: #666;
            
            &:hover {
                background: #e8e9eb;
            }
        }
    }
}
</style>
