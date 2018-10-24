package com.github.ipcjs.explorer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class DrawableWrapperFragment extends ViewWrapperFragment {
    private static final String ARG_DRAWABLE_CLASS = "drawableClass";

    public static DrawableWrapperFragment newDrawableFragment(Class<? extends Drawable> drawableClass) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEW_CLASS, View.class);
        args.putSerializable(ARG_DRAWABLE_CLASS, drawableClass);

        DrawableWrapperFragment fragment = new DrawableWrapperFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        try {
            ViewCompat.setBackground(view, createDrawableFromArgs());
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorView(e);
        }
        return view;
    }

    private Drawable createDrawableFromArgs() throws Exception {
        Bundle arguments = getArguments();
        Class<? extends Drawable> drawableClass;
        if (arguments != null) {
            drawableClass = (Class<? extends Drawable>) arguments.getSerializable(ARG_DRAWABLE_CLASS);
            if (drawableClass != null) {
                try {
                    return drawableClass.getConstructor().newInstance();
                } catch (NoSuchMethodException e) {
                    return drawableClass.getConstructor(Context.class).newInstance(getContext());
                }
            }
        }
        throw new RuntimeException("未设置drawableClass");
    }
}
