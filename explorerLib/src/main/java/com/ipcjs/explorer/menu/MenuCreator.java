package com.ipcjs.explorer.menu;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by JiangSong on 2016/1/19.
 */
public interface MenuCreator {
    boolean onCreateOptionsMenu(Menu menu);

    boolean onOptionsItemSelected(android.view.MenuItem item);

    int getItemCount();

    /**
     * 和{@link EnumMenuHelper}配合使用
     * 使用太过繁琐, 废弃
     * @param <HACK>
     */
    @Deprecated
    interface OnActionListener<HACK> {
        void onAction(Context context, HACK hack);
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
