package com.lcodecore.library;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by lcodecore on 16/3/2.
 */
public class TwinklingRefreshLayout extends FrameLayout {

    private static final int PULL_DOWN_REFRESH = 0;
    private static final int PULL_UP_LOAD = 1;
    private int state = PULL_DOWN_REFRESH; //标志当前进入的刷新模式

    //波浪的高度
    protected float mWaveHeight;

    //头部的高度
    protected float mHeadHeight;

    //子控件
    private View mChildView;

    //头部layout
    protected FrameLayout mHeadLayout;


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

    //当前Y的位置
    private float mCurrentY;

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
    RoundDotView roundDotView;

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

        //设置头部和底部View
        roundDotView = new RoundDotView(getContext());
        ProgressView progressView = new ProgressView(getContext());
        progressView.setIndicatorId(ProgressView.BallPulse);
        progressView.setIndicatorColor(getResources().getColor(R.color.Orange));
        setHeaderView(roundDotView);
        setBottomView(progressView);

        setPullListener(new PullListener() {
            @Override
            public void onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction) {
//                Toast.makeText(getContext(), "下拉", Toast.LENGTH_SHORT).show();
                roundDotView.setScaleX(1 + fraction);
                roundDotView.setScaleY(1 + fraction);
//                roundDotView.animate().scaleX(1+fraction).scaleY(1+fraction).start();
            }

            @Override
            public void onPullingUp(TwinklingRefreshLayout refreshLayout, float fraction) {
//                Toast.makeText(getContext(), "上拉", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPullReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
//                Toast.makeText(getContext(), "下拉释放", Toast.LENGTH_SHORT).show();
            }
        });

        setRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                roundDotView.startAnim();
                //TODO
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishRefreshing();
                    }
                }, 4000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
//                progressView.setAnimStart();
                //TODO
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishLoadmore();
                    }
                }, 4000);
            }
        });
    }


    private float mVelocityY;
    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mVelocityY = velocityY;
            return false;
        }
    });



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i("cjj", "onAttachedToWindow");

        //添加头部
        FrameLayout headViewLayout = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.TOP;
        headViewLayout.setLayoutParams(layoutParams);

        mHeadLayout = headViewLayout;

        this.addView(mHeadLayout);//addView(view,-1)添加到-1的位置

        //添加底部
        FrameLayout bottombViewLayout = new FrameLayout(getContext());
        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams2.gravity = Gravity.BOTTOM;
        bottombViewLayout.setLayoutParams(layoutParams2);

        mBottomLayout = bottombViewLayout;

        this.addView(mBottomLayout);


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

        ((RecyclerView) mChildView).addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState==RecyclerView.SCROLL_STATE_IDLE && mVelocityY>=5000 &&ScrollingUtil.isRecyclerViewToTop((RecyclerView) mChildView)){
                    System.out.println("滚动到顶部了。。。");

                    mChildView.animate().translationY(mHeadHeight).setDuration(150).start();
                    mChildView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mChildView.animate().translationY(0).start();
                        }
                    },150);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mChildView.animate().setInterpolator(new DecelerateInterpolator());//设置速率为递减
        mChildView.animate().setUpdateListener(//通过addUpdateListener()方法来添加一个动画的监听器
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int height = (int) mChildView.getTranslationY();//获得mChildView当前y的位置
                        height = Math.abs(height);

                        //Log.i("cjj", "mChildView.getTranslationY----------->" + height);
                        if (state == PULL_DOWN_REFRESH) {
                            mHeadLayout.getLayoutParams().height = height;
                            mHeadLayout.requestLayout();//重绘
                            if (pullListener != null) {
                                pullListener.onPullReleasing(TwinklingRefreshLayout.this, height / mHeadHeight);
                            }
                        } else {
                            mBottomLayout.getLayoutParams().height = height;
                            mBottomLayout.requestLayout();
                            if (pullListener != null) {
                                pullListener.onPullReleasing(TwinklingRefreshLayout.this, height / mBottomHeight);
                            }
                        }
                    }
                }
        );
    }

    /**
     * 拦截事件
     *
     * @param ev
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
     *
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isRefreshing) {
            return super.onTouchEvent(e);
        }

        if (isLoadingmore) {
            return super.onTouchEvent(e);
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();

                float dy = mCurrentY - mTouchY;

                if (state == PULL_DOWN_REFRESH) {
                    dy = Math.min(mWaveHeight * 2, dy);
                    dy = Math.max(0, dy);

                    //System.out.println("dy的值是:" + dy + "下拉部分");

                    if (mChildView != null) {
                        float offsetY = decelerateInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2;
                        mChildView.setTranslationY(offsetY);

                        mHeadLayout.getLayoutParams().height = (int) offsetY;
                        mHeadLayout.requestLayout();

                        if (pullListener != null) {
                            pullListener.onPullingDown(TwinklingRefreshLayout.this, offsetY / mHeadHeight);
                        }
                    }
                } else {
                    //加载更多的动作
                    dy = Math.min(mBottomHeight * 2, Math.abs(dy));
                    dy = Math.max(0, dy);
                    System.out.println("dy的值是:" + dy);
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
                            System.out.println("进入刷新状态...");
                            mChildView.animate().translationY(mHeadHeight).start();//回到限制的最大高度处
                            System.out.println("mChildView应该执行动画");
                            isRefreshing = true;
                            if (refreshListener != null) {
                                refreshListener.onRefresh(TwinklingRefreshLayout.this);
                            }
                        } else {
                            System.out.println("执行了else里的语句...");
                            mChildView.animate().translationY(0).start();
                        }
                    } else {
                        if (Math.abs(mChildView.getTranslationY()) >= mBottomHeight - 15) {
                            isLoadingmore = true;
                            mChildView.animate().translationY(-mBottomHeight).start();
                            if (refreshListener != null) {
                                refreshListener.onLoadMore(TwinklingRefreshLayout.this);
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
        if (android.os.Build.VERSION.SDK_INT < 14) {
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
    private RefreshListener refreshListener;

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    /**
     * 设置wave监听
     */
    private PullListener pullListener;

    public void setPullListener(PullListener pullListener) {
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
    public void setHeaderView(final View headerView) {
        post(new Runnable() {
            @Override
            public void run() {
                mHeadLayout.addView(headerView);
            }
        });
    }

    /**
     * 设置底部
     */
    public void setBottomView(final View bottomView) {
        post(new Runnable() {
            @Override
            public void run() {
                mBottomLayout.addView(bottomView);
            }
        });
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
    }

    public float dp2sp(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}