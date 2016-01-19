package com.ipcjs.explorersample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ipcjs.explorer.Explorer;
import com.ipcjs.explorer.MenuFragmentCompat;

/**
 * Created by JiangSong on 2016/1/19.
 */
public class MenuHelperFragment extends MenuFragmentCompat {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new Button(getContext());
    }

    @Explorer.MenuItem
    private void todo() {

    }
}
