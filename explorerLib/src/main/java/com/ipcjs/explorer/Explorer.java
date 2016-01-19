package com.ipcjs.explorer;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

    /**
     * 和{@link EnumMenuHelper}配合使用
     * 使用太过繁琐, 废弃
     * @param <HACK>
     */
    @Deprecated
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

    /**
     * Created by JiangSong on 2016/1/19.
     */
    interface IMenuCreator {
        boolean onCreateOptionsMenu(Menu menu);

        boolean onOptionsItemSelected(android.view.MenuItem item);

        int getItemCount();
    }

    /**
     * Created by JiangSong on 2016/1/19.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface MenuItem {
        String title() default "";

        int order() default 0;

        int showAsAction() default MenuItemCompat.SHOW_AS_ACTION_IF_ROOM;
        boolean checkable() default false;
        int iconRes() default 0;
    }
}
