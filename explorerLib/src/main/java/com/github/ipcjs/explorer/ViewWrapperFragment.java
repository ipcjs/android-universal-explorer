package com.github.ipcjs.explorer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ipcjs.explorer.compat.CompatFragment;

import java.lang.reflect.Constructor;

import androidx.annotation.Nullable;

/**
 * Created by JiangSong on 2015/12/10.
 */
public class ViewWrapperFragment extends CompatFragment {
    protected static final String ARG_VIEW_CLASS = "viewClass";

    public static ViewWrapperFragment newViewFragment(Class<? extends View> viewClass) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEW_CLASS, viewClass);

        ViewWrapperFragment fragment = new ViewWrapperFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return createViewFromArgs();
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorView(e);
        }
    }

    protected View createErrorView(Throwable e) {
        final TextView tv = new TextView(getContext());
        tv.setText(e.toString());
        tv.setTextColor(Color.RED);
        return tv;
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
