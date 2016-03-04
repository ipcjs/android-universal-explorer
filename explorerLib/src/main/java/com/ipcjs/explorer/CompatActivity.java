package com.ipcjs.explorer;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by ipcjs on 2016/3/5.
 */
public class CompatActivity extends FragmentActivity {
    public Context getContext() {
        return this;
    }

    public <V extends View> V $(@IdRes int id) {
        return (V) findViewById(id);
    }
}
