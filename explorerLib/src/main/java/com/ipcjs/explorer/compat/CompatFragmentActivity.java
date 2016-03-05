package com.ipcjs.explorer.compat;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by JiangSong on 2016/3/5.
 */
public class CompatFragmentActivity extends FragmentActivity implements CompatContext {

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public <V extends View> V $(@IdRes int id) {
        return (V) findViewById(id);
    }
}
