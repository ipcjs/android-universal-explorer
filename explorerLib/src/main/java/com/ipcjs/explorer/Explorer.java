package com.ipcjs.explorer;

import android.content.Context;

import java.util.List;

/**
 * Created by JiangSong on 2015/12/4.
 */
public interface Explorer {
    interface Explorable {
        void onAction(Context context, ExplorerContainer container);

        Explorable getParent();

        boolean isDir();

        List<Explorable> getChildren(ExplorerContainer container);

        String getPath();

        String getName();

    }

    interface OnActionListener {
        void onAction(Context context, Object hack);
    }

    interface ExplorerContainer {
        int getContainId();

        List<String> getExploreRange();
    }
}
