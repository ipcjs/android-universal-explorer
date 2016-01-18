package com.ipcjs.explorer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Constructor;

/**
 * Created by JiangSong on 2015/12/10.
 */
public class ViewWrapperFragment extends FragmentCompat {

    public static final String ARG_VIEW_CLASS = "viewClass";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        try {
            view = createViewFromArgs();
        } catch (Exception e) {
            e.printStackTrace();
            final TextView tv = new TextView(getContext());
            tv.setText(e.toString());
            tv.setTextColor(Color.RED);
            view = tv;
        }
        return view;
    }

    private View createViewFromArgs() throws Exception {
        if (getArguments() != null) {
            Class<View> viewClass = (Class<View>) getArguments().getSerializable(ARG_VIEW_CLASS);
            if (viewClass != null) {
                final Constructor<View> constructor = viewClass.getConstructor(Context.class);
                return constructor.newInstance(getContext());
            }
        }
        throw new RuntimeException("未设置要要创建的View");
    }
}
