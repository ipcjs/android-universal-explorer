package com.ipcjs.explorer.compat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by ipcjs on 2016/3/5.
 */
public class AppActivity extends Activity implements CompatContext {
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public <V extends View> V $(@IdRes int id) {
        return (V) findViewById(id);
    }
}
