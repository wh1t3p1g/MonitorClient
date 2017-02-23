package com.okami.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/15
 */
@Component
public class Utils {

    /**
     * Map trans to Json
     * @param params
     * @return String
     */
    public static String toJson(Map params){
        Gson gson=new Gson();
        return gson.toJson(params);
    }

    /**
     * Json string to HashMap<String,Object>
     * @param jsonStr
     * @return HashMap<String,Object>
     */
    public static HashMap<String,Object> fromJson(String jsonStr){
        Gson gson=new Gson();
        Type type=new TypeToken<HashMap<String,Object>>(){}.getType();
        return gson.fromJson(jsonStr,type);
    }
}
