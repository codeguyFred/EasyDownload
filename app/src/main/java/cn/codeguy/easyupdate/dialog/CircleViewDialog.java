package cn.codeguy.easyupdate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.codeguy.easyupdate.R;

/**
 * Created by fred
 * Date: 2018/9/26.
 * Time: 17:33
 * classDescription:
 */
public class CircleViewDialog extends Dialog {


    public CircleViewDialog(@NonNull Context context) {
        this(context, R.style.TipDialog);
    }

    public CircleViewDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
        initDialogWidth();
    }

    private void initDialogWidth() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wmLp = window.getAttributes();
            wmLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(wmLp);
        }
    }
}
