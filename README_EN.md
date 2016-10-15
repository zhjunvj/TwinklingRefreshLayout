# TwinklingRefreshLayout
TwinklingRefreshLayout extended the thoughts of SwipeRefreshLayout,using a ViewGroup to include a list of Views, to maintain its low coupling and high versatility. Follows are its main features.
 
 - Support RecyclerView, ScrollView, AbsListView, WebView and so on.
 - Support to load more.
 - Default support cross-border rebound.
 - You can open a pure bounds rebound mode.
 -  Lots of methods in the class OnRefreshListener.
 - It provides an interface to the callback during the sliding coefficient. Personalized offer good support.

![](art/pic_large.png)

## Demo
[Download Demo](art/app-debug.apk)

![](art/gif_recyclerview.gif)  ![](art/gif_listview.gif)  ![](art/gif_gridview.gif)  ![](art/gif_scrollview.gif)  ![](art/gif_webview.gif)

## Usage
#### 1.Add a gradle dependency.
```
compile 'com.lcodecorex:tkrefreshlayout:1.0.3'
```

#### 2.Add TwinklingRefreshLayout in the layout xml.
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.lcodecore.library.TwinklingRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:tr_wave_height="180dp"
    app:tr_head_height="100dp">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#fff" />
</com.lcodecore.library.TwinklingRefreshLayout>
```

#### 3.Coding in the Activity or Fragment.
##### Change of state need to be manually controlled.
```java
refreshLayout.setOnRefreshListener(new TwinklingRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadmore();
                    }
                },2000);
            }
        });
    }
```

Use finishRefreshing() method to end refresh, finishLoadmore() method to end load more. OnRefreshListener there are other methods, you can choose need to override. 

##### setWaveHeight、setHeaderHeight、setBottomHeight
- setWaveHeight is used To set the maximum height of the head can be stretched.
- setHeaderHeight is used to set the standard head height.
- setBottomHeight is used to set the Bottom height.

#### setEnableRefresh、setEnableLoadmore
Flexible settings for whether to disable the pull-down.

##### setHeaderView(IHeaderView headerView)、setBottomView(IBottomView bottomView)

##### setEnableOverlayRefreshView()
Whether to allow the display refresh control at the rebound out of bounds, the default is true.

##### setPureScrollModeOn()
To open the pure bounds rebound mode.

#### 4.Attributes
- tr_wave_height - Flexible head height
- tr_head_height -  Head height
- tr_bottom_height - Bottom height
- tr_overscroll_height - OverScroll Height 
- tr_enable_loadmore - default is true
- tr_pureScrollMode_on - default is false
- tr_show_overlay_refreshview default is true

## Other
### 1.setOnRefreshListener
- onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction)  
- onPullingUp(TwinklingRefreshLayout refreshLayout, float fraction)    
- onPullDownReleasing(TwinklingRefreshLayout refreshLayout, float fraction)  
- onPullUpReleasing(TwinklingRefreshLayout refreshLayout, float fraction)  
- onRefresh(TwinklingRefreshLayout refreshLayout)  
- onLoadMore(TwinklingRefreshLayout refreshLayout)  

其中fraction表示当前下拉的距离与Header高度的比值(或者当前上拉距离与Footer高度的比值)。

### 3.Header and Footer
#### Header
**BezierLayout**(pic 1)，**GoogleDotView**(pic 2)，**SinaRefreshView**(pic 3)

####Footer
**BottomProgressView**(pic 2)，**LoadingView**(pic 3)，Here is more animations.[AVLoadingIndicatorView](https://github.com/81813780/AVLoadingIndicatorView)。

### 3.Personalize the Header and Footer.
The Header needs to implement IHeaderView interface and Footer in in the same way(IBottomView).
```java
public interface IHeaderView {
    View getView();

    void onPullingDown(float fraction,float maxHeadHeight,float headHeight);

    void onPullReleasing(float fraction,float maxHeadHeight,float headHeight);

    void startAnim(float maxHeadHeight,float headHeight);
}
```

getView() method is not allow to return null.

#### Let's implement a simple refresh dynamic efficiency.
1.Define SinaRefreshHeader extended from FrameLayout and implement IHeaderView interface.

2.Return this in the method getView(). 

3.Inflate and find Views in the layout xml.

```java
void init() {
        if (rootView == null) {
            rootView = View.inflate(getContext(), R.layout.view_sinaheader, null);
            refreshArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
            refreshTextView = (TextView) rootView.findViewById(R.id.tv);
            loadingView = (ImageView) rootView.findViewById(R.id.iv_loading);
            addView(rootView);
        }
    }
```

4.Override some methods.
```java
@Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) refreshTextView.setText(pullDownStr);
        if (fraction > 1f) refreshTextView.setText(releaseRefreshStr);
        refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);


    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            refreshTextView.setText(pullDownStr);
            refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);
            if (refreshArrow.getVisibility() == GONE) {
                refreshArrow.setVisibility(VISIBLE);
                loadingView.setVisibility(GONE);
            }
        }
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        refreshTextView.setText(refreshingStr);
        refreshArrow.setVisibility(GONE);
        loadingView.setVisibility(VISIBLE);
    }
```

5.layout xml.
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">
    <ImageView
        android:id="@+id/iv_arrow"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@drawable/ic_arrow"/>

    <ImageView
        android:id="@+id/iv_loading"
        android:visibility="gone"
        android:layout_width="34dp"
        android:layout_height="34dp"
        android:src="@drawable/anim_loading_view"/>

    <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="16dp"
        android:textSize="16sp"
        android:text="pull down to refresh"/>
</LinearLayout>
```

Pay attention to the using of the parameter `fraction`. Such as the code above`refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180)`，`fraction * headHeight` is the translationY of the Head and 180 is the angle the arrow would rotate，so that we can make the arrow rotate 180 degrees when the translationY is come to the maxHeadHeight.


onPullingDown/onPullingUp
onPullReleasing
startAnim - be called automatically after the method onRefresh/onLoadMore is called.

Congratulations! Simple to use and simple to Personalise.（To see a more simple example. **TextHeaderView(pic 4)**）。

## Update Logs
#### v1.03
- more attributes.
- Fix the NullPointerException bug in Fragment.
- Fix the Sliding conflict.