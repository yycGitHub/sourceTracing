package com.surekam.common.utils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

/**
 * Client 数据接口客户端
 */
public class Client {
    // 日志处理
    private static Logger logger = LoggerFactory.getLogger(Client.class);
    
    private static HttpClient hc = new DefaultHttpClient(new PoolingClientConnectionManager());
   
        /**
         * @param args
         */
        public static void main(String[] args) { 
            String url = "http://localhost:80/gtcms/sd.action";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("OrigAppId", "0001"));
            String body = get(url, params,"http://localhost:80/gtcms/sd.action?");
            System.out.println("Body = "+body);
            System.exit(0);
        }

        /**
         * Get请求
         * @param url ---host
         * @param params
         * @return
         */
        public static String get(String url, List<NameValuePair> params,String address) {
            String body = null;
            //HttpClient hc = new DefaultHttpClient();
            // Get请求
            HttpGet httpget = null;
            try {
                httpget = new HttpGet(url);
                // 设置参数
                String str = EntityUtils.toString(new UrlEncodedFormEntity(params,"UTF-8"),"UTF-8");
                httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));
                // 发送请求
                HttpResponse httpresponse = hc.execute(httpget);
                // 判断是否正常返回  
                if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
                 // 获取返回数据
                    HttpEntity entity = httpresponse.getEntity();
                    body = EntityUtils.toString(entity,"UTF-8");
                    if (entity != null) {
                    	EntityUtils.consume(entity);
                    }
                    logger.info("获取到的对方信息 ： "+body);
                }else{
                    logger.info("接口地址："+address+" 返回状态码："+httpresponse.getStatusLine().getStatusCode()); 
                }   
            } catch (Exception e) {
                logger.error("接口地址："+address);
                body = null;
            }finally{
                try
                {
                    httpget.releaseConnection(); 
                }catch (Exception e){
                    logger.error("释放连接出现异常！");
                    body = null;
                }
                hc.getConnectionManager().closeExpiredConnections();  
            } 
            return body;
        }
        
        
        public static String post(String url, JSONObject jsonObject, String encoding) {
    		String body = "";
    		// 创建httpclient对象
    		CloseableHttpClient client = HttpClients.createDefault();
    		// 创建post方式请求对象
    		HttpPost httpPost = new HttpPost(url);

    		// 装填参数
    		StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
    		s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    		// 设置参数到请求对象中
    		httpPost.setEntity(s);
    		System.out.println("请求地址：" + url);
    		// System.out.println("请求参数："+nvps.toString());

    		// 设置header信息
    		httpPost.setHeader("Content-type", "application/json");
    		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

    		// 执行请求操作，并拿到结果（同步阻塞）
    		CloseableHttpResponse response = null;
    		try {
    			response = client.execute(httpPost);
    		} catch (ClientProtocolException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		} catch (IOException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}
    		// 获取结果实体
    		HttpEntity entity = response.getEntity();
    		if (entity != null) {
    			// 按指定编码转换结果实体为String类型
    			try {
    				body = EntityUtils.toString(entity, encoding);
    			} catch (ParseException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    		try {
    			EntityUtils.consume(entity);
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		// 释放链接
    		try {
    			response.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return body;
    	}

        /**
         * Post请求
         * @param url --- host
         * @param params
         * @return
         */
        public static String post(String url, List<NameValuePair> params,String address) {
            String body = null;
            //HttpClient hc = new DefaultHttpClient();
            // Post请求
            HttpPost httppost = null;
            try {
                httppost = new HttpPost(url);
                // 设置参数
                httppost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
                // 发送请求
                HttpResponse httpresponse = hc.execute(httppost);
                if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
                    // 获取返回数据
                       HttpEntity entity = httpresponse.getEntity();
                       body = EntityUtils.toString(entity,"UTF-8");
                       if (entity != null) {
                    	   EntityUtils.consume(entity);
                       }
                       logger.info("获取到的对方信息 ： "+body);
                   }else{
                       logger.info("接口地址："+address+" 返回状态码："+httpresponse.getStatusLine().getStatusCode()); 
                   } 
            } catch (Exception e) {
                logger.error("接口地址："+address);
                body = null;
            }finally{
                try
                {
                    httppost.releaseConnection();  
                }catch (Exception e){
                    logger.error("释放连接出现异常！");
                    body = null;
                }
                hc.getConnectionManager().closeExpiredConnections();  
            }
            return body;
        }

}
