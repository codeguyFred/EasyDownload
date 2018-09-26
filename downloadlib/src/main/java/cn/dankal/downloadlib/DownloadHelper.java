package cn.dankal.downloadlib;

import android.util.Log;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * Created by fred
 * Date: 2018/9/14.
 * Time: 09:50
 * classDescription:
 * https://www.jianshu.com/p/92bb85fc07e8
 */
public class DownloadHelper {
    private static final DownloadHelper downloadHelper = new DownloadHelper();
    private Builder builder = new Builder();

    private  String pathname;
    private DownloadHelper() {
    }

    public static DownloadHelper getInstance() {
        return downloadHelper;
    }

    public  Builder setDownloadCallback(DownloadCallback downloadCallback) {
        builder.setDownloadCallback(downloadCallback);
        return builder;
    }

    public Builder getBuilder() {
        return builder;
    }

    public class Builder {
        private static final String ON_START = "onStart";
        private static final String ON_UPDATE = "onUpdate";
        private static final String ON_DOWNLOAD_COMPLETE = "onDownloadComplete";
        private static final String ON_DOWNLOAD_ERROR = "onDownloadError";
        private static final String ON_UNZIP_START = "unZipStart";
        private static final String ON_UNZIP_UPDATE = "unZipUpdate";
        private static final String ON_UNZIP_COMPLETE = "unZipComplete";
        private static final String ON_UNZIP_ERROR = "unZipError";

        private DownloadCallback downloadCallback;

        public void setDownloadCallback(DownloadCallback downloadCallback) {
            this.downloadCallback = downloadCallback;

        }


        public void download(final String url,final String name,
                             final String path,
                             final boolean uncompress) {

            action(ON_START);

            Callback<ResponseBody> callback = new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("Download", "server contacted and has file");
                        boolean writtenToDisk = false;
                        pathname = MyApp.FILES_DIR +path+ File.separator + name;
                        File dir = new File(MyApp.FILES_DIR + path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        if (uncompress||pathname.endsWith(Constants.TYPE_ZIP)) {

                            writtenToDisk = writeResponseBodyToDisk(response.body(), pathname);

                            //解压缩
                            if (writtenToDisk) {
                                action(ON_DOWNLOAD_COMPLETE);
                                File file = new File(pathname);
                                if (file.exists() && file.isFile()) {
                                    try {
                                        action(ON_UNZIP_START);
                                        unZip(file, file.getParent());
                                        unZipComplete(pathname);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        action(ON_UNZIP_ERROR);
                                    }
                                }
                            } else {
                                action(ON_DOWNLOAD_ERROR);

                            }
                        } else {
                            writtenToDisk = writeResponseBodyToDisk(response.body(), pathname);
                            if (writtenToDisk) {
                                action(ON_DOWNLOAD_COMPLETE);

//                                if (pathname.endsWith(Constants.TYPE_APK)){
//                                    InstallApk.startInstall(MyApp.getContext(),pathname);
//                                }
                            } else {
                                action(ON_DOWNLOAD_ERROR);
                            }
                        }

                        Log.e("Download", "file download was a success? " + writtenToDisk);
                    } else {
                        Log.e("Download", "server contact failed");
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("download", t.getMessage());
                    downloadCallback.onDownloadError(t.getMessage());
                }
            };

            DownloadApi.getInstance()
                    .download(url)
                    .enqueue(callback);


        }

        private void action(String action) {
            switch (action) {
                case ON_START:
                    downloadCallback.onStart();
                    break;
                case ON_DOWNLOAD_COMPLETE:
                    downloadCallback.onDownloadComplete("保存成功",pathname);
                    break;
                case ON_DOWNLOAD_ERROR:
                    downloadCallback.onDownloadError("保存失败");
                    break;

                case ON_UNZIP_START:
                    downloadCallback.unZipStart();

                case ON_UNZIP_ERROR:
                    downloadCallback.unZipError("解压失败");
                    break;
                default:

            }
        }

        private void unZipComplete(final String pathname) {
            downloadCallback.unZipComplete("已解压到：" + pathname);
        }



        public void onUpdate(long totalBytesRead, long contentLength) {
            downloadCallback.onUpdate(totalBytesRead,contentLength);
        }
    }

    public static final int BUFFER_SIZE = 1024;

    /**
     * 解压 zip 文件
     *
     * @param zipFile zip 压缩文件
     * @param destDir zip 压缩文件解压后保存的目录
     * @return 返回 zip 压缩文件里的文件名的 list
     * @throws Exception
     */
    public static List<String> unZip(File zipFile, String destDir) throws Exception {
        // 如果 destDir 为 null, 空字符串, 或者全是空格, 则解压到压缩文件所在目录
        if (StringUtils.isBlank(destDir)) {
            destDir = zipFile.getParent();
        }

        destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
        ZipArchiveInputStream is = null;
        List<String> fileNames = new ArrayList<String>();

        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            ZipArchiveEntry entry = null;

            while ((entry = is.getNextZipEntry()) != null) {
                fileNames.add(entry.getName());

                if (entry.isDirectory()) {
                    File directory = new File(destDir, entry.getName());
                    directory.mkdirs();
                } else {
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(destDir, entry.getName())), BUFFER_SIZE);
                        IOUtils.copy(is, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
            zipFile.delete();
        }

        return fileNames;
    }

    //https://www.jianshu.com/p/92bb85fc07e8
    private static boolean writeResponseBodyToDisk(ResponseBody body, String name) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(name);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    long finalFileSizeDownloaded = fileSizeDownloaded;
                    getInstance().getBuilder().onUpdate( finalFileSizeDownloaded,fileSize);

                    Log.d("Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    public static String regularizePrice(double price) {
        return String.format(Locale.CHINESE, "%.2f", price);
    }

}
