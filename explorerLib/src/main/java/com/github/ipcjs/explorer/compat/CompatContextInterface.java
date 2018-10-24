package com.github.ipcjs.explorer.compat;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;

/**
 * Created by JiangSong on 2016/3/5.
 */
public interface CompatContextInterface {
    Context getContext();

    <V extends View> V $(@IdRes int id);
}
