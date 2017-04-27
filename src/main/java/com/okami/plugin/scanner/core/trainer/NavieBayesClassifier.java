package com.okami.plugin.scanner.core.trainer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;

/**
 * @author wh1t3P1g
 * @since 2017/4/25
 */
@Component
@Scope("prototype")
public class NavieBayesClassifier {

    private Instances instances=null;

    private Classifier classifier=null;

    public void loadArff(String path){
        try {
            DataSource source = new DataSource(path);
            instances=source.getDataSet();
            if (instances.classIndex() == -1)
                instances.setClassIndex(instances.numAttributes() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadModel(String path){
        try {
            File file=new File(path);
            if(!file.exists()){
                loadArff("config/train.arff");
                training("config/test.arff");
            }
            classifier=(NaiveBayesUpdateable)SerializationHelper.read(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String training(String path){
        if(instances==null)
            return "training data not found";
        try {
            classifier=new NaiveBayesUpdateable();
            classifier.buildClassifier(instances);
            loadArff(path);
            Evaluation eTest = new Evaluation(instances);
            eTest.evaluateModel(classifier, instances);
            String strSummary = eTest.toSummaryString();
            System.out.println(strSummary);

            SerializationHelper.write("config/bayes.model",classifier);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String updateModel(){
        if(classifier==null){
            loadModel("config/bayes.model");
        }//todo
        return "";
    }

    public double[] prediction(TrainerDataSet trainerDataSet){
        if(classifier==null){
            loadModel("config/bayes.model");
        }
        Instances instances = GenerateArff.init("prediction");

        Instance instance=new DenseInstance(5);
        instance.setValue(0,trainerDataSet.getCompression());
        instance.setValue(1,trainerDataSet.getEntropy());
        instance.setValue(2,trainerDataSet.getLanguageIC());
        instance.setValue(3,trainerDataSet.getLongestWord());
        instance.setValue(4,trainerDataSet.getFileSize());
        instances.add(instance);
        try {
            return classifier.distributionForInstance(instances.instance(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
