package com.example.fragments;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WinterFragmentRestored extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_winter_restored, container, false);

        ImageView winterIcon = view.findViewById(R.id.winterIcon);
        View dialogContainer = view.findViewById(R.id.dialogContainer);

        // Setup pop-in animation for dialogue box
        setupRestoredAnimation(dialogContainer, winterIcon);

        // Setup click listener for transition
        view.setOnClickListener(v -> transitionToRestoreEarth(view));

        return view;
    }

    private void setupRestoredAnimation(View dialogContainer, ImageView winterIcon) {
        if (dialogContainer != null) {
            // Wait for layout to complete, then set pivot and animate
            dialogContainer.post(() -> {
                // Set pivot to center of the view
                dialogContainer.setPivotX(dialogContainer.getWidth() / 2f);
                dialogContainer.setPivotY(dialogContainer.getHeight() / 2f);
                
                // Set initial state
                dialogContainer.setScaleX(0f);
                dialogContainer.setScaleY(0f);
                dialogContainer.setAlpha(0f);

                // Animate
                dialogContainer.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(600)
                        .setStartDelay(100)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
            });
        }

        // Animate the winter icon with rotation
        if (winterIcon != null) {
            float scale = getResources().getDisplayMetrics().density;
            winterIcon.setCameraDistance(8000 * scale);
            ObjectAnimator animator = ObjectAnimator.ofFloat(winterIcon, "rotationY", 0f, 360f);
            animator.setDuration(6000);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.start();
        }
    }

    private void transitionToRestoreEarth(View view) {
        // Play sparkle sound
        playSparkleSfx();

        // Mark winter as restored
        RestoreEarthFragment.markSeasonRestored(getContext(), "Winter");

        // Create white flash overlay
        View whiteFlash = new View(getContext());
        whiteFlash.setBackgroundColor(0xFFFFFFFF);
        whiteFlash.setAlpha(0f);
        ((ViewGroup) view).addView(whiteFlash, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Fade in white flash and fade out view simultaneously
        whiteFlash.animate()
                .alpha(1f)
                .setDuration(350)
                .start();

        view.animate()
                .alpha(0f)
                .setDuration(350)
                .withEndAction(() -> {
                    if (getActivity() != null) {
                        androidx.fragment.app.FragmentTransaction transaction = getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();
                        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        transaction.replace(R.id.fragment_container, new RestoreEarthFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                })
                .start();
    }

    private void playSparkleSfx() {
        try {
            MediaPlayer sparklePlayer = MediaPlayer.create(getContext(), R.raw.sfx_sparkle);
            if (sparklePlayer != null) {
                sparklePlayer.setVolume(0.4f, 0.4f);
                sparklePlayer.setOnCompletionListener(MediaPlayer::release);
                sparklePlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}