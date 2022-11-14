package com.mcla.kdccollectorbukkit.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.Map;
import java.util.Objects;

public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private HttpUtil() {
    }

    //多线程共享实例
    private static CloseableHttpClient httpClient;

    static {
        SSLContext sslContext = createSSLContext();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        // 注册http套接字工厂和https套接字工厂
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslsf)
                .build();
        // 连接池管理器
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connMgr.setMaxTotal(300);//连接池最大连接数
        connMgr.setDefaultMaxPerRoute(300);//每个路由最大连接数，设置的过小，无法支持大并发
        connMgr.setValidateAfterInactivity(5 * 1000); //在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证
        // 请求参数配置管理器
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60000)
                .setSocketTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();
        // 获取httpClient客户端
        httpClient = HttpClients.custom()
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }


    /**
     * POST请求/有参数
     *
     * @param url
     * @param param
     * @return
     */
    public static String postJson(String url, String param) {
        return postJson(url, null, param);
    }


    /**
     * POST请求/有参数带头部
     *
     * @param url
     * @param header
     * @param params
     * @return
     */
    public static String postJson(String url, Map<String, String> header, String params) {
        return sendHttp(HttpMethod.POST, url, header, params);
    }

    /**
     * 发送http请求(通用方法)
     *
     * @param httpMethod 请求方式（GET、POST、PUT、DELETE）
     * @param url        请求路径
     * @param header     请求头
     * @param params     请求body（json数据）
     * @return 响应文本
     */
    public static String sendHttp(HttpMethod httpMethod, String url, Map<String, String> header, String params) {
        String infoMessage = "request sendHttp，url:" + url + "，method:" + httpMethod.name() + "，header:" + JSONObject.toJSONString(header) + "，param:" + params;
        log.info(infoMessage);
        //返回结果
        String result = null;
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            ContentType contentType = ContentType.APPLICATION_JSON.withCharset("UTF-8");
            HttpRequestBase request = buildHttpMethod(httpMethod, url);
            if (Objects.nonNull(header) && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    //打印头部信息
                    if (log.isDebugEnabled()) {
                        log.debug(entry.getKey() + ":" + entry.getValue());
                    }
                    request.setHeader(entry.getKey(), entry.getValue());
                }
            }
            if (StringUtils.isNotEmpty(params)) {
                if (HttpMethod.POST.equals(httpMethod)) {
                    ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(params, contentType));
                }
            }
            response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            log.info("sendHttp response status:{}", response.getStatusLine());
            if (Objects.nonNull(httpEntity)) {
                result = EntityUtils.toString(httpEntity, "UTF-8");
                log.info("sendHttp response body:{}", result);
            }
        } catch (Exception e) {
            log.error(infoMessage + " failure", e);
        } finally {
            HttpClientUtils.closeQuietly(response);//关闭返回对象
        }
        long endTime = System.currentTimeMillis();
        log.info("request sendHttp response time cost:" + (endTime - beginTime) + " ms");
        return result;
    }

    /**
     * 请求方法（全大写）
     */
    public enum HttpMethod {
        POST
    }

    /**
     * 构建请求方法
     *
     * @param method
     * @param url
     * @return
     */
    private static HttpRequestBase buildHttpMethod(HttpMethod method, String url) {
        if (HttpMethod.POST.equals(method)) {
            return new HttpPost(url);
        } else {
            return null;
        }
    }
    /**
     * 配置证书
     * @return
     */
    private static SSLContext createSSLContext(){
        try {
            //信任所有,支持导入ssl证书
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            return sslContext;
        } catch (Exception e) {
            log.error("初始化ssl配置失败", e);
            throw new RuntimeException("初始化ssl配置失败");
        }
    }
}