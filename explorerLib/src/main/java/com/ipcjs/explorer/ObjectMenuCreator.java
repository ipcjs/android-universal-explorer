package com.ipcjs.explorer;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ipcjs.explorer.ExUtils.error;
import static com.ipcjs.explorer.ExUtils.p;

/**
 * Created by JiangSong on 2016/1/19.
 */
public class ObjectMenuCreator implements Explorer.MenuCreator {
    private int mGroupId;
    private static Explorer.MenuItem sDefaultMenuItem;

    private static synchronized Explorer.MenuItem getDefaultMenuItem() {
        if (sDefaultMenuItem == null) {
            sDefaultMenuItem = ExUtils.newAnnotation(Explorer.MenuItem.class);
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
            error("object不能为null");
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
            error("object和cls不能同时为null");
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
            if (!mustHasMenuItemAnnotation || method.isAnnotationPresent(Explorer.MenuItem.class)) {
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
        final Explorer.MenuItem defaultMenuItem = getDefaultMenuItem();
        for (int i = 0; i < mMethodList.size(); i++) {
            final Method method = mMethodList.get(i);
            String title = method.getName();
            int order = defaultMenuItem.order();
            int showAsAction = defaultMenuItem.showAsAction();
            boolean checkable = defaultMenuItem.checkable();
            int iconRes = defaultMenuItem.iconRes();

            final Explorer.MenuItem annotation = method.getAnnotation(Explorer.MenuItem.class);
            if (annotation != null) {// 若有注解
                order = annotation.order();
                showAsAction = annotation.showAsAction();
                if (!TextUtils.isEmpty(annotation.title())) {
                    title = annotation.title();
                }
            }
            // 使用: mMethodList中的index作为itemId
            final MenuItem item = menu.add(mGroupId, i, order, title);
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
                    error("result", result);
                }
                return true;
            } catch (/*IllegalAccessException | InvocationTarget*/Exception e) {
                error(e, e.toString());
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mMethodList.size();
    }
}
