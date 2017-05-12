package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.handler.scanner.ssdeep.FuzzyHash;
import com.okami.plugin.scanner.core.scanner.AbstractScanner;
import com.okami.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 * ssdeep 基于webshell样本库
 * ssdeep size>4096
 */
@Component
@Scope("prototype")
public class FuzzyHashScanner extends AbstractScanner{

    @Autowired
    private FuzzyHash fuzzyHash;

    private int threshold=80;

    @Override
    public Map<String,String> calculate() {
        Map<String,String> retData=new HashMap<>();
        fuzzyHash.setThreshold(threshold);
        List<FileContent> fileContents=getTask().getFileContents();
        for(FileContent fileContent:fileContents){
            if(fileContent.getSize()<=4096)continue;
            String content= FileUtil.readAll(fileContent.getFilePath());
            String result= fuzzyHash.calculate(content);
            if(!result.equals("false")){
                retData.put(fileContent.getFilePath(),result);
            }
        }
        return retData;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
