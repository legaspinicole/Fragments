package com.example.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
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

        if (winterIcon != null) {
            startTokenRotation(winterIcon);
        }

        return view;
    }

    private void startTokenRotation(View view) {
        // "rotationY" creates the 3D spinning effect
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f);
        animator.setDuration(3000); // 3 seconds per spin
        animator.setInterpolator(new LinearInterpolator()); // Consistent speed
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat forever
        animator.start();
    }
}