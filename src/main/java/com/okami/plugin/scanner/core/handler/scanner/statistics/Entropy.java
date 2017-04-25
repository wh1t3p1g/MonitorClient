package com.okami.plugin.scanner.core.handler.scanner.statistics;

import com.okami.plugin.scanner.bean.FileContent;
import com.okami.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 信息熵(Entropy):通过使用ASCII码表来衡量文件的不确定性；
 * @author wh1t3P1g
 * @since 2017/2/1
 */
@Component
@Scope("prototype")
public class Entropy extends NeoPi {

    @Override
    public void calculate(){
        String data=null;
        String striped_data=null;
        for(FileContent fileContent:fileContents){
            if(!fileContent.getFileExt().equals(fileExt)){
                continue;
            }
            double entropy=0;
            byte[] content=FileUtil.readByte(fileContent.getFilePath());
            if(content==null)continue;
            data= new String(content);
            striped_data=data.replace(" ","");
            int size=striped_data.length();
            int[] temp=charCount(striped_data);
            for(int i:temp){
                if(i==0)continue;
                double p_i=(i*1.00)/size;
                if(p_i>0){
                    entropy+=-p_i*(Math.log(p_i)/Math.log(2));
                }
            }
            Map<String,String> result=new HashMap<>();
            result.put("filename",fileContent.getFilePath());
            result.put("value",Double.toString(entropy));
            results.add(result);
        }
    }

    @Override
    public double calculate(FileContent fileContent) {
        double entropy=0;
        String data=null;
        String striped_data=null;
        byte[] content=FileUtil.readByte(fileContent.getFilePath());
        data= new String(content);
        striped_data=data.replace(" ","");
        int size=striped_data.length();
        int[] temp=charCount(striped_data);
        for(int i:temp){
            if(i==0)continue;
            double p_i=(i*1.00)/size;
            if(p_i>0){
                entropy+=-p_i*(Math.log(p_i)/Math.log(2));
            }
        }
        return entropy;
    }


}
