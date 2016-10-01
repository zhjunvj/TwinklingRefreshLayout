package com.lcodecore.library.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.lcodecore.library.ProgressView;

/**
 * Created by lcodecore on 2016/10/1.
 */

public class BottomProgressView extends ProgressView implements IBottomView {
    public BottomProgressView(Context context) {
        super(context);
    }

    public BottomProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BottomProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingUp(float fraction) {

    }

    @Override
    public void startAnim() {

    }

    @Override
    public void onPullReleasing(float fraction) {

    }
}
