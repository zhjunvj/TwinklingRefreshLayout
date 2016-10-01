package com.lcodecore.library.v2;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.lcodecore.library.ProgressView;
import com.lcodecore.library.R;
import com.lcodecore.library.RoundDotView;
import com.lcodecore.library.ScrollingUtil;

/**
 * Created by lcodecore on 16/3/2.
 */
public class TwinklingRefreshLayout extends FrameLayout {

    private static final int PULL_DOWN_REFRESH = 1;//标志当前进入的刷新模式
    private static final int PULL_UP_LOAD = 2;
    private int state = PULL_DOWN_REFRESH;

    //波浪的高度,最大扩展高度
    protected float mWaveHeight;

    //头部的高度
    protected float mHeadHeight;

    //子控件
    private View mChildView;

    //头部layout
    protected FrameLayout mHeadLayout;

    private IHeaderView mHeadView;
    private IBottomView mBottomView;

    //底部高度
    private float mBottomHeight;

    //底部layout
    private FrameLayout mBottomLayout;


    //刷新的状态
    protected boolean isRefreshing;

    //加载更多的状态
    protected boolean isLoadingmore;

    //是否需要加载更多,默认需要
    protected boolean enableLoadmore = true;


    //触摸获得Y的位置
    private float mTouchY;

    //动画的变化率
    private DecelerateInterpolator decelerateInterpolator;


