package com.example.fragments;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SpringFragment extends DialogFragment {

    private Handler typingHandler = new Handler(Looper.getMainLooper());

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable android.os.Bundle savedInstanceState) {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(android.R.style.Animation);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable android.os.Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spring_choice, container, false);

        // Hide dialogue boxes initially
        View dogDialogue = view.findViewById(R.id.dogDialogueText);
        View catDialogue = view.findViewById(R.id.catDialogueText);
        View buttonLayout = view.findViewById(R.id.buttonLayout);

        if (dogDialogue != null) {
            dogDialogue.setAlpha(0f);
            dogDialogue.setScaleX(0f);
            dogDialogue.setScaleY(0f);
        }
        if (catDialogue != null) {
            catDialogue.setAlpha(0f);
            catDialogue.setScaleX(0f);
            catDialogue.setScaleY(0f);
        }
        if (buttonLayout != null) {
            buttonLayout.setVisibility(View.GONE);
        }

        // Start dialogue sequence
        startDialogueSequence(view);

        return view;
    }

    private void popInView(View v) {
        v.setVisibility(View.VISIBLE);
        v.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();
    }

    private void startDialogueSequence(View view) {
        View dogDialogue = view.findViewById(R.id.dogDialogueText);
        View catDialogue = view.findViewById(R.id.catDialogueText);
        View buttonLayout = view.findViewById(R.id.buttonLayout);
        TextView dogText = view.findViewById(R.id.dogDialogueText);
        TextView catText = view.findViewById(R.id.catDialogueText);

        // Step 1: Pop in first dialogue box
        typingHandler.postDelayed(() -> {
            if (dogDialogue != null) popInView(dogDialogue);
            if (dogText != null) dogText.setText("THIS PLACE NEEDS TIME TO GROW");
            
            // Step 2: Pop in second dialogue box
            typingHandler.postDelayed(() -> {
                if (catDialogue != null) popInView(catDialogue);
                if (catText != null) catText.setText("EVERYTHING FEELS SO QUIET HERE");
                
                // Step 3: Show buttons
                typingHandler.postDelayed(() -> {
                    if (buttonLayout != null) {
                        buttonLayout.setVisibility(View.VISIBLE);
                        buttonLayout.setAlpha(0f);
                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(buttonLayout, View.ALPHA, 0f, 1f);
                        fadeIn.setDuration(800);
                        fadeIn.start();
                    }
                }, 500);
            }, 700);
        }, 200);

        // Setup button listeners
        setupButtonListeners(view);
    }

    private void setupButtonListeners(View view) {
        android.widget.Button btn1 = view.findViewById(R.id.btnChoice1);
        android.widget.Button btn2 = view.findViewById(R.id.btnChoice2);
        android.widget.Button btn3 = view.findViewById(R.id.btnChoice3);

        View.OnClickListener listener = v -> showStillBloomScene();
        if (btn1 != null) btn1.setOnClickListener(listener);
        if (btn2 != null) btn2.setOnClickListener(listener);
        if (btn3 != null) btn3.setOnClickListener(listener);
    }

    public void showStillBloomScene() {
        if (getFragmentManager() != null) {
            StillBloomSceneFragment stillBloomSceneFragment = new StillBloomSceneFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, stillBloomSceneFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void showRestoredScene() {
        if (getFragmentManager() != null) {
            SpringRestoredFragment springRestoredFragment = new SpringRestoredFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, springRestoredFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void exitWithWhiteFlash() {
        View view = getView();
        if (view == null || !isAdded()) return;

        // Create white flash
        View flashView = new View(getContext());
        flashView.setBackgroundColor(0xFFFFFFFF);
        flashView.setAlpha(0f);
        ((ViewGroup) view).addView(flashView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Animate flash
        ObjectAnimator fadeInFlash = ObjectAnimator.ofFloat(flashView, View.ALPHA, 0f, 1f);
        fadeInFlash.setDuration(200);
        fadeInFlash.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Fade out and dismiss
                ObjectAnimator fadeOutFlash = ObjectAnimator.ofFloat(flashView, View.ALPHA, 1f, 0f);
                fadeOutFlash.setDuration(300);
                fadeOutFlash.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        if (isAdded()) {
                            dismiss();
                        }
                    }
                });
                fadeOutFlash.start();
            }
        });
        fadeInFlash.start();
    }
}
