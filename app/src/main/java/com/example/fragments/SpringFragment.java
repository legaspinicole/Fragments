package com.example.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SpringFragment extends Fragment {

    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable typingRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spring_choice, container, false);

        // Hide dialogue boxes initially
        View dogDialogueContainer = view.findViewById(R.id.dogDialogueContainer);
        View catDialogueContainer = view.findViewById(R.id.catDialogueContainer);
        View buttonLayout = view.findViewById(R.id.buttonLayout);

        if (dogDialogueContainer != null) {
            dogDialogueContainer.setVisibility(View.INVISIBLE);
        }
        if (catDialogueContainer != null) {
            catDialogueContainer.setVisibility(View.INVISIBLE);
        }
        if (buttonLayout != null) {
            buttonLayout.setVisibility(View.INVISIBLE);
        }

        // Start dialogue sequence
        startDialogueSequence(view);

        return view;
    }

    private void typeText(TextView textView, String text, Runnable onComplete) {
        if (typingRunnable != null) {
            typingHandler.removeCallbacks(typingRunnable);
        }

        final int[] index = {0};
        typingRunnable = new Runnable() {
            @Override
            public void run() {
                if (index[0] <= text.length()) {
                    textView.setText(text.substring(0, index[0]));
                    index[0]++;
                    typingHandler.postDelayed(this, 50);
                } else {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        };
        typingHandler.post(typingRunnable);
    }


    private void startDialogueSequence(View view) {
        TextView dogText = view.findViewById(R.id.dogDialogueText);
        TextView catText = view.findViewById(R.id.catDialogueText);
        View dogDialogueContainer = view.findViewById(R.id.dogDialogueContainer);
        View catDialogueContainer = view.findViewById(R.id.catDialogueContainer);
        View buttonLayout = view.findViewById(R.id.buttonLayout);

        // Step 1: Show and type first dialogue
        typingHandler.postDelayed(() -> {
            if (dogDialogueContainer != null) dogDialogueContainer.setVisibility(View.VISIBLE);
            typeText(dogText, "THIS PLACE NEEDS TIME TO GROW", () -> {
                // Step 2: Show and type second dialogue
                typingHandler.postDelayed(() -> {
                    if (catDialogueContainer != null) catDialogueContainer.setVisibility(View.VISIBLE);
                    typeText(catText, "EVERYTHING FEELS SO QUIET HERE", () -> {
                        // Step 3: Show buttons
                        typingHandler.postDelayed(() -> {
                            if (buttonLayout != null) {
                                buttonLayout.setVisibility(View.VISIBLE);
                                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(buttonLayout, View.ALPHA, 0f, 1f);
                                fadeIn.setDuration(800);
                                fadeIn.start();
                            }
                        }, 500);
                    });
                }, 700);
            });
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
        View view = getView();
        if (view == null) return;

        // Hide the choice UI
        view.findViewById(R.id.winterTitle).setVisibility(View.GONE);
        view.findViewById(R.id.dialogue_layout).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLayout).setVisibility(View.GONE);

        // Show the bloom scene
        StillBloomScene stillBloomScene = new StillBloomScene();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, stillBloomScene)
                .addToBackStack(null)
                .commit();
    }

    public void showRestoredScene() {
        SpringRestoredFragment springRestoredFragment = new SpringRestoredFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, springRestoredFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onRestoredFragmentClicked() {
        if (isAdded() && getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (typingRunnable != null) {
            typingHandler.removeCallbacks(typingRunnable);
        }
    }
}
