package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.handler.scanner.regex.RegexEvilWords;
import com.okami.plugin.scanner.core.handler.scanner.ssdeep.FuzzyHash;
import com.okami.plugin.scanner.core.scanner.AbstractScanner;
import com.okami.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成3种扫描方式
 * @author wh1t3P1g
 * @since 2017/4/21
 */
@Component
@Scope("prototype")
public class FullScanner extends AbstractScanner {

    @Autowired
    private FuzzyHash fuzzyHash;

    @Autowired
    private RegexEvilWords regexEvilWords;
    @Autowired
    private StatisticsScanner statisticsScanner;

    private String scriptExtension;

    @Override
    public Map<String, String> calculate() {
        //init
        Map<String,String> fullResults=new HashMap<>();
        List<FileContent> fileContents=getTask().getFileContents();
        fuzzyHash.setThreshold(80);
        // /.init
        for (FileContent fileContent : fileContents) {
            String content=FileUtil.readAll(fileContent.getFilePath(),"UTF-8");
            //checking fuzzyhash
            String result=fuzzyHash.calculate(content);
            if(!result.equals("false")){
                fullResults.put(fileContent.getFilePath(),result);
            }
            //checking static
            result=regexEvilWords.calculate(content);
            if(!result.equals("false")){
                if(fullResults.containsKey(fileContent.getFilePath())){
                    result=fullResults.get(fileContent.getFilePath())+";"+result;
                }
                fullResults.put(fileContent.getFilePath(),result);
            }
            //checking statistic
            if(fileContent.getFileExt().equals(this.scriptExtension)){
                result=statisticsScanner.calculate(fileContent);
                if(!result.equals("false")){
                    if(fullResults.containsKey(fileContent.getFilePath())){
                        result=fullResults.get(fileContent.getFilePath())+";"+result;
                    }
                    fullResults.put(fileContent.getFilePath(),result);
                }
            }

        }
        return fullResults;
    }

    public String getScriptExtension() {
        return scriptExtension;
    }

    public void setScriptExtension(String scriptExtension) {
        this.scriptExtension = scriptExtension;
    }
}
