package cn.codeguy.easyupdate;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class DKHandler extends Handler {
    WeakReference<Context> mActivityReference;
    public DKHandler(Context context) {
        mActivityReference= new WeakReference<Context>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        final Context activity = mActivityReference.get();
        if (activity != null) {
            super.handleMessage(msg);
        }
    }
}