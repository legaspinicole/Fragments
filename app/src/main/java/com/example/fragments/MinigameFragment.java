package com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MinigameFragment extends Fragment {

    private String season;

    public MinigameFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minigame, container, false);

        // Get season from arguments
        if (getArguments() != null) {
            season = getArguments().getString("season", "UNKNOWN");
        }

        TextView seasonText = view.findViewById(R.id.seasonText);
        seasonText.setText(season + " MINIGAME");

        Button completeButton = view.findViewById(R.id.completeButton);
        completeButton.setOnClickListener(v -> completeMinigame());

        return view;
    }

    private void completeMinigame() {
        // Mark spring as restored
        RestoreEarthFragment.markSeasonRestored(getContext(), "Spring");

        // Navigate back
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
