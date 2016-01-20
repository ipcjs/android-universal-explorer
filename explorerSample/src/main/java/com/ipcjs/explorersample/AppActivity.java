package com.ipcjs.explorersample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import com.ipcjs.explorer.Explorer;

import static com.ipcjs.explorer.ExUtils.p;

/**
 * Created by JiangSong on 2016/1/20.
 */
@Explorer.ExClassName(summary = "Menu的生命周期")
public class AppActivity extends Activity {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        p();
        super.onCreate(savedInstanceState);
        getActionBar();
    }

    @Override
    protected void onResume() {
        p();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        p();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        p();
        return super.onPrepareOptionsMenu(menu);
    }
}
