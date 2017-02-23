package com.okami.plugin.scanner.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wh1t3P1g on 2017/1/1.
 * 返回结果元数据
 */
@Component
@Scope("prototype")
public class RetMetaData {

    /**
     *  扫描结果集合
     *  基于静态特征扫描=><存在木马的文件路径,具体的恶意代码>
     */
    private Map<String,String> staticScanResults=new HashMap<>();
    /**
     *  扫描结果集合
     *  基于文件相似性扫描=><存在木马的文件路径,对应特征库中的文件名：ssdeep值：相似度>
     */
    private Map<String,String> ssdeepScanResults=new HashMap<>();
    /**
     * 扫描结果集合
     * 基于统计分析=><算法名,top10(filename:value)>
     */
    private Map<String,Map<String,String>> statisticsScanResults=new HashMap<>();
    /**
     * 扫描结果集合
     * 基于日志分析=><存在木马的url,实际木马路径>
     */
    private Map<String,String> weblogScanResults=new HashMap<>();
    private String scanLevel;
    private long createTime;//创建时间
    private Date finishTime;//结束时间

    public Map<String, String> getStaticScanResults() {
        return staticScanResults;
    }

    public void setStaticScanResults(Map<String, String> staticScanResults) {
        this.staticScanResults = staticScanResults;
    }

    public Map<String, String> getSsdeepScanResults() {
        return ssdeepScanResults;
    }

    public void setSsdeepScanResults(Map<String, String> ssdeepScanResults) {
        this.ssdeepScanResults = ssdeepScanResults;
    }

    public Map<String, Map<String, String>> getStatisticsScanResults() {
        return statisticsScanResults;
    }

    public void setStatisticsScanResults(Map<String, Map<String, String>> statisticsScanResults) {
        this.statisticsScanResults = statisticsScanResults;
    }

    public Map<String, String> getWeblogScanResults() {
        return weblogScanResults;
    }

    public void setWeblogScanResults(Map<String, String> weblogScanResults) {
        this.weblogScanResults = weblogScanResults;
    }

    public String getScanLevel() {
        return scanLevel;
    }

    public void setScanLevel(String scanLevel) {
        this.scanLevel = scanLevel;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
