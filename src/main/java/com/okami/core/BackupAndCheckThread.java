package com.okami.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.bean.RequrieBean;
import com.okami.common.HttpHandler;
import com.okami.dao.impl.FileIndexDao;
import com.okami.entities.FileIndex;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import com.okami.util.ZLibUtil;
import com.okami.util.ZipUtil;

@Component
@Scope("prototype")
public class BackupAndCheckThread extends Thread{
	
	@Autowired
	private HttpHandler httpHandler;
	
	@Autowired
	private GlobaVariableBean globaVariableBean;
	
	private String bakPath;
	
	private String cachPath;
	
	private Queue<String> qHeartBeats;
	
	private Queue<String> qMonitor;
	
	private MonitorTask monitorTask;
	
	private String time ;
	
	private FileIndexDao fileIndexDao;
	
	private ConfigBean configBean;
	
	private int rarId = 1;  // rar的id
	
	private int size = 0;  // 压缩rar当前的大小
	
	private int maxSize ;   // 一个rar最大的长度
	
	private int status =0 ;
	
	private String[] whiteList = null;
	
	

	/**
	 * 初始化
	 * @data 2017年3月11日
	 * @return
	 */
    public boolean init(MonitorTask monitorTask,FileIndexDao fileIndexDao){
		this.configBean = IOC.instance().getClassobj(ConfigBean.class);
		this.monitorTask = monitorTask;
		this.bakPath = configBean.getBakPath()+File.separator + monitorTask.getTaskName();
		this.cachPath  = configBean.getCachPath()+File.separator + monitorTask.getTaskName();
		this.fileIndexDao = fileIndexDao;
		this.maxSize = Integer.parseInt(monitorTask.getMaxSize());
		this.time = DataUtil.getTime();
        if(monitorTask.getWhiteList()!=null&&!monitorTask.getWhiteList().equals("")){
            this.whiteList = monitorTask.getWhiteList().split(",");
        }
        else{
            this.whiteList = null;
        }
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
    public void setQqueue(Queue<String> qHeartBeats,Queue<String> qMonitor,Stack<RequrieBean> qRepaire){
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
		cashPath.mkdirs();
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
			bakPath.mkdirs();
			
//			File bakZip = new File(this.bakPath+".zip");
//			if(bakZip.exists()){
//				FileUtil.deleteAll(bakZip);
//			}
		}
		return true;
	}
	
	public void run(){
		initPath();
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
		
		if(monitorTask.getStatus()==0){
			return false;
		}
		
		// 新建数据库
		try {
			if(!fileIndexDao.isTableExist()){
				fileIndexDao.createTable();
			}else{
//				fileIndexDao.deleteAll();
				fileIndexDao.deleteTable();
				fileIndexDao.createTable();
			}
		} catch (Exception e) {
			e.printStackTrace();
			IOC.log.error(e.getMessage());
		}

		
		//备份
		backup(new File(monitorTask.getMonitorPath()));
		
		
		if(monitorTask.getStatus()==0){
			return false;
		}
		
		

		// 切割大文件
		addRar(new File(this.bakPath),bakPath);
		
		// 切换至安全模式		
		qMonitor.offer("True");
		qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tTurn To Safe Mode: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
		IOC.log.warn("Info: Turn To Safe Mode: " + monitorTask.getMonitorPath());
		


		
		// 以后变成自检模式，并插入数据库
		monitorTask.setBCMode(1);
		for(int i=0;i<globaVariableBean.getMonitorTaskList().size();i++){
			if(globaVariableBean.getMonitorTaskList().get(i).getTaskName().equals(monitorTask.getTaskName())){
				globaVariableBean.getMonitorTaskList().get(i).setBCMode(1);
			}
		}
		try {
			globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
		} catch (Exception e) {
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
		}
		
//		boolean flag = true;
//		
//		// 上传rar文件
//		String result = null ;
//		File[] files = new File(this.cachPath).listFiles();
//		for(File file:files){
//			result = httpHandler.upload(file);
//			if(result==null || result.indexOf("success")<=0)
//			{
//				flag = false;
//			}
//		}

//		// 上传flag文件 
//		File file = new File(configBean.getBakPath() + File.separator + monitorTask.getFlagName());
//		result = httpHandler.upload(file);
//		if(result==null || result.indexOf("success")<=0)
//		{
//			flag = false;
//		}
//		
//		if(flag){
//			// 更新数据库的上传标志
//			try {
//				monitorTask.setUpload(1);
//				globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
//			} catch (Exception e) {
//				e.printStackTrace();
//				IOC.log.error(e.getMessage());
//			}
//			
//			qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tUpload Success: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
//			IOC.log.warn("Info: Upload Success: " + monitorTask.getMonitorPath());
//			
//			// 上传后删除rar文件
//			File cashPath= new File(this.cachPath);
//			if(cashPath.exists()){
//				FileUtil.deleteAll(cashPath);
//			}
//		}else{
//			qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tUpload Failed: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
//			IOC.log.warn("Info: Upload Failed: " + monitorTask.getMonitorPath());
//		}
//		
		

		
		return true;
	}
	
