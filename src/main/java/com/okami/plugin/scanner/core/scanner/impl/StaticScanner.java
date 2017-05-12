package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.handler.scanner.regex.RegexEvilWords;
import com.okami.plugin.scanner.core.scanner.AbstractScanner;
import com.okami.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/1
 * 基于静态匹配
 * 匹配关键字 恶意函数 | 文件属性维度
 */
@Component
@Scope("prototype")
public class StaticScanner extends AbstractScanner{

    @Autowired
    private RegexEvilWords regexEvilWords;

    @Override
    public Map<String,String> calculate() {
        Map<String,String> retData=new HashMap<>();
        List<FileContent> fileContents=getTask().getFileContents();
        for (FileContent fileContent:fileContents){
            String content= FileUtil.readAll(fileContent.getFilePath());
            if(content==null||content.isEmpty())continue;
            String result=regexEvilWords.calculate(content.replaceAll("((\"|\')\\s*(\\&|\\+|\\.)\\s*(\"|\'))",""));
            if(!result.equals("false")){
//                File file=new File(fileContent.getFilePath());
//                file.delete();
                retData.put(fileContent.getFilePath(),result);
            }
        }

        return retData;
    }

}
