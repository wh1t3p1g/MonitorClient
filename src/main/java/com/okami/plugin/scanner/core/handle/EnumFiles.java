package com.okami.plugin.scanner.core.handle;

import com.okami.MonitorClientApplication;
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
    /**
     * 待扫描的文件路径
     */
    private String filePath;

    /**
     * 文件遍历+填充遍历到的文件属性到FileContent
     * @return List<FileContent>
     */
    public List<FileContent> run()
    {
        Path path= Paths.get(this.filePath);
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
            return fileContents;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public static List<FileContent> filter(List<FileContent> fileContents, String[] whitePaths) {
        if(fileContents==null||fileContents.size()==0)
            return null;
        List<FileContent> newFileContents=new ArrayList<FileContent>();
        List<String> filterExt=
                Arrays.asList(
                        "jpg","png","gif","js","css","zip","rar","swf","ttf","dat",
                        "mp3","mp4","avi","mov","aiff","mpeg","mpg","qt","ram","viv",
                        "flv","wav","map","svg","woff","woff2","eot","psd"
                );
        List<String> filterVideoExt=
                Arrays.asList("mp3","mp4","avi","mov","aiff","mpeg","mpg","qt","ram","viv",
                        "flv","wav");
        for(FileContent fileContent:fileContents){
            boolean flag=true;
            for(String whitePath:whitePaths){
                if(fileContent.getFilePath().contains(whitePath)) {//白名单内部
                    flag=false;
                    if(!filterVideoExt.contains(fileContent.getFileExt())) {//白名单内部视频文件不进行扫描
                        fileContent.setInWhitePath(true);
                        newFileContents.add(fileContent);
                    }
                    break;
                }
            }
            if(flag){//不在白名单内 剔除静态文件 jpg,png,js,css,gif等
                if(!filterExt.contains(fileContent.getFileExt())){
                    fileContent.setInWhitePath(false);
                    newFileContents.add(fileContent);
                }
            }
        }
        return newFileContents;
    }

    /**
     * 设置属性
     * @param path 文件路径
     * @return fileConent
     */
    public FileContent setAttrs(Path path) {
        try{

            DateFormat df = new SimpleDateFormat("yyyy/MM/dd H:m:s");
            FileContent fileContent= MonitorClientApplication.ctx.getBean(FileContent.class);
            String fileName=path.getFileName().toString();
            String fileExt=fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
            BasicFileAttributeView basicFileAttributeView=
                    Files.getFileAttributeView(path,BasicFileAttributeView.class);
            fileContent.setFilePath(path.toString());//文件全路径
            fileContent.setPath(path);
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


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args)
    {
        EnumFiles enumFiles = new EnumFiles();
        enumFiles.setFilePath("/Users/wh1t3P1g/Desktop/webshell");
        List<FileContent> fileContents=enumFiles.run();
        System.out.println(fileContents.size());
        String[] whitePaths={"/Users/wh1t3P1g/Desktop/webshell/aspx"};
        fileContents=EnumFiles.filter(fileContents,whitePaths);
        System.out.println(fileContents.size());

    }
}
