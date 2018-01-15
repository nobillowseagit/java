package com.sensetime.motionsdksamples.Common;

import com.socks.library.KLog;

import java.util.List;

/**
 * Created by lyt on 2017/10/24.
 */

public class InfoProxy implements IInfo {
    private IInfo mIInfo = null;

    public InfoProxy(IInfo iInfo){
        this.mIInfo = iInfo;
    }

    @Override
    public List<Person> getAllPerson() {
        return mIInfo.getAllPerson();
    }

    @Override
    public void insertPerson(Person person) {
        mIInfo.insertPerson(person);
    }

    @Override
    public void modifyPerson(Person person) {
        mIInfo.modifyPerson(person);
    }

    @Override
    public void onInfoRes(Person person) {
        KLog.trace();
    }
}
