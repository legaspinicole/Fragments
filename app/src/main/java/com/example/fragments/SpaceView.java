package com.example.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

/**
 * SpaceView - draws a pixel-style starfield with twinkling and occasional shooting stars.
 * Optimized to avoid allocations on the draw path and to be lifecycle-friendly.
 */
public class SpaceView extends View implements Choreographer.FrameCallback {

    private final Paint starPaint = new Paint();
    private final Paint shootingPaint = new Paint();

    private final ArrayList<Star> stars = new ArrayList<>();
    private final ShootingStar[] shootingPool;

    private final Random random = new Random();

    private boolean running = false;
    private long lastFrameTime = 0L;

    // tuned params
    private int starCount = 160; // baseline, scaled in init
    private final float density;
    private boolean pixelStars = true; // if true draw 1px rectangles, else smooth circles
    // size of pixel block in screen pixels when pixelStars=true; default 2 for visibility on high density screens
    private int pixelSize = 2;
    private float shootingFreqPerSecond = 0.12f; // approx 1 every ~8s by default
    // direction bias: -1.0 = left-to-right only, 0 = balanced, +1.0 = right-to-left only
    private float shootingDirectionBias = 0f;

    // gesture/interaction
    private final GestureDetector gestureDetector;
    private boolean longPressActive = false;
    private float attractorX = 0f, attractorY = 0f; // for follow-on-long-press
    private float fingerPrevX = 0f, fingerPrevY = 0f;
    private float fingerVelX = 0f, fingerVelY = 0f; // px per ms
    private long lastTouchTime = 0L;

    // shooting stars

    public SpaceView(Context context) {
        this(context, null);
    }

    public SpaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        starPaint.setStyle(Paint.Style.FILL);
        starPaint.setAntiAlias(false); // pixel feel
        starPaint.setColor(0xFFFFFFFF);

        shootingPaint.setStyle(Paint.Style.STROKE);
        shootingPaint.setAntiAlias(true);
        shootingPaint.setStrokeCap(Paint.Cap.ROUND);
        shootingPaint.setColor(0xFFFFFFFF);

        shootingPool = new ShootingStar[3];
        for (int i = 0; i < shootingPool.length; i++) shootingPool[i] = new ShootingStar();

        // density scale by display density
        density = context.getResources().getDisplayMetrics().density;
        starCount = (int) (starCount * density);
        // initialize pixel size scaled by density so pixels are visible on high-DPI displays
        pixelSize = Math.max(1, (int) (2f * density));

