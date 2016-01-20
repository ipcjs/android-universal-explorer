package com.ipcjs.explorer.menu;

import android.view.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JiangSong on 2016/1/20.
 */
public class MultiMenuCreator implements MenuCreator {
    private List<MenuCreator> mMenuCreatorList = new ArrayList<>();

    public List<MenuCreator> getList() {
        return mMenuCreatorList;
    }

    public void add(MenuCreator creator) {
        mMenuCreatorList.add(creator);
    }

    public void remove(MenuCreator creator) {
        mMenuCreatorList.remove(creator);
    }

    public MultiMenuCreator(MenuCreator... creators) {
        mMenuCreatorList.addAll(Arrays.asList(creators));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean show = false;
        for (MenuCreator creator : mMenuCreatorList) {
            show |= creator.onCreateOptionsMenu(menu);
        }
        return show;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        for (MenuCreator creator : mMenuCreatorList) {
            if (creator.onOptionsItemSelected(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (MenuCreator creator : mMenuCreatorList) {
            count += creator.getItemCount();
        }
        return count;
    }
}
