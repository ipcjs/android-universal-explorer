package com.github.ipcjs.explorer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.github.ipcjs.explorer.ExUtils.tError;
import static com.github.ipcjs.explorer.ExUtils.tInfo;

/**
 * Created by JiangSong on 2015/12/2.
 */
public class ExClass implements Explorer.Explorable {
    private static final Pattern sSplitPattern = Pattern.compile("\\.");
    public static final char SPLIT_DOT = '.';
    public static final char SPLIT_LF = '\n';
    private String mPackage;

    public ExClass(String pkg) {
        mPackage = pkg;
    }

    public ExClass(Class cls) {
        this(cls.getName());
    }

    private int getContainerId(Explorer.ExplorerContainer fragment) {
        if (fragment != null) {
            return fragment.getContainId();
        }
        return android.R.id.content;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onAction(Context context, Explorer.ExplorerContainer container) {
        try {
            Class<?> cls = Class.forName(mPackage);
            if (Activity.class.isAssignableFrom(cls)) {
                Intent intent = new Intent(context, cls);
                if (!(context instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Fragment.class.isAssignableFrom(cls)) {
                replaceFragment(context, container, cls, cls, null, false);
                // 试图移除ExplorerFragment
                if (container instanceof ExplorerFragment && context instanceof FragmentActivity) {
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .remove((androidx.fragment.app.Fragment) container)
                            .addToBackStack(null)
                            .commit();
                }
            } else if (androidx.fragment.app.Fragment.class.isAssignableFrom(cls)) {
                replaceFragment(context, container, cls, cls, null, true);
            } else if (View.class.isAssignableFrom(cls)) {
                final Bundle args = new Bundle();
                args.putSerializable(ViewWrapperFragment.ARG_VIEW_CLASS, cls);
                replaceFragment(context, container, cls, ViewWrapperFragment.class, args, true);
            } else {
                // public static void main(String... args);
                Method mainMethod = cls.getMethod("main", new String[]{}.getClass());
                mainMethod.invoke(null, new Object[]{new String[]{}});// 这样才能和args对应...
                tInfo("执行main(), " + cls.getSimpleName());
            }
        } catch (Exception e) {
            tError(e, e.getCause());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void replaceFragment(Context context, Explorer.ExplorerContainer container, Class<?> cls,
                                 Class fragmentClass, Bundle args, boolean isV4) {
        if (!(context instanceof Activity)) {
            return;
        }
        ((Activity) context).setTitle(cls.getSimpleName());
        final int containerId = getContainerId(container);
        final String tag = cls.getName();
        if (!isV4) {// app包的fgt
            ((Activity) context).getFragmentManager().beginTransaction()
                    .replace(containerId, Fragment.instantiate(context, fragmentClass.getName(), args), tag)
                    .addToBackStack(null)
                    .commit();
        } else if (context instanceof FragmentActivity) {// v4包的fgt, 且context为FragmentActivity
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(containerId, androidx.fragment.app.Fragment.instantiate(context, fragmentClass.getName(), args), tag)
                    .addToBackStack(null)
                    .commit();
        } else {
            tError("v4包的fgt, 放到Activity中..., da me");
        }
    }

    @Override
    public Explorer.Explorable getParent() {
        if (mPackage.isEmpty()) {
            return null;
        }
        String[] split = sSplitPattern.split(mPackage);
        StringBuilder parent = new StringBuilder();
        for (int i = 0; i < split.length - 1/*除了最后一段, 其他段组成parent*/; i++) {
            parent.append(split[i]).append(SPLIT_DOT);
        }
        return new ExClass(parent.toString());
    }

    @Override
    public boolean isDir() {
        return mPackage.isEmpty() || mPackage.charAt(mPackage.length() - 1) == SPLIT_DOT;
    }

    @Override
    public List<Explorer.Explorable> getChildren(Explorer.ExplorerContainer container) {
        if (container == null) {
            return null;
        }
        final ArrayList<Explorer.Explorable> list = new ArrayList<>();
        for (String name : container.getExploreRange()) {
            if (name.startsWith(mPackage)) {
                int nextDotIndex = name.indexOf(SPLIT_DOT, mPackage.length());
                String pkg;
                if (nextDotIndex != -1) {
                    pkg = name.substring(0, nextDotIndex + 1);
                } else {
                    pkg = name;
                }
                final ExClass node = new ExClass(pkg);
                if (node.isDir()) {// 只有目录要去重
                    list.remove(node);
                }
                list.add(node);
            }
        }
        return list;
    }

    @Override
    public String getPath() {
        return mPackage;
    }

    /**
     * 当前, 只用来在{@link ExplorerFragment}的list中显示item的文字,
     * <br>为了同时体现title,summary的概念, 使用{@link #SPLIT_LF}分隔它们~~
     * <br>显示时在解析出来, 分别应用不同的样式
     * @see com.github.ipcjs.explorer.ExplorerFragment.ExplorerAdapter#onBindViewHolder(ExplorerFragment.ExplorerViewHolder, int)
     */
    @Override
    public String getName() {
        String[] split = sSplitPattern.split(mPackage);
        String title = split[split.length - 1];// 返回mPackage的最后一段
        String summary = null;

        if (!isDir()) {// 非目录, 试图读取注解中的title
            try {
                Class<?> cls = Class.forName(mPackage);
                Explorer.ExClassName exClassName = cls.getAnnotation(Explorer.ExClassName.class);
                if (exClassName != null) {
                    if (!TextUtils.isEmpty(exClassName.value())) {
                        title = exClassName.value();// 返回注解中的title
                    }
                    summary = exClassName.summary();// 注解中的summary
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        // 使用'\n'拆分title和summary
        return title + (TextUtils.isEmpty(summary) ? "" : SPLIT_LF + summary);
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
