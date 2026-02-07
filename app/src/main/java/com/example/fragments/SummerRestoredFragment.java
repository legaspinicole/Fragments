package com.example.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SummerRestoredFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restored, container, false);

        // 1. Setup Animations
        setupClouds(view);
        // This method handles the sun rotation (Coin style)
        setupSunAnimation(view);

        // 2. Animate the Dialog Box Entry (Pop-up effect)
        View dialogContainer = view.findViewById(R.id.dialogContainer);
        if (dialogContainer != null) {
            dialogContainer.setScaleX(0f);
            dialogContainer.setScaleY(0f);
            dialogContainer.setAlpha(0f);

            dialogContainer.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(600)
                    .setStartDelay(100)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }

        // 3. Click anywhere to close with smooth fade transition
        view.setOnClickListener(v -> {
            if (dialogContainer != null) {
                dialogContainer.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .alpha(0f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator(0.5f))
                        .withEndAction(() -> {
                            dismiss();
                            // Also dismiss the underlying SummerGameFragment
                            androidx.fragment.app.Fragment gameFragment = getParentFragmentManager().findFragmentByTag("SummerGame");
                            if (gameFragment instanceof SummerGameFragment) {
                                ((SummerGameFragment) gameFragment).dismiss();
                            }
                        })
                        .start();
            }
        });

        return view;
    }

    /**
     * UPDATED: Rotates the Sun on the Y-AXIS (Flipping like the coin).
     */
    private void setupSunAnimation(View view) {
        ImageView sun = view.findViewById(R.id.iconSun);

        if (sun != null) {
            // CHANGED: "rotationY" makes it flip like a coin.
            // (Use "rotation" if you want it to spin like a wheel).
            ObjectAnimator rotate = ObjectAnimator.ofFloat(sun, "rotationY", 0f, 360f);

            // Duration: 6 seconds for a nice steady flip
            rotate.setDuration(6000);

            rotate.setRepeatCount(ValueAnimator.INFINITE);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.start();
        }
    }

    private void setupClouds(View view) {
        View clouds = view.findViewById(R.id.cloudsView);
        if (clouds != null) {
            clouds.setScaleX(1.2f);
            clouds.setScaleY(1.2f);

            // Move clouds high up
            clouds.setTranslationY(-500f);

            ObjectAnimator cloudAnim = ObjectAnimator.ofFloat(clouds, "translationX", -40f, 40f);
            cloudAnim.setDuration(20000);
            cloudAnim.setRepeatCount(ValueAnimator.INFINITE);
            cloudAnim.setRepeatMode(ValueAnimator.REVERSE);
            cloudAnim.setInterpolator(new LinearInterpolator());
            cloudAnim.start();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            // Use BLACK background to hide the underlying RestoreEarth fragment
            window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            
            // Hide system bars (status bar and navigation bar)
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            
            // Make sure the window takes full screen
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
}