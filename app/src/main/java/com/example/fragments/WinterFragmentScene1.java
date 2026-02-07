package com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WinterFragmentScene1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the layout for the "2 MORE CHANCES" screen
        View view = inflater.inflate(R.layout.fragment_winter_scene1, container, false);

        // Find the path buttons
        View quiet = view.findViewById(R.id.btnQuietPath);
        View gentle = view.findViewById(R.id.btnGentlePath);
        View peaceful = view.findViewById(R.id.btnPeacefulPath);
        View calm = view.findViewById(R.id.btnCalmPath);

        // Listener to trigger transition to Scene 2
        View.OnClickListener pathListener = v -> navigateToNextScene();

        quiet.setOnClickListener(pathListener);
        gentle.setOnClickListener(pathListener);
        peaceful.setOnClickListener(pathListener);
        calm.setOnClickListener(pathListener);

        return view;
    }

    private void navigateToNextScene() {
        // Create the next fragment in the sequence
        WinterFragmentScene2 nextScene = new WinterFragmentScene2();

        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, nextScene)
                    .commit();
        }
    }
}