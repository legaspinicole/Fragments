package com.example.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PixelSceneView extends View {
    private Bitmap bgBitmap = null;
    private final Paint paint = new Paint();
    private final Random random = new Random();
    private int viewW, viewH;
    private int pixelSize;
    private long lastTimeMs;

    private final List<Butterfly> butterflies = new ArrayList<>();

    public PixelSceneView(Context context) { this(context, null); }
    public PixelSceneView(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public PixelSceneView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint.setAntiAlias(false);
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewW = w;
        viewH = h;
        pixelSize = Math.max(4, w / 80);
        try {
            Bitmap raw = BitmapFactory.decodeResource(getResources(), R.drawable.spring_bg);
            if (raw != null) {
                bgBitmap = Bitmap.createScaledBitmap(raw, w, h, true);
            }
        } catch (Exception e) { e.printStackTrace(); }
        initScene();
        lastTimeMs = SystemClock.elapsedRealtime();
        post(animationRunnable);
    }

    private void initScene() {
        butterflies.clear();
        for (int i = 0; i < 12; i++) {
            Butterfly b = new Butterfly();
            b.x = random.nextInt(viewW);
            b.y = viewH / 2f + random.nextInt(viewH / 3);
            b.speedX = 60f + random.nextFloat() * 80f;
            b.color = (i % 2 == 0) ? Color.YELLOW : Color.rgb(0, 255, 255);
            b.phase = random.nextFloat() * 6.28f;
            butterflies.add(b);
        }
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            postDelayed(this, 20);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long now = SystemClock.elapsedRealtime();
        float t = (now - lastTimeMs) / 1000f;
        lastTimeMs = now;

        if (bgBitmap != null) {
            canvas.drawBitmap(bgBitmap, 0, 0, null);
        }

        for (Butterfly b : butterflies) {
            b.x += b.speedX * t;
            float flutter = (float) Math.sin(now / 100.0 + b.phase) * 12f;
            float wave = (float) Math.sin(now / 1000.0 + b.phase) * 25f;
            if (b.x > viewW + 50) b.x = -50;
            drawButterfly(canvas, b.x, b.y + flutter + wave, b.color, now);
        }
    }

    private void drawButterfly(Canvas canvas, float x, float y, int color, long now) {
        paint.setColor(color);

        int wingSize = (Math.sin(now / 100.0) > 0) ? pixelSize : pixelSize / 2;
        canvas.drawRect(x, y, x + wingSize, y + pixelSize, paint);
        canvas.drawRect(x + wingSize + 2, y, x + wingSize * 2 + 2, y + pixelSize, paint);
    }

    private static class Butterfly { float x, y, speedX, phase; int color; }
}