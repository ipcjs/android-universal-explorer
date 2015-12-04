package com.ipcjs.explorer;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by JiangSong on 2015/12/3.
 */
public class EnumMenuHelper<ACTION extends Enum & Explorer.OnActionListener> {
    private Class<ACTION> mEnumType;
    private Context mContext;
    private Explorer.ExplorerContainer mContainer;

    /**
     * @param enumType
     * @param context   传递给: {@link ACTION#onAction(Context, Explorer.ExplorerContainer)}
     * @param container 传递给: {@link ACTION#onAction(Context, Explorer.ExplorerContainer)}
     */
    public EnumMenuHelper(Class<ACTION> enumType, Context context, Explorer.ExplorerContainer container) {
        mEnumType = enumType;
        mContext = context;
        mContainer = container;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        ACTION[] enumArray = mEnumType.getEnumConstants();
        for (ACTION action : enumArray) {
            MenuItem item = menu.add(action.name());
            item.setTitleCondensed(mEnumType.getName());
        }
        return enumArray.length > 0;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mEnumType.getName().equals(item.getTitleCondensed().toString())) {
            /*这里的类型貌似写ACTION会报错~, 故先写Enum, 再强转~~*/
            Enum action = Enum.valueOf(mEnumType, item.getTitle().toString());
            ((ACTION) action).onAction(mContext, mContainer);
            return true;
        } else {
            return false;
        }
    }

    public int getItemCount() {
        return mEnumType.getEnumConstants().length;
    }
}
