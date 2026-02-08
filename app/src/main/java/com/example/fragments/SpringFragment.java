package com.example.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class SpringFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.spring_activity);

        hideSystemBars();

        final View mainRoot = findViewById(R.id.main);

        if (mainRoot != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainRoot, (v, insets) -> {
                // By returning CONSUMED, we are telling the system that we've handled the insets.
                return WindowInsetsCompat.CONSUMED;
            });

            mainRoot.post(() -> setupClouds(mainRoot));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SpringChoiceFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemBars();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemBars();
        }
    }

    private void setupClouds(View view) {
        if (view == null) return;
        final ImageView clouds = (ImageView) view.findViewById(R.id.cloudsView);

        if (clouds != null) {
            clouds.setScaleX(1.5f);
            clouds.setScaleY(1.5f);
            clouds.setTranslationY(180f);

            clouds.post(() -> {
                float screenWidth = (float) view.getWidth();

                ObjectAnimator cloudAnim = ObjectAnimator.ofFloat(clouds, "translationX", -1000f, screenWidth + 500f);

                cloudAnim.setDuration(25000);
                cloudAnim.setRepeatCount(ValueAnimator.INFINITE);
                cloudAnim.setRepeatMode(ValueAnimator.RESTART);
                cloudAnim.setInterpolator(new LinearInterpolator());
                cloudAnim.start();
            });
        }
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Hide the system bars.
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        // Configure the behavior for showing the system bars.
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }


    public void showStillBloomScene() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, new StillBloomScene())
                .addToBackStack(null)
                .commit();
    }

    public void showRestoredScene() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, new RestoredScene())
                .addToBackStack(null)
                .commit();
    }

    public void navigateToEarthRestore() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, new RestoreEarthFragment()) // Use the correct fragment class
                .commit();
    }
}
