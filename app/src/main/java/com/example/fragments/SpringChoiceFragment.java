package com.example.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

public class SpringChoiceFragment extends Fragment {

    private final Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable typingRunnable;
    private MediaPlayer typingPlayer;
    private ImageView petDog, petCat;

    public interface OnTypeFinishListener {
        void onFinished();
    }

    public SpringChoiceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spring_choice, container, false);

        // Configure the window to not fit system windows
        if (getActivity() != null) {
            WindowCompat.setDecorFitsSystemWindows(getActivity().getWindow(), false);
        }


        TextView dogDialogue = view.findViewById(R.id.dogDialogueText);
        TextView catDialogue = view.findViewById(R.id.catDialogueText);
        View choicesLayout = view.findViewById(R.id.buttonLayout);
        petDog = view.findViewById(R.id.dogImage);
        petCat = view.findViewById(R.id.catImage);

        // Set pets to invisible initially
        if (petDog != null) petDog.setAlpha(0f);
        if (petCat != null) petCat.setAlpha(0f);


        View choice1 = view.findViewById(R.id.btnChoice1);
        View choice2 = view.findViewById(R.id.btnChoice2);
        View choice3 = view.findViewById(R.id.btnChoice3);

// Create a shared click listener
        View.OnClickListener choiceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This function will trigger the transition to Picture 2
                navigateToPictureTwo();
            }
        };

// Assign the listener to your buttons
        choice1.setOnClickListener(choiceClickListener);
        choice2.setOnClickListener(choiceClickListener);
        choice3.setOnClickListener(choiceClickListener);

        catDialogue.setVisibility(View.INVISIBLE);
        choicesLayout.setVisibility(View.INVISIBLE);
        // Fade in dog at the same time as typing starts
        if (petDog != null) {
            petDog.animate().alpha(1f).setDuration(600).start();
            animatePet(petDog);
        }
        typeText(dogDialogue, "“THIS PLACE NEEDS TIME TO GROW.”", () -> {
            typingHandler.postDelayed(() -> {
                // Fade in cat at the same time as dialogue appears
                if (petCat != null) {
                    petCat.animate().alpha(1f).setDuration(600).start();
                    animatePet(petCat);
                }
                catDialogue.setVisibility(View.VISIBLE);
                typeText(catDialogue, "”EVERYTHING FEELS SO QUIET HERE...”", () -> {
                    typingHandler.postDelayed(() -> {
                        choicesLayout.setVisibility(View.VISIBLE);
                        choicesLayout.setAlpha(0f);
                        choicesLayout.animate().alpha(1f).setDuration(800).start();
                    }, 1000);
                });
            }, 1500);
        });
        return view;
    }

    private void animatePet(View view) {
        if (view == null) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -20f);
        animator.setDuration(1500);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    private void navigateToPictureTwo() {
        if (getActivity() != null && getActivity() instanceof SpringFragment) {
            // We call a method in SpringFragment to handle the fragment swap
            ((SpringFragment) getActivity()).showStillBloomScene();
        }
    }
    private void typeText(TextView target, String text, OnTypeFinishListener listener) {
        if (typingRunnable != null) {
            typingHandler.removeCallbacks(typingRunnable);
        }
        stopTypingSfx();

        target.setText("");
        final int[] index = {0};
        startTypingSfx();

        typingRunnable = new Runnable() {
            @Override
            public void run() {
                if (index[0] <= text.length()) {
                    target.setText(text.substring(0, index[0]));
                    index[0]++;
                    typingHandler.postDelayed(this, 50);
                } else {
                    stopTypingSfx();
                    if (listener != null) {
                        listener.onFinished();
                    }
                }
            }
        };
        typingHandler.post(typingRunnable);
    }

    // 6. AUDIO HELPER METHODS
    private void startTypingSfx() {
        if (getContext() == null) return;
        stopTypingSfx();
        // Make sure you have sfx_typing.mp3 in your res/raw folder!
        typingPlayer = MediaPlayer.create(getContext(), R.raw.sfx_typing);
        if (typingPlayer != null) {
            typingPlayer.setLooping(true);
            typingPlayer.setVolume(0.3f, 0.3f);
            typingPlayer.start();
        }
    }

    private void stopTypingSfx() {
        if (typingPlayer != null) {
            if (typingPlayer.isPlaying()) {
                typingPlayer.stop();
            }
            typingPlayer.release();
            typingPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (typingRunnable != null) {
            typingHandler.removeCallbacks(typingRunnable);
        }
        stopTypingSfx();
    }
}

