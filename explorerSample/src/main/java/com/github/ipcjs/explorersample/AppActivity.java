package com.github.ipcjs.explorersample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.github.ipcjs.explorer.Explorer;
import com.github.ipcjs.explorer.ExplorerFragment;
import com.github.ipcjs.explorer.menu.ObjectMenuCreator;

import static com.github.ipcjs.explorer.ExUtils.p;

/**
 * Created by JiangSong on 2016/1/20.
 */
@Explorer.ExClassName(summary = "Menu的生命周期")
public class AppActivity extends FragmentActivity {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        p();
        super.onCreate(savedInstanceState);
        Button btn = new Button(this);
        setContentView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ExplorerFragment fragment = ExplorerFragment.setupExplorer(AppActivity.this, ExplorerActivity.class);
                fragment.getMultiMenuCreator().add(new ObjectMenuCreator().setObject(new Object() {
                    void clear() {
                        fragment.getMultiMenuCreator().getList().clear();
                    }
                }, null, ObjectMenuCreator.METHOD_RANGE_DECLARED));
            }
        });
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
