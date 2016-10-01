package com.lcodecore.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.lcodecore.library.v2.IHeaderView;

/**
 * Created by cjj on 2015/8/27.
 */
public class RoundDotView extends View implements IHeaderView {
    private Paint mPath;
    private float r = 12;
    private int num = 5;

    public void setCir_x(int cir_x) {
        this.cir_x = cir_x;
    }

    private int cir_x;

    float fraction1;
    float fraction2;
    boolean animating = false;

    public RoundDotView(Context context) {
        this(context, null, 0);
    }

    public RoundDotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundDotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Paint();
        mPath.setAntiAlias(true);
        mPath.setColor(Color.rgb(114, 114, 114));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getMeasuredWidth() / num - 10;
        for (int i = 0; i < num; i++) {
            if (animating) {
                switch (i) {
//                    case 0:
//                        mPath.setAlpha(35);
//                        mPath.setColor(getResources().getColor(R.color.Orange));
//                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 3 - 3 * w / 3 * 2, getMeasuredHeight() / 2, r*fraction2, mPath);
//                        break;
                    case 0:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 2 - 2 * w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
                    case 1:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Green));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 1 - w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
                    case 2:
                        mPath.setAlpha(255);
                        mPath.setColor(getResources().getColor(R.color.Blue));
                        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r * fraction1, mPath);
                        break;
                    case 3:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Orange));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 1 + w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
                    case 4:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 2 + 2 * w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
//                    case 6:
//                        mPath.setAlpha(35);
//                        mPath.setColor(getResources().getColor(R.color.Green));
//                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 3 + 3 * w / 3 * 2, getMeasuredHeight() / 2, r*fraction2, mPath);
//                        break;
                }
            } else {
                switch (i) {
//                    case 0:
//                        mPath.setAlpha(35);
//                        mPath.setColor(getResources().getColor(R.color.Orange));
//                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 3 - 3 * w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
//                        break;
                    case 0:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 2 - 2 * w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 1:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Green));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 1 - w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 2:
                        mPath.setAlpha(255);
                        mPath.setColor(getResources().getColor(R.color.Blue));
                        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 3:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Orange));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 1 + w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 4:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 2 + 2 * w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
//                    case 6:
//                        mPath.setAlpha(35);
//                        mPath.setColor(getResources().getColor(R.color.Green));
//                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 3 + 3 * w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
//                        break;
                }
            }
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction) {
        System.out.println("下拉参数是:"+fraction);
        setScaleX(1 + fraction);
        setScaleY(1 + fraction);
    }

    @Override
    public void onPullReleasing(float fraction) {

    }

    @Override
    public void startAnim() {
        animating = true;
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 1.3f, 1f, 0.8f);
        animator.setDuration(800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction1 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();


        ValueAnimator animator1 = ValueAnimator.ofFloat(1f, 0.8f, 1f, 1.3f);
        animator1.setDuration(800);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction2 = (float) animation.getAnimatedValue();
            }
        });
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator1.start();
    }

}
