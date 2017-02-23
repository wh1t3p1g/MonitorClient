package com.okami.plugin.scanner.bean;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/4
 */
@Component
public class RetDataSet {

    private List<RetMetaData> retMetaDataList=new ArrayList<>();

    private String finishTime;

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public List<RetMetaData> getRetMetaDataList() {
        return retMetaDataList;
    }

    public void setRetMetaDataList(List<RetMetaData> retMetaDataList) {
        this.retMetaDataList = retMetaDataList;
    }
}
