package com.ipcjs.explorer;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.ipcjs.explorer.ExUtils.error;
import static com.ipcjs.explorer.ExUtils.p;

/**
 * Created by JiangSong on 2016/1/19.
 */
public class ObjectMenuCreator implements Explorer.IMenuCreator {

    private int mGroupId;
    private static Explorer.MenuItem sDefaultMenuItem;

    private static synchronized Explorer.MenuItem getDefaultMenuItem() {
        if (sDefaultMenuItem == null) {
            sDefaultMenuItem = ExUtils.newAnnotationInstance(Explorer.MenuItem.class);
        }
        return sDefaultMenuItem;
    }

    private Context mContext;
    private Object mObject;

    public ObjectMenuCreator() {
        this(null);
    }

    public ObjectMenuCreator(Object object) {
        this(null, object);
    }

    public ObjectMenuCreator(Context context, Object object) {
        mContext = context;
        setObject(object);
    }

    public void setObject(Object object) {
        if (object == null) {
            error("object不能为null");
            return;
        }
        setObject(object, object.getClass(), true);
    }

    /**
     * @param object
     * @param cls
     * @param mustHasMenuItemAnnotation
     */
    public void setObject(Object object, Class cls, boolean mustHasMenuItemAnnotation) {
        if (object == null && cls == null) {
            error("object和cls不能同时为null");
            return;
        }
        mObject = object;
        // object为null则使用cls的hasCode
        mGroupId = mObject == null ? cls.hashCode() : mObject.hashCode();
        // cls为null则使用object.getClass
        parseClass(cls == null ? mObject.getClass() : cls, mustHasMenuItemAnnotation);
    }

    private List<Method> mMethodList = new ArrayList<>();

    private void parseClass(Class cls, boolean mustHasMenuItemAnnotation) {
        mMethodList.clear();
        final Method[] methods = cls.getMethods();
        final Method[] declaredMethods = cls.getDeclaredMethods();

        for (int i = 0; i < methods.length + declaredMethods.length; i++) {
            Method method = i < methods.length ? methods[i] : declaredMethods[i - methods.length];
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
                mMethodList.get(item.getItemId()).invoke(mObject);
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
