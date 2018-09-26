package cn.dankal.downloadlib;

/**
 * Created by fred
 * Date: 2018/9/14.
 * Time: 14:14
 * classDescription:
 */
public interface DownloadCallback<T> {
    void onStart();
    void onUpdate(long totalBytesRead, long contentLength);
    void onDownloadComplete(String msg,String pathname);
    void onDownloadError(String msg);
    void unZipStart();
    void unZipUpdate(String value);
    void unZipComplete(String msg);
    void unZipError(String msg);

    //自定义类型
    void onAction(T t);

}
