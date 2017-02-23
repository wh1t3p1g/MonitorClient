package com.okami.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.bean.MonitorTaskBean;
import com.okami.util.ZLibUtils;
import com.okami.common.DataUtil;
import com.okami.common.fileHandle;

/**
 * 备份以及自检模块
 * @author orleven
 * @date 2016年12月31日
 */

public class BackupAndCheckThread extends Thread{
	
	private String cBakPath;
	private String cCashPath;
	private MonitorTaskBean taskBean;
//	
//	BackupAndCheckThread(MonitorTaskBean taskBean)
//	{
//		this.taskBean = taskBean;
//		
//		// 备份模式
//		if(taskBean.getBCMode() == 0){
//			File bakPath = new File(taskBean.getBakPath());
//			if(bakPath.exists()){
//				fileHandle.deleteAll(bakPath);
//			}
//			bakPath.mkdir();
//			
//			File cashPath = new File(taskBean.getCashPath());
//			if(cashPath.exists()){
//				fileHandle.deleteAll(cashPath);
//			}
//			cashPath.mkdir();
//		}
//		
//		cBakPath = taskBean.getBakPath();
//		cCashPath = taskBean.getCashPath();
//	}
//	
//	public void run(){
//		//备份
//		File dir = new File(taskBean.getMonitorPath());
//		backup(dir);
//		backupFlag(new File(taskBean.getBakPath()));
//		try {
//		// 追加flag文件
//			cBakPath = Paths.get(cBakPath).getParent().toString();
//			String flagStr = "Flie:"+taskBean.getProjectName()+":"+taskBean.getMonitorPath()+
//					":"+String.valueOf(dir.canRead())+
//					":"+String.valueOf(dir.canWrite())+
//					":"+String.valueOf(dir.canExecute());
//			FileWriter fw = new FileWriter(new File(cBakPath+File.separator+ taskBean.getFlagName()), true);
//			PrintWriter pw = new PrintWriter(fw);
//			pw.println(flagStr);
//			pw.flush();
//			fw.flush();
//			pw.close();
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// 压缩成zip
//		
//		
//		
//		
//		
//		if(taskBean.getRunMode()==0){
//			return;
//		}
//		// 切换至安全模式
//		
//
//		// 上传
//	}
//	
//	/**
//	 * 备份
//	 * @param dir
//	 */
//	public void backup(File dir){
//		if(taskBean.getRunMode()==0){
//			return;
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
//					flagStr = "Fold:"+MD5Str+":"+file[i].getName()+
//							":"+String.valueOf(file[i].canRead())+
//							":"+String.valueOf(file[i].canWrite())+
//							":"+String.valueOf(file[i].canExecute());
//					cBakPath = cBakPath+File.separator+MD5Str;
//	
//					tBakFile = new File(cBakPath);
//					tBakFile.mkdir();
//					
////					System.out.println(file[i].getAbsolutePath());
////					System.out.println(cBakPath+"\n");
//					
//					// 文件夹为空则创建flag文件
//					if(file[i].listFiles().length == 0){
//						tBakFile = new File(cBakPath+File.separator+taskBean.getFlagName());
//						tBakFile.createNewFile();
//					}
//					backup(file[i]);
//					cBakPath = tBakFile.getParent();
//				}else{
//					// 压缩文件
////					System.out.println(cBakPath);
//					MD5Str = DataUtil.getMd5ByFile(file[i]);
//					flagStr = "Flie:"+MD5Str+":"+file[i].getName()+
//							":"+String.valueOf(file[i].canRead())+
//							":"+String.valueOf(file[i].canWrite())+
//							":"+String.valueOf(file[i].canExecute());
//		            byte[] contentBytes = ZLibUtils.compress(Files.readAllBytes(Paths.get(file[i].toString())));          
//		            Files.write(Paths.get(cBakPath+File.separator+MD5Str), contentBytes,
//		                    StandardOpenOption.CREATE);
//				}
//				
//				// 追加flag文件
//				FileWriter fw = new FileWriter(new File(cBakPath+File.separator+ taskBean.getFlagName()), true);
//				PrintWriter pw = new PrintWriter(fw);
//				pw.println(flagStr);
//				pw.flush();
//				fw.flush();
//				pw.close();
//				fw.close();
//				}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * 复制一份一样的flag文件。
//	 * @param dir
//	 */
//	public void backupFlag(File dir){
//		try {
//			File[] file = dir.listFiles();
//			File tCashFile = null;
//			for(int i=0; i<file.length; i++){
//				if(file[i].isDirectory()){
//					cCashPath = cCashPath+File.separator+ file[i].getName();
//					tCashFile = new File(cCashPath);
//					tCashFile.mkdir();
//					backupFlag(file[i]);
//					cCashPath = tCashFile.getParent();
//				}else if(file[i].isFile()&&file[i].getName().equals(taskBean.getFlagName())){
//					byte[] contentBytes = Files.readAllBytes(Paths.get(file[i].toString()));          
//		            Files.write(Paths.get(cCashPath+File.separator+file[i].getName()), contentBytes,
//		                    StandardOpenOption.CREATE);
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//					
//	}
//	
//    public static void main(String[] args) {  
////        String monitorPath = "C:\\Users\\dell\\Desktop\\测试文件";  
////        TaskBean taskBean = new TaskBean("aaaa");
////        taskBean.setMonitorPath(monitorPath);
////        taskBean.setFlagName("flag");
////        taskBean.setBakPath("C:\\Users\\dell\\Desktop\\毕设地址\\bak\\test");
////        taskBean.setCashPath("C:\\Users\\dell\\Desktop\\毕设地址\\cash\\test");
////        taskBean.setProjectName(dataHandle.getMD5(new Date().toString()));
////        taskBean.setBCMode(0);
////        BackupAndCheck backupAndCheck = new BackupAndCheck(taskBean);
////        backupAndCheck.run();
// 
//    }  

}

