package com.sensetime.motionsdksamples.Dialog;

import android.app.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyt on 2017/11/23.
 */

public class DialogManager {
    private static DialogManager instance = new DialogManager();
    public static DialogManager getInstance() {
        return instance;
    }

    public List<DialogBase> mDialogList;

    public DialogManager() {
        mDialogList = new ArrayList<>();
    }

    public void registerDialog(DialogBase dialog) {
        mDialogList.add(dialog);
    }

    public DialogBase checkNluResKeyWords(String nluResString) {
        DialogBase dialog = null;
        for (int i = 0; i < mDialogList.size(); i++) {
            dialog = mDialogList.get(i);
            for (int j = 0; j < dialog.mNluResKeyWords.size(); j++) {
                String keyWord = dialog.mNluResKeyWords.get(j);
                if (nluResString.contains(keyWord)) {
                    break;
                }
            }
        }
        return dialog;
    }

    public DialogBase checkNlgResKeyWords(String nluResString) {
        DialogBase dialog = null;
        for (int i = 0; i < mDialogList.size(); i++) {
            dialog = mDialogList.get(i);
            for (int j = 0; j < dialog.mNlgResKeyWords.size(); j++) {
                String keyWord = dialog.mNlgResKeyWords.get(j);
                if (nluResString.contains(keyWord)) {
                    break;
                }
            }
        }
        return dialog;
    }

    public Domain getDomainFromString(String domain_str) {
        int index = -1;
        Domain domain = null;
        for (int i = 0; i < mDialogList.size(); i++) {
            DialogBase dialog = mDialogList.get(i);
            if (domain_str.equals(dialog.toStringCN())) {
                index = i;
                domain = dialog;
                break;
            }
        }
        return domain;
    }
}
