package com.okami.plugin.scanner.core.handler.scanner.ssdeep;

import com.okami.MonitorClientApplication;
import com.okami.controller.WebshellController;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.WebshellFeatures;
import com.okami.util.FileUtil;
import info.debatty.java.spamsum.SpamSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/3/18
 */
@Component
@Scope("prototype")
public class FuzzyHash {

    /**
     * 阀值
     */
    private int threshold;

    private SpamSum spamSum;

    /**
     * 特征库
     */
    private Map<String,String> features;

    private WebshellFeatures webshellFeatures;

    public FuzzyHash(){
        webshellFeatures=MonitorClientApplication.ctx.getBean(WebshellFeatures.class);
        this.spamSum=new SpamSum();
        if(webshellFeatures!=null){
            this.features=webshellFeatures.load("FuzzHash");
            MonitorClientApplication.log.info("WebshellFeature<FuzzHash> load success");
        }
        else
            MonitorClientApplication.log.error("WebshellFeature<FuzzHash> load error");
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

    public String calculate(String content) {
        String hash=this.spamSum.HashString(content);
        int num;
        for(Map.Entry<String, String> entry : this.features.entrySet()) {
            num=this.spamSum.match(hash,entry.getValue());
            if(num>this.threshold){
                return "static:("+entry.getKey()+"|"+num+"%)";
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
