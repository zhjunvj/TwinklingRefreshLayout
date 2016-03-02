package com.lcodecore.library;

/**
 * Created by lcodecore on 16/1/31.
 */
public interface PullListener {
    /**
     * 下拉中
     * @param refreshLayout
     * @param fraction
     */
    void onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction);

    /**
     * 上拉
     */
    void onPullingUp(TwinklingRefreshLayout refreshLayout, float fraction);

    /**
     * 下拉松开
     * @param refreshLayout
     * @param fraction
     */
    void onPullReleasing(TwinklingRefreshLayout refreshLayout, float fraction);
}
