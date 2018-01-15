package com.sensetime.motionsdksamples;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

//import com.sensetime.motion.LicenseLoader;
//import com.sensetime.motion.StMotionException;
import com.sensetime.motionsdksamples.Common.FaceAttrFragment;
import com.sensetime.motionsdksamples.Detection.Detection;
import com.sensetime.motionsdksamples.Dialog.DetectDialog;
import com.sensetime.motionsdksamples.Dialog.General;
import com.sensetime.motionsdksamples.Dialog.Hello;
import com.sensetime.motionsdksamples.Dialog.Interactive;
import com.sensetime.motionsdksamples.Dialog.Introduce;
import com.sensetime.motionsdksamples.Dialog.Photography;
import com.sensetime.motionsdksamples.Dialog.UserConfig;
import com.sensetime.motionsdksamples.Dialog.DialogServer;
import com.sensetime.motionsdksamples.Dialog.Guess;
import com.sensetime.motionsdksamples.Common.FaceServer;
import com.sensetime.motionsdksamples.Common.MotionServer;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;
import com.sensetime.motionsdksamples.Nlg.NlgServer;
import com.sensetime.motionsdksamples.Common.InfoServer;
import com.sensetime.motionsdksamples.Photography.Photo;
import com.sensetime.motionsdksamples.Remote.RemoteServer;
import com.sensetime.motionsdksamples.Sound.SoundServer;
import com.sensetime.motionsdksamples.Speech.SpeechServer;
import com.sensetime.motionsdksamples.Utils.BitmapUtil;
import com.sensetime.motionsdksamples.Utils.Config;
import com.sensetime.motionsdksamples.frameAnimation.FrameAnimationManager;
import com.sensetime.motionsdksamples.frameAnimation.FrameAnimationUtils;

import java.io.File;

import com.sensetime.motionsdksamples.Speech.AISpeech.service.AISpeechService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_UI;

public class MainActivity extends Activity implements BitmapUtil.PrepareLicenseAsyncTask.LicenseResultListener, View.OnClickListener {
//public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public Config mConfig;

    private Fragment f1, f2, f3, f4, f5;
    private FaceAttrFragment mFaceAttrFragment;
    private FragmentManager manager;
    //private FragmentTransaction transaction;
    private Context mApplicationContext;
    private Context mContext;
    private Context mBaseContext;

    private Guess mGuess;
    private Hello mHello;
    private UserConfig mUserConfig;
    private Introduce mIntroduce;
    private General mGenerel;
    private Photography mPhotograhy;

    private FaceServer mFaceServer;
    private DialogServer mDialogServer;
    private MotionServer mMotionServer;
    private NlgServer mNlgServer;
    private RemoteServer mRemoteServer;
    private InfoServer mInfoServer;
    private SpeechServer mSpeechServer;
    private SoundServer mSoundServer;
    private Interactive mInteractive;
    private DetectDialog mDetectDialog;

    private Photo mPhoto;
    private Detection mDetection;

    private FrameAnimationUtils frameAnimationUtils;
    private FrameAnimationManager mFrameAnimationManager;
    private Button mBtSpeak, mBtHead, mBtHit;
    private ImageView mIvImage;

    private UiFsm mUiFsm;
    private EditText mETInfo;
    private EditText mETRes;

    private static final String DB_NAME = "person.db";
    String mPath;
    String mDbFileName;
    String mDbPath;

    private AISpeechService aiSpeechService;
    private PowerManager.WakeLock mWakeLock;
    private PowerManager.WakeLock mLcdWakeLock;

