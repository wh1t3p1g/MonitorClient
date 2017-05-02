package com.okami.core;

import java.io.File;
import java.util.List;
import java.util.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.common.HttpHandler;
import com.okami.config.DBConfig;
import com.okami.dao.impl.FileIndexDao;
import com.okami.entities.FileIndex;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import com.okami.util.ZLibUtil;

/**
 * 还原线程
 * @author orleven
 * @date 2017年2月26日
 */
@Component
public class RepaireThread extends Thread{
	
//	private String cachPath;
	private String bakPath;
	private Queue<String> qHeartBeats;
	private Queue<String> qRepaire;
	private ConfigBean configBean;

	
	@Autowired
	private GlobaVariableBean globaVariableBean;

	/**
	 * 初始化
	 */
	public boolean init()
	{		
		this.configBean = IOC.instance().getClassobj(ConfigBean.class);
//		this.cachPath = configBean.getCachPath();
		this.bakPath = configBean.getBakPath();
		this.qHeartBeats = globaVariableBean.getQHeartBeats();
		this.qRepaire = globaVariableBean.getQRepaire();
		return true;
	}
	
	public void run(){

		
		while(true){	
			// 等待几秒，才能取出qRepaire的内容
			try {
				sleep(10);
			} catch (InterruptedException e) {
				IOC.log.error(e.getMessage());
			}
			
			
			if(!qRepaire.isEmpty()){ 
				String text = qRepaire.poll();
				String[] textLine = text.split("\t");
				switch(textLine[0]){
	        	case "Restore":
	        		// 还原flag中有的文件
	     
	        		if(restore(textLine[3],textLine[4])){
	        			qHeartBeats.offer(DataUtil.getTime()+"\t"+textLine[1]+"-Machine\t"+textLine[2]+textLine[3]+" Deal Success!"+"\t"+textLine[4]);
	        			IOC.log.warn(textLine[1]+ "-Machine: "+textLine[2]+textLine[3]+" Deal Success!");
	        		}else{
	        			qHeartBeats.offer(DataUtil.getTime()+"\t"+ textLine[1]+"-Machine\t"+textLine[2]+textLine[3]+" Deal Failed!"+"\t"+textLine[4]);
	        			IOC.log.warn(textLine[1]+ "-Machine: "+textLine[2]+textLine[3]+" Deal Failed!");
	        		}
	        		break;
	        	default:
	        		return;
	        	}
			}
		}	
	}
	
	public void setQHeartBeats(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
	}
	
//	public void setQMonitor(Queue<String> qMonitor){
//		this.qMonitor = qMonitor;
//	}
//	
	public void setQRepaire(Queue<String> qRepaire){
		this.qRepaire = qRepaire;
	}
	
//    public void setQqueue(Queue<String> qHeartBeats,Queue<String> qMonitor,Queue<String> qRepaire){
//        this.qHeartBeats = qHeartBeats;
//        this.qMonitor = qMonitor;
//        this.qRepaire = qRepaire;
//    }
	
	/**
	 * 还原文件
	 * @param dir
	 */
	public boolean restore(String indexPath,String taskName){
		MonitorTask monitorTask;
		
		for(int i=0;i<globaVariableBean.getMonitorTaskList().size();i++){
			monitorTask = globaVariableBean.getMonitorTaskList().get(i);
			
			// 找到对应的任务，并进行恢复
			if(monitorTask.getTaskName().equals(taskName)){
				FileIndexDao fileIndexDao = globaVariableBean.getFileIndexDaoList().get(i);
				try {

					for(FileIndex fileIndex:fileIndexDao.queryIndexLikePath(indexPath)){
						
						// 如果任务已经停止，则退出
						if(monitorTask.getRunMode()==0){
							return false;
						}
						
						// 如果是文件夹，则进行重建
						if(fileIndex.getType().equals("Fold")){
							File file = new File(monitorTask.getMonitorPath()+fileIndex.getPath());
							while(!file.exists()){
								file.mkdirs();
								file.setExecutable(fileIndex.getExec()==1);
								file.setWritable(fileIndex.getWrite()==1);
								file.setReadable(fileIndex.getRead()==1);
							}						
						}
						
						// 如果是文件
						else if(fileIndex.getType().equals("File")){
							// 如果父路径不存在， 则创建
							
							File file = new File(monitorTask.getMonitorPath()+fileIndex.getPath());
							while(!file.getParentFile().exists()){
								file.getParentFile().mkdirs();
							}
							
							// 恢复文件
							String bakname = this.bakPath + File.separator + monitorTask.getTaskName() + File.separator + fileIndex.getSha1().substring(0,2);
							bakname = bakname + File.separator + fileIndex.getSha1().substring(2);
							byte[] contentBytes = ZLibUtil.decompress(FileUtil.readByte(bakname));
							FileUtil.write(monitorTask.getMonitorPath()+fileIndex.getPath(), contentBytes);		
							file.setExecutable(fileIndex.getExec()==1);
							file.setReadable(fileIndex.getRead()==1);
							file.setReadable(fileIndex.getRead()==1);
						}
					}
					

				} catch (Exception e) {
					IOC.log.error(e.getMessage());
					return false;
				} 
				break;
			}
		}

		return true;
	}
	
	
	/**
	 * 用来删除flag中有的文件，例如webshell
	 * @param tarPath
	 * @return
	 */
	public boolean remove(String indexPath){
		MonitorTask monitorTask = null;
		try {
			for(MonitorTask mTask:globaVariableBean.getMonitorTaskDao().queryTask()){
				if(indexPath.indexOf(mTask.getMonitorPath())==0){
					monitorTask = mTask;

				}
			}
			File webFile = new File(indexPath);
			if(monitorTask==null){
				FileUtil.deleteAll(webFile);
			}else{
				monitorTask.setUpload(0);
				globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
				
				
				String bakPathStr = configBean.getBakPath()+File.separator+monitorTask.getFlagName();
				FileIndexDao fileIndexDao = new FileIndexDao();
				fileIndexDao.setDataSource(new DBConfig().indexDataSource(bakPathStr));
				fileIndexDao.connectDB();
				
				String flagIndexPath = indexPath.substring(monitorTask.getMonitorPath().length()); 
				
				// 移除文件
				for(FileIndex fileIndex:fileIndexDao.queryIndexLikePath(flagIndexPath)){
					// 如果是文件，则删除备份
					if(fileIndex.getType().equals("File")){
						String bakname = this.bakPath + File.separator + monitorTask.getTaskName() + File.separator + 
								fileIndex.getSha1().substring(0,2)+ File.separator + fileIndex.getSha1().substring(2);
						File file = new File(bakname);
						if(file.exists()){
							FileUtil.deleteAll(new File(bakname));
						}
						
					}
				}
				
				// 删除数据库中对应的行
				fileIndexDao.deleteIndexLikePath(flagIndexPath);
				
				
				// 删除网站源文件
				FileUtil.deleteAll(webFile);
				
				//  上传flag文件
				String result = null;
				HttpHandler httpHandler = IOC.instance().getClassobj(HttpHandler.class);
				File file = new File(this.bakPath + File.separator +monitorTask.getFlagName());
				result = httpHandler.upload(file);
				if(result==null || result.indexOf("success")<=0)
				{
					return false;
				}
				
				monitorTask.setUpload(1);
				globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
				
			}
		
		} catch (Exception e1) {
			e1.printStackTrace();
			IOC.log.error(e1.getMessage());
			return false;
		}
		
		
		return true;
	}
	
