package com.example.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class StillBloomScene extends Fragment {

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private ImageView flowerImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates your fragment_spring activity.xml
        View view = inflater.inflate(R.layout.fragment_spring_activity, container, false);

        flowerImage = view.findViewById(R.id.flowerImage);

        // 1. Start the 30-second timer immediately upon entering the screen
        timerHandler.postDelayed(() -> {

            // This runs after 30 seconds of "quiet time"
            bloomFlowerSlowly();

        }, 5000); // 30,000 milliseconds = 30 seconds

        return view;
    }

    private void bloomFlowerSlowly() {
        if (flowerImage == null) return;

        // 1. Force the flower to be invisible and "flat" before anything else happens
        flowerImage.setAlpha(0f);
        flowerImage.setScaleY(0f);
        flowerImage.setVisibility(View.VISIBLE);

        // 2. Add a very tiny delay (200ms) to let the Fragment "settle"
        // This stops the 'flash' or 'jump' you are seeing.
        flowerImage.postDelayed(() -> {

            flowerImage.setPivotX(flowerImage.getWidth() / 2f);
            flowerImage.setPivotY(flowerImage.getHeight());

            flowerImage.animate()
                    .alpha(1f)
                    .scaleY(1f)
                    .scaleX(1f)
                    .translationY(0)
                    .setDuration(8000)
                    .withEndAction(() -> {
                        timerHandler.postDelayed(this::finishScene, 5000);
                    })
                    .start();

        }, 200);
    }


    private void finishScene() {
        if (getActivity() instanceof SpringFragment) {
            // Move to the final screen (Picture 4)
            ((SpringFragment) getActivity()).showRestoredScene();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop all timers if the user leaves the fragment to prevent memory leaks
        timerHandler.removeCallbacksAndMessages(null);
    }
}

