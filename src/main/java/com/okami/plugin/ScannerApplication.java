package com.okami.plugin;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.handle.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/15
 */
@Component
public class ScannerApplication {

    public void run(){
        EnumFiles enumFiles= MonitorClientApplication.ctx.getBean(EnumFiles.class);
        enumFiles.setFilePath("/Users/wh1t3p1g/Documents/Code/phpProject/DataCenter/");
        String[] whitePath={"/Users/wh1t3P1g/Documents/Code/phpProject/DataCenter/runtime"};
        List<FileContent> fileContentList=EnumFiles.filter(enumFiles.run(),whitePath);
        System.out.println("待扫描文件共:"+fileContentList.size());


        // longestword test start
        LongestWord longestWord=MonitorClientApplication.ctx.getBean(LongestWord.class);
        longestWord.setFileExt("php");
        longestWord.setFileContents(fileContentList);
        longestWord.calculate();
        List<Map<String,String>> results=longestWord.rsort();
        // longestword test end
        // entropy test start
//        Entropy entropy=MonitorClientApplication.ctx.getBean(Entropy.class);
//        entropy.setFileExt("php");
//        entropy.setFileContents(fileContentList);
//        entropy.calculate();
//        List<Map<String,String>> results=entropy.rsort();
        // entropy test end
        // languageic test start
//        LanguageIC languageIC=MonitorClientApplication.ctx.getBean(LanguageIC.class);
//        languageIC.setFileContents(fileContentList);
//        languageIC.setFileExt("php");
//        languageIC.calculate();
//        List<Map<String,String>> results=languageIC.sort();
        //languageic test end
        for(Map<String,String> result:results){
            System.out.println(result.get("filename")+" "+result.get("value"));
        }
//        AssignScanner assignScanner=MonitorClientApplication.ctx.getBean(AssignScanner.class);
//        assignScanner.setFileContents(fileContentList);
//        assignScanner.setScanlevel("2");
//        assignScanner.assignTask();
//        LanguageIC.test();
    }
}
