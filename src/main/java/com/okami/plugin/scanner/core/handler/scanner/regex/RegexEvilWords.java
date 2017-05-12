package com.okami.plugin.scanner.core.handler.scanner.regex;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.WebshellFeatures;
import com.okami.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wh1t3P1g
 * @since 2017/3/18
 */
@Component
@Scope("prototype")
public class RegexEvilWords {

    private List<FileContent> fileContents;

    private Map<String,String> features;

    public RegexEvilWords(){
        WebshellFeatures webshellFeatures=
                MonitorClientApplication.ctx.getBean(WebshellFeatures.class);
        this.features=webshellFeatures.load("Static");
    }

    public String calculate(String content){
        for (Map.Entry<String,String> entry:features.entrySet()){
            Pattern r = buildRegex(entry.getValue());
            Matcher m = r.matcher(content);
            if (m.find( )) {
                MonitorClientApplication.log.info("Found value: " + m.group());
                return "statistics:("+m.group()+")";
            }
        }
        return "false";
    }

    private Pattern buildRegex(String regex){
        return Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
    }

    public List<FileContent> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<FileContent> fileContents) {
        this.fileContents = fileContents;
    }
}
