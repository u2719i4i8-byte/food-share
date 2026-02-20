package cn.kmbeast.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 网络工具类
 * 功能：获取本机网络信息
 */
public class RootUtils {

    /**
     * 获取本机的IP地址
     * 
     * @return 本机IP地址字符串
     * @throws UnknownHostException 无法解析主机名时抛出
     */
    public static String getIpAddr() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        return localhost.getHostAddress();
    }

}
