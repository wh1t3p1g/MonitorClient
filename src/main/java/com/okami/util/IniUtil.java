package com.okami.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
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
	public static Map<String,String> getConfig(String iniPath){
		Map<String,String>  config = new HashMap<String, String>();
        Wini ini;
		try {
			
			ini = new Wini(new File(iniPath));
			config.put("lhost", ini.get("Remote", "Local Host",String.class));
			config.put("lport", ini.get("Remote", "Local Port",String.class));
			config.put("rhost", ini.get("Remote", "Remote Host",String.class));
			config.put("rport", ini.get("Remote", "Remote Port",String.class));
			config.put("delay", ini.get("Remote", "HeartBeat Delay",String.class));
			config.put("storagePath", ini.get("Storage", "Storage Path",String.class));
			config.put("monitorPathList", ini.get("Monitor", "Monitor Path List",String.class));
			if(config.get("monitorPathList")==null||config.get("monitorPathList").equals("")){
				 IOC.log.error("Please configure the configuration file (config.ini)");
				 System.exit(0);
			}
			return config;
		} catch (InvalidFileFormatException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}  

		//  如果取不到配置文件则，自动配置

		config.put("lhost", "127.0.0.1");
		config.put("lport", "61234");
		config.put("rhost", "192.168.199.183");
		config.put("rport", "80");
		config.put("delay", "60" );
		config.put("storagePath", System.getProperty("user.dir")+File.separator+"MonitorC_Backup");
		config.put("monitorPathList", "" );
     	IniUtil.setConfig(config,System.getProperty("user.dir") + File.separator + "config/config.ini");
		return config;
	}
	
	/**
	 * 设置配置文件
	 * @data 2017年4月19日
	 * @param configBean
	 * @param iniPath
	 * @return
	 */
	public static boolean setConfig(Map<String,String>  config,String iniPath){
        Wini ini;
		try {
			File file = new File(iniPath);
			if(!file.exists()){
				file.createNewFile();
			}
			ini = new Wini(file);
			ini.setComment("Client configuration. \r\nTo avoid unnecessary coding problems, please use the English. \r\n\r\n");
			ini.putComment("Remote", "Configured to connect to the server. \r\n");
			ini.add("Remote", "Local Host",config.get("lhost"));
			ini.add("Remote", "Local Port",config.get("lport"));
			ini.add("Remote", "Remote Host",config.get("rhost"));
			ini.add("Remote", "Remote Port",config.get("rport"));
			ini.add("Remote", "HeartBeat Delay",config.get("delay"));
			ini.putComment("Storage", "To avoid unnecessary coding problems, please use the English name of the directory. \r\nMainly used for backup files, log files and cache file storage.\r\n e.g. C:\\Users\\dell\\Desktop\\MonitorC_Backup");
			ini.add("Storage", "Storage Path",config.get("storagePath"));
			ini.putComment("Monitor", "To avoid unnecessary coding problems, please use the English name of the directory. \r\nThis configuration is sent to the server to set up the monitoring directory. \r\n e.g. C:\\Soft\\xampp\\htdocs,C:\\Users\\dell\\Desktop\\test \r\n");
			ini.add("Monitor", "Monitor Path List",config.get("monitorPathList"));	
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
			return "61234";
		} catch (IOException e) {
			return "61234";
		}  
		return "61234";
	}
	
	public static boolean setDelay(int delay,String iniPath){
        Wini ini;
		try {
			ini = new Wini(new File(iniPath));
			ini.add("Remote", "HeartBeat Delay",delay);
			ini.store();
		} catch (InvalidFileFormatException e) {
			return false;
		} catch (IOException e) {
			return false;
		}  
		return true;
	}
}
