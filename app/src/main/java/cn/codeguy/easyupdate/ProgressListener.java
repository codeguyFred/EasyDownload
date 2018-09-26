package cn.codeguy.easyupdate;

/**
 * Created by fred
 * Date: 2018/9/18.
 * Time: 14:40
 * classDescription:
 */
public interface ProgressListener {
    void onProgress(long totalBytesRead, long contentLength, boolean done);
}
