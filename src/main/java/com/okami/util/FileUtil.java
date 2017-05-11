package com.okami.util;

import org.springframework.stereotype.Component;

import com.okami.core.IOC;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/6
 */
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

    /**
     * 写入文件
     * @param filePath 文件地址
     * @param content 写入内容
     * @param flag 追加
     * @return
     */
    public static boolean write(String filePath,String content,boolean flag)
    {
        FileWriter fw;
        BufferedWriter bw;
        try{
            fw=new FileWriter(filePath,flag);
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
    
    /**
     * 写入文件
     * @data 2017年4月29日
     * @param filePath
     * @param bytes
     * @return
     */
    public static boolean write(String filePath,byte[] bytes,boolean flag){
    	File file = new File(filePath);
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(file,flag);
			fos.write(bytes);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    /**
     * 删除一个文件下的所有文件或者删除某个文件
     * @param file
     */
	public static boolean deleteAll(File file) {

		while(file.exists()){
			delSub(file);

		}
		return true;
	}
	
	/**
	 * 删除一个文件下的所有文件或者删除某个文件
	 * @param file
	 * @return
	 */
	public static boolean delSub(File file){
		try{
			if (file.isFile()|| (file.list()!=null&&file.list().length == 0) ) {
				file.delete();
			} 
			else {
				File[] files = file.listFiles();
				for (int i = 0;  i < files.length; i++) {
					deleteAll(files[i]);
					files[i].delete();
				}
			}
			if (file.exists()) // 如果文件本身就是目录 ，就要删除目录
				file.delete();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	


    /**
     * 按size分割文件
     * @data 2017年5月3日
     * @param file
     */
    public static int cutFile(String filename,int size){
    	File file = new File(filename);
        BufferedInputStream in = null;  
        int i = 1 ;
        try {  
            in = new BufferedInputStream(new FileInputStream(file));  
            byte[] buffer = new byte[size];  
            while (-1 != in.read(buffer, 0, size)) {  
                FileUtil.write(filename+"_"+i, buffer,false);
                i++;
            }  
            
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return i;
    }
    
    /**
     * 连接被切的文件
     * @data 2017年5月4日
     * @param filename
     * @param size
     */
    public static boolean combineFile(String filename,int num){
    	
    	byte[] content = null;
    	for(int i=1;i<=num;i++){
    		content = FileUtil.readByte(filename+"_"+i);
    		FileUtil.write(filename, content, true);
    	}
    	return true;
    }
}
