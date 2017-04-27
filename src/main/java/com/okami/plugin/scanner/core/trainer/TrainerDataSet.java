package com.okami.plugin.scanner.core.trainer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author wh1t3P1g
 * @since 2017/4/25
 */
@Component
@Scope("prototype")
public class TrainerDataSet {

    private double compression;

    private double entropy;

    private double languageIC;

    private double LongestWord;

    private double fileSize;

    private String isWebShell;

    public double getCompression() {
        return compression;
    }

    public void setCompression(double compression) {
        this.compression = compression;
    }

    public double getEntropy() {
        return entropy;
    }

    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }

    public double getLanguageIC() {
        return languageIC;
    }

    public void setLanguageIC(double languageIC) {
        this.languageIC = languageIC;
    }

    public double getLongestWord() {
        return LongestWord;
    }

    public void setLongestWord(double longestWord) {
        LongestWord = longestWord;
    }

    public String getIsWebShell() {
        return isWebShell;
    }

    public void setIsWebShell(String isWebShell) {
        this.isWebShell = isWebShell;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }
}
