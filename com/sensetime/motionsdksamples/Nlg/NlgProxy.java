package com.sensetime.motionsdksamples.Nlg;

import com.sensetime.motionsdksamples.Common.Person;
import com.socks.library.KLog;

/**
 * Created by lyt on 2017/10/24.
 */

public class NlgProxy implements INlg {
    private INlg mINlg = null;

    public NlgProxy(INlg iNlg){
        this.mINlg = iNlg;
    }

    @Override
    public void onNlgResult(Person person) {
        KLog.trace();
    }
}
