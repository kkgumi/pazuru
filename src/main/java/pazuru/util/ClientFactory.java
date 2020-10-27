package pazuru.util;

import okhttp3.*;

import java.net.CookieManager;
import java.net.CookiePolicy;
import okhttp3.JavaNetCookieJar;
import java.util.concurrent.TimeUnit;

public class ClientFactory {

    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36";
    public static String ACCEPT_LANGUAGE = "zh-CN,zh-TW;q=0.9,zh;q=0.8,en-CA;q=0.7,en;q=0.6,ja;q=0.5";
    public static String ACCEPT_ENCODING = "gzip, deflate, br";

    public static OkHttpClient newClient() {
        return new OkHttpClient.Builder().connectTimeout(5000, TimeUnit.MILLISECONDS)
                .addInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .header("User-Agent", USER_AGENT)
                        .header("Accept-Language", ACCEPT_LANGUAGE)
                        .build()))
                .cookieJar(newClientCookieJar()).build();
    }

    public static CookieJar newClientCookieJar() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return new JavaNetCookieJar(cookieManager);
    }

}
