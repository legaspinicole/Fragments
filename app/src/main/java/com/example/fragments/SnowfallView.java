package com.example.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SnowfallView extends View {

    private static final int NUM_SNOWFLAKES = 300;
    private final List<Snowflake> snowflakes = new ArrayList<>();

    public SnowfallView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0) {
            snowflakes.clear();
            for (int i = 0; i < NUM_SNOWFLAKES; i++) {
                Snowflake snowflake = new Snowflake();
                snowflake.reset(w, h, true);
                snowflakes.add(snowflake);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }

        for (Snowflake snowflake : snowflakes) {
            snowflake.draw(canvas);
            snowflake.update(getWidth(), getHeight());
        }

        invalidate(); // Redraw the view to animate
    }
}
