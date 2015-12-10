package com.ipcjs.explorersample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ipcjs.explorer.Explorer;

/**
 * Created by JiangSong on 2015/12/10.
 */
@Explorer.ExClassName("自定义View")
public class CustomView extends View {

    private final Paint mPaint;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText("Custom", getWidth() / 2, getHeight() / 2, mPaint);
    }
}
