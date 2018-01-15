package com.sensetime.motionsdksamples.Uart;

import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;
import com.sensetime.motionsdksamples.Photography.Photo;

import java.io.File;

/**
 * Created by lyt on 2017/12/7.
 */

public class UartServer extends ServerThread {
    private static UartServer instance = new UartServer();
    public static UartServer getInstance() {
        return instance;
    }

    public ReceiveAndSendUtils mReceiveAndSendUtils;

    public UartServer() {
        super("uart");
    }




}
