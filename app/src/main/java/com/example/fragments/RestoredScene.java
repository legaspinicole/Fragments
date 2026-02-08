package com.example.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RestoredScene extends Fragment {

    public RestoredScene() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spring_restored, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEggAnimation(view);
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
}
