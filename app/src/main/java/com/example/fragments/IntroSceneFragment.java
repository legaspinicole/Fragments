package com.example.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class IntroSceneFragment extends Fragment {

    private TextView dialogueText;
    private TextView speakerName;
    private int currentStep = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_scene, container, false);

        ImageView dogImage = view.findViewById(R.id.dogImage);
        ImageView catImage = view.findViewById(R.id.catImage);
        dialogueText = view.findViewById(R.id.dialogueText);
        speakerName = view.findViewById(R.id.speakerName);

        dogImage.setImageResource(R.drawable.pet_dog);
        catImage.setImageResource(R.drawable.pet_cat);

        view.setOnClickListener(v -> advanceDialogue());

        return view;
    }

    private void advanceDialogue() {
        playTapSfx();
        switch (currentStep) {
            case 0:
                dialogueText.setText("\"THERE'S NO RUSH, JUST STAY WITH US.\"");
                speakerName.setText("- LUMA");
                speakerName.setTextColor(0xFF90EE90);
                currentStep++;
                break;
            case 1:
                dialogueText.setText("\"THE WORLD OUT THERE CAN WAIT A LITTLE LONGER.\"");
                speakerName.setText("- SOL");
                speakerName.setTextColor(0xFF87CEFA);
                currentStep++;
                break;
            case 2:
                dialogueText.setText("\"WHEN YOU'RE READY... WE'LL LET YOU GO.\"");
                speakerName.setText("- BOTH");
                speakerName.setTextColor(0xFFFFFFFF);
                currentStep++;
                break;
            case 3:
                goToWinter();
                break;
        }
    }

    private void goToWinter() {
        WinterFragment winterFragment = new WinterFragment();
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, winterFragment)
                .commit();
    }

    private void playTapSfx() {
        MediaPlayer sfxPlayer = MediaPlayer.create(getContext(), R.raw.sfx_tap_to_start);
        if (sfxPlayer != null) {
            sfxPlayer.setVolume(0.05f, 0.05f);
            sfxPlayer.setOnCompletionListener(MediaPlayer::release);
            sfxPlayer.start();
        }
    }
}