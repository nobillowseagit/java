package com.sensetime.motionsdksamples.Remote;

/**
 * Created by lyt on 2017/10/24.
 */

public class RemoteProxy implements IRemote {
    private IRemote mIRemote = null;

    public RemoteProxy(IRemote iRemote){
        this.mIRemote = iRemote;
    }
}
