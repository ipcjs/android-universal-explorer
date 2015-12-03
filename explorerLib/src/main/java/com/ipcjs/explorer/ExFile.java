package com.ipcjs.explorer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JiangSong on 2015/12/2.
 */
public class ExFile implements Explorable {
    private java.io.File mFile;
    private AsyncTask<File, Void, String> mCopyTask;

    public ExFile(java.io.File file) {
        mFile = file;
    }

    public ExFile(String path) {
        this(new File(path));
    }

    @Override
    public void onAction(final Context context, Object extra) {
        boolean isInternalFile = getPath().startsWith(context.getFilesDir().getParentFile().getAbsolutePath());
        if (isInternalFile) {
            File dir = new File(ExUtils.getDir(context, true, true), "file_explorer");
            if (!dir.exists() && !dir.mkdirs()) {
                ExUtils.error("创建目录失败:" + dir);
            }
            final File inFile = mFile;
            final File outFile = new File(dir, ExUtils.getRandomName(4) + "_" + getName());
            if (mCopyTask != null) {
                mCopyTask.cancel(true);
            }
            mCopyTask = new AsyncTask<File, Void, String>() {
                public static final String RESULT_OK = "ok!";

                @Override
                protected void onPreExecute() {
                    ExUtils.error("复制文件%s到%s", inFile.getName(), outFile.getPath());
                }

                @Override
                protected String doInBackground(File... params) {
                    try {
                        ExUtils.copy(inFile, outFile);
                        return RESULT_OK;
                    } catch (IOException e) {
                        return e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    ExUtils.error(result);
                    if (RESULT_OK.equals(result)) {
                        viewFile(context, outFile);
                    }
                }
            }.execute();
        } else {
            viewFile(context, mFile);
        }
    }

    private void viewFile(Context context, File file) {
        String type = null;
        int dotIndex = file.getName().indexOf('.');
        if (dotIndex != -1) {
            type = getMimeTypeMap().get(file.getName().substring(dotIndex + 1).toLowerCase());
        }
        if (type == null) {
            type = "*/*";
        }

        Uri data = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            context.startActivity(intent.setDataAndType(data, type));
        } catch (Exception e) {// 若没找到Activity, 会抛异常~~
            ExUtils.error(e, getClass().getSimpleName(), e.getMessage());
            context.startActivity(intent.setDataAndType(data, "*/*"));
        }
    }

    private static Map<String, String> sMimeTypeMap;

    private static Map<String, String> getMimeTypeMap() {
        if (sMimeTypeMap == null) {
            sMimeTypeMap = new HashMap<>();
            sMimeTypeMap.put("jpg", "image/*");
            sMimeTypeMap.put("png", "image/*");
            sMimeTypeMap.put("ogg", "audio/*");
            sMimeTypeMap.put("mp3", "audio/*");
            sMimeTypeMap.put("3gp", "video/*");
            sMimeTypeMap.put("mp4", "video/*");
            sMimeTypeMap.put("xml", "text/*");
            sMimeTypeMap.put("json", "text/*");
            sMimeTypeMap.put("html", "text/*");
            sMimeTypeMap.put("text", "text/*");
            sMimeTypeMap.put("apk", "application/vnd.android.package-archive");
            sMimeTypeMap.put("pdf", "application/pdf");
        }
        return sMimeTypeMap;
    }

    @Override
    public Explorable getParent() {
        File parentFile = mFile.getParentFile();
        if (parentFile == null) {
            return null;
        }
        return new ExFile(parentFile);
    }

    @Override
    public boolean isDir() {
        return mFile.isDirectory();
    }

    @Override
    public List<Explorable> getChildren() {
        final java.io.File[] files = mFile.listFiles();
        if (files == null) {
            return null;
        }
        List<Explorable> list = new ArrayList<>();
        for (java.io.File file : files) {
            list.add(file == null ? null : new ExFile(file));
        }
        return list;
    }

    @Override
    public String getPath() {
        return mFile.getAbsolutePath();
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public String toString() {
        return getPath();
    }
}
