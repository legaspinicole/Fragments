package com.example.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

public class SpringRestoredFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spring_restored, container, false);

        ImageView eggImage = view.findViewById(R.id.egg_image);
        
        // Programmatically set the animation
        // AnimationDrawable eggAnimation = (AnimationDrawable) AppCompatResources.getDrawable(getContext(), R.drawable.egg_spin_animation);
        // eggImage.setImageDrawable(eggAnimation);
        //
        // if (eggAnimation != null) {
        //    eggAnimation.start();
        // }
        eggImage.setImageResource(R.drawable.egg1);

        view.setOnClickListener(v -> {
            if (getParentFragment() != null && getParentFragment() instanceof SpringFragment) {
                ((SpringFragment) getParentFragment()).onRestoredFragmentClicked();
            }
        });

        return view;
    }
}
