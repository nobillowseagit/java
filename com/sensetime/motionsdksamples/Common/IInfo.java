package com.sensetime.motionsdksamples.Common;

import java.util.List;

/**
 * Created by lyt on 2017/10/24.
 */

public interface IInfo {
    public List<Person>getAllPerson();
    public void insertPerson(Person person);
    public void modifyPerson(Person person);
    public void onInfoRes(Person person);
}
