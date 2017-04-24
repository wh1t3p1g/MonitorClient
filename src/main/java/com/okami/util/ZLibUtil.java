package com.okami.util;

import java.util.zip.Deflater;  
import java.util.zip.DeflaterOutputStream;  
import java.util.zip.Inflater;  
import java.util.zip.InflaterInputStream;
import java.io.*;

/**
 * zlib 压缩算法
 * @author orleven
 * @date 2016年12月31日
 */
public class ZLibUtil {
  
    /** 
     * 压缩 
     * @param data 待压缩数据 
     * @return byte[] 压缩后的数据 
     */  
    public static byte[] compress(byte[] data) {  
        byte[] output = new byte[0];  
  
        Deflater compresser = new Deflater();  
  
        compresser.reset();  
        compresser.setInput(data);  
        compresser.finish();  
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);  
        try {  
            byte[] buf = new byte[1024];  
            while (!compresser.finished()) {  
                int i = compresser.deflate(buf);  
                bos.write(buf, 0, i);  
            }  
            output = bos.toByteArray();  
        } catch (Exception e) {  
            output = data;  
            e.printStackTrace();  
        } finally {  
            try {  
                bos.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        compresser.end(); 

        return output;  
    }  
  
    /** 
     * 压缩  这是一个测试
     * @param data 待压缩数据 
     * @param os 输出流  
     */  
    public static void compress(byte[] data, OutputStream os) {  
        DeflaterOutputStream dos = new DeflaterOutputStream(os);  
  
        try {  
            dos.write(data, 0, data.length);  
  
            dos.finish();  
  
            dos.flush(); 
            dos.close();
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 解压缩 
     * @param data 待压缩的数据 
     * @return byte[] 解压缩后的数据 
     */  
    public static byte[] decompress(byte[] data) {  
        byte[] output = new byte[0];  
  
        Inflater decompresser = new Inflater();  
        decompresser.reset();  
        decompresser.setInput(data);  
  
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);  
        try {  
            byte[] buf = new byte[1024];  
            while (!decompresser.finished()) {  
                int i = decompresser.inflate(buf);  
                o.write(buf, 0, i);  
            }  
            output = o.toByteArray();  
        } catch (Exception e) {  
            output = data;  
            e.printStackTrace();  
        } finally {  
            try {  
                o.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        decompresser.end();  
        return output;  
    }  
  
    /** 
     * 解压缩 
     * @param is 输入流        
     * @return byte[] 解压缩后的数据 
     */  
    public static byte[] decompress(InputStream is) {  
        InflaterInputStream iis = new InflaterInputStream(is);  
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);  
        byte[] result = null;
        try {  
            int i = 1024;  
            byte[] buf = new byte[i];  
  
            while ((i = iis.read(buf, 0, i)) > 0) {  
                o.write(buf, 0, i);  
            }  
            result = o.toByteArray();
            o.close();
            iis.close();
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return   result;
    }  
    
    public static void main(String args[]){

        byte[] data = FileUtil.readByte("C:\\Users\\dell\\Desktop\\accounts.db");
        byte[] output = ZLibUtil.decompress(data);
        System.err.println("解压缩后字节长度:\t" + output.length);  
        String outputStr = new String(output);  
        System.err.println("输出字符串:\t" + outputStr);  
  
    }  
}