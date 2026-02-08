package com.example.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class PixelatedButterflyView extends View {

    private Bitmap butterflyBitmap;
    private Paint paint = new Paint();
    private float xPos = -1, yPos = -1;
    private float xVelocity, yVelocity;
    private Random random = new Random();
    private boolean hasInitialized = false;

    public PixelatedButterflyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (!hasInitialized && w > 0 && h > 0) {
            hasInitialized = true;
            try {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.pixel_butterfly);
                if (drawable != null) {
                    int width = drawable.getIntrinsicWidth();
                    int height = drawable.getIntrinsicHeight();
                    if (width > 0 && height > 0) {
                        butterflyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(butterflyBitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);

                        int effectiveW = w - butterflyBitmap.getWidth();
                        int effectiveH = h - butterflyBitmap.getHeight();

                        xPos = random.nextInt(Math.max(1, effectiveW));
                        yPos = random.nextInt(Math.max(1, effectiveH));

                        xVelocity = random.nextFloat() * 4 - 2;
                        yVelocity = random.nextFloat() * 4 - 2;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                butterflyBitmap = null;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!hasInitialized || butterflyBitmap == null) {
            return;
        }

        xPos += xVelocity;
        yPos += yVelocity;

        if (random.nextInt(100) < 5) {
            xVelocity += random.nextFloat() * 2 - 1;
            yVelocity += random.nextFloat() * 2 - 1;
        }

        xVelocity = Math.max(-3, Math.min(3, xVelocity));
        yVelocity = Math.max(-3, Math.min(3, yVelocity));

        if (xPos < 0 || xPos > getWidth() - butterflyBitmap.getWidth()) {
            xVelocity = -xVelocity;
        }

        if (yPos < 0 || yPos > getHeight() - butterflyBitmap.getHeight()) {
            yVelocity = -yVelocity;
        }

        xPos = Math.max(0, Math.min(xPos, getWidth() - butterflyBitmap.getWidth()));
        yPos = Math.max(0, Math.min(yPos, getHeight() - butterflyBitmap.getHeight()));

        canvas.drawBitmap(butterflyBitmap, xPos, yPos, paint);

        invalidate();
    }
}
