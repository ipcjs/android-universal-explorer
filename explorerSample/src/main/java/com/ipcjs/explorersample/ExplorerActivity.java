package com.ipcjs.explorersample;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ipcjs.explorer.EnumMenuHelper;
import com.ipcjs.explorer.Explorer;
import com.ipcjs.explorer.ExplorerFragment;

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
    };

    enum Action implements Explorer.OnActionListener<ExplorerActivity> {
        action() {
            @Override
            public void onAction(Context context, ExplorerActivity hack) {
                Toast.makeText(context, "action", Toast.LENGTH_SHORT).show();
            }
        },
        /**/;

        @Override
        public void onAction(Context context, ExplorerActivity hack) {

        }
    }

    private EnumMenuHelper<Action, ExplorerActivity> mEnumMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("");
        setContentView(tv);// 调用一次setContentView(), 触发ensureSubDecor(), 使Toolbar下面的部分变成android.R.id.content~~
        mEnumMenuHelper = new EnumMenuHelper<>(Action.class, this, this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, ExplorerFragment.newInstance(sClassArray), ExplorerFragment.class.getName())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return mEnumMenuHelper.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item) || mEnumMenuHelper.onOptionsItemSelected(item);
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
