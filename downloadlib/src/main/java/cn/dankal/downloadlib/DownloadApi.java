package cn.dankal.downloadlib;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fred
 * Date: 2018/9/14.
 * Time: 10:11
 * classDescription:
 */
public class DownloadApi {

    // 连接超时
    private static final int CONNECT_TIMEOUT = 20;
    // 读超时
    private static final int READ_TIMEOUT = 20;
    // 写超时
    private static final int WRITE_TIMEOUT = 20;


    private DownloadService downloadService;

    /**
     * 获取单例
     *
     * @return 该类对象
     */
    public static DownloadApi getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public Call<ResponseBody> download(String url) {
        return downloadService.download(url);
    }


    private DownloadApi() {
        downloadService = new Retrofit.Builder()
                .baseUrl("https://three.inffur.com/")
                .addConverterFactory(GsonConverterFactory.create(buildGson()))
                .client(getOKhttpInstance())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .callbackExecutor(Executors.newFixedThreadPool(1))
                .build()
                .create(DownloadService.class);
    }

    private Gson buildGson() {
        return new GsonBuilder().create();
    }


    public static OkHttpClient getOKhttpInstance() {
        File cacheFile = new File(MyApp.getContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {

                    /**
                     * Verify that the host name is an acceptable match with
                     * the server's authentication scheme.
                     *
                     * @param hostname the host name
                     * @param session  SSLSession used on the connection to host
                     * @return true if the host name is acceptable
                     */
                    //忽略证书
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .cache(cache)
//              .addNetworkInterceptor(DOWNLOAD_INTERCEPTOR)
                .build();
    }

private static class SingletonHolder {
    private static final DownloadApi INSTANCE = new DownloadApi();
}

}
