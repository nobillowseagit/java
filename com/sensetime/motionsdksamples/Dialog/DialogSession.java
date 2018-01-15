package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.Person;
import com.sensetime.motionsdksamples.Utils.UniqueId;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by lyt on 2017/11/2.
 */

public class DialogSession extends Handler {
    public long id;
    public Domain currentDomain = null;
    public Person currentPerson = null;
    public Domain domain = null;
    public Person person = null;
    public DialogTransaction transaction = null;

    public DialogSession() {
        id = UniqueId.getUid();
        domain = new Domain();
        person = new Person();
    }

    @Override
    public void publish(LogRecord record) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    public boolean update(Person person, Domain domain) {
        boolean ret = false;
        if (!currentDomain.compare(domain)) {
            currentDomain = domain;
            ret = true;
        }

        if (!currentPerson.compare(person)) {
            currentPerson = person;
            ret = true;
        }

        return ret;
    }

    public boolean update(Person person) {
        boolean ret = false;

        if (!currentPerson.compare(person)) {
            currentPerson = person;
            ret = true;
        }

        return ret;
    }

    public boolean update(Domain domain) {
        boolean ret = false;

        if (!currentDomain.compare(domain)) {
            currentDomain = domain;
            ret = true;
        }

        return ret;
    }

    public boolean checkChange(Person person) {
        boolean ret = false;

        if (!currentPerson.compare(person)) {
            ret = true;
        }

        return ret;
    }

    public boolean checkChange(Domain domain) {
        boolean ret = false;

        if (!currentDomain.compare(domain)) {
            ret = true;
        }

        return ret;
    }

    public boolean checkChange(Person person, Domain domain) {
        boolean ret = false;

        if (!currentDomain.compare(domain)) {
            ret = true;
        }

        if (!currentPerson.compare(person)) {
            ret = true;
        }

        return ret;
    }

    public Person getCurrentPerson() {
        return currentPerson;
    }

    public Domain getCurrentDomain() {
        return currentDomain;
    }

    public void runRequest(DialogContext context) {
        transaction.request(context);
    }

    public void runRespond(DialogContext context) {
        transaction.respond(context);
    }

    public boolean compare(DialogSession session) {
        if (!this.domain.compare(session.domain)) {
            return false;
        }

        if (!this.person.compare(session.person)) {
            return false;
        }

        return true;
    }

}
