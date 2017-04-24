package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.RetMetaData;
import com.okami.plugin.scanner.bean.WebshellFeatures;
import com.okami.plugin.scanner.core.handle.EnumFiles;
import com.okami.plugin.scanner.core.scanner.AbstractScanner;
import com.okami.util.FileUtil;
import info.debatty.java.spamsum.SpamSum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 * ssdeep 基于webshell样本库
 * ssdeep size>4096
 */
@Component
@Scope("prototype")
public class FuzzyHashScanner extends AbstractScanner implements Runnable{

    public static SpamSum spamSum=new SpamSum();

    public static int threshold=90;

    /**
     * 生成webshell ssdeep值
     * @param filepath
     */
    public static void generate(String filepath)
    {
        EnumFiles enumFiles= MonitorClientApplication.ctx.getBean(EnumFiles.class);
        enumFiles.setFilePath(filepath);
        List<FileContent> fileContentList=enumFiles.run();
        String content="";
        System.out.println(fileContentList.size());
        for(FileContent fileContent:fileContentList){
            if(fileContent.getSize()>4096){
                String res=FileUtil.readAll(fileContent.getFilePath());
                String hash=FuzzyHashScanner.spamSum.HashString(res);
                content+=fileContent.getFileName()+"="+hash+"\n";//1表示大于4096 采用ssdeep算法检查
                try {
                    Files.delete(fileContent.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        FileUtil.write("config/webshellFeatures.ini",content);
    }

    /**
     *
     * @return
     */
    @Override
    public RetMetaData calculate() {
        WebshellFeatures webshellFeatures=MonitorClientApplication.ctx.getBean(WebshellFeatures.class);
        RetMetaData retMetaData=MonitorClientApplication.ctx.getBean(RetMetaData.class);
        Map<String,String> features=webshellFeatures.load("ssdeep");
        int num;

        for(FileContent fileContent:getFileContents()){
            String content= FileUtil.readAll(fileContent.getFilePath());
            String hash=spamSum.HashString(content);
            System.out.println("开始扫描"+fileContent.getFilePath());
            for(Map.Entry<String, String> entry : features.entrySet()) {
                num=spamSum.match(hash,entry.getValue());
                if(num>threshold){
                    System.out.println("is vul "+fileContent.getFilePath());
                    retMetaData.getSsdeepScanResults().put(fileContent.getFilePath(),entry.getKey()+"|"+entry.getValue()+"|"+num);
                    break;
                }
            }
        }
        retMetaData.setFinishTime(new Date());
        retMetaData.setScanLevel("2");
        return retMetaData;
    }

    @Override
    public void run() {
        RetMetaData RetMetaData=this.calculate();
        System.out.println("scan done");
        System.out.println(RetMetaData.getSsdeepScanResults().size());
    }
}
