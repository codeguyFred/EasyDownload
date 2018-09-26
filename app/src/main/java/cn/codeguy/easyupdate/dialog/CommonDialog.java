package cn.codeguy.easyupdate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.codeguy.easyupdate.DKCallBackBoolean;
import cn.codeguy.easyupdate.R;


/**
 * description: 重用的消息Dialog
 *
 * @author vane
 * @since 2018/8/8
 */

public class CommonDialog extends Dialog {

    public CommonDialog(@NonNull Context context) {
        this(context, R.style.NormalDialogStyle);
    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public static class Builder {

        private Context mContext;

        private int mCurrentIcon;

        private CharSequence mTitleText;
        private CharSequence mDesText;
        private CharSequence mSubtitleText;

        private CharSequence mNegativeButtonText;
        private CharSequence mPositiveButtonText;

        private int mNegativeButtonColor;
        private int mPositiveButtonColor;

        private DKCallBackBoolean mPositiveButtonListener;
        private DKCallBackBoolean mNegativeButtonListener;

        private CommonDialog mDialog;

        private boolean mCancelable=true;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置 icon 显示的内容
         * TODO 头部突出ICON
         */
        public Builder setIcon(@DrawableRes int icon) {
            mCurrentIcon = icon;
            return this;
        }

        /**
         * 设置显示的标题文案
         */
        public Builder setTitle(CharSequence titleWord) {
            mTitleText = titleWord;
            return this;
        }

        /**
         * 设置显示的描述文案(标题下面的描述文案)
         */
        public Builder setDes(CharSequence desWord) {
            mDesText = desWord;
            return this;
        }

        /**
         * 设置显示的副标题文案(不是标题下面的描述文案)
         */
        public Builder setSubtitle(CharSequence subtitleWord) {
            mSubtitleText = subtitleWord;
            return this;
        }


        public Builder setPositiveButton(CharSequence text, int textColor, DKCallBackBoolean DKCallBackBoolean) {
            mPositiveButtonText = text;
            mPositiveButtonListener = DKCallBackBoolean;
            mPositiveButtonColor = textColor;
            return this;
        }

        public Builder setPositiveButton(@StringRes int textId, int textColor, DKCallBackBoolean DKCallBackBoolean) {
            mPositiveButtonText = mContext.getText(textId);
            mPositiveButtonListener = DKCallBackBoolean;
            mPositiveButtonColor = textColor;
            return this;
        }


        public Builder setNegativeButton(CharSequence text, int textColor, DKCallBackBoolean DKCallBackBoolean) {
            mNegativeButtonText = text;
            mNegativeButtonListener = DKCallBackBoolean;
            mNegativeButtonColor = textColor;
            return this;
        }


        public boolean ismCancelable() {
            return mCancelable;
        }

        public void setmCancelable(boolean mCancelable) {
            this.mCancelable = mCancelable;
        }

        public Builder setNegativeButton(@StringRes int textId, int textColor, DKCallBackBoolean DKCallBackBoolean) {
            mNegativeButtonText = mContext.getText(textId);
            mNegativeButtonListener = DKCallBackBoolean;
            mNegativeButtonColor = textColor;
            return this;
        }

        public CommonDialog create() {
            return create(true);
        }

        public CommonDialog getDialog() {
            return mDialog;
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 {@link Dialog#show()} 方法
         *
         * @param cancelable 按系统返回键是否可以取消
         * @return 创建的 Dialog
         */
        public CommonDialog create(boolean cancelable) {
            CommonDialog dialog = new CommonDialog(mContext);
            dialog.setCancelable(cancelable);
            dialog.setContentView(R.layout.dialog_common);

            LinearLayout mTitleDesContainer = dialog.findViewById(R.id.title_des_container);
            TextView tvTitle = dialog.findViewById(R.id.tv_title);
            TextView tvDes = dialog.findViewById(R.id.tv_des);
            TextView tvSubtitle = dialog.findViewById(R.id.tv_subtitle);
            TextView btNegative = dialog.findViewById(R.id.bt_negative);
            TextView btPositive = dialog.findViewById(R.id.bt_positive);
            View veriDivider = dialog.findViewById(R.id.veri_divider);

            tvTitle.setText(mTitleText);
            tvDes.setText(mDesText);
            tvDes.setVisibility(TextUtils.isEmpty(mDesText) ? View.GONE : View.VISIBLE);
            tvSubtitle.setText(mSubtitleText);
            btNegative.setText(mNegativeButtonText);
            btPositive.setText(mPositiveButtonText);

            btNegative.setTextColor(mNegativeButtonColor);
            btPositive.setTextColor(mPositiveButtonColor);

            btNegative.setVisibility(TextUtils.isEmpty(mNegativeButtonText) ? View.GONE : View.VISIBLE);
            btPositive.setVisibility(TextUtils.isEmpty(mPositiveButtonText) ? View.GONE : View.VISIBLE);

            /*
            * 是否显示两个Button中间的分割线
            * 如果两个Button文案都不为空，则显示
            */
            boolean isShowDivider = !TextUtils.isEmpty(mNegativeButtonText) && !TextUtils.isEmpty(mPositiveButtonText);
            veriDivider.setVisibility(isShowDivider ? View.VISIBLE : View.GONE);

            /*
             * 是否显示SubTitle
             * 如果 mSubtitleText 不为空，则显示
             */
            boolean isShowSubtitle = !TextUtils.isEmpty(mSubtitleText);
            mTitleDesContainer.setVisibility(isShowSubtitle ? View.GONE : View.VISIBLE);
            tvSubtitle.setVisibility(isShowSubtitle ? View.VISIBLE : View.GONE);


            btNegative.setOnClickListener(view -> {
                if (mNegativeButtonListener != null)
                    mNegativeButtonListener.action(DKCallBackBoolean.NO);
            });
            btPositive.setOnClickListener(view -> {
                if (mPositiveButtonListener != null)
                    mPositiveButtonListener.action(DKCallBackBoolean.YES);
            });

            dialog.setCancelable(mCancelable);
            this.mDialog = dialog;
            return dialog;
        }

    }
}
