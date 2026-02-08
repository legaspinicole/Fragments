package com.example.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MovingCloudsView extends View {

    private Bitmap cloudsBitmap;
    private Paint paint = new Paint();
    private float xPos = 0;
    private boolean hasInitialized = false;

    public MovingCloudsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!hasInitialized && w > 0 && h > 0) {
            hasInitialized = true;
            try {
                cloudsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clouds2);
            } catch (Exception e) {
                e.printStackTrace();
                cloudsBitmap = null;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (cloudsBitmap == null) {
            return; // Don't draw if the bitmap is missing
        }

        xPos -= 0.5f;

        if (xPos < -cloudsBitmap.getWidth()) {
            xPos = 0;
        }

        canvas.drawBitmap(cloudsBitmap, xPos, 0, paint);
        canvas.drawBitmap(cloudsBitmap, xPos + cloudsBitmap.getWidth(), 0, paint);

        invalidate();
    }
}
