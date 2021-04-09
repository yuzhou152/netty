package com.zgg.common.util;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSONObject;

/**
 * 普通restful请求
 */
public class RestfulUtil {

    private static PoolingHttpClientConnectionManager connManager = null;
    private static CloseableHttpClient httpclient = null;

    static {
        connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(1000);
        connManager.setDefaultMaxPerRoute(1000);
        httpclient = HttpClients.custom().setConnectionManager(connManager).build();
    }

    public static String doPost(String url, Map<String, Object> map) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        if (map != null) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                json.put(key, map.get(key));
            }
        }

        post.setEntity(new StringEntity(json.toString(), "UTF-8"));

        CloseableHttpResponse response = httpclient.execute(post);

        try {
            HttpEntity entity = response.getEntity();
            String httpResult = EntityUtils.toString(entity, "UTF-8");
            return httpResult;
        } finally {
            response.close();
        }
    }

    public static String doPost(String url, String bodyJson) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(bodyJson, "UTF-8"));

        CloseableHttpResponse response = httpclient.execute(post);

        try {
            HttpEntity entity = response.getEntity();
            String httpResult = EntityUtils.toString(entity, "UTF-8");
            return httpResult;
        } finally {
            response.close();
        }
    }

    public static String doGet(String url, Map<String, Object> map) throws IOException {
        StringBuilder urlSB = new StringBuilder(url);
        if (map != null) {
            urlSB.append("?");

            for (Entry<String, Object> entry : map.entrySet()) {
                urlSB.append(entry.getKey()).append("=");
                urlSB.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                urlSB.append("&");
            }
            url = urlSB.substring(0, urlSB.length() - 1);
        }

        HttpGet httpgets = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpgets);
        try {
            HttpEntity entity = response.getEntity();
            String httpResult = EntityUtils.toString(entity, "UTF-8");
            return httpResult;
        } finally {
            response.close();
        }
    }


    public static String doPut(String url, Map<String, Object> map) throws IOException {
        HttpPut put = new HttpPut(url);
        put.setHeader("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        if (map != null) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                json.put(key, map.get(key));
            }
        }

        put.setEntity(new StringEntity(json.toString(), "UTF-8"));

        CloseableHttpResponse response = httpclient.execute(put);

        try {
            HttpEntity entity = response.getEntity();
            String httpResult = EntityUtils.toString(entity, "UTF-8");
            return httpResult;
        } finally {
            response.close();
        }
    }

    public static String doHttpsRequest(String requestUrl, JSONObject params, String requestMethodType, String bodyStr) {
        String result = null;
        try {
            if (params != null && params.entrySet().size() > 0) {
                Iterator iter = params.entrySet().iterator();
                StringBuffer sb = new StringBuffer("?");
                while (iter.hasNext()) {
                    Entry entry = (Entry) iter.next();
                    sb.append(entry.getKey().toString()).append("=").append(entry.getValue().toString());
                    if (iter.hasNext()) {
                        sb.append("&");
                    }
                }
                requestUrl += sb.toString();
            }
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            javax.net.ssl.SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-type", "application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethodType);
            // 当outputStr不为null时向输出流写数据
            if (null != bodyStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(bodyStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            result = buffer.toString();
        } catch (Exception e) {
            LoggerUtil.getInstance().api_error("http", null, null, e);
        }
        return result;
    }

    /**
     * 下载
     */
    private static InputStream downloadFiles(String url, Map<String, Object> map, long timeOut) throws IOException {
        StringBuilder urlSB = new StringBuilder(url);
        if (map != null) {
            urlSB.append("?");

            for (Entry<String, Object> entry : map.entrySet()) {
                urlSB.append(entry.getKey()).append("=");
                urlSB.append(URLEncoder.encode(entry.getValue().toString(),
                        "UTF-8"));
                urlSB.append("&");
            }
            url = urlSB.substring(0, urlSB.length() - 1);
        }
        HttpGet httpgets = new HttpGet(url);
        final CloseableHttpResponse response = httpclient.execute(httpgets);
        try {
            HttpEntity entity = response.getEntity();
            return entity.getContent();
        } finally {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LockSupport.parkNanos(3000000000L);
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 上传
     */
    public static String postFile(File file, String token, String url) {
        if (file == null || StringUtils.isEmpty(token))
            return null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
            mEntityBuilder.addBinaryBody("file", new FileInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getName());
            httpPost.addHeader("token", token);
            httpPost.setEntity(mEntityBuilder.build());
            response = httpclient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
                EntityUtils.consume(resEntity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            HttpClientUtils.closeQuietly(response);
        }
        return result;
    }

    static class MyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            //        return new X509Certificate[0];
            return null;
        }
    }
}
