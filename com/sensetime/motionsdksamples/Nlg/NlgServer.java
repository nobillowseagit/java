package com.sensetime.motionsdksamples.Nlg;

import android.content.Context;
import android.os.Environment;

import com.example.https.utils.HTTPSUtils;
import com.sensetime.motionsdksamples.Dialog.DetectDialog;
import com.sensetime.motionsdksamples.Dialog.DialogContext;
import com.sensetime.motionsdksamples.Dialog.DialogManager;
import com.sensetime.motionsdksamples.Dialog.DialogMsg;
import com.sensetime.motionsdksamples.Dialog.DialogServer;
import com.sensetime.motionsdksamples.Dialog.Domain;
import com.sensetime.motionsdksamples.Dialog.General;
import com.sensetime.motionsdksamples.Dialog.Guess;
import com.sensetime.motionsdksamples.Dialog.Interactive;
import com.sensetime.motionsdksamples.Dialog.Introduce;
import com.sensetime.motionsdksamples.Dialog.Photography;
import com.sensetime.motionsdksamples.Dialog.UserConfig;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;
import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;
import com.sensetime.motionsdksamples.Common.Person;
import com.sensetime.motionsdksamples.UiMsg;
import com.sensetime.motionsdksamples.Utils.Config;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_RES;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_GENEREL;
import static com.sensetime.motionsdksamples.Dialog.UserConfig.USER_DOMAIN_SET_AGE;
import static com.sensetime.motionsdksamples.Dialog.UserConfig.USER_DOMAIN_SET_GENDER;
import static com.sensetime.motionsdksamples.Dialog.UserConfig.USER_DOMAIN_SET_NAME;
import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_NLG;

/**
 * Created by lyt on 2017/10/16.
 */

public class NlgServer extends ServerThread implements INlg {
    private static final String TAG = "aaa";
    private static NlgServer instance = new NlgServer();
    public static NlgServer getInstance() {
        return instance;
    }

    private Context mContext;
    private String mResString;
    private Config mConfig;
    private String mLock = new String();
    private DialogServer mDialogServer;
    private DialogManager mDialogManager;

    public NlgServer() {
        super("nlg-server");
        mDialogServer = DialogServer.getInstance();
        mDialogManager = DialogManager.getInstance();
    }

    public void init(Config config, Context context) {
        mConfig = config;
        mContext = context;
    }

    public void onNlgResult(Person person) {

    }

    @Override
    public void run() {
        KLog.trace();

        //执行耗时操作
        while (isRunning) {
            //count();
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MsgBase msgBase) {
        /* Do something */
        KLog.trace();

        if (msgBase.type != MSG_TYPE_NLG) {
            return;
        }

        NlgMsg msg = (NlgMsg) msgBase;
        DialogContext context = msg.context;
        switch (msg.cmd) {
            case NLG_CMD_REQ:
                handleReq(context);
                break;
        }
    }

    private String handleReq(DialogContext context) {
        int cnt = 0;
        Domain domain = context.getDomain();
        Person person = context.getPerson();

        String question = new String();

        if (domain != null) {
            question += "$d:";
            question += domain.toStringCN();
            question += ".";
        }

        if (person != null) {
            question += "$s:";
            if (person.mRegistered == 0) {
                question += "未注册;";
            } else {
                question += "已注册;";
            }

            if (person.mName != null) {
                question += person.mName;
                question += ";";
            }
            if (person.getGender() != null) {
                question += person.getGender();
                question += ";";
            }
            if (person.mAge != 0) {
                question += "年龄";
                question += String.valueOf(person.mAge);
                question += ";";
            }

            if (DOMAIN_GENEREL == domain.type) {
                if (person.getEmotion() != null) {
                    question += person.getEmotion();
                    question += ";";
                }
            }
            /*
            if (person.mStrUid != null) {
                question += ",";
                question += person.mStrUid;
            }
            */
            question += ".";
        }

        Random random = new Random();
        int pattern = random.nextInt(2);
        pattern = 0;
        question += "$p:样式";
        question += String.valueOf(pattern);
        question += ".";


        if (context.nlgReqStr != null) {
            question += "$q:";
            question += context.nlgReqStr;
            question += ".";
        }

        //getHttpsHtml(question);
        getHttpHtml(question);

        synchronized (mLock) {
            while (mResString == null && cnt < 5) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cnt++;
            }
        }

        if (mResString != null) {
            return mResString;
        }

        return null;
    }

    /*
    private void handleRes(String strRes) {
        DialogContext context = new DialogContext();
        context.nlgResStr = strRes;
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_RES;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }
    */

