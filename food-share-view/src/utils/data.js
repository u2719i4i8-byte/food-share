/**
 * 数据处理工具类
 * 功能：时间格式化等数据处理函数
 */

/**
 * 将日期字符串转换为相对时间描述
 * @param {string} dateString - 日期字符串
 * @returns {string} 相对时间描述（如：5分钟前、2小时前、3天前）
 */
export function timeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);
    const secondsPast = (now.getTime() - date.getTime()) / 1000;
    
    // 小于1分钟
    if (secondsPast < 60) {
        return `${Math.floor(secondsPast)} 秒前`;
    } 
    // 小于1小时
    else if (secondsPast < 3600) {
        return `${Math.floor(secondsPast / 60)} 分钟前`;
    } 
    // 小于等于1天
    else if (secondsPast <= 86400) {
        return `${Math.floor(secondsPast / 3600)} 小时前`;
    } 
    // 大于1天
    else {
        const daysPast = Math.floor(secondsPast / 86400);
        if (daysPast === 1) {
            return '1 天前';
        } else {
            return `${daysPast} 天前`;
        }
    }
}
