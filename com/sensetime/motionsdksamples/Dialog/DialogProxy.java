package com.sensetime.motionsdksamples.Dialog;

/**
 * Created by lyt on 2017/10/29.
 */

public class DialogProxy implements IDialog {
    private IDialog mIDialog = null;

    public DialogProxy(IDialog iDialog){
        this.mIDialog = iDialog;
    }
}
