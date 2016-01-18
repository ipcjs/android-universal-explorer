package com.ipcjs.explorer;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.lang.reflect.Method;

/**
 * Created by ipcjs on 2016/1/19.
 */
public class FragmentCompat extends Fragment {
    private static final boolean sHasGetContextMethod;

    static {
        Method getContextMethod = null;
        try {
            getContextMethod = Fragment.class.getMethod("getContext");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
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
}
