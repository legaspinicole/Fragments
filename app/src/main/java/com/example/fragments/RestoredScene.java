package com.example.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

public class RestoredScene extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spring_restored, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupEggAnimation(view);

        View restoredDialog = view.findViewById(R.id.restoredDialog);
        if (restoredDialog != null) {
            restoredDialog.setScaleX(0f);
            restoredDialog.setScaleY(0f);
            restoredDialog.setAlpha(0f);
            popInView(restoredDialog, 1.0f);
        }

        // Add the click listener to the root view
        view.setOnClickListener(v -> {
            RestoreEarthFragment.markSeasonRestored(getContext(), "Spring");
            if (getActivity() instanceof SpringFragment) {
                ((SpringFragment) getActivity()).navigateToEarthRestore();
            }
        });
    }

    private void setupEggAnimation(View view) {
        ImageView egg = view.findViewById(R.id.eggImage);
        if (egg != null) {
            ObjectAnimator rotate = ObjectAnimator.ofFloat(egg, "rotationY", 0f, 360f);
            rotate.setDuration(6000);
            rotate.setRepeatCount(ValueAnimator.INFINITE);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.start();
        }
    }

    private void popInView(View v, float targetScaleX) {
        v.setVisibility(View.VISIBLE);
        v.animate()
                .alpha(1f)
                .scaleX(targetScaleX)
                .scaleY(1f)
                .setDuration(600)
                .setStartDelay(100)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }
}
