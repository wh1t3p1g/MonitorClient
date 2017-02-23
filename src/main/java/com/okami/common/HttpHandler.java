package com.okami.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * http request
 * @author wh1t3P1g
 * @since 2017/1/15
 */
@Component
@Scope("prototype")
public class HttpHandler {

    private Map<String,String> headers;

    private HttpClient httpClient;

    private HttpGet httpGet;

    private HttpPost httpPost;

    private int timeout;

    public HttpHandler(){
//        RequestConfig config = RequestConfig.custom()
//                                    .setConnectTimeout(timeout * 1000)
//                                    .setConnectionRequestTimeout(timeout * 1000)
//                                    .setSocketTimeout(timeout * 1000).build();
//        this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        this.httpClient = HttpClientBuilder.create().build();

    }

    /**
     * get
     * @param url
     * @return
     */
    public HttpResponse Get(String url){
        httpGet=new HttpGet(url);
        addHeaders("get");
        try {
            HttpResponse httpResponse= this.httpClient.execute(httpGet);
            return httpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post data
     * @param url
     * @param data
     * @return
     */
    public HttpResponse Post(String url,String data){
        httpPost=new HttpPost(url);
        addHeaders("post");
        httpPost.setEntity(new StringEntity(data, "utf-8"));
        try {
            HttpResponse httpResponse=this.httpClient.execute(httpPost);
            return httpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get response content
     * @param httpResponse
     * @return
     */
    public String parserResponse(HttpResponse httpResponse){
        if(httpResponse==null)
            return null;
        try {
            String content= EntityUtils.toString(httpResponse.getEntity());
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addHeaders(String method){
        if(method.equals("get")) {
            for (String key : headers.keySet()) {
                httpGet.addHeader(key, headers.get(key));
            }
        }else{
            for (String key : headers.keySet()) {
                httpPost.addHeader(key, headers.get(key));
            }
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpGet getHttpGet() {
        return httpGet;
    }

    public void setHttpGet(HttpGet httpGet) {
        this.httpGet = httpGet;
    }

    public HttpPost getHttpPost() {
        return httpPost;
    }

    public void setHttpPost(HttpPost httpPost) {
        this.httpPost = httpPost;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
