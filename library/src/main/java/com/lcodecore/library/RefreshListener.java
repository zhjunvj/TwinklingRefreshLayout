package com.lcodecore.library;

/**
 * Created by lcodecore on 16/1/31.
 */
public interface RefreshListener {
    /**
     * 刷新中。。。
     * @param refreshLayout
     */
    void onRefresh(TwinklingRefreshLayout refreshLayout);

    /**
     * 加载更多中
     */
    void onLoadMore(TwinklingRefreshLayout refreshLayout);
}
