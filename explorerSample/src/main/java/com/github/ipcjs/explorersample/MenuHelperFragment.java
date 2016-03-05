package com.github.ipcjs.explorersample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.ipcjs.explorer.Explorer;
import com.github.ipcjs.explorer.menu.MenuCreator;
import com.github.ipcjs.explorer.menu.MenuFragment;
import com.github.ipcjs.explorer.menu.ObjectMenuCreator;

import static com.github.ipcjs.explorer.ExUtils.tInfo;

/**
 * Created by JiangSong on 2016/1/19.
 */
@Explorer.ExClassName(value = "菜单", summary = "使用ObjectMenuCreator自动创建菜单")
public class MenuHelperFragment extends MenuFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new Button(getContext());
    }

    @MenuCreator.MenuItem
    private int todo() {
        tInfo("todo");
        return 0;
    }

    @MenuCreator.MenuItem
    void menuView() {
        getMultiMenuCreator().add(new ObjectMenuCreator().setObject(getView(), null, ObjectMenuCreator.METHOD_RANGE_PUBLIC));
        getActivity().supportInvalidateOptionsMenu();
    }
}
