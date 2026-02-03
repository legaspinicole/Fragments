package com.example.fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Snowflake {

    private float x, y;
    private float velocityX, velocityY;
    private int radius;
    private Paint paint;
    private Random random = new Random();
    private double angle;
    private double angleIncrement;


    public Snowflake() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    public void reset(int width, int height) {
        reset(width, height, false);
    }

    public void reset(int width, int height, boolean randomizeY) {
        x = random.nextInt(width);
        if (randomizeY) {
            y = random.nextInt(height);
        } else {
            y = -radius;
        }
        velocityY = 2 + random.nextFloat() * 3; // Speed of falling
        velocityX = -1 + random.nextFloat() * 2; // Horizontal speed for wind effect
        radius = 5 + random.nextInt(5); // Size of snowflake
        angle = random.nextDouble() * 2 * Math.PI;
        angleIncrement = (random.nextDouble() - 0.5) * 0.1;
        paint.setColor(Color.argb(150 + random.nextInt(105), 255, 255, 255));
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public void update(int width, int height) {
        y += velocityY;
        x += velocityX + (float) (Math.sin(angle) * 1.5);
        angle += angleIncrement;


        if (y > height + radius) {
            reset(width, height);
        }

        // Wrap around horizontally
        if (x > width + radius) {
            x = -radius;
        } else if (x < -radius) {
            x = width + radius;
        }
    }
}
