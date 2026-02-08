package com.example.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

public class StayHereFragment extends Fragment {

    private SpaceView spaceView;
    private ImageView earthView;
    private ObjectAnimator earthAnimator;

    // 1 = normal clockwise, -1 = reverse
    private int earthDirection = 1;
    private boolean earthLongPressActive = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stayhere, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Make the activity fullscreen
        if (getActivity() != null) {
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView());
            if (windowInsetsController != null) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
                windowInsetsController.setSystemBarsBehavior(
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }

        spaceView = view.findViewById(R.id.spaceView);
        earthView = view.findViewById(R.id.earthView);

        // Example visual tuning - change these to adjust appearance
        // Pixel stars: true = 1px blocks, false = smooth circles
        if (spaceView != null) {
            spaceView.setPixelStars(true);
            // frequency in events per second (0.1 = one every 10s on average)
            spaceView.setShootingFrequency(0.12f);
            // direction bias: -1 = left-to-right, 0 = balanced, +1 = right-to-left
            spaceView.setShootingDirectionBias(0f);
            // make pixel stars larger so they are clearly visible (in screen pixels)
            spaceView.setPixelSize(4);
        }

        // prepare earth rotation animator (360 degrees every 24 seconds)
        setupEarthAnimator(earthDirection);

        // gestures on earth: single tap -> reverse rotation, long press -> pause while held
        final GestureDetector earthGesture = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleEarthDirection();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // pause while long pressing
                earthLongPressActive = true;
                if (earthAnimator != null && earthAnimator.isRunning()) {
                    earthAnimator.pause();
                }
            }
        });

        // Attach touch listener to earthView so we can detect ACTION_UP to resume after long press
        earthView.setOnTouchListener((v, event) -> {
            earthGesture.onTouchEvent(event);
            if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                if (earthLongPressActive) {
                    earthLongPressActive = false;
                    // resume animation
                    if (earthAnimator != null) {
                        earthAnimator.resume();
                    }
                }
            }
            // return true to consume the touch so SpaceView doesn't receive taps on top of Earth
            return true;
        });
    }

    private void setupEarthAnimator(int direction) {
        if (earthView == null) return;
        // cancel any existing animator
        if (earthAnimator != null) {
            try { earthAnimator.cancel(); } catch (Exception ignored) {}
            earthAnimator = null;
        }
        // create animator that rotates from current rotation to current + 360*direction and repeats
        float start = earthView.getRotation();
        float end = start + 360f * direction;
        earthAnimator = ObjectAnimator.ofFloat(earthView, "rotation", start, end);
        earthAnimator.setDuration(24_000);
        earthAnimator.setInterpolator(new LinearInterpolator());
        earthAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        earthAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    private void toggleEarthDirection() {
        // invert direction and restart animator preserving current rotation
        earthDirection = -earthDirection;
        // compute current rotation to avoid jumps
        float current = earthView.getRotation();
        if (earthAnimator != null) {
            try { earthAnimator.cancel(); } catch (Exception ignored) {}
        }
        float end = current + 360f * earthDirection;
        earthAnimator = ObjectAnimator.ofFloat(earthView, "rotation", current, end);
        earthAnimator.setDuration(24_000);
        earthAnimator.setInterpolator(new LinearInterpolator());
        earthAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        earthAnimator.setRepeatMode(ObjectAnimator.RESTART);
        earthAnimator.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (spaceView != null) spaceView.start();
        if (earthAnimator != null) {
            // restart from current position
            earthAnimator.start();
        }
    }

    @Override
    public void onPause() {
        if (spaceView != null) spaceView.stop();
        if (earthAnimator != null) earthAnimator.pause();
        super.onPause();
    }
}