    private void hideFragment(FragmentTransaction transaction) {
        if (f1 != null) {
            transaction.hide(f1); //隐藏方法也可以实现同样的效果，不过我一般使用去除
            //transaction.remove(f1);
        }
        if (f2 != null) {
            transaction.hide(f2);
            //transaction.remove(f2);
        }
        if (f3 != null) {
            transaction.hide(f3);
            //transaction.remove(f3);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mETInfo = (EditText)findViewById(R.id.etInfo);
        mETRes = (EditText)findViewById(R.id.etRes);

        mIvImage = findViewById(R.id.iv_image);
        /*
        frameAnimationUtils = new FrameAnimationUtils(mIvImage, ImageUtils.getImageRes(this, R.array.image_arm), 1000, true);
        mBtSpeak = findViewById(R.id.bt_speak);
        mBtHead = findViewById(R.id.bt_head);
        mBtHit = findViewById(R.id.bt_hit);
        mBtSpeak.setOnClickListener(this);
        mBtHead.setOnClickListener(this);
        mBtHit.setOnClickListener(this);
        */

        mBtSpeak = findViewById(R.id.bt_speak);
        mBtSpeak.setOnClickListener(this);

        mFrameAnimationManager = FrameAnimationManager.getInstance();
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.short_time_head, 1000, true);
        idle();

        manager = getFragmentManager();
        //transaction = manager.beginTransaction();

        // Out tracker can only detect target with 'UP'(like all-fingers-up in a palm gesture)
        // orientation, but a typical android frontal camera's preview image is 270 degrees clockwise
        // rotated from portrait(Nexus 5X and Nexus 6P are 90, and there may be some special
        // devices rotated 0 and 180, you can get it from CameraInfo.orientation).
        // So we make the UI landscape, and hope you will hold the device landscape.
        // in this case, when you show an UP orientation gesture to the camera,
        // the tracker will receive images with 'UP' orientation target.

        // set the screenOrientation to landscape in AndroidManifest.xml



        /*
        ListView mainListView = (ListView)findViewById(R.id.listview_main);
        final String[] items = new String[]{
                "Hand Tracking",
                "Fingertip Tracking",
                "Human Body Tracking",
                "Face Detect",
                "Face Attr"
        };
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        // Hand Tracking
                        //startActivity(new Intent(MainActivity.this, HandActivity.class));
                        FragmentTransaction transaction = manager.beginTransaction();
                        hideFragment(transaction);
                        f1 = new HandActivity();
                        transaction.replace(R.id.fl_content, f1);
                        transaction.commit();
                        break;
                    }
                    case 1: {
                        // Fingertip Tracking
                        //startActivity(new Intent(MainActivity.this, FingertipActivity.class));
                        FragmentTransaction transaction = manager.beginTransaction();
                        hideFragment(transaction);
                        f2 = new FingertipActivity();
                        transaction.replace(R.id.fl_content, f2);
                        transaction.commit();
                        break;
                    }
                    case 2: {
                        // Body Tracking
                        //startActivity(new Intent(MainActivity.this, BodyActivity.class));
                        FragmentTransaction transaction = manager.beginTransaction();
                        hideFragment(transaction);
                        f3 = new BodyActivity();
                        transaction.replace(R.id.fl_content, f3);
                        transaction.commit();
                        break;
                    }
                    case 3: {
                        // Face Tracking
                        //startActivity(new Intent(MainActivity.this, BodyActivity.class));
                        FragmentTransaction transaction = manager.beginTransaction();
                        hideFragment(transaction);
                        f4 = new FaceDetectActivity();
                        transaction.replace(R.id.fl_content, f4);
                        transaction.commit();
                        break;
                    }
                    case 4: {
                        // Face Attr Tracking
                        //startActivity(new Intent(MainActivity.this, BodyActivity.class));
                        FragmentTransaction transaction = manager.beginTransaction();
                        hideFragment(transaction);
                        f5 = new FaceAttrFragment();
                        transaction.replace(R.id.fl_content, f5);
                        transaction.commit();
                        break;
                    }
                    default:
                        // Nothing else
                }
            }
        });
        */

        // Request permissions at runtime on Android 6.0 or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Request all permissions at the very beginning
            requestAllPermissionsIfNeed();
        }

        BitmapUtil.PrepareLicenseAsyncTask task = new BitmapUtil.PrepareLicenseAsyncTask(this, this);
        task.execute();

        // FIXME You shall load the license yourself, before trying to touch other parts of the SDK.
        // Here is an example of loading license from assets/license.lic
        //PrepareLicenseAyncTask prepareLicenseAyncTask = new PrepareLicenseAyncTask(this);
        //prepareLicenseAyncTask.execute();

       //read config file
        initConfig();

        initPower();

        //init Database
        //PersonDb db;

        initEnv();
        //initDb();

        mUiFsm = new UiFsm();
        mUiFsm.start();

        //SpeechAuth auth = new SpeechAuth(mApplicationContext);
        //auth.runAuth();
        /*
        Context context = this;
        SpeechLicenseAyncTask speech = new SpeechLicenseAyncTask(this);
        speech.execute();
        */

        //String aaa = System.getProperty("java.library.path");
        //Intent it = new Intent(this, AuthActivity.class);
        //this.startActivity(it);

        //SpeechAuth auth = new SpeechAuth(mContext);
        //auth.runAuth();

        initService();
        //startServices();
        //MotionClient motionclient = new MotionClient("test-client");
        //motionclient.start();

        //mAnimationFragment = new FrameImageFragment();

        //start detect face
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        //f5 = new FaceAttrFragment();
        //transaction.replace(R.id.fl_content, f5);
        mFaceAttrFragment = new FaceAttrFragment();
        transaction.replace(R.id.fl_content, mFaceAttrFragment);
        transaction.commit();

        EventBus.getDefault().register(this);


        initImage();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestAllPermissionsIfNeed() {
        // Camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.CAMERA }, 0);
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Got it & Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        speak();
        mNlgServer.upImage();
    }

    class SpeechLicenseAyncTask extends AsyncTask<Void, Void, String> {
        private Context mContext;

