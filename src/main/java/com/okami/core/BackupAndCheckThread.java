package com.okami.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import com.okami.bean.ConfigBean;
import com.okami.config.DBConfig;
import com.okami.core.MonitorThread.Listener;
import com.okami.dao.impl.FileIndexDao;
import com.okami.entities.FileIndex;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import com.okami.util.ZLibUtils;

public class BackupAndCheckThread extends Thread{
	
	private String bakPath;
	
	private String cachPath;
	
	private Queue<String> qHeartBeats;
	
	private Queue<String> qMonitor;
	
	private MonitorTask monitorTask;
	
	private String time ;
	
	private FileIndexDao fileIndexDao;

	/**
	 * 初始化
	 * @data 2017年3月11日
	 * @return
	 */
    public boolean init(MonitorTask monitorTask,FileIndexDao fileIndexDao){
		ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
		this.monitorTask = monitorTask;
		this.bakPath = configBean.getBakPath()+File.separator + monitorTask.getTaskName();
		this.cachPath  = configBean.getCachPath()+File.separator + monitorTask.getTaskName();
		this.fileIndexDao = fileIndexDao;
		initPath();
		return true;
	}
    
    /**
     * 设置消息队列
     * @data 2017年4月10日
     * @param qHeartBeats
     * @param qMonitor
     * @param qRepaire
     */
    public void setQqueue(Queue<String> qHeartBeats,Queue<String> qMonitor,Queue<String> qRepaire){
        this.qHeartBeats = qHeartBeats;
        this.qMonitor = qMonitor;
    }
		
	/**
	 * 初始化路径
	 * @data 2017年4月10日
	 * @return
	 */
	private boolean initPath(){
		// 记得恢复
		File cashPath = new File(this.cachPath);
		if(cashPath.exists()){
			FileUtil.deleteAll(cashPath);
		}

//		File cashZip = new File(this.cachPath+".zip");
//		if(cashZip.exists()){
//			FileUtil.deleteAll(cashZip);
//		}
		
		// 如果是备份模式，清除备份模式。
		if(monitorTask.getBCMode() == 0){
			File bakPath = new File(this.bakPath);
			if(bakPath.exists()){
				FileUtil.deleteAll(bakPath);
			}
			bakPath.mkdir();
			
			
//			File bakZip = new File(this.bakPath+".zip");
//			if(bakZip.exists()){
//				FileUtil.deleteAll(bakZip);
//			}
		}
		return true;
	}
	
