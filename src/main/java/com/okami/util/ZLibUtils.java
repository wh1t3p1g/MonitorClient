package com.okami.util;

  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.util.zip.Deflater;  
import java.util.zip.DeflaterOutputStream;  
import java.util.zip.Inflater;  
import java.util.zip.InflaterInputStream;  
  
/**
 * zlib 压缩算法
 * @author orleven
 * @date 2016年12月31日
 */
public class ZLibUtils {
  
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
        try {  
            int i = 1024;  
            byte[] buf = new byte[i];  
  
            while ((i = iis.read(buf, 0, i)) > 0) {  
                o.write(buf, 0, i);  
            }  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return o.toByteArray();  
    }  
    
//    public final void testBytes() {  
//        System.err.println("字节压缩／解压缩测试");  
//        String inputStr = "snowolf@zlex.org;dongliang@zlex.org;zlex.dongliang@zlex.org";  
//        System.err.println("输入字符串:\t" + inputStr);  
//        byte[] input = inputStr.getBytes();  
//        System.err.println("输入字节长度:\t" + input.length);  
//  
//        byte[] data = ZLibUtils.compress(input);  
//        System.err.println("压缩后字节长度:\t" + data.length);  
//  
//        byte[] output = ZLibUtils.decompress(data);  
//        System.err.println("解压缩后字节长度:\t" + output.length);  
//        String outputStr = new String(output);  
//        System.err.println("输出字符串:\t" + outputStr);  
//  
//        assertEquals(inputStr, outputStr);  
//    }  
//  
//    @Test  
//    public final void testFile() {  
//        String filename = "zlib";  
//        File file = new File(filename);  
//        System.err.println("文件压缩／解压缩测试");  
//        String inputStr = "snowolf@zlex.org;dongliang@zlex.org;zlex.dongliang@zlex.org";  
//        System.err.println("输入字符串:\t" + inputStr);  
//        byte[] input = inputStr.getBytes();  
//        System.err.println("输入字节长度:\t" + input.length);  
//  
//        try {  
//  
//            FileOutputStream fos = new FileOutputStream(file);  
//            ZLibUtils.compress(input, fos);  
//            fos.close();  
//            System.err.println("压缩后字节长度:\t" + file.length());  
//        } catch (Exception e) {  
//            fail(e.getMessage());  
//        }  
//  
//        byte[] output = null;  
//  
//        try {  
//            FileInputStream fis = new FileInputStream(file);  
//            output = ZLibUtils.decompress(fis);  
//            fis.close();  
//  
//        } catch (Exception e) {  
//            fail(e.getMessage());  
//        }  
//        System.err.println("解压缩后字节长度:\t" + output.length);  
//        String outputStr = new String(output);  
//        System.err.println("输出字符串:\t" + outputStr);  
//  
//        assertEquals(inputStr, outputStr);  
//    }  
}  