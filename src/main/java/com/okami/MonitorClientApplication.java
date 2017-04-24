package com.okami;
import com.okami.bean.ConfigBean;
import com.okami.common.PathTree;
import com.okami.core.ControlCenter;
import com.okami.core.IOC;
import com.okami.plugin.ScannerApplication;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import com.okami.util.IniUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class MonitorClientApplication extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {

	public static ApplicationContext  ctx;

	public static Log log ;
	
	public static void main(String[] args) {	
		IOC.ctx  = SpringApplication.run(MonitorClientApplication.class, args); 
		ctx =  IOC.ctx; 
		log = IOC.log; 
		
		ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
		controlCenter.init();
        controlCenter.audoLoad();
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8");
        configurableEmbeddedServletContainer.setMimeMappings(mappings );
		String Lport = IniUtil.getLport(System.getProperty("user.dir") + File.separator + "config/config.ini");
		configurableEmbeddedServletContainer.setPort(Integer.parseInt(Lport));
	}

}
