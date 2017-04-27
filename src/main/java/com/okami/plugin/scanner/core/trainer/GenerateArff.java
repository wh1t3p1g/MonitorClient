package com.okami.plugin.scanner.core.trainer;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.handler.scanner.statistics.Compression;
import com.okami.plugin.scanner.core.handler.scanner.statistics.Entropy;
import com.okami.plugin.scanner.core.handler.scanner.statistics.LanguageIC;
import com.okami.plugin.scanner.core.handler.scanner.statistics.LongestWord;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.core.*;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/4/25
 */
@Component
public class GenerateArff {


    @Autowired
    private Compression compression;
    @Autowired
    private Entropy entropy;
    @Autowired
    private LanguageIC languageIC;
    @Autowired
    private LongestWord longestWord;

    private Instances instances;

    public static Instances init(String status){

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("compression"));
        attributes.add(new Attribute("entropy"));
        attributes.add(new Attribute("languageIC"));
        attributes.add(new Attribute("longestWord"));
        attributes.add(new Attribute("fileSize"));
        if(status.equals("train")){
            FastVector values=new FastVector(2);
            values.addElement("yes");
            values.addElement("no");
            attributes.add(new Attribute("class",values));
        }

        Instances instances = new Instances("statistic_data_set",attributes,0);
        if(instances.classIndex()==-1)
            instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    public TrainerDataSet generateTrainerDataSet(FileContent fileContent){
        TrainerDataSet trainerDataSet =
                MonitorClientApplication.ctx.getBean(TrainerDataSet.class);
        trainerDataSet.setCompression(compression.calculate(fileContent));
        trainerDataSet.setEntropy(entropy.calculate(fileContent));
        trainerDataSet.setLanguageIC(languageIC.calculate(fileContent));
        trainerDataSet.setLongestWord(longestWord.calculate(fileContent));
        trainerDataSet.setFileSize(fileContent.getSize());
        return trainerDataSet;
    }


    public List<TrainerDataSet> generateData(List<FileContent> fileContents,String flag){
        List<TrainerDataSet> trainerDataSets=new ArrayList<>();
        for (FileContent filecontent : fileContents) {
//            if(filecontent.getSize()<4096)continue;
            TrainerDataSet trainerDataSet =
                    MonitorClientApplication.ctx.getBean(TrainerDataSet.class);
            trainerDataSet.setCompression(compression.calculate(filecontent));
            trainerDataSet.setEntropy(entropy.calculate(filecontent));
            trainerDataSet.setLanguageIC(languageIC.calculate(filecontent));
            trainerDataSet.setLongestWord(longestWord.calculate(filecontent));
            trainerDataSet.setFileSize(filecontent.getSize());
            trainerDataSet.setIsWebShell(flag);
            trainerDataSets.add(trainerDataSet);
        }
        return trainerDataSets;
    }


    public Instances generateInstances(List<TrainerDataSet> trainerDataSets,String status) {
        //set instances
        instances=init(status);
        //add instance
        for (TrainerDataSet trainerDataSet: trainerDataSets) {
            Instance instance = new DenseInstance(instances.numAttributes());
            instance.setValue(0,trainerDataSet.getCompression());
            instance.setValue(1,trainerDataSet.getEntropy());
            instance.setValue(2,trainerDataSet.getLanguageIC());
            instance.setValue(3,trainerDataSet.getLongestWord());
            instance.setValue(4,trainerDataSet.getFileSize());
            if(status.equals("train"))
                instance.setValue(instances.attribute("class"),trainerDataSet.getIsWebShell());
            instances.add(instance);
        }
        return instances;
    }


    public void generateArffFile(Instances instances, String path) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        try {
            saver.setFile(new File(path));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