	/**
	 * 用来编辑flag中有的文件，例如webshell
	 * @param tarPath
	 * @return
	 */
	public boolean edit(String indexPath,String cachFileStr){
		MonitorTask monitorTask = null;
		File tarCachFile = new File(cachFileStr); 
		try {
			for(MonitorTask mTask:globaVariableBean.getMonitorTaskDao().queryTask()){
				if(indexPath.indexOf(mTask.getMonitorPath())==0){
					monitorTask = mTask;
				}
			}
			if(monitorTask==null){
				FileUtil.write(indexPath, FileUtil.readByte(cachFileStr));
				
			}else{
				monitorTask.setUpload(0);
				globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
				
				String bakPathStr = configBean.getBakPath()+File.separator+monitorTask.getFlagName();
				FileIndexDao fileIndexDao = new FileIndexDao();
				fileIndexDao.setDataSource(new DBConfig().indexDataSource(bakPathStr));
				fileIndexDao.connectDB();
				
				List<FileIndex> fileIndexs = fileIndexDao.queryIndexByPath(indexPath.substring(monitorTask.getMonitorPath().length()));
				if(fileIndexs.size()>=1){
					
					// 添加备份文件
					String tarSHA1 = DataUtil.getSHA1ByFile(tarCachFile);
					String srcSHA1 = fileIndexs.get(0).getSha1();
		        	byte[] contentBytes = ZLibUtil.compress(FileUtil.readByte(cachFileStr));
		        	String bakFold = configBean.getBakPath()+File.separator+monitorTask.getTaskName()+
							File.separator+tarSHA1.substring(0,2);
		        	File file = new File(bakFold);
		        	if(!file.exists())
		        	{
		        		file.mkdirs();
		        	}
		       
					FileUtil.write(bakFold+File.separator+tarSHA1.substring(2), contentBytes);
					
					// 更新数据库
					fileIndexs.get(0).setSha1(tarSHA1);
					fileIndexDao.updateIndex(fileIndexs.get(0));
					
					// 删除原始备份文件
					File srcBakFile = new File(configBean.getBakPath()+File.separator+monitorTask.getTaskName()+
							File.separator+srcSHA1.substring(0,2)+File.separator+srcSHA1.substring(2));
					if(srcBakFile.exists()){
						FileUtil.deleteAll(srcBakFile);
					}
					
					// 修改源文件
					contentBytes = FileUtil.readByte(cachFileStr);
					FileUtil.write(monitorTask.getMonitorPath()+fileIndexs.get(0).getPath(), contentBytes);
					
					//  上传flag文件
					String result = null;
					HttpHandler httpHandler = IOC.instance().getClassobj(HttpHandler.class);
					file = new File(this.bakPath + File.separator +monitorTask.getFlagName());
					result = httpHandler.upload(file);
					if(result==null || result.indexOf("success")<=0)
					{
						return false;
					}
					
					monitorTask.setUpload(1);
					globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
				}
			}
			if(tarCachFile.exists()){
				FileUtil.deleteAll(tarCachFile);
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			IOC.log.error(e1.getMessage());
			return false;
		}
		return true;
	}
}
