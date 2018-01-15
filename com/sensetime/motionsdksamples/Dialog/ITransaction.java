package com.sensetime.motionsdksamples.Dialog;

/**
 * Created by lyt on 2017/11/2.
 */

public interface ITransaction {
    public void request(DialogContext context);
    public void respond(DialogContext context);
}
