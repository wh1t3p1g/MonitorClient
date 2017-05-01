package com.okami.plugin.scanner.core.handler.scanner.statistics;

import com.okami.plugin.scanner.bean.FileContent;
import com.okami.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/2/20
 */
@Component
@Scope("prototype")
public class LongestWord extends NeoPi {

    @Override
    public void calculate() {
        String data;
        for(FileContent fileContent:fileContents){
            if(!fileContent.getFileExt().equals(fileExt))continue;
            data= FileUtil.readAll(fileContent.getFilePath(),"UTF-8");
            String[] words=data.split("[\\s\\n\\r]");
            int longest=0;
            String longest_word="";
            for(String word:words){
                int length=word.length();
                if(length>longest){
                    longest=length;
                    longest_word=word;
                }
            }
            Map<String,String> temp=new HashMap<>();
            temp.put("filename",fileContent.getFilePath());
            temp.put("value",Integer.toString(longest));
            temp.put("longest_word",longest_word);
            results.add(temp);
        }
    }

    @Override
    public double calculate(FileContent fileContent) {
        String data= FileUtil.readAll(fileContent.getFilePath(),"UTF-8");
        String[] words=data.split("\\s|\\n|\\r");
        int longest=0;
        String longest_word="";
        for(String word:words){
            int length=word.length();
            if(length>longest){
                longest=length;
                longest_word=word;
            }
        }
        return longest;
    }
}
