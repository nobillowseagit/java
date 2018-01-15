package com.sensetime.motionsdksamples.Speech.SoundAI;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sensetime.motionsdksamples.Speech.SoundAI.sdk.SoundAIAPIs;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class SoundAIService extends Service implements SoundAIAPIs.SoundAICallback{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int id = 1234569;
        SoundAIAPIs soundAIAPIs = new SoundAIAPIs();
        soundAIAPIs.init_system("sai_mi2017_xm78675x2j90",0.45, "./sai_config",this,this,this,this,this,this,intToByteArray(id),intToByteArray(id),intToByteArray(id));
        return super.onStartCommand(intent, flags, startId);
    }

    private byte[] intToByteArray(final int integer) {
        int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
        byte[] byteArray = new byte[4];

        for (int n = 0; n < byteNum; n++)
            byteArray[3 - n] = (byte) (integer>>> (n * 8));
        return (byteArray);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return false;
    }

    @Override
    public void asr_data_callback(byte[] usr_data_asr, String buffer, int size) {

    }

    @Override
    public void wakeup_callback(byte[] usr_data_wk, int wakeup_result, float angle) {

    }

    @Override
    public void ivw_data_callback(String buffer, int size) {

    }

    @Override
    public void voip_data_callback(byte[] usr_data_voip, String buffer, int size) {

    }

    @Override
    public void oneshot_callback(int is_one_shot) {

    }

    @Override
    public void wav_energy_callback(float val) {

    }
}
