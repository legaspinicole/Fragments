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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SpringChoiceFragment extends Fragment {

    private final Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable typingRunnable;
    private MediaPlayer typingPlayer;

    public interface OnTypeFinishListener {
        void onFinished();
    }

    public SpringChoiceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spring_choice, container, false);


        TextView dogDialogue = view.findViewById(R.id.dogDialogueText);
        TextView catDialogue = view.findViewById(R.id.catDialogueText);
        View choicesLayout = view.findViewById(R.id.buttonLayout);


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

        typeText(dogDialogue, "“THIS PLACE NEEDS TIME TO GROW.”", () -> {
            typingHandler.postDelayed(() -> {

                catDialogue.setVisibility(View.VISIBLE);
                typeText(catDialogue, "“EVERYTHING FEELS SO QUIET HERE...”", () -> {
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

    private void startFloatingAnimation(View view, int delay, int duration) {
        if (view == null) return;

        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(view, "translationY", 0, -20f, 0f);
        floatAnim.setDuration(duration);
        floatAnim.setStartDelay(delay);
        floatAnim.setRepeatCount(ValueAnimator.INFINITE);
        floatAnim.setRepeatMode(ValueAnimator.REVERSE);
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnim.start();
    }

    private void navigateToPictureTwo() {
        if (getParentFragment() != null && getParentFragment() instanceof SpringFragment) {
            // We call a method in SpringFragment to handle the fragment swap
            ((SpringFragment) getParentFragment()).showStillBloomScene();
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

