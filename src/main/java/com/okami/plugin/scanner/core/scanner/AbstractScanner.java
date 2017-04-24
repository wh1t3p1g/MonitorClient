package com.okami.plugin.scanner.core.scanner;

import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.RetMetaData;
import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/1
 */
public abstract class AbstractScanner {
    /**
     * 包含待扫描文件的文件属性
     */
    private List<FileContent> fileContents;

    /**
     * 扫描计算返回结果
     * @return RetMetaData
     */
    public abstract RetMetaData calculate();

    public List<FileContent> getFileContents() {
        return fileContents;
    }

    public void setFileContent(List<FileContent> fileContents) {
        this.fileContents = fileContents;
    }
}