        public SpeechLicenseAyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            //String errorMessage = prepareLicense();

            /*
            Intent it = new Intent(mContext, AuthActivity.class);
            mContext.startActivity(it);
            */
            return "aaa";
        }
    }

    /*
    class PrepareLicenseAyncTask extends AsyncTask<Void, Void, String> {
        private Context mContext = null;
        private ProgressDialog mProgressDialog = null;

        public PrepareLicenseAyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setMessage("Loading license...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String errorMessage = prepareLicense();
            return errorMessage;
        }

        @Override
        protected void onPostExecute(String errorMessage) {
            super.onPostExecute(errorMessage);
            if((mProgressDialog != null) && (mProgressDialog.isShowing())) {
                mProgressDialog.dismiss();
            }
            if (errorMessage != null) {
                showDialog("Fail to load license", errorMessage);
            }
        }

        private String prepareLicense() {
            String licensePath = null;
            String errorMessage = null;
            try {
                String licenseRepoPath = null;
                licensePath = LicenseUtils.copyLicenseFile(mContext, 2);
                licenseRepoPath = LicenseUtils.getLicenseFilePath(mContext);
                // Init licenseLoader (and load shared library)
                LicenseLoader licenseLoader = LicenseLoader.sharedLoader();
                //for load license from private license server
                //licenseLoader.loadPrivateLicense("10.0.1.255",8080, licenseRepoPath);
                //for load license from local file
                licenseLoader.loadLicenseFromData(licensePath, licenseRepoPath);
            } catch (StMotionException e) {
                Log.d("MainActivity", e.getMessage());
                errorMessage = e.getMessage();
            } catch (FileNotFoundException ex) {
                errorMessage = "No suitable License File ends with .lic in assets dir,please check";
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
                errorMessage = "Fail to init LicenseLoader with message:"+e.getMessage()+
                "\nYou may check source code of LicenseLoader";
            }
            return errorMessage;
        }
    }
    */

    @Override
    public void onLicenseInitFailed(String errorMessage) {
    }

    public void onLicenseInitSuccess() {
    };

    public EditText getETInfo() {
        return mETInfo;
    }

    public EditText getETRes() {
        return mETRes;
    }

    public FaceAttrFragment getFaceAttrFragment() {
        return mFaceAttrFragment;
    }

    public void initConfig() {
        String path = null;
        path = Environment.getExternalStorageDirectory().getPath() + File.separator + "lijia1";
        mConfig = new Config(this, path);
    }

    public Config getConfig() {
        return mConfig;
    }

    public Context getContext() {
        return mContext;
    }

    public void initEnv() {
        mPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "lijia1";
        mDbPath = mPath;
        mDbFileName = mPath + File.separator + DB_NAME;
        mApplicationContext = getApplicationContext();
        mContext = this;
        mBaseContext = getBaseContext();
    }

    public void startServices() {

        //init server
        mInfoServer = InfoServer.getInstance();
        mInfoServer.init(mBaseContext);

        mFaceServer = FaceServer.getInstance();

        mMotionServer = MotionServer.getInstance();
        mMotionServer.setMotorType(2);  //1: smartshow, 2:smartagent

        mDialogServer = DialogServer.getInstance();

        mNlgServer = NlgServer.getInstance();
        mNlgServer.init(mConfig, mContext);

        mRemoteServer = RemoteServer.getInstance();
        mRemoteServer.init(mConfig, mFaceAttrFragment);
        mRemoteServer.setMainActivity(this);

        mSpeechServer = SpeechServer.getInstance();
        mSpeechServer.init(mContext, mApplicationContext, aiSpeechService);


        //init dialog
        mHello = Hello.getInstance();

        mUserConfig = UserConfig.getInstance();

        mIntroduce = Introduce.getInstance();

        mGuess = Guess.getInstance();

        mGenerel = General.getInstance();

        mPhotograhy = Photography.getInstance();
        mInteractive = Interactive.getInstance();

        mDetectDialog = DetectDialog.getInstance();
        mDetectDialog.setContext(mContext);
        mDetectDialog.setConfig(mConfig);

        mPhoto = Photo.getInstance();
        mPhoto.init(mFaceAttrFragment);

        mDetection = Detection.getInstance();
        mDetection.setContext(mContext);
        mDetection.setConfig(mConfig);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aiSpeechService = ((AISpeechService.AISpeechBinder)service).getService();
            startServices();
        }
    };

    private void initService() {
        Intent intent = new Intent(this, AISpeechService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MsgBase msgBase) {
        if (msgBase.type != MSG_TYPE_UI) {
            return;
        }
        UiMsg msg = (UiMsg) msgBase;
        String string = msg.string;
        switch (msg.cmd) {
            case UI_CMD_STRING:
                mETRes.setText(string);
                break;
            case UI_CMD_EMOTION:
                switch (msg.emotion) {
                    case 1:
                        smile();
                        break;
                    case 2:
                        sleep();
                        break;
                    case 3:
                        speak();
                        break;
                    case 4:
                        idle();
                        break;
                }
                break;
        }
    }

    /*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MsgBase msgBase) {
        if (msgBase.type != MSG_TYPE_UI) {
            return;
        }
        UiMsg msg = (UiMsg)msgBase;
        String string = "empty";
        switch (msg.cmd) {
            case UI_CMD_WATCHED:
                //string = msg.string;
                mETInfo.setText(msg.string);
                KLog.debug(msg.string);
                break;
            case UI_CMD_SOUND_TRIGGER:
                mUiFsm.soundTrigger();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
            case UI_CMD_VISION_TRIGGER:
                mUiFsm.enterVisionTriggerSync();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
            case UI_CMD_LISTEN:
                mUiFsm.soundTrigger();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
            case UI_CMD_LISTENED:
                mUiFsm.voiceDetected();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
            case UI_CMD_THINKED:
                mUiFsm.thinkFeedback();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
            case UI_CMD_SPEAK_COMPLETED:
                mUiFsm.speakCompleted();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
            case UI_CMD_TIMEOUT:
                mUiFsm.runTimeout();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
            case UI_CMD_RUN_FAILED:
                mUiFsm.runFailed();
                string = mUiFsm.getString();
                KLog.debug(string);
                mETRes.setText(string);
                break;
        }
    }
    */

    public void speak() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.speak, 1000, true);
    }

    public void head() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.long_time_head, 1000, true);
    }

    public void hit() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.yawn, 1000, true);
    }

    public void touchFace() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.short_face, 1000, true);
    }

    public void touchChin() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.short_touch_of_the_chin, 1000, true);
    }

    public void pinchArm() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.pinch_the_arm, 1000, true);
    }

    public void kiss() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.kiss, 1000, true);
    }

    public void hug() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.ask_for_a_hug, 1000, true);
    }

    public void fall() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.fall, 1000, true);
    }

    public void smile() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.smile, 1000, true);
    }

    public void sleep() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.sleep, 1000, true);
    }

    public void idle() {
        mFrameAnimationManager.setCurrencyMethod(this, mIvImage, R.array.idle, 1000, true);
    }

    PowerManager mPowerManager;

    private void initPower() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "partial");
        mLcdWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "lcd");
        mWakeLock.acquire();
    }

    private void openLcd() {
        //mPowerManager.goToSleep(SystemClock.uptimeMillis());
        mLcdWakeLock.acquire();
    }

    private void closeLcd() {
        //mPowerManager.goToSleep(SystemClock.uptimeMillis());
        mLcdWakeLock.release();
    }

    private ImageView mFullImageView;
    private Bitmap mFullBitmap;
    float scaleWidth;
    float scaleHeight;
    int h;
    boolean num=false;

    private void initImage() {
        mFullImageView =(ImageView)findViewById(R.id.full_image);
    }

    private void prepareImage() {
        DisplayMetrics dm = new DisplayMetrics();//创建矩阵
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //mFullBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.xiaoyua);
        mFullBitmap = BitmapFactory.decodeFile("/sdcard/Download/res.jpg");

        int width = mFullBitmap.getWidth();
        int height = mFullBitmap.getHeight();
        int w = dm.widthPixels; //得到屏幕的宽度
        int h = dm.heightPixels; //得到屏幕的高度
        scaleWidth=((float)w)/width;
        scaleHeight=((float)h)/height;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()){

            case MotionEvent.ACTION_DOWN:  //当屏幕检测到第一个触点按下之后就会触发到这个事件。

                prepareImage();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth,scaleHeight);

                Bitmap newBitmap = Bitmap.createBitmap(mFullBitmap, 0, 0, mFullBitmap.getWidth(), mFullBitmap.getHeight(), matrix, true);
                mFullImageView.setImageBitmap(newBitmap);
                num = false;

                /*
                if(num == true)        {
                    prepareImage();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth,scaleHeight);

                    Bitmap newBitmap = Bitmap.createBitmap(mFullBitmap, 0, 0, mFullBitmap.getWidth(), mFullBitmap.getHeight(), matrix, true);
                    mFullImageView.setImageBitmap(newBitmap);
                    num = false;
                }
                else{
                    prepareImage();
                    Matrix matrix = new Matrix();
                    matrix.postScale(0.1f,0.1f);
                    Bitmap newBitmap = Bitmap.createBitmap(mFullBitmap, 0, 0, mFullBitmap.getWidth(), mFullBitmap.getHeight(), matrix, true);
                    mFullImageView.setImageBitmap(newBitmap);
                    num=true;
                }
                */
                break;
        }
        return super.onTouchEvent(event);
    }
}