        // Gesture detector for taps, double taps, long presses
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                rippleAt(e.getX(), e.getY());
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // spawn a handful of shooting stars on double-tap
                int spawn = 3;
                int w = getWidth();
                int h = getHeight();
                for (int i = 0; i < spawn; i++) spawnShootingStar(w, h);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // begin attractor/follow mode
                longPressActive = true;
                attractorX = e.getX();
                attractorY = e.getY();
                fingerPrevX = attractorX;
                fingerPrevY = attractorY;
                lastTouchTime = SystemClock.uptimeMillis();
            }
        });
        gestureDetector.setIsLongpressEnabled(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // start if view is visible
        if (getVisibility() == VISIBLE) start();
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) start(); else stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // feed gestures
        gestureDetector.onTouchEvent(event);

        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        long now = SystemClock.uptimeMillis();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                fingerPrevX = x; fingerPrevY = y; lastTouchTime = now; break;
            case MotionEvent.ACTION_MOVE:
                if (longPressActive) {
                    // update attractor and finger velocity
                    long dt = Math.max(1L, now - lastTouchTime);
                    float dx = x - fingerPrevX;
                    float dy = y - fingerPrevY;
                    fingerVelX = dx / (float) dt; // px per ms
                    fingerVelY = dy / (float) dt;
                    attractorX = x; attractorY = y;
                    fingerPrevX = x; fingerPrevY = y; lastTouchTime = now;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // end long press mode
                longPressActive = false;
                fingerVelX = 0f; fingerVelY = 0f;
                break;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void ensureStars(int width, int height) {
        if (!stars.isEmpty()) return;
        int w = Math.max(1, width);
        int h = Math.max(1, height);
        for (int i = 0; i < starCount; i++) {
            Star s = new Star();
            s.x = random.nextFloat() * w;
            s.y = random.nextFloat() * h;
            s.vx = 0f;
            // pixel-like radius between 0.4 and 2 dp
            float rdp = 0.4f + random.nextFloat() * 2.0f;
            s.radius = rdp * density;
            s.baseAlpha = 0.3f + random.nextFloat() * 0.7f;
            // twinkle period 500..4000ms
            float period = 500f + random.nextFloat() * 3500f;
            s.twinkleSpeed = (float) (2 * Math.PI / period);
            s.twinklePhase = random.nextFloat() * (float) (2 * Math.PI);
            // subtle vertical drift
            s.vy = (random.nextFloat() - 0.5f) * 6f * density / 1000f; // px per ms
            // pixel lifetime jitter used for small flickers when pixel mode
            s.pixelOffset = random.nextInt(2);
            stars.add(s);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        stars.clear();
        ensureStars(w, h);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // draw stars
        if (pixelStars) {
            // draw as small rectangles for pixelated look
            for (int i = 0, n = stars.size(); i < n; i++) {
                Star s = stars.get(i);
                int alpha = (int) (255 * Math.max(0f, Math.min(1f, s.baseAlpha * (0.6f + 0.4f * (float) Math.sin(s.twinklePhase)))));
                starPaint.setAlpha(alpha);
                // draw a small rectangle; round coords to achieve pixel look
                float cx = Math.round(s.x);
                float cy = Math.round(s.y);
                canvas.drawRect(cx, cy, cx + pixelSize, cy + pixelSize, starPaint);
            }
        } else {
            for (int i = 0, n = stars.size(); i < n; i++) {
                Star s = stars.get(i);
                int alpha = (int) (255 * Math.max(0f, Math.min(1f, s.baseAlpha * (0.6f + 0.4f * (float) Math.sin(s.twinklePhase)))));
                starPaint.setAlpha(alpha);
                canvas.drawCircle(s.x, s.y, s.radius, starPaint);
            }
        }
        // draw shooting stars
        for (ShootingStar ss : shootingPool) {
            if (!ss.active) continue;
            float progress = ss.elapsedMs / (float) ss.lifeMs;
            int a = (int) (255 * (1f - progress));
            shootingPaint.setAlpha(a);
            shootingPaint.setStrokeWidth(ss.width);
            float endX = ss.x - ss.vx * ss.length;
            float endY = ss.y - ss.vy * ss.length;
            canvas.drawLine(ss.x, ss.y, endX, endY, shootingPaint);
        }
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (!running) return;
        long now = SystemClock.uptimeMillis();
        if (lastFrameTime == 0) lastFrameTime = now;
        long dt = now - lastFrameTime;
        lastFrameTime = now;

        // update stars
        int w = getWidth();
        int h = getHeight();
        for (int i = 0, n = stars.size(); i < n; i++) {
            Star s = stars.get(i);
            s.twinklePhase += s.twinkleSpeed * dt;
            // apply attractor pull when long-press active
            if (longPressActive) {
                float dx = attractorX - s.x;
                float dy = attractorY - s.y;
                float dist = (float) Math.hypot(dx, dy) + 0.001f;
                float pull = 0.06f; // tuned pull strength
                s.vx += (dx / dist) * pull * (dt / 16f);
                s.vy += (dy / dist) * pull * (dt / 16f);
                // also nudge with finger velocity so stars appear to follow
                s.vx += fingerVelX * 0.15f;
                s.vy += fingerVelY * 0.15f;
            }

            // basic movement update
            s.x += s.vx * dt;
            s.y += s.vy * dt;

            // subtle baseline vertical drift if zeroed
            if (Math.abs(s.vx) < 0.01f && Math.abs(s.vy) < 0.01f) {
                s.y += s.vy * dt;
            }

            // wrap around edges
            if (s.x < -10) s.x = w + 10;
            else if (s.x > w + 10) s.x = -10;
            if (s.y < -10) s.y = h + 10;
            else if (s.y > h + 10) s.y = -10;

            // damp velocities slowly
            s.vx *= 0.995f;
            s.vy *= 0.995f;
        }

        // shooting stars update: use probability per second (Poisson) to spawn
        if (shootingFreqPerSecond > 0f) {
            float probThisFrame = 1f - (float) Math.exp(-shootingFreqPerSecond * (dt / 1000f));
            if (random.nextFloat() < probThisFrame) {
                spawnShootingStar(w, h);
            }
        }

        for (ShootingStar ss : shootingPool) {
            if (!ss.active) continue;
            ss.elapsedMs += dt;
            ss.x += ss.vx * dt;
            ss.y += ss.vy * dt;
            if (ss.elapsedMs >= ss.lifeMs) ss.active = false;
        }

        postInvalidateOnAnimation();
        Choreographer.getInstance().postFrameCallback(this);
    }

    // ripple outward from a tap point: push stars away like a water ripple
    private void rippleAt(float x, float y) {
        float maxDist = Math.max(300f, Math.max(getWidth(), getHeight()) * 0.6f);
        float strength = 0.6f * density; // tuned
        for (Star s : stars) {
            float dx = s.x - x;
            float dy = s.y - y;
            float d = (float) Math.hypot(dx, dy);
            if (d < maxDist) {
                float norm = (d <= 0.1f) ? 1f : (1f - (d / maxDist));
                float push = strength * norm;
                s.vx += (dx / (d + 0.001f)) * push;
                s.vy += (dy / (d + 0.001f)) * push;
            }
        }
    }

    /**
     * Set whether stars should be drawn as 1px pixel blocks (true) or smooth circles (false).
     */
    public void setPixelStars(boolean pixel) {
        this.pixelStars = pixel;
        invalidate();
    }

    /** Set pixel block size in screen pixels for pixel-star mode (>=1). */
    public void setPixelSize(int px) {
        this.pixelSize = Math.max(1, px);
        invalidate();
    }

    /**
     * Set expected shooting stars frequency in events per second (e.g., 0.1 = one every 10s on average).
     */
    public void setShootingFrequency(float perSecond) {
        this.shootingFreqPerSecond = Math.max(0f, perSecond);
    }

    /**
     * Set direction bias for shooting stars: -1 = left-to-right, 0 = balanced, +1 = right-to-left.
     */
    public void setShootingDirectionBias(float bias) {
        this.shootingDirectionBias = Math.max(-1f, Math.min(1f, bias));
    }

    private void spawnShootingStar(int w, int h) {
        // find an inactive slot
        ShootingStar slot = null;
        for (ShootingStar s : shootingPool) if (!s.active) { slot = s; break; }
        if (slot == null) return;
        // start from a random point on top or left/right edge heading diagonally
        boolean fromTop = random.nextBoolean();
        float startX, startY;
        float vx, vy;
        if (fromTop) {
            startX = random.nextFloat() * w;
            startY = -10f;
            // bias angle based on shootingDirectionBias
            float base = 120f;
            float deviation = random.nextFloat() * 60f - 30f; // -30..+30
            // apply bias: positive bias nudges angle toward right-down (smaller angle), negative to left-down (larger angle)
            float biasAdj = -shootingDirectionBias * 20f; // scale bias
            float angle = (float) Math.toRadians(base + deviation + biasAdj);
            float speed = 1200f + random.nextFloat() * 2200f; // px per second
            vx = (float) Math.cos(angle) * speed / 1000f; // px per ms
            vy = (float) Math.sin(angle) * speed / 1000f;
        } else {
            // allow start from left or right depending on bias
            boolean fromLeft = random.nextFloat() < (0.5f - 0.5f * shootingDirectionBias);
            if (fromLeft) startX = -10f; else startX = w + 10f;
            startY = random.nextFloat() * h;
            float base = fromLeft ? 30f : 150f; // right-down or left-down
            float deviation = random.nextFloat() * 60f;
            float angle = (float) Math.toRadians(base + deviation);
            float speed = 1400f + random.nextFloat() * 2600f;
            vx = (float) Math.cos(angle) * speed / 1000f;
            vy = (float) Math.sin(angle) * speed / 1000f;
        }
        slot.active = true;
        slot.x = startX;
        slot.y = startY;
        slot.vx = vx;
        slot.vy = vy;
        slot.length = 80f + random.nextFloat() * 220f;
        slot.lifeMs = 300 + random.nextInt(700);
        slot.elapsedMs = 0L;
        slot.width = 2f + random.nextFloat() * 3f;
    }

    public void start() {
        if (running) return;
        running = true;
        lastFrameTime = 0L;
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void stop() {
        if (!running) return;
        running = false;
        Choreographer.getInstance().removeFrameCallback(this);
        lastFrameTime = 0L;
    }

    private static class Star {
        float x, y;
        float vx, vy;
        float radius;
        float baseAlpha;
        float twinklePhase;
        float twinkleSpeed;
        int pixelOffset; // for lifetime jitter in pixel mode
    }

    private static class ShootingStar {
        boolean active = false;
        float x, y;
        float vx, vy; // px per ms
        float length;
        int lifeMs;
        long elapsedMs;
        float width;
    }
}
