package com.okami;

import com.okami.plugin.ScannerApplication;
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
//		ConfigBean configBean = new ConfigBean();
//		configBean.setStoragePath("C:\\Users\\dell\\Desktop\\存储地址");
//		ControllerCenter controllerCenter = new ControllerCenter(configBean);
//		controllerCenter.work();

		ctx=SpringApplication.run(MonitorClientApplication.class, args);
		ScannerApplication scannerApplication= ctx.getBean(ScannerApplication.class);
		scannerApplication.run();
//		byte[] content= FileUtil.readByte("/Users/wh1t3p1g/Documents/Code/phpProject/DataCenter/vendor/topthink/think-captcha/src/Captcha.php");
//		System.out.println(new String(content));
		//run scanner plugin, just for test
		//MonitorClientApplication.ctx.getBean(Monitor.class);
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
		configurableEmbeddedServletContainer.setPort(61234);
	}

}
