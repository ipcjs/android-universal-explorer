package com.ipcjs.explorer.compat;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.reflect.Method;

import static com.ipcjs.explorer.ExUtils.tError;

/**
 * Created by ipcjs on 2016/1/19.
 */
public class CompatFragment extends Fragment implements CompatContextInterface {
    private static final boolean sHasGetContextMethod;

    static {
        Method getContextMethod = null;
        try {
            getContextMethod = Fragment.class.getMethod("getContext");
        } catch (NoSuchMethodException e) {
            tError(e);
        }
        sHasGetContextMethod = getContextMethod != null;
    }

    @Override
    public Context getContext() {
        if (sHasGetContextMethod) {
            return super.getContext();
        } else {
            return getActivity();
        }
    }

    @Override
    public <V extends View> V $(@IdRes int id) {
        return (V) getView().findViewById(id);
    }
}
