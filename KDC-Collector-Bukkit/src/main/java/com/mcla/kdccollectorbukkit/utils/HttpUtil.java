package com.mcla.kdccollectorbukkit.utils;

import org.bukkit.util.Consumer;

import javax.swing.text.AbstractDocument;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @Description: http util
 * @ClassName: HttpUtil
 * @Author: ice_light
 * @Date: 2022/10/12 21:58
 * @Version: 1.0
 */
public class HttpUtil {
    /** The bytebin URL */
    private final String url;
    /** The client user agent */
    private final String userAgent;

    public HttpUtil(String url, String userAgent) {
        this.url = url + (url.endsWith("/") ? "" : "/");
        this.userAgent = userAgent;
    }

    private void postContent(String contentType, Consumer<OutputStream> consumer) throws IOException {
        URL url = new URL(this.url + "post");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("User-Agent", this.userAgent);
            connection.setRequestProperty("Content-Encoding", "gzip");

            connection.connect();
            try (OutputStream output = connection.getOutputStream()) {
                consumer.accept(output);
            }

            String key = connection.getHeaderField("Location");
            if (key == null) {
                throw new IllegalStateException("Key not returned");
            }
        } finally {
            connection.getInputStream().close();
            connection.disconnect();
        }
    }
}
