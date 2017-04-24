package com.okami.plugin.scanner.core.handle;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.scanner.impl.FuzzyHashScanner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 */
@Component
@Scope("prototype")
public class AssignScanner {

    private List<FileContent> fileContents;

    private String scanlevel;

    public List<FileContent> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<FileContent> fileContents) {
        this.fileContents = fileContents;
    }

    public String getScanlevel() {
        return scanlevel;
    }

    public void setScanlevel(String scanlevel) {
        this.scanlevel = scanlevel;
    }

    public void assignTask(){
        switch (scanlevel){
            case "1"://基于静态匹配
                break;
            case "2"://基于文件相似性
                FuzzyHashScanner fuzzyHashScanner= MonitorClientApplication.ctx.getBean(FuzzyHashScanner.class);
                fuzzyHashScanner.setFileContent(getFileContents());
                fuzzyHashScanner.run();
                break;
            case "3"://基于统计学
                break;
            case "4"://基于日志
                break;
        }
    }

}
