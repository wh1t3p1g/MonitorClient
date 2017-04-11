package com.okami.common;

import com.okami.bean.ConfigBean;

/**
 * 命令行参数处理行数,暂时
 * @author orleven
 * @date 2017年2月7日
 */
public class ArgHandle {

	/**
	 * 帮助函数
	 */
    public void helper(){
        System.out.println("Usage:\n\tjava -jar xxx.jar -h [host] -p [port] {options -f \"uploadfile.txt\" -c \"ls -l\"}\n\tWindows use commandline need \"cmd /c\" \n");
    }
    
    /**
     * 命令行参数处理
     * @param args
     * @param configBean
     * @return
     */
    public ConfigBean usage(String[] args,ConfigBean configBean) {
    	int flag = 1;
    	
    	//无参数读取，读取配置文件
    	if(args.length==0){
    		
    	}
    	
    	// 有参数则按照参数处理 
    	else{
    		for (int i=0;i<args.length;i++) {
            	//帮助
            	if (args[i].compareToIgnoreCase("-h") ==  0 || args[i].compareToIgnoreCase("-help") ==  0) {
            		helper();
            		return null;
                }
            	
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
    	
        
        
        return configBean;
    }
    

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArgHandle argHandle = new ArgHandle();
		argHandle.helper();
	}

}
