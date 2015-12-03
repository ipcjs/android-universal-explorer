package com.ipcjs.explorer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by JiangSong on 2015/12/3.
 */
public class ExUtils {
    @NonNull
    public static Explorable newExplorable(String curPath) {
        return curPath.contains("/") ? new ExFile(curPath) : new ExClass(curPath);
    }

    public static void error(Object... objs) {
        error(null, objs);
    }

    public static void error(Throwable e, Object... objs) {
        String msg = Arrays.toString(objs);
        Log.e("explorer", msg);
        if (ExplorerFragment.getApplication() != null) {
            Toast.makeText(ExplorerFragment.getApplication(), msg, Toast.LENGTH_SHORT).show();
        }
        if (e != null) {
            e.printStackTrace();
        }
    }

    /**
     * @param isCache        是否使用缓存目录
     * @param preferExternal 是否优先使用外置存储
     * @return
     */
    public static File getDir(Context context, boolean isCache, boolean preferExternal) {
        File dir = null;
        if (preferExternal && hasExternalStorage(context)) {
            dir = isCache ? context.getExternalCacheDir() : context.getExternalFilesDir(null);
        }
        if (dir == null) {
            dir = isCache ? context.getCacheDir() : context.getFilesDir();
        }
        return dir;
    }

    public static boolean hasExternalStorage(Context context) {
        String state;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {// (sh)it happens
            e.printStackTrace();
            state = "";
        }
        int writePermission = context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return Environment.MEDIA_MOUNTED.equals(state) && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    public static String getRandomName(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) (Math.random() * ('Z' - 'A') + 'A'));
        }
        return sb.toString();
    }

    /**
     * 拷贝文件
     * @param inFile
     * @param outFile
     * @return
     */
    public static void copy(File inFile, File outFile) throws IOException {
        if (!inFile.exists()) {
            throw new IOException("inFile不存在：" + inFile.getAbsolutePath());
        }
        File outDir = outFile.getParentFile();
        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new IOException("创建outFile的父目录失败：" + outFile.getAbsolutePath());
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(inFile));
            bos = new BufferedOutputStream(new FileOutputStream(outFile));
            byte[] b = new byte[1024];
            int len;
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

    public static void forceShowOverflowMenu(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }
}
