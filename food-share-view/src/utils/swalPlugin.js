/**
 * SweetAlert2弹窗插件
 * 功能：封装SweetAlert2确认弹窗，提供统一的弹窗样式
 * 
 * 使用示例：
 * this.$swalConfirm({
 *     title: '删除确认',
 *     text: '确定要删除这条记录吗？',
 *     icon: 'warning'
 * }).then(confirmed => {
 *     if (confirmed) {
 *         // 用户点击确认
 *     }
 * });
 * 
 * 图标类型：success、error、info、warning、question
 */

import Swal from 'sweetalert2';

const swalPlugin = {
  /**
   * Vue插件安装方法
   * @param {Object} Vue - Vue实例
   */
  install(Vue) {
    /**
     * 确认弹窗方法
     * @param {Object} options - 弹窗配置选项
     * @param {string} options.title - 弹窗标题
     * @param {string} options.text - 弹窗内容
     * @param {string} options.icon - 图标类型
     * @returns {Promise<boolean>} 用户是否确认
     */
    Vue.prototype.$swalConfirm = async function(options = {}) {
      const defaultOptions = {
        title: '提示',
        text: '',
        icon: 'info',
        reverseButtons: true,
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        customClass: {
          confirmButton: 'sweet-btn-primary',
        },
        ...options,
      };

      try {
        const result = await Swal.fire(defaultOptions);
        return result.isConfirmed;
      } catch (error) {
        console.error('Swal Error:', error);
        return false;
      }
    };
  },
};

export default swalPlugin;
