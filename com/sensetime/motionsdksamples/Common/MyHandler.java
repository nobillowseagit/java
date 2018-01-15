package com.sensetime.motionsdksamples.Common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by lyt on 2017/11/28.
 */

public class MyHandler extends Handler {
    public MyHandler() {

    }

    @Override
    public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleMessage(msg);
        // 此处可以更新UI
        Bundle b = msg.getData();
        String color = b.getString("color");
    }
}
