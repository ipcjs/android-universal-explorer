package com.ipcjs.explorer;

import android.content.Context;

import java.util.List;

/**
 * Created by ipcjs on 2015/11/30.
 */
public interface Explorable {
    void doAction(Context context);

    Explorable getParent();

    boolean isDir();

    List<Explorable> getChildren();

    String getPath();

    String getName();

    interface OnActionListener {
        /**
         * @param context 上下文
         * @param extra   终极hack...
         */
        void onAction(Context context, Object extra);
    }

}
