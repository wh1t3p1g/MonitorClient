package com.okami.plugin.scanner.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since  2017/1/1
 * 任务类 传入的任务类
 */
@Component
@Scope("prototype")
public class BaseTask {
    /**
     * 任务名
     */
    private String taskName;
    /**
     * 任务唯一标示
     */
    private String taskId;
    /**
     * 扫描类型
     * 1 fast scan
     * 2 full scan
     */
    private int type;
    /**
     * type 为1时使用
     * 待检查的脚本后缀
     * 格式: php,jsp
     */
    private String scriptExtension;
    /**
     * 待扫描路径
     */
    private String filePath;
    /**
     * 被排除的文件路径
     * 格式: c:\,c:\test
     */
    private String exceptPath;
    /**
     * 被排除的文件后缀
     * 格式：jpg,css,js
     */
    private String exceptExtension;
    /**
     * 是否需要将排除项过滤
     */
    private boolean filter;

    /**
     *  扫描结果集合
     *  基于静态特征扫描=><存在木马的文件路径,具体的恶意代码>
     */
    private Map<String,String> staticScanResults;
    /**
     *  扫描结果集合
     *  基于文件相似性扫描=><存在木马的文件路径,对应特征库中的文件名：ssdeep值：相似度>
     */
    private Map<String,String> fuzzHashScanResults;
    /**
     * 扫描结果集合
     * 基于统计分析=><算法名,top10(filename:value)>
     */
    private Map<String,Map<String,String>> statisticsScanResults;

    private List<FileContent> fileContents;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getScriptExtension() {
        return scriptExtension;
    }

    public void setScriptExtension(String scriptExtension) {
        this.scriptExtension = scriptExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExceptPath() {
        return exceptPath;
    }

    public void setExceptPath(String exceptPath) {
        this.exceptPath = exceptPath;
    }

    public String getExceptExtension() {
        return exceptExtension;
    }

    public void setExceptExtension(String exceptExtension) {
        this.exceptExtension = exceptExtension;
    }

    public List<FileContent> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<FileContent> fileContents) {
        this.fileContents = fileContents;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public Map<String, String> getStaticScanResults() {
        return staticScanResults;
    }

    public void setStaticScanResults(Map<String, String> staticScanResults) {
        this.staticScanResults = staticScanResults;
    }

    public Map<String, String> getFuzzHashScanResults() {
        return fuzzHashScanResults;
    }

    public void setFuzzHashScanResults(Map<String, String> fuzzHashScanResults) {
        this.fuzzHashScanResults = fuzzHashScanResults;
    }

    public Map<String, Map<String, String>> getStatisticsScanResults() {
        return statisticsScanResults;
    }

    public void setStatisticsScanResults(Map<String, Map<String, String>> statisticsScanResults) {
        this.statisticsScanResults = statisticsScanResults;
    }
}
