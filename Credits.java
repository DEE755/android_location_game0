package com.example.myapplicationtest1;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Credits extends AppCompatActivity
{
    @Override
    protected void onStart() {
        super.onStart();





        setContentView(R.layout.activity_credits);
        TextView textView = findViewById(R.id.credit_txt_view);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.credits_go_up);
        textView.setAnimation(animation);
        textView.startAnimation(animation);



        textView.post(() -> {
            int width = textView.getWidth();
            int height = textView.getHeight();

            // Full fade at the top
            LinearGradient gradient = new LinearGradient(
                    0, 0, 0, height / 3f, // Fade only in the top third
                    new int[]{Color.TRANSPARENT, Color.WHITE, Color.WHITE}, // White fades to transparent
                    new float[]{0f, 0.2f, 1f}, // Fully visible at 70%, disappears at 100%
                    Shader.TileMode.CLAMP
            );

            textView.getPaint().setShader(gradient);
            textView.invalidate();
        });



        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Restart the animation
                textView.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }
}


