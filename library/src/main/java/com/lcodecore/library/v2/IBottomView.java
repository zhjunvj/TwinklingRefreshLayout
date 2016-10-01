package com.lcodecore.library.v2;

import android.view.View;

/**
 * Created by lcodecore on 2016/10/1.
 */

public interface IBottomView {
    View getView();

    /**
     * 上拉准备加载更多的动作
     * @param fraction 上拉高度与Bottom总高度之比
     */
    void onPullingUp(float fraction);

    void startAnim();

    /**
     * 上拉释放过程
     */
    void onPullReleasing(float fraction);
}
