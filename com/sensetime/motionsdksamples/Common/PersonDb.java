package com.sensetime.motionsdksamples.Common;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;

/**
 * Created by lyt on 2017/10/10.
 */

public class PersonDb {
    PersonDao mPersonDao;

    public PersonDb(Context context) {
        mPersonDao = new PersonDao(context);
        if (!mPersonDao.isDataExist()){
            mPersonDao.initTable();
        }
    }

    public Person getPersonByName(String name) {
        List<Person> listPerson = mPersonDao.getPersonByName(name);
        if (listPerson.isEmpty()) {
            return null;
        }
        return listPerson.get(0);
    }

    public Person getPersonByUid(String strUid) {
        List<Person> listPersons = mPersonDao.getPersonByUid(strUid);
        if (listPersons == null || listPersons.isEmpty()) {
            return null;
        }
        return listPersons.get(0);
    }

    public Person getPersonById(int id) {
        List<Person> listPersons = mPersonDao.getPersonById(id);
        if (listPersons == null || listPersons.isEmpty()) {
            return null;
        }
        return listPersons.get(0);
    }

    public List<Person> getAllPersons() {
        return mPersonDao.getAllPersons();
    }

    public long insertPerson(Person person) {
        ContentValues cv = packPerson(person);
        mPersonDao.insertPerson(cv);
        return person.mDid;
    }

    public long updatePersonByUid(Person person) {
        ContentValues cv = packPerson(person);
        mPersonDao.updatePersonByUid(cv);
        return person.mDid;
    }

    private ContentValues packPerson(Person person) {
        ContentValues cv = new ContentValues();
        if(person.mStrUid == null) {
            return null;
        }
        cv.put("Uid", person.mStrUid);

        if(person.mName != null) {
            cv.put("Name", person.mName);
        }

        if(person.mPinyin != null) {
            cv.put("Pinyin", person.mPinyin);
        }

        if(person.mGender != null) {
            cv.put("Gender", person.mGender);
        }

        if(person.mAge != 0) {
            cv.put("Age", person.mAge);
        }

        cv.put("Registered", person.mRegistered);

        if(person.mFeatureByte != null) {
            cv.put("Feature", person.mFeatureByte);
        }

        return cv;
    }

    public void deleteAllPerson() {
        mPersonDao.deleteAll();
    }
}