    public TwinklingRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public TwinklingRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwinklingRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        Log.i("cjj", "init");

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwinklingRefreshLayout, defStyleAttr, 0);
        mWaveHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_wave_height, (int) dp2sp(120));
        mHeadHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_head_height, (int) dp2sp(80));
        mBottomHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_bottom_height, (int) dp2sp(60));
        a.recycle();
    }

    /**
     * 初始化
     */

    private void init() {
        //使用isInEditMode解决可视化编辑器无法识别自定义控件的问题
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("只能拥有一个子控件哦");
        }

        //在动画开始的地方快然后慢;
        decelerateInterpolator = new DecelerateInterpolator(10);

        setPullListener(new SimplePullListener());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        System.out.println("onAttachedToWindow绑定窗口");

        //添加头部
        FrameLayout headViewLayout = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.TOP;
        headViewLayout.setLayoutParams(layoutParams);

        mHeadLayout = headViewLayout;

        this.addView(mHeadLayout);//addView(view,-1)添加到-1的位置

        //添加底部
        FrameLayout bottomViewLayout = new FrameLayout(getContext());
        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams2.gravity = Gravity.BOTTOM;
        bottomViewLayout.setLayoutParams(layoutParams2);

        mBottomLayout = bottomViewLayout;
        this.addView(mBottomLayout);

        //设置头部和底部View
        if (mHeadView == null) setHeaderView(new RoundDotView(getContext()));
        if (mBottomView == null) {
            BottomProgressView progressView = new BottomProgressView(getContext());
            progressView.setIndicatorId(ProgressView.BallPulse);
            progressView.setIndicatorColor(getResources().getColor(R.color.Orange));
            setBottomView(progressView);
        }

        //获得子控件
        mChildView = getChildAt(0);

        if (mChildView == null) {
            return;
        }

        mChildView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        //TODO  mChildView适配更多的ViewGroup
        if (mChildView instanceof AbsListView) {
            ((AbsListView) mChildView).setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (!isRefreshing && !isLoadingmore && firstVisibleItem == 0 || ((AbsListView) mChildView).getLastVisiblePosition() == totalItemCount - 1) {
                        if (mVelocityY >= 5000 && ScrollingUtil.isAbsListViewToTop((AbsListView) mChildView)) {
                            mVelocityY = 0;
                            state = PULL_DOWN_REFRESH;
                            mChildView.animate().translationY(mHeadHeight).setDuration(150).start();
                            mChildView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mChildView.animate().translationY(0).start();
                                }
                            }, 150);
                        }
                        if (mVelocityY <= -5000 && ScrollingUtil.isAbsListViewToBottom((AbsListView) mChildView)) {
                            System.out.println("滚动速度:" + mVelocityY);
                            mVelocityY = 0;
                            state = PULL_UP_LOAD;
                            mChildView.animate().translationY(-mHeadHeight).setDuration(150).start();
                            mChildView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mChildView.animate().translationY(0).start();
                                }
                            }, 150);
                        }
                    }
                }
            });
        } else if (mChildView instanceof RecyclerView) {
            ((RecyclerView) mChildView).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (!isRefreshing && !isLoadingmore && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (mVelocityY >= 5000 && ScrollingUtil.isRecyclerViewToTop((RecyclerView) mChildView)) {
                            mVelocityY = 0;
                            state = PULL_DOWN_REFRESH;
                            mChildView.animate().translationY(mHeadHeight).setDuration(150).start();
                            mChildView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mChildView.animate().translationY(0).start();
                                }
                            }, 150);
                        }
                        if (mVelocityY <= -5000 && ScrollingUtil.isRecyclerViewToBottom((RecyclerView) mChildView)) {
                            System.out.println("滚动速度:" + mVelocityY);
                            mVelocityY = 0;
                            state = PULL_UP_LOAD;
                            mChildView.animate().translationY(-mHeadHeight).setDuration(150).start();
                            mChildView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mChildView.animate().translationY(0).start();
                                }
                            }, 150);
                        }
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

        mChildView.animate().setInterpolator(new DecelerateInterpolator());//设置速率为递减
        mChildView.animate().setUpdateListener(//通过addUpdateListener()方法来添加一个动画的监听器
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int height = (int) mChildView.getTranslationY();//获得mChildView当前y的位置
                        height = Math.abs(height);

                        if (state == PULL_DOWN_REFRESH) {
                            mHeadLayout.getLayoutParams().height = height;
                            mHeadLayout.requestLayout();//重绘
                            if (pullListener != null) {
                                pullListener.onPullDownReleasing(TwinklingRefreshLayout.this, height / mHeadHeight);
                            }
                        } else if (state == PULL_UP_LOAD) {
                            mBottomLayout.getLayoutParams().height = height;
                            mBottomLayout.requestLayout();
                            if (pullListener != null) {
                                pullListener.onPullUpReleasing(TwinklingRefreshLayout.this, height / mBottomHeight);
                            }
                        }
                    }
                }
        );
    }

    //针对部分没有OnScrollListener的View的延时策略
    private static final int MSG_START_COMPUTE_SCROLL = 0; //开始计算
    private static final int MSG_CONTINUE_COMPUTE_SCROLL = 1;//继续计算
    private static final int MSG_STOP_COMPUTE_SCROLL = 2; //停止计算

    private int cur_delay_times = 0; //当前计算次数
    private static final int ALL_DELAY_TIMES = 20;  //10ms计算一次,总共计算20次
    private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_COMPUTE_SCROLL:
                    cur_delay_times = -1; //这里没有break,写作-1方便计数
                case MSG_CONTINUE_COMPUTE_SCROLL:
                    cur_delay_times++;

                    if (!isRefreshing && !isLoadingmore && mVelocityY >= 5000 && (mChildView != null && Math.abs(mChildView.getScrollY()) <= mTouchSlop)) {
                        mVelocityY = 0;
                        state = PULL_DOWN_REFRESH;
                        mChildView.animate().translationY(mHeadHeight).setDuration(150).start();
                        mChildView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChildView.animate().translationY(0).start();
                            }
                        }, 150);
                        cur_delay_times = ALL_DELAY_TIMES;
                    }

                    if (!isRefreshing && !isLoadingmore && mVelocityY <= -5000 && (mChildView != null && mChildView.getScrollY() >= mChildView.getHeight())) {
                        System.out.println("滚动速度:" + mVelocityY);
                        mVelocityY = 0;
                        state = PULL_UP_LOAD;
                        mChildView.animate().translationY(-mHeadHeight).setDuration(150).start();
                        mChildView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChildView.animate().translationY(0).start();
                            }
                        }, 150);
                        cur_delay_times = ALL_DELAY_TIMES;
                    }

                    if (cur_delay_times < ALL_DELAY_TIMES)
                        mHandler.sendEmptyMessageDelayed(MSG_CONTINUE_COMPUTE_SCROLL, 10);
                    break;
                case MSG_STOP_COMPUTE_SCROLL:
                    cur_delay_times = ALL_DELAY_TIMES;
                    break;
            }
        }
    };

    private float mVelocityY;
    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            System.out.println("滚动的距离:"+distanceY);
            if (isRefreshing && distanceY >= mTouchSlop ) finishRefreshing();
            if (isLoadingmore && distanceY <= -mTouchSlop) finishLoadmore();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mVelocityY = velocityY;
            if (!(mChildView instanceof AbsListView || mChildView instanceof RecyclerView)) {
                //既不是AbsListView也不是RecyclerView,由于这些没有实现OnScrollListener接口,无法回调状态,只能采用延时策略
                if (Math.abs(mVelocityY) >= 5000) {
                    mHandler.sendEmptyMessage(MSG_START_COMPUTE_SCROLL);
                } else {
                    cur_delay_times = ALL_DELAY_TIMES;
                }
            }
            return false;
        }
    });

    /**
     * 拦截事件
     *
     * @return return true时,ViewGroup的事件有效,执行onTouchEvent事件
     * return false时,事件向下传递,onTouchEvent无效
     * <p/>
     * 正在刷新
     * 或者向下滑动且子列表空间到达顶部(不能向上滚动 !canChildScrollUp)
     * 或者子列表空间到达了底部(hasComeToBottom)
     * 时拦截事件(拒绝子View操作)
     */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // if (isRefreshing) return true;
        //if (isLoadingmore) return true;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - mTouchY;

                if (dy > 0 && !canChildScrollUp()) {
                    state = PULL_DOWN_REFRESH;
                    System.out.println("下拉刷新状态");
                    return true;
                } else if (dy < 0 && !canChildScrollDown() && enableLoadmore) {
                    state = PULL_UP_LOAD;
                    System.out.println("上拉加载更多状态");
                    return true;
                }

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 父View响应事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isRefreshing || isLoadingmore) return super.onTouchEvent(e);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dy = e.getY() - mTouchY;

                if (state == PULL_DOWN_REFRESH) {
                    dy = Math.min(mWaveHeight * 2, dy);
                    dy = Math.max(0, dy);

                    if (mChildView != null) {
                        float offsetY = decelerateInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2;
                        mChildView.setTranslationY(offsetY);

                        mHeadLayout.getLayoutParams().height = (int) offsetY;
                        mHeadLayout.requestLayout();

                        if (pullListener != null) {
                            pullListener.onPullingDown(TwinklingRefreshLayout.this, offsetY / mHeadHeight);
                        }
                    }
                } else if (state == PULL_UP_LOAD) {
                    //加载更多的动作
                    dy = Math.min(mBottomHeight * 2, Math.abs(dy));
                    dy = Math.max(0, dy);
                    if (mChildView != null) {
                        float offsetY = -decelerateInterpolator.getInterpolation(dy / mBottomHeight / 2) * dy / 2;
                        mChildView.setTranslationY(offsetY);

                        mBottomLayout.getLayoutParams().height = (int) -offsetY;
                        mBottomLayout.requestLayout();

                        if (pullListener != null) {
                            pullListener.onPullingUp(TwinklingRefreshLayout.this, offsetY / mHeadHeight);
                        }
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (state == PULL_DOWN_REFRESH) {
                        if (mChildView.getTranslationY() >= mHeadHeight - 15) {
                            mChildView.animate().translationY(mHeadHeight).start();//回到限制的最大高度处
                            isRefreshing = true;
                            if (pullListener != null) {
                                pullListener.onRefresh(TwinklingRefreshLayout.this);
                            }
                        } else {
                            mChildView.animate().translationY(0).start();
                        }
                    } else if (state == PULL_UP_LOAD) {
                        if (Math.abs(mChildView.getTranslationY()) >= mBottomHeight - 15) {
                            isLoadingmore = true;
                            mChildView.animate().translationY(-mBottomHeight).start();
                            if (pullListener != null) {
                                pullListener.onLoadMore(TwinklingRefreshLayout.this);
                            }
                        } else {
                            mChildView.animate().translationY(0).start();
                        }
                    }
                }
                return true;
        }

        return super.onTouchEvent(e);
    }

    /**
     * 用来判断是否可以下拉
     *
     * @return boolean
     */
    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    /**
     * Whether it is possible for the child view of this layout to
     * scroll down. Override this if the child view is a custom view.
     * 判断是否可以上拉
     *
     * @return
     */
    protected boolean canChildScrollDown() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() < 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, 1);
        }
    }

    /**
     * 设置下拉监听
     */
    //private RefreshListener refreshListener;

