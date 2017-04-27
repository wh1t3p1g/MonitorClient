package com.okami.plugin.scanner.core.handler.scanner.statistics;

import com.okami.plugin.scanner.bean.FileContent;

import java.util.*;

/**
 * @author wh1t3P1g
 * @since 2017/2/20
 */
public abstract class NeoPi {
    public List<FileContent> fileContents;

    public String fileExt;

    public List<Map<String,String>> results=new ArrayList<>();

    abstract public void calculate();

    abstract public double calculate(FileContent fileContent);

    public int[] charCount(String data){
        int[] charCount=new int[256];
        int t;
        for(int i=0;i<data.length();i++){//计算字母出现的次数
            t=(int)data.charAt(i);
            try{
                charCount[t]++;
            }catch (ArrayIndexOutOfBoundsException e){

            }
        }
        return charCount;
    }

    public int[] charCount(byte[] data){
        int[] charCount=new int[256];

        for(byte b:data){
            charCount[b&0xFF]++;
        }
        return charCount;
    }

    /**
     * 从小到大排序
     * @return
     */
    public List<Map<String,String>> sort(){

        Collections.sort(results, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return new Double(o1.get("value")).compareTo(new Double(o2.get("value")));
            }
        });
        return results;
    }

    /**
     * 从大到小排序
     * @return
     */
    public List<Map<String,String>> rsort(){
        Collections.sort(results, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return new Double(o2.get("value")).compareTo(new Double(o1.get("value")));
            }
        });
        return results;
    }


    public List<FileContent> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<FileContent> fileContents) {
        this.fileContents = fileContents;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public List<Map<String, String>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, String>> results) {
        this.results = results;
    }
}
