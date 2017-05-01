package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.handler.scanner.statistics.Compression;
import com.okami.plugin.scanner.core.handler.scanner.statistics.Entropy;
import com.okami.plugin.scanner.core.handler.scanner.statistics.LanguageIC;
import com.okami.plugin.scanner.core.handler.scanner.statistics.LongestWord;
import com.okami.plugin.scanner.core.scanner.AbstractScanner;
import com.okami.plugin.scanner.core.trainer.GenerateArff;
import com.okami.plugin.scanner.core.trainer.NavieBayesClassifier;
import com.okami.plugin.scanner.core.trainer.TrainerDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import weka.core.Instance;
import weka.core.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 * 基于统计学扫描
 */
@Component
@Scope("prototype")
public class StatisticsScanner extends AbstractScanner{

    @Autowired
    GenerateArff generateArff;
    @Autowired
    NavieBayesClassifier navieBayesClassifier;

    @Override
    public Map<String,String> calculate() {
        Map<String,String> retData=new HashMap<>();
        List<FileContent> fileContents=getTask().getFileContents();
        for(FileContent fileContent:fileContents){
            TrainerDataSet trainerDataSet=generateArff.generateTrainerDataSet(fileContent);
            double[] result=navieBayesClassifier.prediction(trainerDataSet);
            if(result[0]>result[1])
                retData.put(fileContent.getFilePath(),"ML:("+Double.toString(result[0]*100)+"%)");
        }
        return retData;
    }

    public String calculate(FileContent fileContent){
        TrainerDataSet trainerDataSet=generateArff.generateTrainerDataSet(fileContent);
        double[] result=navieBayesClassifier.prediction(trainerDataSet);
        if(result[0]>result[1])
            return "ML:("+Double.toString(result[0]*100)+"%)";
        else
            return "false";
    }

}
