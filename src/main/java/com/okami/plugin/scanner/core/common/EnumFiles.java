package com.okami.plugin.scanner.core.common;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/2
 */
@Component
@Scope("prototype")
public class EnumFiles {
    private BaseTask task;

    /**
     * 文件遍历+填充遍历到的文件属性到FileContent
     * @return List<FileContent>
     */
    public List<FileContent> run()
    {
        Path path= Paths.get(task.getFilePath());
        List<FileContent> fileContents=new ArrayList<FileContent>();
        ListFileTree listFileTree=new ListFileTree();
        try{
            Files.walkFileTree(path,listFileTree);
            for(Path path1:listFileTree.paths){
                FileContent fileContent=setAttrs(path1);
                if(fileContent!=null){
                    fileContents.add(fileContent);
                }
            }
            if(task.isFilter()){
                fileContents=this.filter(fileContents,task.getExceptPath(),task.getExceptExtension());
            }
            if(task.getMode()==1){//fast scanner
                fileContents=this.filter(fileContents,task.getScriptExtension());
            }
            return fileContents;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    private List<FileContent> filter(List<FileContent> fileContents,String extension){
        List<FileContent> lists=new ArrayList<>();
        List<String> extensions=Arrays.asList(extension.split(","));
        for (FileContent fileContent :fileContents) {
            String ext=fileContent.getFileExt();
            if(extensions.contains(ext)){
                lists.add(fileContent);
            }
        }
        return lists;
    }

    private List<FileContent> filter(List<FileContent> fileContents,String exceptPath,String exceptExtension){
        List<String> exceptPaths=null;
        List<String> exceptExtensions=null;
        if(exceptPath!=null&&!exceptPath.isEmpty())
            exceptPaths=Arrays.asList(exceptPath.split(","));
        if(exceptExtension!=null&&!exceptExtension.isEmpty())
            exceptExtensions=Arrays.asList(exceptExtension.split(","));


        List<FileContent> lists=new ArrayList<>();
        for (FileContent fileContent:fileContents) {
            String extension=fileContent.getFileExt();
            String dirname=fileContent.getDirname();
            dirname=dirname.substring(0,dirname.length()-1);
            if(exceptExtensions!=null&&
                    !exceptExtension.isEmpty()&&
                    exceptExtensions.contains(extension)){
                continue;
            }
            if(exceptPaths!=null&&
                    !exceptPath.isEmpty()){
                boolean flag=false;
                for (String path:exceptPaths) {
//                	System.out.println(path);
//                	System.out.println(dirname);
                    if(dirname.contains(path)){
                        flag=true;
                        break;
                    }
                }
                if(flag)
                    continue;
            }
            lists.add(fileContent);

        }
        return lists;
    }
    /**
     * 设置属性
     * @param path 文件路径
     * @return fileConent
     */
    public FileContent setAttrs(Path path) {
        try{
            if(Files.isDirectory(path))return null;
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd H:m:s");
            FileContent fileContent= MonitorClientApplication.ctx.getBean(FileContent.class);
            String fileName=path.getFileName().toString();
            String dirname=path.toString().substring(0,path.toString().indexOf(fileName));
            String fileExt=fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
            BasicFileAttributeView basicFileAttributeView=
                    Files.getFileAttributeView(path,BasicFileAttributeView.class);
            fileContent.setFilePath(path.toString());//文件全路径
            fileContent.setPath(path);
            fileContent.setDirname(dirname);
            fileContent.setFileName(fileName);//文件名
            fileContent.setFileExt(fileExt);//文件后缀名
            fileContent.setExecutable(Files.isExecutable(path));//是否可执行
            fileContent.setWriteable(Files.isWritable(path));//是否可写
            fileContent.setReadable(Files.isReadable(path));//是否可读
            fileContent.setHidden(Files.isHidden(path));//是否是隐藏文件
            fileContent.setOwner(Files.getOwner(path).toString());//文件拥有者
            fileContent.setSize(Files.size(path));//文件大小
            fileContent.setLastAccessTime(//文件最后打开时间
                    df.format(basicFileAttributeView.readAttributes().lastAccessTime().toMillis()));
            fileContent.setLastModifyTime(//文件最后修改时间
                    df.format(basicFileAttributeView.readAttributes().lastModifiedTime().toMillis()));
            fileContent.setCreateTime(//文件创建时间
                    df.format(basicFileAttributeView.readAttributes().creationTime().toMillis()));
            return fileContent;
        }catch (IOException e){
            return null;
        }

    }

    public BaseTask getTask() {
        return task;
    }

    public void setTask(BaseTask task) {
        this.task = task;
    }
}
