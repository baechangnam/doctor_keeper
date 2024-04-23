package com.apps.doctorkeeper_android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class BlinkingCircleView extends View {

    private Paint paint;
    private boolean isBlinking = false;

    public BlinkingCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = Math.min(getWidth(), getHeight()) / 2;
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, paint);
    }

    public void startBlinkAnimation() {
        if (!isBlinking) {
            isBlinking = true;
            AlphaAnimation blinkAnimation = new AlphaAnimation(1, 0);
            blinkAnimation.setDuration(500);
            blinkAnimation.setRepeatMode(Animation.REVERSE);
            blinkAnimation.setRepeatCount(Animation.INFINITE);
            startAnimation(blinkAnimation);
        }
    }

    public void stopBlinkAnimation() {
        clearAnimation();
        isBlinking = false;
        setAlpha(1f);
    }
}
