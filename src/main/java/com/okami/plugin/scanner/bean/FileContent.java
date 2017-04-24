package com.okami.plugin.scanner.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * @author wh1t3P1g
 * @since 2017/1/2
 * 待扫描检查文件类
 */
@Component
@Scope("prototype")
public class FileContent {
    /**
     * file name
     */
    private String fileName;
    /**
     * file privilege whether readable
     */
    private boolean isReadable;
    /**
     * file privilege whether Writeable
     */
    private boolean isWriteable;
    /**
     * file privilege whether Executable
     */
    private boolean isExecutable;
    /**
     * file privilege whether hidden
     */
    private boolean isHidden;
    /**
     * file full path like c://path/to/read/some.txt
     */
    private String filePath;
    /**
     * file extension like php,jpg
     */
    private String fileExt;
    /**
     * file modify time
     */
    private String lastModifyTime;
    /**
     * file create time
     */
    private String createTime;
    /**
     * file last access time
     */
    private String lastAccessTime;
    /**
     * check the file in white file path
     */
    private boolean isInWhitePath;
    /**
     * file owner
     */
    private String owner;
    /**
     * file size
     */
    private long size;

    private String dirname;

    private Path path;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public void setReadable(boolean readable) {
        isReadable = readable;
    }

    public boolean isWriteable() {
        return isWriteable;
    }

    public void setWriteable(boolean writeable) {
        isWriteable = writeable;
    }

    public boolean isExecutable() {
        return isExecutable;
    }

    public void setExecutable(boolean executable) {
        isExecutable = executable;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public boolean isInWhitePath() {
        return isInWhitePath;
    }

    public void setInWhitePath(boolean inWhitePath) {
        isInWhitePath = inWhitePath;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDirname() {
        return dirname;
    }

    public void setDirname(String dirname) {
        this.dirname = dirname;
    }

    public void print(){
        System.out.println("one filecontent start");
        System.out.println("filename=>"+this.getFileName());
        System.out.println("fileExt=>"+this.getFileExt());
        System.out.println("filePath=>"+this.getFilePath());
        System.out.println("fileOwner=>"+this.getOwner());
        System.out.println("Executable=>"+this.isExecutable());
        System.out.println("Hidden=>"+this.isHidden());
        System.out.println("Readable=>"+this.isReadable());
        System.out.println("Writeable=>"+this.isWriteable());
        System.out.println("lastAccessTime=>"+this.getLastAccessTime());
        System.out.println("lastModifyTime=>"+this.getLastModifyTime());
        System.out.println("createTime=>"+this.getCreateTime());
    }
}
