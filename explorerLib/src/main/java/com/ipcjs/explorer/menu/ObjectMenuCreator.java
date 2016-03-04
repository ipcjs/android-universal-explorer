package com.ipcjs.explorer.menu;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;

import com.ipcjs.explorer.BuildConfig;
import com.ipcjs.explorer.ExUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ipcjs.explorer.ExUtils.p;
import static com.ipcjs.explorer.ExUtils.tError;
import static com.ipcjs.explorer.ExUtils.tInfo;

/**
 * 因为用作生成菜单的方法基本上都不会在代码里直接调用, 故很容易被ProGuard移除掉...
 * <br>推荐, 添加下面的混淆配置, 防止方法被移除
 * <code>
 * -keep interface com.ipcjs.explorer.menu.MenuCreator$MenuItem
 * -keepclassmembers class * {
 * @com.ipcjs.explorer.menu.MenuCreator$MenuItem *;
 * }
 * </code>
 * Created by JiangSong on 2016/1/19.
 */
public class ObjectMenuCreator implements MenuCreator {
    private int mGroupId;
    private static MenuItem sDefaultMenuItem;

    private static synchronized MenuItem getDefaultMenuItem() {
        if (sDefaultMenuItem == null) {
            sDefaultMenuItem = ExUtils.newAnnotation(MenuItem.class);
        }
        return sDefaultMenuItem;
    }

    /** mContext暂时没用~~ */
    private Context mContext;
    private Object mObject;

    public ObjectMenuCreator() {
    }

    public ObjectMenuCreator(Object object) {
        setObject(object);
    }

    public ObjectMenuCreator setContext(Context context) {
        mContext = context;
        return this;
    }

    public ObjectMenuCreator setObject(Object object) {
        if (object == null) {
            tError("object不能为null");
        } else {
            setObject(object, object.getClass(), METHOD_RANGE_DEFAULT);
        }
        return this;
    }

    /** 添加声明的方法 */
    public static final int METHOD_RANGE_DECLARED = 1;
    /** 添加public的方法 */
    public static final int METHOD_RANGE_PUBLIC = 2;
    /** 必须要有MenuItem注解才添加 */
    public static final int METHOD_RANGE_MUST_HAS_MENU_ITEM = 4;
    /** 默认值 */
    public static final int METHOD_RANGE_DEFAULT = METHOD_RANGE_DECLARED | METHOD_RANGE_PUBLIC | METHOD_RANGE_MUST_HAS_MENU_ITEM;

    /**
     * @param object
     * @param cls
     * @param methodRangeFlag
     * @return
     */
    public ObjectMenuCreator setObject(Object object, Class cls, int methodRangeFlag) {
        if (object == null && cls == null) {
            tError("object和cls不能同时为null");
        } else {
            mObject = object;
            // object为null则使用cls的hasCode
            mGroupId = mObject == null ? cls.hashCode() : mObject.hashCode();
            // cls为null则使用object.getClass
            parseClass(cls == null ? mObject.getClass() : cls, methodRangeFlag);
        }
        return this;
    }

    private List<Method> mMethodList = new ArrayList<>();

    private void parseClass(Class cls, int methodRangeFlag) {
        mMethodList.clear();
        List<Method> tmpList = new ArrayList<>();
        if ((methodRangeFlag & METHOD_RANGE_PUBLIC) != 0) {
            tmpList.addAll(Arrays.asList(cls.getMethods()));
        }
        if ((methodRangeFlag & METHOD_RANGE_DECLARED) != 0) {
            tmpList.addAll(Arrays.asList(cls.getDeclaredMethods()));
        }
        boolean mustHasMenuItemAnnotation = (methodRangeFlag & METHOD_RANGE_MUST_HAS_MENU_ITEM) != 0;
        for (Method method : tmpList) {
            if (!mustHasMenuItemAnnotation || method.isAnnotationPresent(MenuItem.class)) {
                if (BuildConfig.DEBUG) p(method);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                mMethodList.remove(method);
                mMethodList.add(method);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem defaultMenuItem = getDefaultMenuItem();
        for (int i = 0; i < mMethodList.size(); i++) {
            final Method method = mMethodList.get(i);
            String title = method.getName();
            int order = defaultMenuItem.order();
            int showAsAction = defaultMenuItem.showAsAction();
            boolean checkable = defaultMenuItem.checkable();
            int iconRes = defaultMenuItem.iconRes();

            final MenuItem annotation = method.getAnnotation(MenuItem.class);
            if (annotation != null) {// 若有注解
                order = annotation.order();
                showAsAction = annotation.showAsAction();
                if (!TextUtils.isEmpty(annotation.title())) {
                    title = annotation.title();
                }
            }
            // 使用: mMethodList中的index作为itemId
            final android.view.MenuItem item = menu.add(mGroupId, i, order, title);
            MenuItemCompat.setShowAsAction(item, showAsAction);
            item.setCheckable(checkable);
            item.setIcon(iconRes);
        }
        return mMethodList.size() > 0;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == mGroupId) {
            try {
                Object result = mMethodList.get(item.getItemId()).invoke(mObject);
                if (result != null) {
                    tInfo("result", result);
                }
                return true;
            } catch (/*IllegalAccessException | InvocationTarget*/Exception e) {
                tError(e, e.getCause());
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mMethodList.size();
    }
}
