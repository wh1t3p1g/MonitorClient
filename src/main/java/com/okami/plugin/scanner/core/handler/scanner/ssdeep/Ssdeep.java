package com.okami.plugin.scanner.core.handler.scanner.ssdeep;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.WebshellFeatures;
import com.okami.plugin.scanner.core.common.EnumFiles;
import com.okami.util.FileUtil;
import info.debatty.java.spamsum.SpamSum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/3/18
 */
@Component
@Scope("prototype")
public class Ssdeep {

    /**
     * 阀值
     */
    private int threshold;

    private SpamSum spamSum;

    /**
     * 特征库
     */
    private Map<String,String> features;

    public Ssdeep(){
        WebshellFeatures webshellFeatures=MonitorClientApplication.ctx.getBean(WebshellFeatures.class);
        this.spamSum=new SpamSum();
        this.features=webshellFeatures.load("ssdeep");
    }


    /**
     * 生成webshell ssdeep值
     * @param filepath
     */
    public void generate(String filepath)
    {
//        EnumFiles enumFiles= MonitorClientApplication.ctx.getBean(EnumFiles.class);
//        enumFiles.setFilePath(filepath);
//        List<FileContent> fileContentList=enumFiles.run(1,"php",false,"","");
//        String content="";
//        System.out.println(fileContentList.size());
//        for(FileContent fileContent:fileContentList){
//            if(fileContent.getSize()>4096){
//                String res= FileUtil.readAll(fileContent.getFilePath());
//                String hash= this.spamSum.HashString(res);
//                content+=fileContent.getFileName()+"="+hash+"\n";//1表示大于4096 采用ssdeep算法检查
//                try {
//                    Files.delete(fileContent.getPath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        FileUtil.write("config/webshellFeatures.ini",content);
    }

    /**
     *
     * @return
     */
    public String calculate(FileContent fileContent) {
        String content= FileUtil.readAll(fileContent.getFilePath());
        String hash=this.spamSum.HashString(content);
        System.out.println("开始扫描"+fileContent.getFilePath());
        int num;
        for(Map.Entry<String, String> entry : this.features.entrySet()) {
            num=this.spamSum.match(hash,entry.getValue());
            if(num>this.threshold){
                return entry.getKey()+"|"+entry.getValue()+"|"+num;
            }
        }
        return "false";
    }


    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
