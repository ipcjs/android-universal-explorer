package com.github.ipcjs.explorersample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.github.ipcjs.explorer.Explorer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


@Explorer.ExClassName("自定义Drawable")
public class CustomDrawable extends Drawable {
    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawColor(Color.YELLOW);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
