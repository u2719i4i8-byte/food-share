package cn.kmbeast.utils;

import java.util.UUID;

/**
 * ID生成工具类
 * 功能：生成唯一标识符
 */
public class IdFactoryUtil {
    
    /**
     * 生成文件ID
     * 使用UUID生成8位唯一标识符
     * 
     * @return 8位唯一ID字符串
     */
    public static String getFileId() {
        return UUID.randomUUID().toString().substring(1, 8);
    }
}
