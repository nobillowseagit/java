package com.sensetime.motionsdksamples.Common;

import android.content.Context;

import com.socks.library.KLog;

import java.util.List;

//eventbus
/*
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
*/
/*
import com.socks.library.KLog;

import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.sensetime.motionsdksamples.Dialog.DialogContext;
import com.sensetime.motionsdksamples.Dialog.DialogMsg;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NEW;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_REGISTERED;
import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_INFO;
*/

/**
 * Created by lyt on 2017/10/16.
 */

public class InfoServer implements IInfo{
    private static InfoServer instance = new InfoServer();
    public static InfoServer getInstance() {
        return instance;
    }

    //private ReadWriteLock mLock = new ReentrantReadWriteLock();
    private PersonDb mPersonDb;
    public Person mCurrentPerson;
    //private CountDownLatch mLatch;
    //private volatile boolean handleFinish;
    //private BlockingQueue<String> queue;

    public InfoServer() {
        //super("person-server");
        mCurrentPerson = new Person();
        //CountDownLatch mLatch = new CountDownLatch(1);
    }

    public void init(Context context) {
        mPersonDb = new PersonDb(context);
        //test();
    }

    @Override
    public void insertPerson(Person person) {
        handleInsert(person);
    }

    @Override
    public void modifyPerson(Person person) {
        handleModify(person);
    }

    @Override
    public List<Person> getAllPerson() {
        return handleGetAll();
    }

    @Override
    public void onInfoRes(Person person) {

    }

    public synchronized Person getCurrentPerson() {
        return mCurrentPerson;
    }

    public synchronized void setCurrentPerson(Person person) {
        mCurrentPerson = person;
    }

    //eventbus
    /*
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MsgBase msgBase) {
        KLog.trace();
        if (msgBase.type != MSG_TYPE_INFO) {
            return;
        }
        InfoMsg msg = (InfoMsg)msgBase;
        switch (msg.mCmd) {
            case INFO_CMD_DETECTED:
                handleDetected(msg.mPerson);
                break;
            case INFO_CMD_DEL:
                break;
            case INFO_CMD_GETALL:
                handleGetAll();
                break;
            case INFO_CMD_NEW_PERSON:
                handleInsert(msg.mPerson);
                break;
            case INFO_CMD_KNOWN:
                handleKnown(msg.mPerson);
                break;
            case INFO_CMD_MODIFY:
                handleModify(msg.mPerson);
                break;
            case INFO_CMD_TEST:
                handleTest(msg.mPerson);
                break;
        }
    }
    */

    private List<Person> handleGetAll() {
        return  mPersonDb.getAllPersons();
    }

    private void handleDetected(Person person) {
    }

    private synchronized void handleDeleteAllPerson() {
        mPersonDb.deleteAllPerson();
    }

    private synchronized void handleInsert(Person person) {
        /*
        DialogContext context = new DialogContext();
        context.domain = new Domain(Domain.DOMAIN_TPYE.DOMAIN_USER);
        context.person = person;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NEW_PERSON;
        msg.context = context;

        EventBus.getDefault().post(msg);
        */

        mPersonDb.insertPerson(person);
    }

    private synchronized void handleModify(Person person) {
        mPersonDb.updatePersonByUid(person);
    }

    /*
    private synchronized void handleKnown(Person person) {
        KLog.trace();
        Person tmpPerson;
        tmpPerson = mPersonDb.getPersonByUid(person.mStrUid);
        if (tmpPerson.mRegistered == 1) {
            handleRegisterd(person);
            return;
        }
        handleUnregistered(person);
    }

    private synchronized void handleUnregistered(Person person) {
        DialogContext context = new DialogContext();
        context.domain = new Domain(Domain.DOMAIN_TPYE.DOMAIN_USER);
        context.person = person;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NEW;
        msg.context = context;

        EventBus.getDefault().post(msg);
    }

    private synchronized void handleRegisterd(Person person) {
        DialogContext context = new DialogContext();
        context.domain = new Domain(Domain.DOMAIN_TPYE.DOMAIN_USER);
        context.person = person;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_REGISTERED;
        msg.context = context;

        EventBus.getDefault().post(msg);
    }

    private synchronized Person handleTest(Person person) {
        person.mRegistered = 1;
        return person;
    }

    public void test() {
        //mPersonDb.test();
        //Person person = mPersonDb.getPersonByName("王一");
        List<Person> listPersons = mPersonDb.getAllPersons();
        Person person = mPersonDb.getPersonByUid("a1508426614055");
        Person person2 = mPersonDb.getPersonById(1);
        Person person3 = mPersonDb.getPersonByUid("2");
    }
    */

    public void deleteAllPerson() {
        handleDeleteAllPerson();
    }

    public void updatePerson(Person person) {
        KLog.i("lijia: update person " + person.mName);
        Person person1 = mPersonDb.getPersonByUid(person.mStrUid);
        if (null == person1) {
            insertPerson(person);
        } else {
            modifyPerson(person);
        }
    }


}
