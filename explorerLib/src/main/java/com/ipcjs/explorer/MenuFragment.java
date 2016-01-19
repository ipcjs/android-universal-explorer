package com.ipcjs.explorer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持处理多个{@link ObjectMenuCreator}实例~~
 * Created by JiangSong on 2016/1/19.
 */
public class MenuFragment extends FragmentCompat {
    private List<ObjectMenuCreator> mObjectMenuCreatorList = new ArrayList<>();

    public List<ObjectMenuCreator> getMenuCreatorList() {
        return mObjectMenuCreatorList;
    }

    protected ObjectMenuCreator getFirstMenuCreator() {
        return mObjectMenuCreatorList.get(0);
    }

    public MenuFragment() {
        mObjectMenuCreatorList.add(new ObjectMenuCreator(this));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int count = 0;
        for (ObjectMenuCreator creator : mObjectMenuCreatorList) {
            count += creator.getItemCount();
        }
        setHasOptionsMenu(count > 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        for (ObjectMenuCreator creator : mObjectMenuCreatorList) {
            creator.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (ObjectMenuCreator creator : mObjectMenuCreatorList) {
            if (creator.onOptionsItemSelected(item)) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
