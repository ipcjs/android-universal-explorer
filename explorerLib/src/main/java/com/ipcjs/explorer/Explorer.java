package com.ipcjs.explorer;

import android.content.Context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    interface OnActionListener<HACK> {
        void onAction(Context context, HACK hack);
    }

    interface ExplorerContainer {
        int getContainId();

        List<String> getExploreRange();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ExClassName {
        String value();
    }
}
