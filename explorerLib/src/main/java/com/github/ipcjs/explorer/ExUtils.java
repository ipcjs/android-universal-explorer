package com.github.ipcjs.explorer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

/**
 * Created by JiangSong on 2015/12/3.
 */
public class ExUtils {
    public static final String TAG = "explorer";
    public static final int STE_CALLER_CALLER_CALLER = 5;
    public static final int STE_CALLER_CALLER = 4;
    public static final int STE_CALLER = 3;
    public static final int STE_CURR = 2;
    private static final char[] PRIORITY_CHARS = {
            '0', '1',// 凑数的~~
            'V', 'D', 'I', 'W', 'E', 'A'// priority的缩写
    };
    private static Context sApplication;
    private static Pattern sCallerCallerNamePattern = Pattern.compile("[pw][A-Z].*");

    public static Context getApplication() {
        return sApplication;
    }

    /**
     * 初始化环境
     */
    public static void initEnvironment(Context context) {
        sApplication = context.getApplicationContext();
        forceShowOverflowMenu(context);
    }

    public static void forceShowOverflowMenu(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    public static String intent2Str(Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("打印Intent：");
        sb.append(intent);
        if (intent != null) {
//            sb.append("Action: " + intent.getAction());
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Set<String> keySet = bundle.keySet();
                for (String key : keySet) {
                    sb.append(", " + key + ": " + bundle.get(key));
                }
            }
            sb.append(", ").append("Component: " + intent.getComponent());
        }
        return sb.toString();
    }

    public static <F> String value2Str(Class<?> cls, F value, String prefix) {
        String result = "unknown_value";
        Field[] fields = cls.getFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            // 如果修饰符是 public static final
            if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)) {
                try {
                    Object object = field.get(null);
                    if (value.equals(object)) {
                        // "hehe".startsWith("") 返回true
                        if (field.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                            result = field.getName();
                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return result + "(" + value + ")";
    }

    /**
     * 提取{@link StackTraceElement}的方法信息
     * <br>形如:className.methodName(L:lineNumber)
     * @param ste
     * @return
     */
    private static String getSTEMethodMsg(StackTraceElement ste) {
        String callerClazzName = ste.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return String.format("%s.%s(L:%d)", callerClazzName, ste.getMethodName(), ste.getLineNumber());
    }

    @NonNull
    public static Explorer.Explorable newExplorable(String curPath) {
        return curPath.contains("/") ? new ExFile(curPath) : new ExClass(curPath);
    }

    /**
     * 打印
     * @param objs
     */
    public static void p(Object... objs) {
        print(TAG, Log.INFO, false, STE_CALLER_CALLER, objs);
    }

    /**
     * 写文件log
     * @param objs
     */
    public static void w(Object... objs) {
        print(TAG, Log.DEBUG, true, STE_CALLER_CALLER, objs);
    }

    /**
     * @param tag      作为log的TAG和打印的文件名, 若为空, 则依据{@link #STE_CALLER_CALLER}的方法名智能提取tag名
     *                 (例如: tagMethodName = "pTag", 则tag为"Tag")
     * @param priority log的等级, {@link Log#INFO},{@link Log#ERROR}等
     * @param toFile   是否输出到文件
     * @param steIndex 输出内容中类/方法信息使用该方法的哪层调用栈来获取? (eg: {@link #STE_CALLER_CALLER_CALLER})
     * @param objs     要输出的内容
     * @return 返回msg, 可以把返回的结果交给其他工具处理
     */
    public static String print(String tag, int priority, boolean toFile, int steIndex, Object... objs) {
        StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
        if (TextUtils.isEmpty(tag)) {// tag为空, 智能提取tag
            String tagMethodName = steArray[steIndex - 1].getMethodName();// 使用steIndex-1层调用栈的方法名作为tag
            // 例如: tagMethodName = "pTag", 则tag为"Tag"
            if (sCallerCallerNamePattern.matcher(tagMethodName).matches()) {// pTag
                tag = tagMethodName.substring(1);// Tag
            } else {
                tag = tagMethodName;
            }
        }
        String msg = String.format("%s: %s", getSTEMethodMsg(steArray[steIndex]), Arrays.deepToString(objs));
        Log.println(priority, tag, msg);
        if (toFile && getApplication() != null) {
            String time = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
            char priorityChar = PRIORITY_CHARS[priority];
            String fileMsg = String.format("%s %s-%s/%s\t%s", time, Process.myPid(), Process.myTid(), priorityChar, msg);
            str2File(fileMsg, new File(getDir(getApplication(), true, true), tag + ".txt"));
        }
        return msg;
    }

    /** 错误 */
    public static void tError(Object... objs) {
        tError(null, objs);
    }

    /** 错误 */
    public static void tError(Throwable e, Object... objs) {
        toast(Log.ERROR, e, objs);
    }

    /** 信息 */
    public static void tInfo(Object... objs) {
        toast(Log.INFO, null, objs);
    }

    public static void toast(int priority, Throwable e, Object... objs) {
        String msg = Arrays.deepToString(objs);
        if (e != null) {
            msg = e.toString() + ", " + msg;
            e.printStackTrace();
        }
        Log.println(priority, TAG, msg);
        if (getApplication() != null) {
            Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
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

    public static void str2File(String str, File file) {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return;
            }
        }
        BufferedWriter bw = null;
        try {
            OutputStream os = new FileOutputStream(file, true);
            bw = new BufferedWriter(new OutputStreamWriter(os));
            bw.write(str);
            bw.write('\n');
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <A extends Annotation> A newAnnotation(Class<A> annotation) {
        return (A) Proxy.newProxyInstance( // 使用动态代理创建一个实现了注解的类。。。
                annotation.getClassLoader(),
                new Class[]{annotation},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.getDefaultValue();// 返回注解方法的默认值
                    }
                }
        );
    }
}
