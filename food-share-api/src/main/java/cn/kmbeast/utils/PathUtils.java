package cn.kmbeast.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 路径工具类
 * 功能：获取项目根路径，兼容不同操作系统
 */
public class PathUtils {
    
    /**
     * 获取类加载器的根路径
     * 自动适配Windows、Mac、Linux等操作系统
     * 
     * @return 项目根路径字符串
     */
    public static String getClassLoadRootPath() {
        String path = "";
        try {
            String prePath = URLDecoder.decode(PathUtils.class.getClassLoader().getResource("").getPath(),"utf-8").replace("/target/classes", "");
            String osName = System.getProperty("os.name");
            if (osName.toLowerCase().startsWith("mac")) {
                // Mac系统
                path = prePath.substring(0, prePath.length() - 1);
            } else if (osName.toLowerCase().startsWith("windows")) {
                // Windows系统
                path = prePath.substring(1, prePath.length() - 1);
            } else if(osName.toLowerCase().startsWith("linux") || osName.toLowerCase().startsWith("unix")) {
                // Linux或Unix系统
                path = prePath.substring(0, prePath.length() - 1);
            } else {
                path = prePath.substring(1, prePath.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

}
