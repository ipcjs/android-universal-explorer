package com.ipcjs.explorer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by JiangSong on 2015/12/2.
 */
public class ExClass implements Explorable {
    private static final Pattern sSplitPattern = Pattern.compile("\\.");
    public static final char DOT = '.';
    private static List<String> sAllClass = new ArrayList<>();
    private String mPackage;

    public static void clearAll() {
        sAllClass.clear();
    }

    public static void addAllName(String... strs) {
        sAllClass.addAll(Arrays.asList(strs));
    }

    public static void addAllClass(Class... clss) {
        for (Class cls : clss) {
            sAllClass.add(cls.getName());
        }
    }

    public ExClass(String pkg) {
        mPackage = pkg;
    }

    public ExClass(Class cls) {
        this(cls.getName());
    }

    private int getContainerId(Object fragment) {
        if (fragment instanceof ExplorerFragment) {
            View view = ((ExplorerFragment) fragment).getView();
            if (view != null && view.getParent() instanceof ViewGroup && ((ViewGroup) view.getParent()).getId() != View.NO_ID) {
                return ((ViewGroup) view.getParent()).getId();
            }
        }
        return android.R.id.content;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onAction(Context context, Object extra) {
        try {
            Class<?> cls = Class.forName(mPackage);
            if (Activity.class.isAssignableFrom(cls)) {
                Intent intent = new Intent(context, cls);
                if (!(context instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Fragment.class.isAssignableFrom(cls)) {
                if (context instanceof Activity) {
                    ((Activity) context).setTitle(cls.getSimpleName());
                    ((Activity) context).getFragmentManager().beginTransaction()
                            .replace(getContainerId(extra), Fragment.instantiate(context, cls.getName(), null), cls.getName())
                            .addToBackStack(null)
                            .commit();
                }
                // 试图移除ExplorerFragment
                if (extra instanceof ExplorerFragment && context instanceof FragmentActivity) {
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .remove((android.support.v4.app.Fragment) extra)
                            .addToBackStack(null)
                            .commit();
                }
            } else if (android.support.v4.app.Fragment.class.isAssignableFrom(cls)) {
                if (context instanceof FragmentActivity) {
                    ((FragmentActivity) context).setTitle(cls.getSimpleName());
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(getContainerId(extra), android.support.v4.app.Fragment.instantiate(context, cls.getName(), null), cls.getName())
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                // public static void main(String... args);
                Method mainMethod = cls.getMethod("main", new String[]{}.getClass());
                mainMethod.invoke(null, new Object[]{new String[]{}});// 这样才能和args对应...
                ExUtils.error("执行main(), " + cls.getSimpleName());
            }
        } catch (Exception e) {
            ExUtils.error(e, e.getMessage());
        }
    }

    @Override
    public Explorable getParent() {
        if (mPackage.isEmpty()) {
            return null;
        }
        String[] split = sSplitPattern.split(mPackage);
        StringBuilder parent = new StringBuilder();
        for (int i = 0; i < split.length - 1/*除了最后一段, 其他段组成parent*/; i++) {
            parent.append(split[i]).append(DOT);
        }
        return new ExClass(parent.toString());
    }

    @Override
    public boolean isDir() {
        return mPackage.isEmpty() || mPackage.charAt(mPackage.length() - 1) == DOT;
    }

    @Override
    public List<Explorable> getChildren() {
        final ArrayList<Explorable> list = new ArrayList<>();
        for (String name : sAllClass) {
            if (name.startsWith(mPackage)) {
                int nextDotIndex = name.indexOf(DOT, mPackage.length());
                String pkg;
                if (nextDotIndex != -1) {
                    pkg = name.substring(0, nextDotIndex + 1);
                } else {
                    pkg = name;
                }
                final ExClass node = new ExClass(pkg);
                list.remove(node);
                list.add(node);
            }
        }
        return list;
    }

    @Override
    public String getPath() {
        return mPackage;
    }

    @Override
    public String getName() {
        String[] split = sSplitPattern.split(mPackage);
        return split[split.length - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExClass aExClass = (ExClass) o;

        return !(mPackage != null ? !mPackage.equals(aExClass.mPackage) : aExClass.mPackage != null);

    }

    @Override
    public int hashCode() {
        return mPackage != null ? mPackage.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getPath();
    }
}
