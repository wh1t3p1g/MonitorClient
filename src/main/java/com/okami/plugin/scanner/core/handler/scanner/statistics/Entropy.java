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
        for(FileContent fileContent:fileContents){
            if(!fileContent.getFileExt().equals(fileExt)){
                continue;
            }
            Map<String,String> result=new HashMap<>();
            result.put("filename",fileContent.getFilePath());
            result.put("value",Double.toString(calculate(fileContent)));
            results.add(result);
        }
    }

    @Override
    public double calculate(FileContent fileContent) {
        double entropy=0;
        byte[] data=null;
        byte[] content=FileUtil.readByte(fileContent.getFilePath());
        data=strip(content);
        int size=data.length;
        int[] temp=charCount(data);

        for(int t:temp){
            if(t==0)continue;
            double p_x=(t*1.0)/(size*1.0);
            if(p_x>0){
                entropy+=-p_x*(Math.log(p_x)/Math.log(2));
            }
        }
        return entropy;
    }

    public byte[] strip(byte[] content){
        List<Byte> temp=new ArrayList<>();
        for(byte b:content){
            if((int)b==0x20)continue;
            temp.add(b);
        }
        byte[] res=new byte[temp.size()];
        for(int i=0;i<temp.size();i++){
            res[i]=temp.get(i);
        }
        return res;
    }


}
