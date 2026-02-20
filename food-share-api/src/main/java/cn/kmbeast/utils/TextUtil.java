package cn.kmbeast.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 文本工具类
 * 功能：富文本处理、文本截取
 */
public class TextUtil {

    /**
     * 提取富文本中的纯文本并截取指定字数
     * 使用Jsoup解析HTML，提取纯文本内容
     *
     * @param targetStr  目标富文本内容
     * @param fontNumber 需要截取的字数
     * @return 截取后的纯文本（超出部分用"..."表示）
     */
    public static String parseText(String targetStr, Integer fontNumber) {
        if (targetStr == null) {
            return null;
        }
        Document document = Jsoup.parse(targetStr);
        String text = document.text();
        if (text.length() < fontNumber) {
            return text;
        }
        return text.substring(0, fontNumber) + "...";
    }

}
