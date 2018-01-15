package com.sensetime.motionsdksamples.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

public class LicenseUtils {
    /**
     * license file should copy from assets to local storage
     * 
     * @param context
     * @return
     * @throws IOException
     */
    public static String copyLicenseFile(Context context, int mask) throws IOException {
        String path = null;
        String licenseFileName = null;
        String[] fileList = context.getAssets().list("");
        for (int i = 0; i < fileList.length; i++) {
            if (mask == 1) {
                //face
                if (fileList[i].endsWith(".lic") && fileList[i].startsWith("FACESDK")) {
                    licenseFileName = fileList[i];
                    break;
                }
            } else if (mask == 2) {
                //motion
                if (fileList[i].endsWith(".lic") && fileList[i].startsWith("MOTIONSDK")) {
                    licenseFileName = fileList[i];
                    break;
                }
            }
        }
        if (licenseFileName == null) {
            throw new FileNotFoundException("No suitable License File ends with .lic in assets dir, please check");
        }

        //lijia
        //path = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + licenseFileName;
        path = Environment.getExternalStorageDirectory().getPath() + File.separator + "lijia1";
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        path = Environment.getExternalStorageDirectory().getPath() + File.separator + "lijia1" + File.separator + licenseFileName;

        InputStream in = null;
        OutputStream out = null;
        File licenseFile = new File(path);
        try {
            if (!licenseFile.exists()) {
                licenseFile.createNewFile();
                in = context.getApplicationContext().getAssets().open(licenseFileName);
                out = new FileOutputStream(licenseFile);
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                }
            }
        } catch (IOException e) {
            licenseFile.delete();
            path = null;
            throw e;
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return path;
    }

    public static String getLicenseFilePath(Context context) {
        String path = null;

        //lijia
        //path = context.getExternalFilesDir(null).getAbsolutePath();
        path = Environment.getExternalStorageDirectory().getPath() + File.separator + "lijia1";

        return path;
    }
}
