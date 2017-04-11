package com.okami.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Component;

import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.dao.impl.FileIndexDao;
import com.okami.entities.FileIndex;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import com.okami.util.ZLibUtils;

/**
 * 还原线程
 * @author orleven
 * @date 2017年2月26日
 */
@Component
public class RepaireThread extends Thread{
	
	private String cachPath;
	private String bakPath;
	private Queue<String> qHeartBeats;
	private Queue<String> qRepaire;


	/**
	 * 初始化
	 */
	public boolean init()
	{		
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
		ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
		this.cachPath = configBean.getCachPath();
		this.bakPath = configBean.getBakPath();
		this.qHeartBeats = globaVariableBean.getQHeartBeats();
		this.qRepaire = globaVariableBean.getQRepaire();
		return true;
	}
	
	public void run(){
		while(true){

//			// 等待几秒，才能取出qRepaire的内容
//			try {
//				sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			
			if(!qRepaire.isEmpty()){
				String[] textLine = qRepaire.poll().split("\t");
				switch(textLine[0]){
	        	case "Restore":
	        		// 还原flag中有的文件
	        		if(restore(textLine[2],textLine[3])){
	        			qHeartBeats.add(DataUtil.getTime()+"\t"+textLine[1]+"\t"+textLine[3]+File.separator+textLine[2]+" have done !");
	        		}
	        		break;
	        	case "Remove":
//	        		// 用来删除flag中有的文件
	        		if(remove(textLine[2],textLine[3])){
	        			qHeartBeats.add(DataUtil.getTime()+"\t"+textLine[1]+"\t"+textLine[3]+File.separator+textLine[2]+" have done !");
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
		// 初始化
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
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
							}						
						}
						
						// 如果是文件
						else if(fileIndex.getType().equals("File")){
							// 如果父路径不存在， 则创建
							File file = new File(monitorTask.getMonitorPath()+fileIndex.getPath());
							while(!file.getParentFile().exists()){
								file.mkdirs();
							}
							
							// 恢复文件
							String bakname = this.bakPath + File.separator + monitorTask.getTaskName() + File.separator + fileIndex.getSha1().substring(0,2);
							bakname = bakname + File.separator + fileIndex.getSha1().substring(2);
							byte[] contentBytes;
							try {
								contentBytes = ZLibUtils.decompress(Files.readAllBytes(Paths.get(bakname)));
								Files.write(Paths.get(monitorTask.getMonitorPath()+fileIndex.getPath()), contentBytes,StandardOpenOption.CREATE);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
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
	public boolean remove(String indexPath,String taskName){
		// 初始化
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
		MonitorTask monitorTask;
		
		for(int i=0;i<globaVariableBean.getMonitorTaskList().size();i++){
			monitorTask = globaVariableBean.getMonitorTaskList().get(i);
			
			// 找到对应的任务，并进行恢复
			if(monitorTask.getTaskName().equals(taskName)){
				FileIndexDao fileIndexDao = globaVariableBean.getFileIndexDaoList().get(i);
				try {
					// 移除文件
					for(FileIndex fileIndex:fileIndexDao.queryIndexLikePath(indexPath)){
						// 如果是文件，则删除备份
						if(fileIndex.getType().equals("File")){
							String bakname = this.bakPath + File.separator + monitorTask.getTaskName() + File.separator + fileIndex.getSha1().substring(0,2);
							bakname = bakname + File.separator + fileIndex.getSha1().substring(2);
							FileUtil.deleteAll(new File(bakname));
						}
				
					}
					
					// 删除数据库中对应的行
					fileIndexDao.deleteIndexLikePath(indexPath);
					
					// 重新备份上传文件
					
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
		}		
		return true;
	}
}
