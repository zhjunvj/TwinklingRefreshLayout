package com.lcodecore.twinklingrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.lcodecore.library.v2.IHeaderView;

/**
 * Created by lcodecore on 2016/10/1.
 */

public class TextHeaderView extends TextView implements IHeaderView {


    public TextHeaderView(Context context) {
        super(context);
    }

    public TextHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction) {
        setScaleX(1+fraction);
        setScaleY(1+fraction);
    }

    @Override
    public void onPullReleasing(float fraction) {

    }

    @Override
    public void startAnim() {

    }
}
