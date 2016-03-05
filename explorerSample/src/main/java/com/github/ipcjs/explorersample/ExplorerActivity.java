package com.github.ipcjs.explorersample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.ipcjs.explorer.Explorer;
import com.github.ipcjs.explorer.ExplorerFragment;

/**
 * Created by JiangSong on 2015/12/2.
 */
@Explorer.ExClassName("浏览器")
public class ExplorerActivity extends AppCompatActivity {
    public static final Class[] sClassArray = {
            TestFragment.class,
            Test.class,
            MainActivity.class,
            ExplorerActivity.class,
            AppFragment.class,
            CustomView.class,
            TestFragment.class,
            MenuHelperFragment.class,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("");
        setContentView(tv);// 调用一次setContentView(), 触发ensureSubDecor(), 使Toolbar下面的部分变成android.R.id.content~~
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, ExplorerFragment.newInstance(sClassArray), ExplorerFragment.class.getName())
                    .commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBackPressed() {
        // 先移除App包下的Fragment~~
        if (!getFragmentManager().popBackStackImmediate()) {
            // App包下的Fragment全部pop了.
        }
        super.onBackPressed();
    }
}
