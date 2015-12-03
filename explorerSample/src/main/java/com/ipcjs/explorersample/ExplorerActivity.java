package com.ipcjs.explorersample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ipcjs.explorer.EnumMenuHelper;
import com.ipcjs.explorer.ExClass;
import com.ipcjs.explorer.Explorable;
import com.ipcjs.explorer.ExplorerFragment;

/**
 * Created by JiangSong on 2015/12/2.
 */
public class ExplorerActivity extends AppCompatActivity {
    public static final Class[] sClassArray = {
            TestFragment.class,
            Test.class,
            MainActivity.class,
            ExplorerActivity.class,
    };

    enum Action implements Explorable.OnActionListener {
        action() {
            @Override
            public void onAction(Context context, Object extra) {
                Toast.makeText(context, "action", Toast.LENGTH_SHORT).show();
            }
        },
        /**/;

        @Override
        public void onAction(Context context, Object extra) {

        }
    }

    static {
        ExClass.addAllClass(sClassArray);// 设置要浏览的class的内容
    }

    private EnumMenuHelper<Action> mEnumMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("");
        setContentView(tv);// 调用一次setContentView(), 触发ensureSubDecor(), 使Toolbar下面的部分变成android.R.id.content~~
        mEnumMenuHelper = new EnumMenuHelper<>(Action.class, this, null);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new ExplorerFragment(), ExplorerFragment.class.getName())
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

}
