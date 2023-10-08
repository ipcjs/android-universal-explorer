package com.github.ipcjs.explorer;

import static com.github.ipcjs.explorer.ExUtils.tError;
import static com.github.ipcjs.explorer.ExUtils.tInfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.github.ipcjs.explorer.compat.OpenFileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JiangSong on 2015/12/2.
 */
public class ExFile implements Explorer.Explorable {
    private java.io.File mFile;
    private AsyncTask<File, Void, String> mCopyTask;

    public ExFile(java.io.File file) {
        mFile = file;
    }

    public ExFile(String path) {
        this(new File(path));
    }

    private boolean isSubDirFor(File... parents) {
        for (File parent : parents) {
            if (parent != null && getPath().startsWith(parent.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAction(final Context context, Explorer.ExplorerContainer container) {
        boolean isInternalFile = isSubDirFor(
                context.getFilesDir().getParentFile(),
                context.getExternalFilesDir(null).getParentFile()
        );
        boolean isProvidedDirs = isSubDirFor(
                context.getFilesDir(),
                context.getCacheDir(),
                context.getExternalFilesDir(null),
                context.getExternalCacheDir()
        ) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isSubDirFor(context.getExternalMediaDirs()));


        // 除files和caches以外的内部文件, 没有被provider声明, 不能直接打开, 需要复制一份...
        if (isInternalFile && !isProvidedDirs) {
            File dir = new File(ExUtils.getDir(context, true, true), "file_explorer");
            if (!dir.exists() && !dir.mkdirs()) {
                tError("创建目录失败:" + dir);
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
                    tInfo(String.format("复制文件%s到%s", inFile.getName(), outFile.getPath()));
                }

                @Override
                protected String doInBackground(File... params) {
                    try {
                        ExUtils.copy(inFile, outFile);
                        return RESULT_OK;
                    } catch (IOException e) {
                        return e.toString();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    tInfo(result);
                    if (RESULT_OK.equals(result)) {
                        viewFile(context, outFile, true);
                    }
                }
            }.execute();
        } else {
            viewFile(context, mFile, isInternalFile);
        }
    }

    private void viewFile(Context context, File file, boolean useProvider) {
        String type = null;
        int dotIndex = file.getName().indexOf('.');
        if (dotIndex != -1) {
            type = getMimeTypeMap().get(file.getName().substring(dotIndex + 1).toLowerCase());
        }
        if (type == null) {
            type = "*/*";
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (useProvider) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + OpenFileProvider.AUTHORITY_SUFFIX, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        try {
            context.startActivity(intent.setDataAndType(uri, type));
        } catch (Exception e) {// 若没找到Activity, 会抛异常~~
            tError(e);
            context.startActivity(intent.setDataAndType(uri, "*/*"));
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
    public Explorer.Explorable getParent() {
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
    public List<Explorer.Explorable> getChildren(Explorer.ExplorerContainer container) {
        final java.io.File[] files = mFile.listFiles();
        if (files == null) {
            return null;
        }
        List<Explorer.Explorable> list = new ArrayList<>();
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
