package com.lcodecore.library.v2;

import android.view.View;

/**
 * Created by lcodecore on 2016/10/1.
 */

public interface IHeaderView {
    View getView();

    /**
     * 下拉准备刷新动作
     * @param fraction  当前下拉高度与总高度的比
     */
    void onPullingDown(float fraction);

    /**
     * 下拉释放过程
     */
    void onPullReleasing(float fraction);

    void startAnim();
}
