package com.sensetime.motionsdksamples.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

public class ProgressDialogUtil {
    private ProgressDialog mProgressDialog = null;

    public void show(Context context, String msg) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mProgressDialog.show();
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
