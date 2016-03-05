package com.ipcjs.explorer.compat;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by JiangSong on 2016/3/5.
 */
public interface CompatContextInterface {
    Context getContext();

    <V extends View> V $(@IdRes int id);
}
