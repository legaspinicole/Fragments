package com.example.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ExplosionChoicesFragment extends Fragment {

    private View questionContainer;
    private android.widget.TextView questionText;
    private android.widget.TextView choice1;
    private android.widget.TextView choice2;
    private android.widget.TextView choice3;
    private boolean showingSecondSet = false;

    public ExplosionChoicesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explosion_choices, container, false);

        ImageView explosionImage = view.findViewById(R.id.earthExplodeImage);
        questionContainer = view.findViewById(R.id.questionContainer);
        questionText = view.findViewById(R.id.questionText);
        choice1 = view.findViewById(R.id.choice1);
        choice2 = view.findViewById(R.id.choice2);
        choice3 = view.findViewById(R.id.choice3);

        explosionImage.setImageResource(R.drawable.earth_explode);

        // Apply grayscale and opacity effect
        applyGrayscaleAndOpacity(explosionImage);

        // Set up click listeners for choices
        View.OnClickListener choiceListener = v -> handleChoiceTap();
        choice1.setOnClickListener(choiceListener);
        choice2.setOnClickListener(choiceListener);
        choice3.setOnClickListener(choiceListener);

        return view;
    }

    private void goToNextScene() {
        if (getActivity() != null) {
            // Navigate to next scene
            androidx.fragment.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.fragment_container, new RestoreEarthFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void handleChoiceTap() {
        if (!showingSecondSet) {
            fadeToSecondSet();
        } else {
            goToNextScene();
        }
    }

    private void fadeToSecondSet() {
        if (questionContainer == null) return;

        questionContainer.animate().alpha(0f).setDuration(800).withEndAction(() -> {
            // Update dialogue + choices
            questionText.setText("REST BRINGS STRENGTH BACK.");
            choice1.setText("THAT FELT PEACEFUL.");
            choice2.setText("I NEEDED THAT");
            choice3.setText("I'M READY TO CONTINUE.");

            questionContainer.setAlpha(0f);
            questionContainer.animate().alpha(1f).setDuration(800).start();
            showingSecondSet = true;
        }).start();
    }

    private void applyGrayscaleAndOpacity(ImageView imageView) {
        // Grayscale effect
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));

        // Opacity to 0.6f
        imageView.setAlpha(0.6f);
    }
}
