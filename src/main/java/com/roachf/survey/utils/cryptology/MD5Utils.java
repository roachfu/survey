package com.roachf.survey.utils.cryptology;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密工具方法
 *
 * @author roach
 */

@Slf4j
public class MD5Utils {

    private MD5Utils() {
    }

    /**
     * 默认UTF-8字符集的加密方法
     *
     * @param src
     * @return
     */
    public static String md5(String src) {
        return md5(src, "UTF-8");
    }

    /**
     * 加密工具
     *
     * @param src     需要加密的数据
     * @param charset 字符集编码
     * @return
     */
    public static String md5(String src, String charset) {
        try {
            StringBuilder buffer = new StringBuilder();
            char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            byte[] bytes = src.getBytes(charset);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] target = md.digest(bytes);
            for (byte b : target) {
                buffer.append(chars[(b >> 4) & 0x0F]);
                buffer.append(chars[b & 0x0F]);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("算法不存在：", e);
        } catch (UnsupportedEncodingException e) {
            log.error("编码有误：", e);
        }
        return null;
    }
}