	/**
	 * 递归备份,同时添加压缩包
	 * @data 2017年3月11日
	 * @param dir
	 * @return
	 */
	public boolean backup(File dir){
		if(monitorTask.getStatus()==0){
			return false;
		}
		
		try {
			File[] file = dir.listFiles();
			String sha1Str = null;

			for(int i=0; i<file.length; i++){
				FileIndex fileIndex = new FileIndex();
				if(file[i].isDirectory()){
					fileIndex.setType("Fold");
					fileIndex.setTime(time);
					fileIndex.setPath(file[i].getAbsolutePath().substring(monitorTask.getMonitorPath().length()));
					fileIndex.setWrite((file[i].canWrite())? 1:0);
					fileIndex.setRead((file[i].canRead())? 1:0);
					fileIndex.setExec((file[i].canExecute())? 1:0);
					fileIndex.setStatus(1);
					fileIndexDao.insertIndex(fileIndex);
					backup(file[i]);
				}else{
					// 压缩文件
					sha1Str = DataUtil.getSHA1ByFile(file[i]);
					String temFold = sha1Str.substring(0,2);
					String bakname = bakPath + File.separator + temFold;
					File bakFile = new File(bakname);
					if(!bakFile.exists()){
						bakFile.mkdir();
					}
					bakname = bakname + File.separator + sha1Str.substring(2);
		        	byte[] contentBytes = ZLibUtil.compress(FileUtil.readByte(file[i].getAbsolutePath()));
					FileUtil.write(bakname, contentBytes,false);
		   
					if(contentBytes.length>=maxSize){
						FileUtil.cutFile(bakname, maxSize);
					}
		            
		            fileIndex.setType("File");
		            fileIndex.setSize(String.valueOf(file[i].length()));
		            fileIndex.setSha1(sha1Str);
					fileIndex.setTime(time);
					fileIndex.setPath(file[i].getAbsolutePath().substring(monitorTask.getMonitorPath().length()));
					fileIndex.setWrite((file[i].canWrite())? 1:0);
					fileIndex.setRead((file[i].canRead())? 1:0);
					fileIndex.setExec((file[i].canExecute())? 1:0);
					fileIndex.setStatus(1);
					fileIndexDao.insertIndex(fileIndex);
				}

			}
		} catch (Exception e) {
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
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
		if(monitorTask.getStatus()==0){
			return true;
		}
		
		byte[] contentBytes;
		
		//根据备份中的flag树整理备份中的文件，不正常则回复文件
		String[] rarIds = checkBakFile();
		if(rarIds!=null&&rarIds.length>0){
			// 恢复备份文件以及网站文件
			qHeartBeats.offer(DataUtil.getTime()+"\tRepaire\tThe Bak Files Are Inconsistent: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
			IOC.log.warn("Repaire: The Bak Files Are Inconsistent: " + monitorTask.getMonitorPath());
			String filename = null;
			
			for(String rarId:rarIds){
				// 下载相应的rar文件并进行解压
				filename = monitorTask.getTaskName()+"_"+rarId+".rar";
				contentBytes = httpHandler.download(filename);
				if(contentBytes!=null){
					FileUtil.write(cachPath+File.separator+filename, contentBytes,false);
					// 解压rar
					ZipUtil.extractZip(cachPath+File.separator+filename, bakPath, monitorTask.getFlagName());
					
				}else{
					// 自检出现异常
					monitorTask.setStatus(0);
	                qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tUnable To Fix Backup File! Stop Monitor: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
	            	IOC.log.warn("Info: Unable To Fix Backup File! Stop Monitor: " + monitorTask.getMonitorPath());
	            	return false;
				}
				
			}
			qHeartBeats.offer(DataUtil.getTime()+"\tRepaire-Machine\tThe Bak Files Has Fixed: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
			IOC.log.warn("Repaire-Machine: The Bak Files File Has Fixed: " + monitorTask.getMonitorPath());
		}
		if(monitorTask.getStatus()==0){
			return false;
		}
		

		// 检查网站文件,发生异常
		checkWebFile();


		if(monitorTask.getStatus()==0){
			return false;
		}
		
		
		// 切换至安全模式
		qMonitor.offer("True");
		qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tTurn To Safe Mode: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
		IOC.log.warn("Info: Turn To Safe Mode: " + monitorTask.getMonitorPath());
		
		// 上传后删除rar文件
		File cashPath= new File(this.cachPath);
		if(cashPath.exists()){
			FileUtil.deleteAll(cashPath);
		}
		
		return true;
	}
	

	
	/**
	 * 检查备份文件
	 * @data 2017年4月11日
	 * @return
	 */
	public String[] checkBakFile(){	
		String rarIds = "";
		try {
			// 查询文件树
			List<FileIndex> fileIndexList = fileIndexDao.queryIndex();
			String bakPath = this.bakPath ;
			String bakname;
			File file;
			
			// 遍历文件树
			for(FileIndex fileIndex:fileIndexList){
				if(monitorTask.getStatus()==0){
					return null;
				}
				
				// 如果是文件则计算备份文件的sha1
				if(fileIndex.getType().equals("File")){
					bakname = bakPath + File.separator + fileIndex.getSha1().substring(0,2)+ File.separator +fileIndex.getSha1().substring(2) ;
					file = new File(bakname);
					if(!file.exists()){
//						System.out.println("a"+fileIndex.getPath());
						rarIds = rarIds + fileIndex.getRarId() + ",";
					}else{
						byte[] contentBytes = FileUtil.readByte(bakname);
						if(contentBytes.length==0||!fileIndex.getSha1().equals(DataUtil.getSHA1(ZLibUtil.decompress(contentBytes)))){
//							System.out.println("b"+fileIndex.getPath());		
							FileUtil.deleteAll(file);
							rarIds = rarIds + fileIndex.getRarId() + ",";
						}
					}	
					
				}

			}
		} catch (Exception e) {
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
		}

		return DataUtil.removeDuplicate(rarIds.split(","));
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
			boolean whiteFlag = false;

			
			// 遍历文件树
			for(FileIndex fileIndex:fileIndexList){
				if(monitorTask.getStatus()==0){
					return true;
				}
				
				webFile = monitorTask.getMonitorPath() + fileIndex.getPath();  
				
				// 白名单跳过自检
				whiteFlag = false;
                String parentPath = webFile.substring(0,webFile.lastIndexOf(File.separator));
                if(whiteList!=null){
	                for(int i=0;i<whiteList.length;i++){
	                	if(parentPath.indexOf(whiteList[i]) == 0){  // 不应该大于0
	                    	whiteFlag = true;
	                    	break;
	                    }
	                }
                }
                if(whiteFlag)
                	continue;
                

				// 如果是文件则计算网站文件的sha1
				if(fileIndex.getType().equals("File")){
					file = new File(webFile);
					
					if(!file.exists()){
						
						qHeartBeats.offer(DataUtil.getTime()+"\tRepaire\tThe Web Site File Is Inconsistent: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
						IOC.log.warn("Repaire: The Web Site File Is Inconsistent: " + monitorTask.getMonitorPath() + fileIndex.getPath());
						restoreFile(fileIndex);
						qHeartBeats.offer(DataUtil.getTime()+"\tRepaire-Machine\tThe Web Site File Has Fixed: "+monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
						IOC.log.warn("Repaire-Machine: The Web Site Files Has Fixed: " + monitorTask.getMonitorPath() + fileIndex.getPath());
					}else{
						webSha1 = DataUtil.getSHA1ByFile(file);
						if(!fileIndex.getSha1().equals(webSha1)){
							qHeartBeats.offer(DataUtil.getTime()+"\tRepaire\tThe Web Site File Is Inconsistent: " + monitorTask.getMonitorPath() + fileIndex.getPath()+"\t"+monitorTask.getTaskName());
							IOC.log.warn("Repaire: The Web Site File Is Inconsistent: " + monitorTask.getMonitorPath() + fileIndex.getPath());
							restoreFile(fileIndex);
							IOC.log.warn("Repaire-Machine: The Web Site Files Has Fixed: " + monitorTask.getMonitorPath() + fileIndex.getPath());
							qHeartBeats.offer(DataUtil.getTime()+"\tRepaire-Machine\tThe Web Site Files Has Fixed: " + monitorTask.getMonitorPath() + fileIndex.getPath()+"\t"+monitorTask.getTaskName());
						}
					}		
				}
				
				// 如果是文件夹，则判断是否存在
				else if(fileIndex.getType().equals("Fold")){
					file = new File(webFile);
					if(!file.exists()){
						qHeartBeats.offer(DataUtil.getTime()+"\tRepaire\tThe Web Site File Is Inconsistent: " + monitorTask.getMonitorPath() + fileIndex.getPath()+"\t"+monitorTask.getTaskName());
						IOC.log.warn("Repaire: The Web Site File Is Inconsistent:" + monitorTask.getMonitorPath() + fileIndex.getPath());
						file.mkdirs();
						file.setExecutable(fileIndex.getExec()==1);
						file.setWritable(fileIndex.getWrite()==1);
						file.setReadable(fileIndex.getRead()==1);
						IOC.log.warn("Repaire-Machine: The Web Site Files Has Fixed: " + monitorTask.getMonitorPath() + fileIndex.getPath());
						qHeartBeats.offer(DataUtil.getTime()+"\tRepaire-Machine\tThe Web Site Files Has Fixed: " + monitorTask.getMonitorPath() + fileIndex.getPath()+"\t"+monitorTask.getTaskName());
					}	
				}
			}
			
			// 判断网站文件中多了某个文件
			checkWebSpilthFile(new File(monitorTask.getMonitorPath()));
			
		} catch (Exception e) {
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
		}
		return true;
	}
	
	/**
	 * 判断网站中是否多了不应该有的文件
	 * @data 2017年4月19日
	 * @param files
	 * @return
	 */
	private boolean checkWebSpilthFile(File files){
		// 白名单跳
		if(whiteList!=null){
	        for(int i=0;i<whiteList.length;i++){
	        	if(files.getAbsolutePath().toString().indexOf(whiteList[i]) == 0){  // 不应该大于0
	            	return true;
	            }
	        }
		}
		
		for(File file:files.listFiles()){
			FileIndex fileIndex = null;
			
			try {
				String temStr = file.getAbsolutePath().substring(monitorTask.getMonitorPath().length());
				fileIndex = this.fileIndexDao.queryOneIndexByPath(temStr);
				if(fileIndex==null){
					qHeartBeats.offer(DataUtil.getTime()+"\tRepaire\tThe Web Site File Is Inconsistent: " + monitorTask.getMonitorPath() + temStr+"\t"+monitorTask.getTaskName());
					IOC.log.info("Repaire: The Web Site File Is Inconsistent:" + monitorTask.getMonitorPath() + temStr);
					FileUtil.deleteAll(file);
					IOC.log.info("Repaire-Machine: The Web Site Files Has Fixed: " + monitorTask.getMonitorPath() + temStr);
					qHeartBeats.offer(DataUtil.getTime()+"\tRepaire-Machine\tThe Web Site Files Has Fixed: " + monitorTask.getMonitorPath() + temStr+"\t"+monitorTask.getTaskName());
				}else{
					if(file.isDirectory()){
						checkWebSpilthFile(file);
					}
				}
			} catch (Exception e) {
				IOC.log.error(e.getMessage());
			}
		}
		return true;
	}
	
	public void setQHeartBeats(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
	}
	
	public void setQMonitor(Queue<String> qMonitor){
		this.qMonitor = qMonitor;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return status;
	}
	
	/**
	 * 还原文件
	 * @param dir
	 */
	private boolean restoreFile(FileIndex fileIndex){
		// 如果父路径不存在， 则创建
		File file = new File(monitorTask.getMonitorPath()+fileIndex.getPath());
		while(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
					
		// 恢复文件
		String bakname = this.bakPath + File.separator + fileIndex.getSha1().substring(0,2);
		bakname = bakname + File.separator + fileIndex.getSha1().substring(2);
		
		file = new File(bakname);
		if(!file.exists()){
			FileUtil.combineFile(bakname, DataUtil.removeDuplicate(fileIndex.getRarId().split(",")).length);
		}
		byte[] contentBytes = ZLibUtil.decompress(FileUtil.readByte(bakname));
		
		FileUtil.write(monitorTask.getMonitorPath()+fileIndex.getPath(), contentBytes,false);
		
		file.setExecutable(fileIndex.getExec()==1);
		file.setReadable(fileIndex.getRead()==1);
		file.setReadable(fileIndex.getRead()==1);
		return true;
	}
	

	/**
	 * 合成bak里的大文件
	 * @data 2017年5月4日
	 * @param dir
	 */
//	public void combineBakFile(File dir){
//		File[] files = dir.listFiles();
//		for(File file:files){
//			if(file.isDirectory()){
//				combineBakFile(file);
//			}else{
//				if(file.getName().length()>38){
//					FileUtil.combineFile(filename, num);
//				}
//			}
//		}
//	}
	
	/**
	 * 添加到压缩文件
	 * @data 2017年5月5日
	 * @param dir
	 */
	public void addRar(File dir,String foldname){
		if(monitorTask.getStatus()==0){
			return ;
		}
		File[] files = dir.listFiles();
		for(File file:files){
			if(file.isDirectory()){
				addRar(file,file.getName());
			}else{
				try {
			        if(file.length()<=maxSize){
			            if(size + file.length() >= maxSize){
			            	rarId += 1;
			                size = 0;
			            }                   
			            size += file.length();
			        	ZipUtil.addFileInZip(file.getAbsolutePath(),file.getParentFile().getName(), this.cachPath +File.separator + monitorTask.getTaskName()+"_"+String.valueOf(rarId)+".rar", monitorTask.getFlagName());
			        	List<FileIndex> fileIndexs = fileIndexDao.queryIndexBySHA1(foldname+file.getName().substring(0,38));
			        	for(FileIndex fileIndex:fileIndexs){
			        		
			        		if(fileIndex.getRarId()==null||fileIndex.getRarId().equals("")){
				        		fileIndex.setRarId(String.valueOf(rarId));
				        	}else{
				        		fileIndex.setRarId(fileIndex.getRarId()+","+String.valueOf(rarId));
				        	}
			        		fileIndexDao.updateIndex(fileIndex);
			        	}
			        	
			        }
			       
				} catch (Exception e) {
//	        		e.printStackTrace();
	        		IOC.log.error(e.getMessage());
				}
			}
		}
	}
}
