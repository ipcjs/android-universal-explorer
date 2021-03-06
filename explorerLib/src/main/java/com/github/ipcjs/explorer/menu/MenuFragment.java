package com.github.ipcjs.explorer.menu;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.ipcjs.explorer.compat.CompatFragment;

/**
 * 支持处理多个{@link ObjectMenuCreator}实例~~
 * Created by JiangSong on 2016/1/19.
 */
public class MenuFragment extends CompatFragment implements View.OnClickListener {
    private final ObjectMenuCreator mFragmentMenuCreator = new ObjectMenuCreator(this);
    private MultiMenuCreator mMenuCreator = new MultiMenuCreator(mFragmentMenuCreator);

    public MultiMenuCreator getMultiMenuCreator() {
        return mMenuCreator;
    }

    public ObjectMenuCreator getFragmentMenuCreator() {
        return mFragmentMenuCreator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// 有option Menu
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenuCreator.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuCreator.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
}
