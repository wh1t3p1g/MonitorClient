package com.okami.plugin.scanner.core.handler.scanner.regex;

import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.WebshellFeatures;
import com.okami.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/3/18
 */
@Component
@Scope("prototype")
public class RegexEvilWords {

    private List<FileContent> fileContents;

    private Map<String,String> features;

    @Autowired
    private WebshellFeatures webshellFeatures;


    public RegexEvilWords(){
        this.features=new HashMap<>();
    }

    /**
     *
     * @return
     */
    public String calculate(FileContent fileContent) {
        String content= FileUtil.readAll(fileContent.getFilePath());
        System.out.println("开始扫描"+fileContent.getFilePath());
        // 正则匹配
        return "false";
    }

    public void loadFeatures(String language,Boolean sensitiveWords){
        Map<String,String> languageList=this.webshellFeatures.load(language);
        if(sensitiveWords){
            Map<String,String> sensitiveWordsList=this.webshellFeatures.load("sensitiveWords");
            this.features.putAll(sensitiveWordsList);
        }
        this.features.putAll(languageList);
    }


    public Map<String, String> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, String> features) {
        this.features = features;
    }

    public List<FileContent> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<FileContent> fileContents) {
        this.fileContents = fileContents;
    }
}
