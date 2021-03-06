/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.ma.sm.animations.apis;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ma.sm.R;

import java.util.ArrayList;

/**
 * This application demonstrates loading Animator objects from
 * XML resources.
 */
public class AnimationLoading extends Activity {

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.animation_loading);
    LinearLayout container = findViewById(R.id.container);
    final MyAnimationView animView = new MyAnimationView(this);
    container.addView(animView);

    Button starter = findViewById(R.id.startButton);
    starter.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        animView.startAnimation();
      }
    });
  }

  public class MyAnimationView extends View
      implements ValueAnimator.AnimatorUpdateListener {

    private static final float BALL_SIZE = 100f;

    public final ArrayList<ShapeHolder> balls = new ArrayList<>();
    Animator animation = null;

    public MyAnimationView(Context context) {
      super(context);
      addBall(50, 50);
      addBall(200, 50);
      addBall(350, 25);
      addBall(500, 25, Color.GREEN);
      addBall(650, 50, Color.BLUE);
    }

    private void createAnimation() {
      Context appContext = AnimationLoading.this;

      if (animation == null) {
        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.
            loadAnimator(appContext, R.animator.object_animator);
        anim.addUpdateListener(this);
        anim.setTarget(balls.get(0));

        ValueAnimator fader = (ValueAnimator) AnimatorInflater.
            loadAnimator(appContext, R.animator.animator);
        fader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          public void onAnimationUpdate(ValueAnimator animation) {
            balls.get(1).setAlpha((Float) animation.getAnimatedValue());
          }
        });

        AnimatorSet seq =
            (AnimatorSet) AnimatorInflater.loadAnimator(appContext,
                R.animator.animator_set);
        seq.setTarget(balls.get(2));

        ObjectAnimator colorAnimator = (ObjectAnimator) AnimatorInflater.
            loadAnimator(appContext, R.animator.color_animator);
        colorAnimator.setTarget(balls.get(3));

        ObjectAnimator colorAnimator2 = (ObjectAnimator) AnimatorInflater.
            loadAnimator(appContext, R.animator.color_animator);
        colorAnimator2.setTarget(balls.get(4));


        animation = new AnimatorSet();
        ((AnimatorSet) animation).playTogether(anim, fader, seq,
            colorAnimator, colorAnimator2);
      }
    }

    public void startAnimation() {
      createAnimation();
      animation.start();
    }

    private ShapeHolder createBall(float x, float y) {
      OvalShape circle = new OvalShape();
      circle.resize(BALL_SIZE, BALL_SIZE);
      ShapeDrawable drawable = new ShapeDrawable(circle);
      ShapeHolder shapeHolder = new ShapeHolder(drawable);
      shapeHolder.setX(x);
      shapeHolder.setY(y);
      return shapeHolder;
    }

    private void addBall(float x, float y, int color) {
      ShapeHolder shapeHolder = createBall(x, y);
      shapeHolder.setColor(color);
      balls.add(shapeHolder);
    }

    private void addBall(float x, float y) {
      ShapeHolder shapeHolder = createBall(x, y);
      int red = (int) (100 + Math.random() * 155);
      int green = (int) (100 + Math.random() * 155);
      int blue = (int) (100 + Math.random() * 155);
      int color = 0xff000000 | red << 16 | green << 8 | blue;
      Paint paint = shapeHolder.getShape().getPaint();
      int darkColor = 0xff000000 | red / 4 << 16 | green / 4 << 8 | blue / 4;
      RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
          50f, color, darkColor, Shader.TileMode.CLAMP);
      paint.setShader(gradient);
      balls.add(shapeHolder);
    }

    @Override
    protected void onDraw(Canvas canvas) {
      for (ShapeHolder ball : balls) {
        canvas.translate(ball.getX(), ball.getY());
        ball.getShape().draw(canvas);
        canvas.translate(-ball.getX(), -ball.getY());
      }
    }

    public void onAnimationUpdate(ValueAnimator animation) {

      invalidate();
      ShapeHolder ball = balls.get(0);
      ball.setY((Float) animation.getAnimatedValue());
    }
  }
}