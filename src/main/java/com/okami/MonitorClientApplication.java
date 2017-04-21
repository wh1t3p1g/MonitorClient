package com.okami;

import com.okami.plugin.ScannerApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.util.FileUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MonitorClientApplication extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {

	public static ApplicationContext  ctx;

	public static Log log = LogFactory.getLog(MonitorClientApplication.class);

	public static void main(String[] args) {
		ctx=SpringApplication.run(MonitorClientApplication.class, args);

		ScannerApplication scannerApplication=ctx.getBean(ScannerApplication.class);
//		BaseTask task=ctx.getBean(BaseTask.class);
//		task.setFilePath("/Users/wh1t3P1g/Desktop/webshell");
//		task.setScriptExtension("txt");
//		task.setType(1);
//		task.setExceptExtension(
//				"js,css,zip,rar,swf,ttf,dat,mp3,mp4,avi,mov,aiff," +
//				"mpeg,mpg,qt,ram,viv,flv,wav,map,svg,woff,woff2,eot,psd,mp3," +
//				"mp4,avi,mov,aiff,mpeg,mpg,qt,ram,viv,flv,wav");
//		task.setExceptPath(
//				"/Users/wh1t3P1g/Desktop/webshell/webshell/138shell/B");
//
//
//		scannerApplication.setTask(task);
//		scannerApplication.run();
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
		configurableEmbeddedServletContainer.setPort(61234);
	}

}
