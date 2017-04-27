package com.okami.plugin.scanner.core.handler.scanner.statistics;

import com.okami.plugin.scanner.bean.FileContent;
import com.okami.util.FileUtil;
import com.okami.util.ZLibUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.channels.ClosedByInterruptException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/2/26
 */
@Component
@Scope("prototype")
public class Compression extends NeoPi {

    @Override
    public void calculate() {
        for(FileContent fileContent:fileContents){
            if(!fileContent.getFileExt().equals(getFileExt()))continue;
            byte[] uncompressData= FileUtil.readByte(fileContent.getFilePath());
            byte[] compressedData= ZLibUtil.compress(uncompressData);
            double uncompressLenth=uncompressData.length;
            double compressedLenth=compressedData.length;
            double ratio = compressedLenth/uncompressLenth;
            Map<String,String> temp=new HashMap<>();
            temp.put("filename",fileContent.getFilePath());
            temp.put("value",Double.toString(ratio));
            results.add(temp);
        }
    }

    @Override
    public double calculate(FileContent fileContent) {
        byte[] uncompressData= FileUtil.readByte(fileContent.getFilePath());
        byte[] compressedData= ZLibUtil.compress(uncompressData);
        double uncompressLenth=uncompressData.length;
        double compressedLenth=compressedData.length;
        return compressedLenth/uncompressLenth;
    }


}