	public void run(){
		if(monitorTask.getRunMode()==0){
			return;
		}
		
		// 备份模式
		if(monitorTask.getBCMode() == 0){
			backupMode();
		}
		
		// 自检模式
		else if (monitorTask.getBCMode()==1){
			checkMode();
		}
		
	}
	
	
	/**
	 * 备份模式运行
	 * @data 2017年3月11日
	 * @return
	 */
	public boolean backupMode(){
		
		if(monitorTask.getRunMode()==0){
			return false;
		}
		
		// 新建数据库
		try {
			fileIndexDao.createTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//备份
		time = DataUtil.getTime();
		File dir = new File(monitorTask.getMonitorPath());
		
		backup(dir);
		

		
		if(monitorTask.getRunMode()==0){
			return false;
		}
		
		// 切换至安全模式		
		qMonitor.offer("True");
		qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tTurn To Safe Mode : " + monitorTask.getMonitorPath());
		
		// 压缩		
//		cBakFlagPath = bakPath + File.separator + "flag" + File.separator  +monitorTask.getTaskName();
//		ZipUtil.addFoldInZip(this.bakPath, null, this.cachPath +".zip", "123456");
//		ZipUtil.addFoldInZip(this.cBakFlagPath, null, this.cCachFlagPath +".zip", "123456");
		
		// 上传,然后删除flag文件
		return true;
	}
	
	/**
	 * 递归备份
	 * @data 2017年3月11日
	 * @param dir
	 * @return
	 */
	public boolean backup(File dir){
		if(monitorTask.getRunMode()==0){
			return false;
		}
		try {
			File[] file = dir.listFiles();
			File tBakFile = null;
			String sha1Str = null;

			for(int i=0; i<file.length; i++){
				FileIndex fileIndex = new FileIndex();
				if(file[i].isDirectory()){
					fileIndex.setType("Fold");
					backup(file[i]);
				}else{
					// 压缩文件
					sha1Str = DataUtil.getSHA1ByFile(file[i]);
					String bakname = bakPath+File.separator+sha1Str.substring(0,2);
					File bakFile = new File(bakname);
					if(!bakFile.exists()){
						bakFile.mkdir();
					}
					bakname = bakname + File.separator + sha1Str.substring(2);
		            byte[] contentBytes = ZLibUtils.compress(Files.readAllBytes(Paths.get(file[i].toString())));   
		            Files.write(Paths.get(bakname), contentBytes,
		                    StandardOpenOption.CREATE);
		            fileIndex.setType("File");
		            fileIndex.setSize(String.valueOf(file[i].length()));
		            fileIndex.setSha1(sha1Str);
				}
				fileIndex.setTime(time);
				fileIndex.setPath(file[i].getAbsolutePath().substring(monitorTask.getMonitorPath().length()));
				fileIndex.setWrite((file[i].canWrite())? 1:0);
				fileIndex.setRead((file[i].canRead())? 1:0);
				fileIndex.setExec((file[i].canExecute())? 1:0);
				fileIndex.setStatus(1);
				fileIndexDao.insertIndex(fileIndex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 自检模式
	 * @data 2017年3月11日
	 * @return
	 */
	public boolean checkMode(){
		
		// 自检
		if(monitorTask.getRunMode()==0){
			return true;
		}
		
		// 下载服务器的文件树
		
		
		// 检查flag树的Sha1，与服务其那边的sha1 是否相同，不正常则恢复flag与文件
		if(checkBakFlag()){
			
			//根据备份中的flag树整理备份中的文件，不正常则回复文件
			if(checkBakFile()){
			
				// 检查网站文件,发生异常
				if(!checkWebFile()){

					
				}
			}
			
			// 恢复备份文件以及网站文件
			else{
				
			}
			
		}else{
			// 更换文件树、还原备份文件以及网页源码
			
		}
		

		
		if(monitorTask.getRunMode()==0){
			return false;
		}
		
		// 关闭
		try {
			fileIndexDao.closeConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 切换至安全模式
		qMonitor.offer("True");
		qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tTurn To Safe Mode : " + monitorTask.getMonitorPath());

		return true;
	}
	
	/**
	 * 检查索引文件是否正常
	 * @data 2017年3月11日
	 * @return
	 */
	public boolean checkBakFlag(){
		String bakFlagSha1 = DataUtil.getSHA1ByFile(new File(bakPath+File.separator+monitorTask.getTaskName()));
		String cloudFlagSha1 = DataUtil.getSHA1ByFile(new File(cachPath+File.separator+monitorTask.getTaskName()));
		return bakFlagSha1.equals(cloudFlagSha1) ;
	}
	
	/**
	 * 检查备份文件
	 * @data 2017年4月11日
	 * @return
	 */
	public boolean checkBakFile(){	
		try {
			// 查询文件树
			List<FileIndex> fileIndexList = fileIndexDao.queryIndex();
			String bakPath = this.bakPath + File.separator + monitorTask.getTaskName();
			String bakSha1;
			String bakname;
			File file;
			
			// 遍历文件树
			for(FileIndex fileIndex:fileIndexList){
				if(monitorTask.getRunMode()==0){
					return true;
				}
				
				// 如果是文件则计算备份文件的sha1
				if(fileIndex.getType().equals("File")){
					bakname = bakPath + File.separator + fileIndex.getSha1().substring(0,2)+ File.separator +fileIndex.getSha1().substring(2) ;
					file = new File(bakname);
					if(!file.exists()){
						return false;
					}else{
						bakSha1 = DataUtil.getSHA1(ZLibUtils.decompress(Files.readAllBytes(Paths.get(bakname))));
						if(!fileIndex.getSha1().equals(bakSha1)){
							return false;
						}
					}		
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * 检查网站文件
	 * @data 2017年4月11日
	 * @return
	 */
	public boolean checkWebFile(){	
		try {
			// 查询文件树
			List<FileIndex> fileIndexList = fileIndexDao.queryIndex();
			String webFile;
			String webSha1;
			File file;
			
			// 遍历文件树
			for(FileIndex fileIndex:fileIndexList){
				if(monitorTask.getRunMode()==0){
					return true;
				}
				
				webFile = monitorTask.getMonitorPath() + fileIndex.getPath();  
				
				// 如果是文件则计算备份文件的sha1
				if(fileIndex.getType().equals("File")){
					file = new File(webFile);
					if(!file.exists()){
						return false;
					}else{
						webSha1 = DataUtil.getSHA1(webFile);
						if(!fileIndex.getSha1().equals(webSha1)){
							return false;
						}
					}		
				}
				
				// 如果是文件夹，则判断是否存在
				else if(fileIndex.getType().equals("Fold")){
					file = new File(webFile);
					if(!file.exists()){
						return false;
					}	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void setQHeartBeats(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
	}
	
	public void setQMonitor(Queue<String> qMonitor){
		this.qMonitor = qMonitor;
	}
	
}
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.Date;
//import java.util.List;
//import java.util.Queue;
//
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import com.okami.MonitorClientApplication;
//import com.okami.bean.ConfigBean;
//import com.okami.util.FileUtil;
//import com.okami.util.ZLibUtils;
//import com.okami.util.ZipUtil;
//import com.okami.util.DataUtil;
//import com.okami.entities.MonitorTask;
//
///**
// * 备份以及自检模块
// * @author orleven
// * @date 2016年12月31日
// */
//
//public class BackupAndCheckThread extends Thread{
//	
//	private String bakPath;
//	
//	private String cachPath;
//	
////	private String checkPath;
//	
//	/**
//	 * 当前备份地址
//	 */
//	private String cBakPath;
//	
//	/**
//	 * 当前缓存地址
//	 */
//	private String cCachPath;
//	
//	/**
//	 * 当前自检地址
//	 */
////	private String cCheckPath;
//	
//	/**
//	 * 当前flag缓存地址
//	 */
//	private String cCachFlagPath;
//	
//	/**
//	 * 当前flag备份地址
//	 */
//	private String cBakFlagPath;
//	
//	private Queue<String> qHeartBeats;
//	private Queue<String> qMonitor;
//	private MonitorTask monitorTask;
//
//	/**
//	 * 初始化
//	 * @param taskConfigBean
//	 */
//	public BackupAndCheckThread(MonitorTask monitorTask,ConfigBean configBean)
//	{
//		this.monitorTask = monitorTask;
//		bakPath = cBakPath = configBean.getBakPath()+File.separator + monitorTask.getTaskName();
//		cachPath = cCachPath = configBean.getCachPath()+File.separator + monitorTask.getTaskName();
//		cBakFlagPath = bakPath + File.separator + "flag" + File.separator  +monitorTask.getTaskName();
//		cCachFlagPath = cachPath + File.separator + "flag" + File.separator  +monitorTask.getTaskName();
////		checkPath = cCheckPath = configBean.getCheckPath() + File.separator + monitorTask.getTaskName();
//	}
//
//	/**
//	 * 初始化
//	 * @data 2017年3月9日
//	 * @return
//	 */
//	public boolean init(){
//		// 记得恢复
////		File cashPath = new File(this.cachPath);
////		if(cashPath.exists()){
////			FileUtil.deleteAll(cashPath);
////		}
////		new File(this.cCachFlagPath).mkdirs();
////		
////		File cashZip = new File(this.cachPath+".zip");
////		if(cashZip.exists()){
////			FileUtil.deleteAll(cashZip);
////		}
//		
//		File bakZip = new File(this.bakPath+".zip");
//		if(bakZip.exists()){
//			FileUtil.deleteAll(bakZip);
//		}
//		
//		if(monitorTask.getBCMode() == 0){
//			File bakPath = new File(this.bakPath);
//			if(bakPath.exists()){
//				FileUtil.deleteAll(bakPath);
//			}
//			new File(this.cBakFlagPath).mkdirs();
//		}
//		
//		return true;
//	}
//	
//	public void run(){
//		if(monitorTask.getRunMode()==0){
//			return;
//		}
//		
//		/// 初始化
//		init();
//
//		// 备份模式
//		if(monitorTask.getBCMode() == 0){
//			backupMode();
//		}
//		
//		// 自检模式
//		else if (monitorTask.getBCMode()==1){
//			checkMode();
//		}
//		
//
//	}
//	
//	/**
//	 * 备份模式
//	 * @return
//	 */
//	public boolean backupMode(){
//		if(monitorTask.getRunMode()==0){
//			return false;
//		}
//		//备份
//		File dir = new File(monitorTask.getMonitorPath());
//		backup(dir);
//		
//		// 追加flag文件
//		String flagStr = "File|"+monitorTask.getTaskName()+"|"+monitorTask.getMonitorPath()+"|"+String.valueOf(dir.canRead())+
//				"|"+String.valueOf(dir.canWrite())+"|"+String.valueOf(dir.canExecute());
//		cBakPath = Paths.get(cBakPath).getParent().toString();
//		String flagPath = cBakPath+File.separator+ monitorTask.getFlagName();
//		boolean flag = true;  // 循环判断标志
//		if(new File(flagPath).exists())
//		{
//			List<String> contents = FileUtil.readLines(flagPath);
//			for(int i=0;i<contents.size();i++){
//				if(contents.get(i).equals(flagStr)){
//					flag = false;
//					break;
//				}
//			}
//		}
//		if(flag){
//			flagStr += "\r\n";
//			FileUtil.write(flagPath, flagStr,true);
//		}
//
//
//		if(monitorTask.getRunMode()==0){
//			return false;
//		}
//		
//		// 切换至安全模式
//		qMonitor.offer("True");
//		qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tTurn To Safe Mode : " + monitorTask.getMonitorPath());
//		
//		// 复制一份flag树 
//		backupFlag(new File(this.bakPath));
//		
//		// 压缩		
//		cBakFlagPath = bakPath + File.separator + "flag" + File.separator  +monitorTask.getTaskName();
//		ZipUtil.addFoldInZip(this.bakPath, null, this.cachPath +".zip", "123456");
//		ZipUtil.addFoldInZip(this.cBakFlagPath, null, this.cCachFlagPath +".zip", "123456");
//		
//		// 上传,然后删除flag文件
//		
//		
//		return true;
//	}
//	
//	/**
//	 * 备份
//	 * @param dir
//	 */
//	public boolean backup(File dir){
//		if(monitorTask.getRunMode()==0){
//			return false;
//		}
//		try {
//			File[] file = dir.listFiles();
//			File tBakFile = null;
//			String MD5Str = null;
//			String flagStr = null;
//			for(int i=0; i<file.length; i++){
//				if(file[i].isDirectory()){
//					// 创建文件夹
//					MD5Str = DataUtil.getMD5(file[i].getName());
//					flagStr = "Fold|"+MD5Str+"|"+file[i].getName()+
//							"|"+String.valueOf(file[i].canRead())+
//							"|"+String.valueOf(file[i].canWrite())+
//							"|"+String.valueOf(file[i].canExecute())+"\r\n";
//					cBakPath = cBakPath+File.separator+MD5Str;
//					tBakFile = new File(cBakPath);
//					tBakFile.mkdir();
//					backup(file[i]);
//					cBakPath = tBakFile.getParent();
//					// 文件夹为空则创建flag文件
//					if(file[i].listFiles().length == 0){
//						tBakFile = new File(tBakFile.getAbsolutePath()+File.separator+monitorTask.getFlagName());
//						tBakFile.createNewFile();
//					}
//					
//				}else{
//					// 压缩文件
//					MD5Str = DataUtil.getMd5ByFile(file[i]);
//					flagStr = "File|"+MD5Str+"|"+file[i].getName()+
//							"|"+String.valueOf(file[i].canRead())+
//							"|"+String.valueOf(file[i].canWrite())+
//							"|"+String.valueOf(file[i].canExecute())+"\r\n";
//		            byte[] contentBytes = ZLibUtils.compress(Files.readAllBytes(Paths.get(file[i].toString())));   
//		            Files.write(Paths.get(cBakPath+File.separator+MD5Str), contentBytes,
//		                    StandardOpenOption.CREATE);
//				}
//				
//				// 追加flag文件
//				FileUtil.write(cBakPath+File.separator+monitorTask.getFlagName(), flagStr,true);
//
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	/**
//	 * 复制一份一样的flag文件。
//	 * @param dir
//	 */
//	public boolean backupFlag(File dir){
//		if(monitorTask.getRunMode()==0){
//			return false;
//		}
//		try {
//			
//			File[] file = dir.listFiles();
//			File tBakFlagFile = null;
//			for(int i=0; i<file.length; i++){
//				if(file[i].isDirectory()){
//					cBakFlagPath = cBakFlagPath+File.separator+ file[i].getName();
//					tBakFlagFile = new File(cBakFlagPath);
//					tBakFlagFile.mkdir();
//					backupFlag(file[i]);
//					cBakFlagPath = tBakFlagFile.getParent();
//				}else if(file[i].isFile()&&file[i].getName().equals(monitorTask.getFlagName())){
//					byte[] contentBytes = Files.readAllBytes(Paths.get(file[i].toString()));          
//		            Files.write(Paths.get(cBakFlagPath+File.separator+file[i].getName()), contentBytes,
//		                    StandardOpenOption.CREATE);
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return true;			
//	}
//	
//	/**
//	 * 自检模式
//	 * @return
//	 */
//	public boolean checkMode(){
//		// 自检
//		if(monitorTask.getRunMode()==0){
//			return true;
//		}
//
//		// 检查flag树，对比flag文件和备份中的falg树，不正常则恢复flag与文件
//		if(checkBakFlag()){
//			//根据备份中的flag树整理备份中的文件，不正常则回复文件
//			if(checkBakFile(new File(this.bakPath))){
//				
//			}
//			
//		}else{
//			// 还原备份文件以及网页源码
//			
//		}
//		
//		//根据备份中的flag文件来检查网站源码是否异常，不正常则回复网站
//		
//		
//		if(monitorTask.getRunMode()==0){
//			return false;
//		}
//		
//		// 切换至安全模式
//		qMonitor.offer("True");
//		qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tTurn To Safe Mode : " + monitorTask.getMonitorPath());
//		
//		
//		return true;
//
//	}
//	
//
//	
//	/**
//	 * 检查备份中的flag文件树
//	 * @return
//	 */
//	public boolean checkBakFlag(){
//		//下载flag文件包到check目录
//		
//		
//		
//		// 复制一份flag树并进行压缩
//		backupFlag(new File(this.bakPath));
//		ZipUtil.addFoldInZip(this.cBakFlagPath, null, this.cBakFlagPath +".zip", "123456");
//		
//		// 对比md5 
//		String bakMd5 = DataUtil.getMd5ByFile(new File(this.cBakFlagPath +".zip"));
//		String CloudMd5 = DataUtil.getMd5ByFile(new File(this.cCachFlagPath +".zip"));
//		if (!CloudMd5.equals(bakMd5)){
//			return false;
//		}
//		return true;
//	}
//	
//	/**
//	 * 检查备份的文件是否正常
//	 * @data 2017年3月9日
//	 * @return
//	 */
//	public boolean checkBakFile(File dir){		
////		File[] file = dir.listFiles();
////		File tBakFile = null;
////		for(int i=0; i<file.length; i++){
////			if(file[i].isDirectory()){
////				cCachPath = cCachPath+File.separator+ file[i].getName();
////				tCashFile = new File(cCachPath);
////				tCashFile.mkdir();
////				backupFlag(file[i]);
////				cCachPath = tCashFile.getParent();
////			}else if(file[i].isFile()&&file[i].getName().equals(monitorTask.getFlagName())){
////				byte[] contentBytes = Files.readAllBytes(Paths.get(file[i].toString()));          
////	            Files.write(Paths.get(cCachPath+File.separator+file[i].getName()), contentBytes,
////	                    StandardOpenOption.CREATE);
////			}
////		}
////		String flagMd5 = 
////		String fileContMd5 = 
////		String fileNameMd5 = 
////		return true;
//	}
//	
//	public void setQHeartBeats(Queue<String> qHeartBeats){
//		this.qHeartBeats = qHeartBeats;
//	}
//	
//	public void setQMonitor(Queue<String> qMonitor){
//		this.qMonitor = qMonitor;
//	}
//	
////    public static void main(String[] args) {  
////        ConfigBean configBean = new ConfigBean();
////        configBean.setStoragePath("C:\\Users\\dell\\Desktop\\存储地址");
////        configBean.setLhost("127.0.0.1");
////        configBean.setRhost("127.0.0.1");
////        configBean.setLport("5002");
////        configBean.setRport("5001");
////        configBean.setRemoteMode(true);
////        
////        String monitorPath = "C:\\Users\\dell\\Desktop\\测试文件"; 
////        MonitorTask monitorTask = new MonitorTask();
////        monitorTask.setTaskName("96780e40b21ae9cc");
////
////        monitorTask.setMonitorPath(monitorPath);
////        monitorTask.setFlagName("126b08f89b70cd74");
////        monitorTask.setProjectName("96780e40b21ae9cc");
////        monitorTask.setBCMode(0);
////        monitorTask.setRunMode(1);
////        BackupAndCheckThread backupAndCheck = new BackupAndCheckThread(monitorTask,configBean);
////        backupAndCheck.run();
////		try {
////			sleep(30000);
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////    }  
//
//}

