package com.sensetime.motionsdksamples.Speech.SoundAI.sdk;

import android.util.Log;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class SoundAIAPIs {

    //public native void asr_data_callback(byte[] usr_data_asr,String buffer,int size);
    //public native void wakeup_callback(byte[] usr_data_wk, int wakeup_result, float angle);
    //public native void ivw_data_callback(String buffer,int size);
    //public native void voip_data_callback(byte[] usr_data_voip,String buffer,int size);
    //public native void oneshot_callback(int is_one_shot);
    //public native void wav_energy_callback(float val);
    public native int init_system(String info,Double threshold,String config_file_path,SoundAICallback asrDataCallback,SoundAICallback wakeupCallback,SoundAICallback ivwDataCallback,SoundAICallback voipDataCallback,SoundAICallback oneshotCallback,SoundAICallback wavEnergyCallback,byte[] usr_data_asr,byte[] usr_data_wk,byte[] usr_data_voip);
    public native int start_service();
    public native int stop_service();
    public native void terminate_system();
    public native void set_unwakeup_status();
    public native void set_wakeup_status();
    public native void set_wakeup_prefix_suffix(int prefix, int suffix);
    public native void set_voip_flag(int flag);
    public native void set_voip_beam(int flag);

    public interface SoundAICallback
    {
        public void asr_data_callback(byte[] usr_data_asr,String buffer,int size);
        public void wakeup_callback(byte[] usr_data_wk, int wakeup_result, float angle);
        public void ivw_data_callback(String buffer,int size);
        public void voip_data_callback(byte[] usr_data_voip,String buffer,int size);
        public void oneshot_callback(int is_one_shot);
        public void wav_energy_callback(float val);
    }

    static{
        try {
            System.loadLibrary("libsai_preprocess");
        }catch (Exception e){
            Log.d("LoadLibraryError",e.getMessage());
        }
    }
}
