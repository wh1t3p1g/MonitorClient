package com.okami.plugin.scanner.bean;

import org.ini4j.Wini;
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
            ini=new Wini(new File("config/webshellFeatures.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,String> load(String configName){
        if(ini==null)
            return null;
        return ini.get(configName);
    }

}
