package com.okami.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream; 
import java.util.Iterator;
import java.util.Properties;

import javax.persistence.criteria.CriteriaBuilder.Case;

import com.okami.bean.ConfigBean; 
/**
 * 配置文件处理
 * @author orleven
 * @date 2017年2月7日
 */
public class PropertyHandle {
	
	/**
	 * 获取配置文件里的内容
	 * @return
	 */
	public ConfigBean getConf(){
		ConfigBean configBean = new ConfigBean();
		Properties prop = new Properties();     
        try{
        	InputStream in = new BufferedInputStream (new FileInputStream("default.properties"));
        	prop.load(in);     ///加载属性列表
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                switch(key){
                case "rhost":
                	configBean.setRhost(prop.getProperty(key));
                	break;
                case "lhost":
                	configBean.setLhost(prop.getProperty(key));
                	break;
                case "rport":
                	configBean.setRport(prop.getProperty(key));
                	break;
                case "lport":
                	configBean.setLhost(prop.getProperty(key));
                	break;
                }
                
                //  .....

            }
            in.close();
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
		return configBean;
	}
	
	/**
	 * 保存为配置文件
	 * @param configBean
	 * @return
	 */
	public boolean setConf(ConfigBean configBean){
		Properties prop = new Properties();     
        try{
        	FileOutputStream oFile = new FileOutputStream("default.properties");//true表示追加打开
            prop.setProperty("lhost", configBean.getLhost());
            prop.setProperty("rhost", configBean.getRhost());
            prop.setProperty("lport", configBean.getLport());
            prop.setProperty("rport", configBean.getRport());
            
            // ....
            prop.store(oFile, "The New properties file");
            oFile.close();
        }catch(Exception e){
            System.out.println(e);
        }
		return true;
	}
	
}
