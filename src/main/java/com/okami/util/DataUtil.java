package com.okami.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okami.common.AESHander;
import com.okami.core.IOC;

import sun.misc.*; 
/**
 * @author orleven
 * @date 2017年2月25日
 */
public class DataUtil {

    /**
     * 计算字符串MD5
     * @author orleven
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        String md5 = null;
		md5 = DigestUtils.md5Hex(str.getBytes());
        return md5;
    }
    
    /**
     * 计算byte  MD5
     * @param byt
     * @return
     */
    public static String getMD5(byte[] byt) {
        String md5 = null;
		md5 = DigestUtils.md5Hex(byt);
        return md5;
    }
    
    /**
     * 计算文件MD5
     * @author orleven
     * @param file
     * @return
     */
    public static String getMd5ByFile(File file) {
        String md5 = null;
        FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fis));
	        IOUtils.closeQuietly(fis); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}    
        return md5;
    }
    
    /**
     * 计算字符串Sha1
     * @author orleven
     * @param str
     * @return
     */
    public static String getSHA1(String str) {
        return DigestUtils.sha1Hex(str.getBytes());
    }
    
    /**
     * 计算byte  Sha1
     * @param byt
     * @return
     */
    public static String getSHA1(byte[] byt) {
        return DigestUtils.sha1Hex(byt);
    }
    
    /**
     * 计算文件Sha1
     * @author orleven
     * @param file
     * @return
     */
    public static String getSHA1ByFile(File file) {
        String md5 = null;
        FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			md5 = DigestUtils.sha1Hex(IOUtils.toByteArray(fis));
	        IOUtils.closeQuietly(fis); 
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}    
        return md5;
    }
    
    /**
     * 获取当前的时间 
     * @author orleven
     * @return 格式：2017-2-18 22:16:40
     */
    public static String getTime() {
    	return DateFormat.getDateTimeInstance().format(new Date());
    }
    
    /**
     * 获取当前的时间戳
     * @author orleven
     * @return
     */
    public static String getTimeStamp() {
    	return String.valueOf(new Date().getTime()).substring(0,10);
    }
    
    /**
     * Map trans to Json
     * @param params
     * @return String
     */
    public static String toJson(Map params){
        Gson gson=new Gson();
        return gson.toJson(params);
    }

    /**
     * 
     * @data 2017年4月24日
     * @param params
     * @return
     */
    public static String toJson(Object src){
        Gson gson=new Gson();
        return gson.toJson(src);
    }

    
    public static Object fromJsonToObject(String jsonStr){
        Gson gson=new Gson();
        Type type=new TypeToken<Object>(){}.getType();
        return gson.fromJson(jsonStr,type);
    }
    
    /**
     * Json string to HashMap<String,Object>
     * @param jsonStr
     * @return HashMap<String,Object>
     */
    public static HashMap<String,Object> fromJson(String jsonStr){
    	Gson gson=new Gson();
    	Type type=new TypeToken<HashMap<String,Object>>(){}.getType();

        return gson.fromJson(jsonStr,type);
    }
    
    /**
     * ArrayList Integer 去重
     * @data 2017年4月19日
     * @param arrayList
     * @return
     */
    public static String[] removeDuplicate(String[] arrayList){
        Set<String> set = new HashSet<>();  
        for(int i=0;i<arrayList.length;i++){  
        	if(arrayList[i]!=null&&!arrayList[i].equals("")){
        		set.add(arrayList[i]); 
        	}
             
        }  
        return (String[]) set.toArray(new String[set.size()]);  
    }
    
    /**
     * base64 解密
     * @data 2017年5月3日
     * @param text
     * @return
     */
    public static byte[] base64Decode(String text)
    {
    	byte[] b = null;  
        try {  
            b = new BASE64Decoder().decodeBuffer(text);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return b;
    }
    
    /**
     * base64 加密
     * @data 2017年5月3日
     * @param text
     * @return
     */
    public static String base64Encode(byte[] text)
    {
        return new BASE64Encoder().encode(text);
    }
    
    /**
     * url 加密
     * @data 2017年5月3日
     * @param text
     * @return
     */
    public static String urlEncode(String text){
    	try {
    		text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
    	return text;
    }
    
    /**
     * url 解密
     * @data 2017年5月3日
     * @param text
     * @return
     */
    public static String urlDecode(String text){
    	try {
    		text = URLDecoder.decode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
    	return text;
    }
    
    /**
     * 操作系统目录斜杠处理
     * @data 2017年5月3日
     * @param text
     * @return
     */
    public static String slashDeal(String text){
    	if(File.separator.equals("\\")){
    		text = text.replaceAll("/", "\\\\");
    	}else{
    		text = text.replaceAll("\\\\", "/");
    	}
    	return text;
    }
    
    /**
     * 去除换行符
     * @data 2017年5月6日
     * @param text
     * @return
     */
    public static String linefeedDeal(String text){
    	text = text.replaceAll("\r", "").replaceAll("\n", "");
    	return text;
    }
    
    public static String decode(String content,AESHander aESHander){
        content = DataUtil.urlDecode(aESHander.decrypt(DataUtil.urlDecode(DataUtil.urlDecode(content))));
    	return content;
    }
    
    public static String encode(String content,AESHander aESHander){
        content = DataUtil.urlEncode(DataUtil.urlEncode(aESHander.encrypt((DataUtil.urlEncode(content)))));
    	return content;
    }
}
