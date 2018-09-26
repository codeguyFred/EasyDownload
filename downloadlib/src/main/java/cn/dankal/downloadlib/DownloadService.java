package cn.dankal.downloadlib;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by fred
 * Date: 2018/9/14.
 * Time: 10:10
 * classDescription:
 */
public interface DownloadService {
    @GET
    @Streaming
    Call<ResponseBody> download(@Url String url);

}
