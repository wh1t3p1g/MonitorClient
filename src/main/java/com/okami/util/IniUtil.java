package com.okami.util;

import java.io.File;
import java.io.IOException;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import com.okami.bean.ConfigBean;
import com.okami.core.IOC;

/**
 * 配置文件工具包
 * @author orleven
 * @date 2017年4月19日
 */
public class IniUtil {

	/**
	 * 获取配置文件里的内容
	 * @data 2017年4月19日
	 * @param configBean
	 * @param iniPath
	 * @return
	 */
	public static ConfigBean getConfig(ConfigBean configBean,String iniPath){
        Wini ini;
		try {
			ini = new Wini(new File(iniPath));
			configBean.setRemoteMode(ini.get("Remote", "Remote Mode",boolean.class));
			configBean.setLhost(ini.get("Remote", "Local Host",String.class));
			configBean.setLport(ini.get("Remote", "Local Port",String.class));
			configBean.setRhost(ini.get("Remote", "Remote Host",String.class));
			configBean.setRport(ini.get("Remote", "Remote Port",String.class));
			configBean.setDelay(ini.get("Remote", "HeartBeat Delay",int.class));
			configBean.setStoragePath(ini.get("Storage", "Storage Path",String.class));
			return configBean;
		} catch (InvalidFileFormatException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}  

		//  如果取不到配置文件则，自动配置
        configBean = IOC.instance().getClassobj(ConfigBean.class);
        configBean.setStoragePath("C:\\Users\\dell\\Desktop\\MonitorC_Backup");
        configBean.setLhost("127.0.0.1");
        configBean.setRhost("192.168.199.183");
        configBean.setLport("61234");
        configBean.setRport("80");
        configBean.setDelay(60);
        configBean.setRemoteMode(true);
     	IniUtil.setConfig(configBean,System.getProperty("user.dir") + File.separator + "config/config.ini");
		return configBean;
	}
	
	/**
	 * 设置配置文件
	 * @data 2017年4月19日
	 * @param configBean
	 * @param iniPath
	 * @return
	 */
	public static boolean setConfig(ConfigBean configBean,String iniPath){
        Wini ini;
		try {
			File file = new File(iniPath);
			if(!file.exists()){
				file.createNewFile();
			}
			ini = new Wini(file);
			ini.setComment("Client configuration. \r\nTo avoid unnecessary coding problems, please use the English. ");
			ini.putComment("Remote", "Configured to connect to the server.");
			ini.add("Remote", "Remote Mode",configBean.getRemoteMode());
			ini.add("Remote", "Local Host",configBean.getLhost());
			ini.add("Remote", "Local Port",configBean.getLport());
			ini.add("Remote", "Remote Host",configBean.getRhost());
			ini.add("Remote", "Remote Port",configBean.getRport());
			ini.add("Remote", "HeartBeat Delay",configBean.getDelay());
			ini.putComment("Storage", "To avoid unnecessary coding problems, please use the English name of the directory. \r\nMainly used for backup files, log files and cache file storage");
			ini.add("Storage", "Storage Path",configBean.getStoragePath());


			ini.store();
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}  
		return true;
	}
	
	public static String getLport(String iniPath){
        Wini ini;
		try {
			ini = new Wini(new File(iniPath));
			String Lport = ini.get("Remote", "Local Port",String.class);
			if(Lport!=null){
				return Lport;
			}
		} catch (InvalidFileFormatException e) {
//			e.printStackTrace();
			return "61234";
		} catch (IOException e) {
//			e.printStackTrace();
			return "61234";
		}  
		return "61234";
	}
}
