package com.okami.plugin.scanner.core.handler.scanner.statistics;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 计算重合指数 低重合指数预示文件代码潜在的被加密或被混效过
 * 通常webshell为了躲避杀软的查杀 将代码加密或混淆
 * @author wh1t3P1g
 * @since 2017/1/25
 */
@Component
@Scope("prototype")
public class LanguageIC extends NeoPi {

    private boolean flag=false;

    @Override
    public void calculate(){
        byte[] data;
        for(FileContent fileContent:fileContents){
            if(!fileContent.getFileExt().equals(fileExt)){
                continue;
            }
            data= FileUtil.readByte(fileContent.getFilePath());
            double char_count=0;
            double total_char_count=0;
            int[] temp=charCount(data);
            for(int i:temp){
                if(i==0)continue;
                char_count+=i*(i-1);
                total_char_count+=i;
            }
            double ic=char_count/(total_char_count*(total_char_count - 1));
            Map<String,String> result=new HashMap<>();
            result.put("filename",fileContent.getFilePath());
            result.put("value",Double.toString(ic));
            results.add(result);
        }
    }

    @Override
    public double calculate(FileContent fileContent) {
        byte[] data= FileUtil.readByte(fileContent.getFilePath());
        double char_count=0;
        double total_char_count=0;
        int[] temp=charCount(data);
        for(int i:temp){
            if(i==0)continue;
            char_count+=i*(i-1);
            total_char_count+=i;
        }
        double ic=char_count/(total_char_count*(total_char_count - 1));
        return ic;
    }


    public static void test(){
        LanguageIC lic= MonitorClientApplication.ctx.getBean(LanguageIC.class);

//        lic.calculateCharCount("this is a test");
    }
}
