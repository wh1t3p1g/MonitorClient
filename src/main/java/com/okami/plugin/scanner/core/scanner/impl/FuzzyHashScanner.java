package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.WebshellFeatures;
import com.okami.plugin.scanner.core.common.EnumFiles;
import com.okami.plugin.scanner.core.handler.scanner.ssdeep.Ssdeep;
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


    public static int threshold=90;



    @Override
    public void run() {
//        Map<String,String> RetMetaData=this.calculate();
//        System.out.println("scan done");
//        System.out.println(RetMetaData.getSsdeepScanResults().size());
    }

    @Override
    public Map<String,String> calculate() {
//        Ssdeep ssdeep=MonitorClientApplication.ctx.getBean(Ssdeep.class);
//        ssdeep.setThreshold(threshold);
//        for(FileContent fileContent:getFileContents()){
//            if(fileContent.getSize()<=4096)continue;
//            String result=ssdeep.calculate(fileContent);
//            if(!result.equals("false")){
//                System.out.println("is vul "+fileContent.getFilePath());
//                task.getSsdeepScanResults().put(fileContent.getFilePath(),result);
//            }
//        }
//        retMetaData.setFinishTime(new Date());
//        retMetaData.setScanLevel("2");
//        return retMetaData;
        return null;
    }
}
