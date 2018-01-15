package com.sensetime.motionsdksamples.Utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    /**
     * 拷贝模型到本地存储<br>
     * copy the model to internal storage
     * 
     * @param context
     *            应用上下文<br>
     *            application or activity context
     * @param modelName
     *            模型名称<br>
     *            the name of model
     * @throws IOException
     *             文件操作过程的IO异常<br>
     *             the IO exception of file operation
     */
    public static void copyModelFileToInternalStorage(Context context, String modelName) throws IOException {
        String path = getModelPath(context, modelName);
        InputStream in = null;
        OutputStream out = null;
        if (path != null) {
            File modelFile = new File(path);
            try {
                //if model file is existed, delete it
                if (modelFile.exists())
                    modelFile.delete();
                modelFile.createNewFile();
                in = context.getApplicationContext().getAssets().open(modelName);
                if (in == null) {
                    Log.e("FileUtils", "the model " + modelName + " is not existed");
                    return;
                }
                out = new FileOutputStream(modelFile);
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                }
            } catch (IOException e) {
                e.printStackTrace();
                modelFile.delete();
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    /**
     * 获取模型路径<br>
     * get the model path
     * 
     * @param context
     *            应用上下文<br>
     *            application or activity context
     * @param modelName
     *            模型名称<br>
     *            the name of model
     * @return 模型的绝对路径<br>
     *         absolute path of model
     */
    public static String getModelPath(Context context, String modelName) {
        String path = null;
        File dataDir = context.getFilesDir();
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + modelName;
        }
        return path;
    }
    
    /**
     * 在外部存储空间创建应用私有存储下的目录<br>
     * Create directory in application-specific directories on the primary
     * shared/external storage
     * 
     * @param context
     *            应用上下文 <br>
     *            application or activity context
     * @param dirname
     *            需要创建的目录名称<br>
     *            the name of directory you need to create
     * @return 新目录的完整路径<br>
     *         the whole path of the new directory
     */
    public static String makeFileDirs(Context context, String dirname) {
        String newPath = context.getExternalFilesDir(null).getAbsolutePath()+File.separator+dirname;
        File file = new File(newPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return newPath;
    }
}
