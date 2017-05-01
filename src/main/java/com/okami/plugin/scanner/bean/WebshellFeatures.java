package com.okami.plugin.scanner.bean;

import com.okami.MonitorClientApplication;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/6
 */
@Component
public class WebshellFeatures {

    private Wini ini=null;

    public WebshellFeatures(){
        try {
            File file=new File("config/webshellFeatures.ini");
            if(file.exists()){
                ini=new Wini(file);
                MonitorClientApplication.log.info("load webshellFeatures.ini success");
            }else{
                MonitorClientApplication.log.error("load webshellFeatures.ini error");
            }
        } catch (IOException e) {
            e.printStackTrace();
            MonitorClientApplication.log.error("load webshellFeatures.ini error");
        }
    }

    public Map<String,String> load(String configName){
        if(ini==null)
            return null;
        return ini.get(configName);
    }
    
    /**
     * 添加一条配置
     * @data 2017年5月1日
     * @param configName
     * @return
     */
    public boolean add(String session,String name,String value){
		try {
			ini.add(session,name,value);
			ini.store();
		} catch (InvalidFileFormatException e) {
			return false;
		} catch (IOException e) {
			return false;
		}  
       return true;
    }
    
    /**
     * 添加一条配置
     * @data 2017年5月1日
     * @param configName
     * @return
     */
    public boolean remove(String session,String name,String value){
		try {
			ini.remove(name);
			ini.store();
		} catch (InvalidFileFormatException e) {
			return false;
		} catch (IOException e) {
			return false;
		}  
       return true;
    }
   

}
