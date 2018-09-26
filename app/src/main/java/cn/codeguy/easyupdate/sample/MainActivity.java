package cn.codeguy.easyupdate.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.codeguy.easyupdate.DKHandler;
import cn.codeguy.easyupdate.ProgressResponseBody;
import cn.codeguy.easyupdate.R;
import cn.codeguy.easyupdate.dialog.CommonDialog;
import cn.dankal.downloadlib.DownloadCallbackImpl;
import cn.dankal.downloadlib.InstallApk;
import cn.dankal.downloadlib.MyApp;
import okhttp3.Interceptor;

import static android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;
import static cn.dankal.downloadlib.DownloadHelper.getInstance;
import static cn.dankal.downloadlib.DownloadHelper.regularizePrice;

public class MainActivity extends AppCompatActivity {
    Button btDownload;
//    DKCircleView circleView;

    NotificationManager notificationManager;

    DKHandler handler = new DKHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Bundle data = msg.getData();
                    long totalBytesRead = data.getLong("totalBytesRead");
                    long contentLength = data.getLong("contentLength");


                    String s = regularizePrice(Double.valueOf(totalBytesRead) / Double.valueOf(contentLength) * 100);
                    String text = "下载中：" + s + "%";

                    builder1.setContentTitle(text);
                    builder1.setProgress(100, (int) (Double.valueOf(totalBytesRead) / Double.valueOf(contentLength) * 100), false);
                    sendNotification(builder1);


//                    ((TextView) findViewById(R.id.tv_progress)).setText(text);
//                    circleView.setValue(
//                            (int) (Double.valueOf(totalBytesRead) / Double.valueOf(contentLength) * 100));
//                    circleView.invalidate();
                    break;
                case 1:

                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intent.putExtra("file", msg.getData().getString("file"));
                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder1.setContentIntent(pendingIntent);
                    builder1.setProgress(100, 100, false);
                    builder1.setContentTitle("下载完成");


                    sendNotification(builder1);

                    Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }
    };

    private NotificationCompat.Builder builder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btDownload = findViewById(R.id.bt_download);
//        circleView = findViewById(R.id.circleView);
//        circleView.setClockwise(true);
//        circleView.setFill(true);


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        btDownload.setOnClickListener(v -> {

            CommonDialog.Builder builder = new CommonDialog.Builder(this);
            builder.setTitle("下载？")
                    .setPositiveButton("是", Color.parseColor("#D70D18"), type -> {

                        builder1 = new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("下载中")
//                              .setAutoCancel(true)
                                .setWhen(System.currentTimeMillis())
                                .setShowWhen(true).setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);
                        //等价于setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
                        //      .setDefaults(Notification.DEFAULT_ALL);

                        begin2Download();


                        sendNotification(builder1);
                        builder.getDialog().dismiss();
                    })
                    .setNegativeButton("否", Color.BLACK, type -> builder.getDialog().dismiss())
                    .create()
                    .show();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras()!=null) {
            InstallApk.startInstall(MyApp.getContext(), intent.getExtras().getString("file"));
        }
    }

    private void sendNotification(NotificationCompat.Builder builder1) {
        notificationManager.notify(1, builder1.build());

    }

    long currentTime;

    private void begin2Download() {
        DownloadCallbackImpl downloadCallback = new DownloadCallbackImpl() {
            @Override
            public void onStart() {

            }

            @Override
            public void onUpdate(long totalBytesRead, long contentLength) {
                if (System.currentTimeMillis() - currentTime > 500) {
                    currentTime = System.currentTimeMillis();
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putLong("totalBytesRead", totalBytesRead);
                    bundle.putLong("contentLength", contentLength);
                    msg.setData(bundle);
                    msg.what = 0;
                    handler.sendMessage(msg);
                }


                Log.e("tag", "totalBytesRead-->" + totalBytesRead + " contentLength-->" + contentLength);
            }

            @Override
            public void onDownloadComplete(String msg, String pathname) {

                Message msg1 = new Message();
                Bundle bundle1 = new Bundle();
                bundle1.putString("file", pathname);
                msg1.setData(bundle1);

                msg1.what = 1;
                handler.sendMessage(msg1);
                Log.e("tag", "下载完成");
            }

            @Override
            public void onDownloadError(String msg) {

            }

        };

        String url_apk = ((EditText) findViewById(R.id.et_apk)).getText().toString();
        String url_zip = ((EditText) findViewById(R.id.et_zip)).getText().toString();

        if (TextUtils.isEmpty(url_apk) && TextUtils.isEmpty(url_zip)) {
            Toast.makeText(this, "下载地址不为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(url_apk)) {

            getInstance()
                    .setDownloadCallback(downloadCallback)
                    .download(url_apk, "app.apk", "app", false);
        } else {

            getInstance()
                    .setDownloadCallback(downloadCallback)
                    .download(url_zip, "pack.zip", "zip", true);
        }

    }

    public static Interceptor DOWNLOAD_INTERCEPTOR = chain -> {
        okhttp3.Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder().body(
                new ProgressResponseBody(originalResponse.body(),
                        (totalBytesRead, contentLength, done) -> {
                            if (getInstance().getBuilder() != null) {
                                getInstance().getBuilder().onUpdate(totalBytesRead, contentLength);
                            }
                        }))
                .build();
    };

}