//    public void setRefreshListener(RefreshListener refreshListener) {
//        this.refreshListener = refreshListener;
//    }

    /**
     * 设置wave监听
     */
    private PullListener pullListener;

    private void setPullListener(PullListener pullListener) {
        this.pullListener = pullListener;
    }

    /**
     * 刷新结束
     */
    public void finishRefreshing() {
        if (mChildView != null) {
            mChildView.animate().translationY(0).start();
        }
        isRefreshing = false;
    }

    /**
     * 加载更多结束
     */
    public void finishLoadmore() {
        if (mChildView != null) {
            mChildView.animate().translationY(0).start();
        }
        isLoadingmore = false;
    }

    /**
     * 设置头部View
     *
     * @param headerView
     */
    public void setHeaderView(final IHeaderView headerView) {
        System.out.println("设置顶部布局");
        if (headerView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHeadLayout.removeAllViewsInLayout();
                    mHeadLayout.addView(headerView.getView());
                }
            });
            mHeadView = headerView;
        }
    }

    /**
     * 设置底部
     */
    public void setBottomView(final IBottomView bottomView) {
        if (bottomView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mBottomLayout.removeAllViewsInLayout();
                    mBottomLayout.addView(bottomView.getView());
                }
            });
            mBottomView = bottomView;
        }
    }

    /**
     * 设置wave的下拉高度
     *
     * @param waveHeight
     */
    public void setWaveHeight(float waveHeight) {
        this.mWaveHeight = waveHeight;
    }

    /**
     * 设置下拉头的高度
     *
     * @param headHeight
     */
    public void setHeaderHeight(float headHeight) {
        this.mHeadHeight = headHeight;
    }

    public void setBottomHeight(float bottomHeight) {
        this.mBottomHeight = bottomHeight;
    }

    public void setEnableLoadmore(boolean enableLoadmore1) {
        enableLoadmore = enableLoadmore1;
        if (mBottomView != null) {
            if (enableLoadmore) mBottomView.getView().setVisibility(VISIBLE);
            else mBottomView.getView().setVisibility(GONE);
        }
    }

    public float dp2sp(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    public interface PullListener {
        /**
         * 下拉中
         *
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
         *
         * @param refreshLayout
         * @param fraction
         */
        void onPullDownReleasing(TwinklingRefreshLayout refreshLayout, float fraction);

        /**
         * 上拉松开
         */
        void onPullUpReleasing(TwinklingRefreshLayout refreshLayout, float fraction);

        /**
         * 刷新中。。。
         */
        void onRefresh(TwinklingRefreshLayout refreshLayout);

        /**
         * 加载更多中
         */
        void onLoadMore(TwinklingRefreshLayout refreshLayout);
    }


    private class SimplePullListener implements PullListener {

        @Override
        public void onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction) {
            mHeadView.onPullingDown(fraction);
            if (refreshListener != null) refreshListener.onPullingDown(refreshLayout, fraction);
        }

        @Override
        public void onPullingUp(TwinklingRefreshLayout refreshLayout, float fraction) {
            mBottomView.onPullingUp(fraction);
            if (refreshListener != null) refreshListener.onPullingUp(refreshLayout, fraction);
        }

        @Override
        public void onPullDownReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
            mHeadView.onPullReleasing(fraction);
            if (refreshListener != null)
                refreshListener.onPullDownReleasing(refreshLayout, fraction);
        }

        @Override
        public void onPullUpReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
            mBottomView.onPullReleasing(fraction);
            if (refreshListener != null) refreshListener.onPullUpReleasing(refreshLayout, fraction);
        }

        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
            mHeadView.startAnim();
            if (refreshListener != null) refreshListener.onRefresh(refreshLayout);
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            mBottomView.startAnim();
            if (refreshListener != null) refreshListener.onLoadMore(refreshLayout);
        }
    }

    private OnRefreshListener refreshListener;

    public static class OnRefreshListener implements PullListener {
        @Override
        public void onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onPullingUp(TwinklingRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onPullDownReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onPullUpReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        if (refreshListener != null) {
            this.refreshListener = refreshListener;
        }
    }
}