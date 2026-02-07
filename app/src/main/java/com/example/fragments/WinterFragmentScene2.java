package com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WinterFragmentScene2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the layout for the "1 MORE CHANCE" screen
        View view = inflater.inflate(R.layout.fragment_winter_scene2, container, false);

        // Path buttons
        View quiet = view.findViewById(R.id.btnQuietPath);
        View gentle = view.findViewById(R.id.btnGentlePath);
        View peaceful = view.findViewById(R.id.btnPeacefulPath);
        View calm = view.findViewById(R.id.btnCalmPath);

        View.OnClickListener pathListener = v -> navigateToFinalScene();

        quiet.setOnClickListener(pathListener);
        gentle.setOnClickListener(pathListener);
        peaceful.setOnClickListener(pathListener);
        calm.setOnClickListener(pathListener);

        return view;
    }

    private void navigateToFinalScene() {
        // Transition to the final panel: WinterFragmentScene3
        WinterFragmentScene3 finalScene = new WinterFragmentScene3();

        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, finalScene)
                    .commit();
        }
    }
}