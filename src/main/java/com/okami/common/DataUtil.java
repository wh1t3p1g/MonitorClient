package com.okami.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;

public class DataUtil {
	/**
	 * 计算字符串MD5
	 * @param str
	 * @return
	 */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16).substring(0,16);
        } catch (Exception e) {
        	System.out.println("MD5加密出现错误");
        }
		return str;
    }
    
    /**
     * 计算文件MD5
     * @param file
     * @return
     */
    public static String getMd5ByFile(File file) {
        String result = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            result = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(null != in) {
                try {
                	in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.substring(0,16);
    }
    
    /**
     * 获取当前的时间 
     * @return 格式：2017-2-18 22:16:40
     */
    public static String getTime() {
    	return DateFormat.getDateTimeInstance().format(new Date());
    }
    
    /**
     * 获取当前的时间戳
     * @return 
     */
    public static String getTimeStamp() {
    	return String.valueOf(new Date().getTime());
    }
}
