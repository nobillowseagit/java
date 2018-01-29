package com.sensetime.motionsdksamples.Utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by lyt on 2017/10/9.
 */

public class Config {
    //private static final String CONFIG_FILE = "/";
    private Properties mProperties;
    public String mServerIp = "192.168.50.62";
    public String mServerPort = "8888";
    public String mCameraEnable = "enable";

    public void setServerIp(String ip) {
        mServerIp = ip;
    }

    public void setServerPort(String port) {
        mServerPort = port;
    }

    public void setCameraEnable(String flag) {
        mCameraEnable = flag;
    }

    /*
    public static Properties loadConfig(Context context, String fullPath) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(fullPath);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    public static boolean saveConfig(Context context, String file, Properties properties) {
        try {
            File fil = new File(file);
            if (!fil.exists())
                fil.createNewFile();
            FileOutputStream s = new FileOutputStream(fil);
            properties.store(s, "");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Config(Context context, String path) {
        String filename = path + "/config.prop";
        boolean b = false;
        String s = "";
        int i = 0;
        mProperties = loadConfig(context, filename);
        if (mProperties == null) {
            // 配置文件不存在的时候创建配置文件 初始化配置信息
            mProperties = new Properties();
            mProperties.put("ServerIp", "192.168.7.105");
            mProperties.put("ServerPort", "8888");
            mProperties.put("string", "aaaaaaaaaaaaaaaa");
            mProperties.put("int", "110");// 也可以添加基本类型数据 get时就需要强制转换成封装类型
            saveConfig(context, filename, mProperties);
        }
        mProperties.put("bool", "no");// put方法可以直接修改配置信息，不会重复添加
        //b = (((String) mProperties.get("bool")).equals("yes")) ? true : false;// get出来的都是Object对象
        // 如果是基本类型需要用到封装类
        //s = (String) mProperties.get("string");
        //i = Integer.parseInt((String) mProperties.get("int"));
        saveConfig(context, filename, mProperties);

        mServerIp = (String)mProperties.get("ServerIp");
        mServerPort = (String)mProperties.get("ServerPort");

    }
    */
}
