package com.example.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import androidx.fragment.app.Fragment;

public class EarthSceneFragment extends Fragment {

    private ImageView earthImage;
    private View dialogueContainer;
    private ObjectAnimator rotationAnimator;
    private TextView dialogueText;
    private TextView speakerName;
    private View questionContainer;
    private TextView questionText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earth_scene, container, false);

        earthImage = view.findViewById(R.id.earthImage);
        dialogueContainer = view.findViewById(R.id.dialogueContainer);
        dialogueText = view.findViewById(R.id.dialogueText);
        speakerName = view.findViewById(R.id.speakerName);
        questionContainer = view.findViewById(R.id.questionContainer);
        questionText = view.findViewById(R.id.questionText);

        // Start the animation sequence
        startAnimationSequence(view);

        return view;
    }

    private void startAnimationSequence(View view) {
        // 1. Flash white
        View flashView = new View(getContext());
        flashView.setBackgroundColor(0xFFFFFFFF);
        flashView.setAlpha(1f);
        ((ViewGroup) view).addView(flashView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        flashView.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ((ViewGroup) view).removeView(flashView);
                // 2. Earth zoom in (without rotation initially)
                earthImage.setVisibility(View.VISIBLE);
                AnimatorSet earthIntro = new AnimatorSet();
                ObjectAnimator zoomX = ObjectAnimator.ofFloat(earthImage, "scaleX", 0f, 1f);
                ObjectAnimator zoomY = ObjectAnimator.ofFloat(earthImage, "scaleY", 0f, 1f);

                earthIntro.playTogether(zoomX, zoomY);
                earthIntro.setDuration(2500);
                earthIntro.setInterpolator(new AccelerateDecelerateInterpolator());
                earthIntro.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Start rotation after zoom completes
                        rotationAnimator = ObjectAnimator.ofFloat(earthImage, "rotation", 0f, 360f);
                        rotationAnimator.setInterpolator(new LinearInterpolator());
                        rotationAnimator.setDuration(8000); // 8 seconds for slower rotation
                        rotationAnimator.start();
                    }
                });
                earthIntro.start();

                // 3. Dialogue appears after 6.5 seconds (longer colored rotation time)
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    dialogueContainer.setVisibility(View.VISIBLE);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(dialogueContainer, "alpha", 0f, 1f);
                    fadeIn.setDuration(1500); // Slower fade-in for dialogue
                    fadeIn.start();

                    // 4. Start grayscale and opacity reduction
                    startGrayscaleAndFadeOut();
                }, 6500);
            }
        }).start();
    }

    private void slowDownRotation() {
        // Already being handled by the 3-second initial rotation
        rotationAnimator.cancel();
    }

    private void startGrayscaleAndFadeOut() {
        // Grayscale animation
        ValueAnimator grayscale = ValueAnimator.ofFloat(0f, 1f);
        grayscale.setDuration(2000);
        grayscale.addUpdateListener(animation -> {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(1 - (float) animation.getAnimatedValue());
            earthImage.setColorFilter(new ColorMatrixColorFilter(matrix));
        });

        // Opacity animation
        ObjectAnimator opacityFade = ObjectAnimator.ofFloat(earthImage, "alpha", 1f, 0.6f);
        opacityFade.setDuration(2000);

        AnimatorSet grayscaleSet = new AnimatorSet();
        grayscaleSet.playTogether(grayscale, opacityFade);
        grayscaleSet.start();

        // After 3 seconds, change dialogue text and start violent shake
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            dialogueText.setText("\"THE WORLD IS COLLAPSING...\"");
            speakerName.setText("-LUMA");
            speakerName.setTextColor(0xFF90EE90);

            // Start violent shake animation
            performViolentShake();
        }, 3000);
    }

    private void performViolentShake() {
        ObjectAnimator shakeX = ObjectAnimator.ofFloat(earthImage, "translationX",
                0, -40, 40, -40, 40, -30, 30, -20, 20, -15, 15, -10, 10, 0);
        shakeX.setDuration(1500);
        shakeX.setInterpolator(new LinearInterpolator());

        ObjectAnimator shakeY = ObjectAnimator.ofFloat(earthImage, "translationY",
                0, -30, 30, -30, 30, -20, 20, -15, 15, -10, 10, -5, 5, 0);
        shakeY.setDuration(1500);
        shakeY.setInterpolator(new LinearInterpolator());

        AnimatorSet shakeSet = new AnimatorSet();
        shakeSet.playTogether(shakeX, shakeY);
        shakeSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // After shake ends, transition to question scene
                transitionToQuestionScene();
            }
        });
        shakeSet.start();
    }

    private void transitionToQuestionScene() {
        // 1. Fade out the dialogue
        ObjectAnimator dialogueFadeOut = ObjectAnimator.ofFloat(dialogueContainer, "alpha", 1f, 0f);
        dialogueFadeOut.setDuration(2500);
        dialogueFadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dialogueContainer.setVisibility(View.INVISIBLE);
                
                // 2. Pan earth upward smoothly
                ObjectAnimator earthPan = ObjectAnimator.ofFloat(earthImage, "translationY", 0f, -220f);
                earthPan.setDuration(1500);
                earthPan.setInterpolator(new AccelerateDecelerateInterpolator());
                earthPan.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 3. Show question and choices
                        showQuestionAndChoices();
                    }
                });
                earthPan.start();
            }
        });
        dialogueFadeOut.start();
    }

    private void showQuestionAndChoices() {
        questionContainer.setVisibility(View.VISIBLE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(questionContainer, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.start();
    }
}
