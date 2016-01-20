package com.ipcjs.explorer.menu;

import android.content.Context;
import android.view.Menu;

/**
 * Created by JiangSong on 2015/12/3.
 * 使用太过繁琐, 废弃, 使用{@link ObjectMenuCreator}替代
 */
@Deprecated
public class EnumMenuHelper<ACTION extends Enum & MenuCreator.OnActionListener<HACK>, HACK> implements MenuCreator {
    private Class<ACTION> mEnumType;
    private Context mContext;
    private HACK mHack;

    /**
     * @param enumType
     * @param context  传递给: {@link ACTION#onAction(Context, Object)}
     * @param hack     传递给: {@link ACTION#onAction(Context, Object)}
     */
    public EnumMenuHelper(Class<ACTION> enumType, Context context, HACK hack) {
        mEnumType = enumType;
        mContext = context;
        mHack = hack;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ACTION[] enumArray = mEnumType.getEnumConstants();
        for (ACTION action : enumArray) {
            android.view.MenuItem item = menu.add(action.name());
            item.setTitleCondensed(mEnumType.getName());
        }
        return enumArray.length > 0;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (mEnumType.getName().equals(item.getTitleCondensed().toString())) {
            /*这里的类型貌似写ACTION会报错~, 故先写Enum, 再强转~~*/
            Enum action = Enum.valueOf(mEnumType, item.getTitle().toString());
            ((ACTION) action).onAction(mContext, mHack);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return mEnumType.getEnumConstants().length;
    }
}