    private void handleRes2(String strRes) {
        String str1 = strRes, str2 = null, res_string = null;
        String action_string = null;
        String str3 = null;
        String action_cmd = null, action_param = null;
        int domain_index_start, domain_index_end;
        int action_index_start, action_index_end, action_sperator_index;
        int answer_index_start, answer_index_end;
        String domain_str;
        Domain domain = null;
        String domain_header = "$d:";
        String action_header = "$c:";
        String answer_header = "$a:";
        domain_index_start = str1.indexOf(domain_header);
        if (domain_index_start >= 0) {
            domain_index_end = str1.indexOf(".");
            if (domain_index_end >= 0) {
                domain_str = strRes.substring(domain_index_start + domain_header.length(), domain_index_end);
                switch (domain_str) {
                    case "通用":
                        domain = General.getInstance();
                        break;
                    case "用户":
                        domain = UserConfig.getInstance();
                        break;
                    case "全景拍照":
                        domain = Photography.getInstance();
                        break;
                    case "介绍":
                        domain = Introduce.getInstance();
                        break;
                    case "猜年龄":
                        domain = Guess.getInstance();
                        break;
                    case "交互":
                        domain = Interactive.getInstance();
                        break;
                    case "识别":
                        domain = DetectDialog.getInstance();
                        break;
                    default:
                }

                mDialogManager.getDomainFromString(domain_str);

                str2 = str1.substring(domain_index_end + 1);
            }
        }

        action_index_start = str2.indexOf(action_header);
        if (action_index_start >= 0) {
            action_index_end = str2.indexOf(".");
            if (action_index_end >= 0) {
                action_string = str2.substring(action_index_start + action_header.length(), action_index_end);
                action_sperator_index = action_string.indexOf("=");
                if (action_sperator_index >=0 ) {
                    action_cmd = action_string.substring(0, action_sperator_index);
                    action_param = action_string.substring(action_sperator_index + 1);
                } else {
                    action_cmd = action_string;
                }

                str3 = str2.substring(action_index_end + 1);
            }
        } else {
            str3 = str2;
        }

        answer_index_start = str3.indexOf(answer_header);
        if (answer_index_start >= 0) {
            answer_index_end = str3.indexOf(".");
            if (answer_index_end >= 0) {
                res_string = str3.substring(answer_index_start + answer_header.length(), answer_index_end);
            }
        }

        DialogContext context = new DialogContext();
        context.nlgResStr = res_string;
        context.nlgActCmdStr = action_cmd;
        context.nlgActParamStr = action_param;
        context.setDomain(domain);

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_RES;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    private void handleRes(String strRes) {
        int index1, index2;
        String targetStr;

        index1 = strRes.indexOf(':');
        if (index1 > 0) {
            String strDomain = strRes.substring(0, index1);
            Domain.DOMAIN_TPYE domainTpye = null;
            Domain domain = null;
            switch (strDomain) {
                case "通用":
                    domain = General.getInstance();
                    break;
                case "用户":
                    domain = UserConfig.getInstance();
                    break;
                case "全景拍照":
                    domain = Photography.getInstance();
                    break;
                case "介绍":
                    domain = Introduce.getInstance();
                    break;
                case "猜年龄":
                    domain = Guess.getInstance();
                    break;
                case "交互":

                default:
                    //domain = new Domain(domainTpye);
            }

            String s2 = strRes.substring(index1 + 1);
            index2 = s2.indexOf(':');
            if (index2 > 0) {
                String strDomain2 = s2.substring(0, index2);
                switch (strDomain2) {
                    case "设置姓名":
                        domain.subDomain = USER_DOMAIN_SET_NAME;
                        break;
                    case "设置性别":
                        domain.subDomain = USER_DOMAIN_SET_GENDER;
                        break;
                    case "设置年龄":
                        domain.subDomain = USER_DOMAIN_SET_AGE;
                        break;
                }
                targetStr = s2.substring(index2 + 1);
            } else {
                targetStr = strRes.substring(index1 + 1);
            }

            //DialogContext context = mDialogServer.getCurrentContext();
            DialogContext context = new DialogContext();
            context.nlgResStr = targetStr;
            context.setDomain(domain);

            DialogMsg msg = new DialogMsg();
            msg.cmd = DIALOG_CMD_NLG_RES;
            msg.context = context;
            EventBus.getDefault().post(msg);
        }
    }

    private void getHttpsHtml(String question) {
        mResString = null;
        String urlString;
        urlString = "https://" + mConfig.mServerIp + ":" + mConfig.mServerPort + "/get_data?question=" + question;
        KLog.i("lijia " + urlString);
        Request request = new Request.Builder()
                //.url("https://192.168.50.65:8888/get_data?question="+question)
                .url(urlString)
                .build();
        HTTPSUtils httpsUtils = new HTTPSUtils(mContext);

        httpsUtils.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("--------------onFailure--------------" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println("--------------onResponse--------------" + response.body().string());
                mResString = response.body().string();

                UiMsg uiMsg = new UiMsg();
                uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_THINKED;
                EventBus.getDefault().post(uiMsg);

                KLog.i("lijia " + mResString);
                if (null != mResString) {
                    handleRes2(mResString);
                }
            }
        });
    }

    private void getHttpHtml(String question) {
        mResString = null;
        String urlString;
        urlString = "http://" + mConfig.mServerIp + ":" + mConfig.mServerPort + "/get_data?question=" + question;
        KLog.i("lijia " + urlString);

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlString)
                .build();

        Call call = okHttpClient.newCall(request);

        /*
        try {
            Response response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                KLog.d("lijia");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResString = response.body().string();

                UiMsg uiMsg = new UiMsg();
                uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_THINKED;
                EventBus.getDefault().post(uiMsg);

                KLog.i("lijia " + mResString);
                if (null != mResString) {
                    handleRes2(mResString);
                }
            }
        });
    }

    public void upImage() {
        File file = new File(Environment.getExternalStorageDirectory()+"/lijia1/lijia.jpg");

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("img", "lijia.jpg",
                        RequestBody.create(MediaType.parse("image/png"), file));

        //RequestBody requestBody = builder.build();
        MultipartBody requestBody = builder.build();

        String strUrl = "https://" + mConfig.mServerIp + ":" + mConfig.mServerPort + "/upload";

        Request request = new Request.Builder()
                .url(strUrl)
                .post(requestBody)
                .build();

        HTTPSUtils httpsUtils = new HTTPSUtils(mContext);

        httpsUtils.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("--------------onFailure--------------" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println("--------------onResponse--------------" + response.body().string());
                String obj = response.body().string();
                obj.trim();
                KLog.v(obj);
            }
        });

    }
}
