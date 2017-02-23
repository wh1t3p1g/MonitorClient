package com.okami.common;


import com.okami.bean.ConfigBean;
import com.okami.bean.MonitorTaskBean;

/**
 * 参数处理
 * @author orleven
 * @date 2017年1月2日
 */
public class ParameterHandle {
	/**
	 * 帮助函数
	 */
    public void helper(){
        System.out.println("Usage:\n\tjava -jar xxx.jar -h [host] -p [port] {options -f \"uploadfile.txt\" -c \"ls -l\"}\n\tWindows use commandline need \"cmd /c\" \n");
    }
    
    /**
     * 参数赋值
     * @param args
     * @param configBean
     */
    public void usage(String[] args,ConfigBean configBean) {
    	int flag = 1;
        for (int i=0;i<args.length;i++) {
            if (args[i].compareToIgnoreCase("-rhost") ==  0) {
                String rhost = args[++i];
                configBean.setRhost(rhost);
                flag *= 2;
            }
            if (args[i].compareToIgnoreCase("-rport") ==  0) {
                String rport = args[++i];
                configBean.setRport(rport);
                flag *= 3;
            }
            if (args[i].compareToIgnoreCase("-lhost") ==  0) {
                String lhost = args[++i];
                configBean.setLhost(lhost);
                flag *= 5;
            }
            if (args[i].compareToIgnoreCase("-lport") ==  0) {
                String lport = args[++i];
                configBean.setLhost(lport);
                flag *= 7;
            }
        }
        if(flag%(2*3*5*7)==0){
        	configBean.setRemoteMode(true);
        }
    }
    
    /**
     * 检查configBean里的参数是否正常
     * @param configBean
     */
    public boolean configBeanDeal(ConfigBean configBean){
    	return true;
    }
    
    /**
     * 检查taskBean里的参数是否正常
     * @param taskBean
     */
    public boolean taskBeanDeal(MonitorTaskBean taskBean){
    	return true;
    }
    
    public static MonitorTaskBean jsonStrTOTaskBean(String jsonStr,ConfigBean configBean){
//    	JSONArray messageJArray = new JSONArray(jsonStr);
//		JSONObject messageJObj = messageJArray.getJSONObject(0);
//    	String taskName = messageJObj.getString("taskName");
    	MonitorTaskBean taskBean = new MonitorTaskBean("改",configBean);
    	
    	
    	return taskBean;
    }
}
