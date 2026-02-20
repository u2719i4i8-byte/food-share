<template>
    <div class="user-manage-page">
        <div class="filter-bar">
            <el-select @change="fetchFreshData" size="small" v-model="userQueryDto.isLogin" placeholder="登录状态">
                <el-option v-for="item in loginStatuList" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <el-select @change="fetchFreshData" size="small" v-model="userQueryDto.isWord" placeholder="禁言状态">
                <el-option v-for="item in wordStatuList" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <el-select @change="fetchFreshData" size="small" v-model="userQueryDto.role" placeholder="用户角色">
                <el-option v-for="item in rolesList" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <el-date-picker @change="fetchFreshData" size="small" v-model="searchTime" type="daterange" range-separator="至" start-placeholder="注册开始" end-placeholder="注册结束"></el-date-picker>
            <el-input size="small" style="width: 180px;" v-model="userQueryDto.userName" placeholder="搜索用户名" clearable @clear="handleFilterClear">
                <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
            </el-input>
            <button class="add-btn" @click="add()">
                <i class="el-icon-plus"></i> 新增用户
            </button>
        </div>
        
        <div class="table-container">
            <el-table :stripe="true" :data="tableData" style="width: 100%">
                <el-table-column prop="userAvatar" width="80" label="头像">
                    <template slot-scope="scope">
                        <el-avatar :size="40" :src="scope.row.userAvatar"></el-avatar>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" label="名称" min-width="100">
                    <template slot-scope="scope">
                        <span class="user-name">{{ scope.row.userName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userAccount" width="140" label="账号">
                    <template slot-scope="scope">
                        <span class="account-text">{{ scope.row.userAccount }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userEmail" width="180" label="邮箱">
                    <template slot-scope="scope">
                        <span class="email-text">{{ scope.row.userEmail }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userRole" width="90" label="角色">
                    <template slot-scope="scope">
                        <span :class="['role-tag', scope.row.userRole === 1 ? 'admin' : 'user']">
                            {{ scope.row.userRole === 1 ? '管理员' : '用户' }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="isLogin" width="90" label="封号状态">
                    <template slot-scope="scope">
                        <span :class="['status-tag', scope.row.isLogin ? 'danger' : 'success']">
                            <i :class="scope.row.isLogin ? 'el-icon-warning' : 'el-icon-success'"></i>
                            {{ scope.row.isLogin ? '已封号' : '正常' }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="isWord" width="90" label="禁言状态">
                    <template slot-scope="scope">
                        <span :class="['status-tag', scope.row.isWord ? 'warning' : 'success']">
                            <i :class="scope.row.isWord ? 'el-icon-warning' : 'el-icon-success'"></i>
                            {{ scope.row.isWord ? '已禁言' : '正常' }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column :sortable="true" prop="createTime" width="160" label="注册时间">
                    <template slot-scope="scope">
                        <span class="time-text">{{ scope.row.createTime }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right">
                    <template slot-scope="scope">
                        <div class="action-buttons">
                            <span class="action-btn setting" @click="handleStatus(scope.row)">
                                <i class="el-icon-setting"></i> 状态
                            </span>
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
        
        <el-dialog :show-close="true" :visible.sync="dialogUserOperaion" width="480px" custom-class="user-dialog">
            <div class="dialog-header">
                <span class="dialog-title">{{ isOperation ? '编辑用户' : '新增用户' }}</span>
            </div>
            <div class="dialog-body">
                <div class="form-item">
                    <label>用户头像</label>
                    <el-upload class="avatar-uploader" action="/api/food-share-sys/v1.0/file/upload" :show-file-list="false" :on-success="handleAvatarSuccess">
                        <img v-if="userAvatar" :src="userAvatar" class="avatar-preview">
                        <div v-else class="avatar-placeholder">
                            <i class="el-icon-plus"></i>
                        </div>
                    </el-upload>
                </div>
                <div class="form-item">
                    <label>用户名</label>
                    <input class="form-input" v-model="data.userName" placeholder="请输入用户名" />
                </div>
                <div class="form-item">
                    <label>账号</label>
                    <input class="form-input" v-model="data.userAccount" placeholder="请输入账号" />
                </div>
                <div class="form-item">
                    <label>邮箱</label>
                    <input class="form-input" v-model="data.userEmail" placeholder="请输入邮箱" />
                </div>
                <div class="form-item">
                    <label>密码 <span class="hint">(修改时留空则不修改密码)</span></label>
                    <input class="form-input" v-model="userPwd" type="password" placeholder="请输入密码" />
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <button class="btn-cancel" @click="cannel()">取消操作</button>
                <button v-if="!isOperation" class="btn-confirm" @click="addOperation()">确定新增</button>
                <button v-else class="btn-confirm" @click="updateOperation()">确定修改</button>
            </span>
        </el-dialog>
        
        <el-dialog :show-close="true" :visible.sync="dialogStatusOperation" width="400px" custom-class="status-dialog">
            <div class="dialog-header">
                <span class="dialog-title">账号状态管理</span>
            </div>
            <div class="dialog-body">
                <div class="status-item">
                    <div class="status-label">
                        <i class="el-icon-warning"></i>
                        <span>封号状态</span>
                    </div>
                    <el-switch inactive-color="#d9d9d9" v-model="data.isLogin" active-text="已封号" inactive-text="正常"></el-switch>
                </div>
                <div class="status-item">
                    <div class="status-label">
                        <i class="el-icon-chat-dot-round"></i>
                        <span>禁言状态</span>
                    </div>
                    <el-switch inactive-color="#d9d9d9" v-model="data.isWord" active-text="已禁言" inactive-text="正常"></el-switch>
                </div>
                <div class="status-item">
                    <div class="status-label">
                        <i class="el-icon-user"></i>
                        <span>用户角色</span>
                    </div>
                    <el-switch inactive-color="#d9d9d9" v-model="isAdmin" active-text="管理员" inactive-text="普通用户"></el-switch>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <button class="btn-cancel" @click="cannel">取消</button>
                <button class="btn-confirm" @click="comfirmStatus">确认修改</button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            userPwd: '',
            userAvatar: '',
            data: {},
            filterText: '',
            isAdmin: false,
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            dialogStatusOperation: false,
            dialogUserOperaion: false,
            isOperation: false,
            tableData: [],
            searchTime: [],
            selectedRows: [],
            userQueryDto: {},
            loginStatuList: [{ value: null, label: '全部' }, { value: 0, label: '正常' }, { value: 1, label: '封号' }],
            wordStatuList: [{ value: null, label: '全部' }, { value: 0, label: '正常' }, { value: 1, label: '禁言' }],
            rolesList: [{ value: null, label: '全部' }, { value: 2, label: '用户' }, { value: 1, label: '管理员' }]
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        cannel() {
            this.data = {};
            this.userAvatar = '';
            this.userPwd = '';
            this.dialogUserOperaion = false;
            this.dialogStatusOperation = false;
            this.isOperation = false;
        },
        comfirmStatus() {
            const userUpdateDto = {
                id: this.data.id,
                isLogin: this.data.isLogin,
                userRole: this.isAdmin ? 1 : 2,
                isWord: this.data.isWord
            }
            this.$axios.put(`/user/backUpdate`, userUpdateDto).then(res => {
                if (res.data.code === 200) {
                    this.$notify({
                        duration: 1500,
                        title: '信息修改',
                        message: '修改成功',
                        type: 'success'
                    });
                    this.cannel();
                    this.fetchFreshData();
                }
            }).catch(error => {
                console.log("修改状态异常：" + error);
            })
        },
        handleStatus(data) {
            this.isAdmin = data.userRole === 1;
            this.dialogStatusOperation = true;
            this.data = data;
        },
        handleAvatarSuccess(res, file) {
            this.$notify({
                duration: 1500,
                title: '头像上传',
                message: res.code === 200 ? '上传成功' : '上传失败',
                type: res.code === 200 ? 'success' : 'error'
            });
            if (res.code === 200) {
                this.userAvatar = res.data;
            }
        },
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除用户数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/user/batchDelete`, ids);
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
                    this.$message.error("用户信息删除异常：", error);
                    console.error(`用户信息删除异常：`, error);
                }
            }
        },
        async updateOperation() {
            if (this.userPwd !== '') {
                const pwd = this.$md5(this.$md5(this.userPwd));
                this.data.userPwd = pwd;
            } else {
                this.data.userPwd = null;
            }
            this.data.userAvatar = this.userAvatar;
            try {
                const response = await this.$axios.put('/user/backUpdate', this.data);
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
            if (this.userPwd !== '') {
                this.data.userPwd = this.$md5(this.$md5(this.userPwd));
            } else {
                this.data.userPwd = null;
            }
            this.data.userAvatar = this.userAvatar;
            try {
                const response = await this.$axios.post('/user/insert', this.data);
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
                    ...this.userQueryDto
                };
                const response = await this.$axios.post('/user/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                this.$message.error("查询用户信息异常:", error);
                console.error('查询用户信息异常:', error);
            }
        },
        add() {
            this.dialogUserOperaion = true;
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
            this.dialogUserOperaion = true;
            this.isOperation = true;
            row.userPwd = null;
            this.userAvatar = row.userAvatar;
            this.data = { ...row }
        },
        handleDelete(row) {
            this.selectedRows.push(row);
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
.user-manage-page {
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
        
        .user-name {
            font-weight: 500;
            color: #333;
        }
        
        .account-text, .email-text {
            color: #666;
        }
        
        .role-tag {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            
            &.admin {
                background: rgba(255, 107, 53, 0.1);
                color: #FF6B35;
            }
            
            &.user {
                background: rgba(0, 122, 255, 0.1);
                color: #007aff;
            }
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
        }
        
        .time-text {
            color: #999;
            font-size: 13px;
        }
        
        .action-buttons {
            display: flex;
            gap: 6px;
            
            .action-btn {
                display: inline-flex;
                align-items: center;
                gap: 4px;
                padding: 5px 10px;
                border-radius: 6px;
                font-size: 12px;
                cursor: pointer;
                transition: all 0.3s ease;
                border: none;
                
                &.setting {
                    background: rgba(0, 122, 255, 0.1);
                    color: #007aff;
                    
                    &:hover {
                        background: rgba(0, 122, 255, 0.2);
                    }
                }
                
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

::v-deep .user-dialog, ::v-deep .status-dialog {
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
                    
                    .hint {
                        color: #999;
                        font-size: 12px;
                        font-weight: normal;
                    }
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
                    .avatar-preview {
                        width: 80px;
                        height: 80px;
                        border-radius: 50%;
                        object-fit: cover;
                    }
                    
                    .avatar-placeholder {
                        width: 80px;
                        height: 80px;
                        border-radius: 50%;
                        background: #f5f6f7;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        border: 2px dashed #ddd;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        
                        &:hover {
                            border-color: #FF6B35;
                        }
                        
                        i {
                            font-size: 24px;
                            color: #999;
                        }
                    }
                }
            }
            
            .status-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 16px;
                background: #fafafa;
                border-radius: 10px;
                margin-bottom: 12px;
                
                .status-label {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    
                    i {
                        font-size: 18px;
                        color: #FF6B35;
                    }
                    
                    span {
                        font-size: 14px;
                        font-weight: 500;
                        color: #333;
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
