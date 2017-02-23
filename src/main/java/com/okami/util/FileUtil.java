package com.okami.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/6
 */
@Component
public class FileUtil {

    public static String readAll(String filePath){

        try {
            FileReader fr=new FileReader(filePath);
            BufferedReader br=new BufferedReader(fr);
            String res="";
            String buf;
            while((buf=br.readLine())!=null){
                res+=buf;
            }
            br.close();
            fr.close();
            return res;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按照编码，读取文本文件所有内容
     * @param path
     * @param charset
     * @return
     */
    public static String readAll(String path,String charset){

        try {
            File file = new File(path);

            if (!file.exists()) {
                return null;
            }

            FileInputStream inputStream = new FileInputStream(file);
            byte[] length = new byte[inputStream.available()];
            inputStream.read(length);
            inputStream.close();
            return new String(length,charset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] readByte(String filePath){
        try {
            FileInputStream fis = new FileInputStream(filePath);
            FileChannel channel = fis.getChannel();
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            long size = channel.size();
            byte[] bytes = new byte[1024];
            ByteBuffer byteBuffer = ByteBuffer.allocate((int)channel.size());
            while((channel.read(byteBuffer)) > 0){
                // do nothing
//              System.out.println("reading");
            }
            channel.close();
            fis.close();
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> readLines(String filePath){

        try {
            FileReader fr=new FileReader(filePath);
            BufferedReader br=new BufferedReader(fr);
            List<String> res=new ArrayList<>();
            String buf;
            while((buf=br.readLine())!=null){
                res.add(buf);
            }
            br.close();
            fr.close();
            return res;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean write(String filePath,String content)
    {
        FileWriter fw;
        BufferedWriter bw;
        try{
            fw=new FileWriter(filePath);
            bw=new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

}
