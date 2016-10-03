## 重写View的onAttachedToWindow()方法
此时View被添加到了窗体上,View有了一个用于显示的Surface,将开始绘制。因此其保证了在onDraw()之前调用,但可能在调用 onDraw(Canvas) 之前的任何时刻，包括调用 onMeasure(int, int) 之前或之后。
比较适合去执行一些初始化操作。(此外在屏蔽Home键的时候也会回调这个方法)

- onDetachedFromWindow()与onAttachedToWindow()方法相对应。
- ViewGroup先是调用自己的onAttachedToWindow()方法，再调用其每个child的onAttachedToWindow()方法，这样此方法就在整个view树中遍布开了，而visibility并不会对这个方法产生影响。

- onAttachedToWindow方法是在Activity resume的时候被调用的，也就是act对应的window被添加的时候，且每个view只会被调用一次，父view的调用在前，不论view的visibility状态都会被调用，适合做些view特定的初始化操作；
- onDetachedFromWindow方法是在Activity destroy的时候被调用的，也就是act对应的window被删除的时候，且每个view只会被调用一次，父view的调用在后，也不论view的visibility状态都会被调用，适合做最后的清理操作；

就TwinklingRefreshLayout来说,Header和Footer需要及时显示出来,View又没有明显的生命周期,因此在onAttachedToWindow()中进行设置可以保证在onDraw()之前添加了刷新控件。
```java
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

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
        //...其它步骤
    }
```

但是当TwinklingRefreshLayout应用在Activity或Fragment中时,可能会因为执行onResume重新触发了onAttachedToWindow()方法而导致重复创建Header和Footer挡住原先添加的View,因此需要加上判断:
```java
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        System.out.println("onAttachedToWindow绑定窗口");

        //添加头部
        if (mHeadLayout == null) {
            FrameLayout headViewLayout = new FrameLayout(getContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            layoutParams.gravity = Gravity.TOP;
            headViewLayout.setLayoutParams(layoutParams);

            mHeadLayout = headViewLayout;

            this.addView(mHeadLayout);//addView(view,-1)添加到-1的位置

            if (mHeadView == null) setHeaderView(new RoundDotView(getContext()));
        }
        //...
    }
```


## 参考
- [View的onAttachedToWindow和onDetachedFromWindow的调用时机分析](http://www.jianshu.com/p/e7b6fa788ae6)