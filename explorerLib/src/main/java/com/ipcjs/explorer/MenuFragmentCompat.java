package com.ipcjs.explorer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by JiangSong on 2016/1/19.
 */
public class MenuFragmentCompat extends FragmentCompat {
    private ObjectMenuCreator mObjectMenuCreator = new ObjectMenuCreator(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(mObjectMenuCreator.getItemCount() > 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mObjectMenuCreator.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mObjectMenuCreator.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
