package com.example.pics;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author haopi
 * @创建时间 2016年7月7日 下午9:38:45
 * @描述 TODO
 * @ 修改提交者:$Author$ @ 提交时间:$Date$ @ 当前版本 $Rev$
 */
public class PicsView extends RelativeLayout {
    private long timeDelayed = 2000;
    private Context context;
    private ViewPager mViewPager;
    private List<ImageView> images = new ArrayList<>();
    private String[] titles;
    private OnClickListener onClickListener;
    private RelativeLayout mRl;
    private Handler handler;
    private AutoScrollTask mAutoScrollTask;
    private boolean isScrolling = false;
    private LinearLayout points;
    private int pointW = 5;
    private int pointH = 5;
    private int pointsPosition = RelativeLayout.ALIGN_PARENT_LEFT;
    private int titlePosition = RelativeLayout.ALIGN_PARENT_RIGHT;
    private TextView title;
    private int textColor = 0xFFFFFFFF;

    public PicsView(Context context) {
        super(context, null);
    }

    public PicsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        handler = new Handler();
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        View.inflate(context, R.layout.pics_view, this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mRl = (RelativeLayout) findViewById(R.id.rl);
    }

    /**
     * 初始化数据，前提是设置图片及文字资源，两者数量一致
     */
    private void initData() {
        points = new LinearLayout(context);
        RelativeLayout.LayoutParams llP = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        llP.setMargins(dp2px(pointW) * 2, dp2px(pointH), dp2px(pointW) * 2, dp2px(pointH));
        llP.addRule(RelativeLayout.CENTER_VERTICAL);
        llP.addRule(pointsPosition);
        title = new TextView(context);
        RelativeLayout.LayoutParams llT = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        title.setPadding(dp2px(pointW) * 2, dp2px(pointH), dp2px(pointW) * 2, dp2px(pointH));
        title.setTextColor(textColor);
        llT.addRule(RelativeLayout.CENTER_VERTICAL);
        llT.addRule(titlePosition);
        // 初始化数据
        for (int i = 0; i < images.size(); i++) {
            // 添加点点
            View point = new View(context);
            point.setBackgroundResource(R.drawable.point_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(pointW), dp2px(pointH));
            if (i != 0) {
                params.leftMargin = dp2px(pointW);
            } else {
                point.setBackgroundResource(R.drawable.point_selected);
                if (titles != null) {
                    title.setText(titles[i]);
                }
            }
            if (points != null) {
                points.addView(point, params);
            }
        }
        mRl.addView(points, llP);
        mRl.addView(title, llT);
        PictureCarouselAdapter carouselAdapter = new PictureCarouselAdapter();
        mViewPager.setAdapter(carouselAdapter);
        // 设置默认选中中间的item
        int middle = Integer.MAX_VALUE / 2;
        int extra = middle % images.size();
        int item = middle - extra;
        // 选中第item个
        mViewPager.setCurrentItem(item);
    }

    private void initEvent() {

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (points != null) {
                    position = position % images.size();
                    // 设置选中的点的样式
                    int count = points.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View view = points.getChildAt(i);
                        view.setBackgroundResource(position == i ? R.drawable.point_selected : R.drawable.point_normal);
                    }
                }
                if (titles != null) {
                    title.setText(titles[position]);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setOnTouchListener(new OnTouchListener() {

            private float mDownX;
            private float mDownY;
            private long mDownTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = event.getX();
                        mDownY = event.getY();
                        mDownTime = System.currentTimeMillis();
                        stopAutoScroll();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        float upX = event.getX();
                        float upY = event.getY();
                        long upTime = System.currentTimeMillis();
                        // 设置点击事件
                        if (mDownX == upX && mDownY == upY) {
                            if (upTime - mDownTime < 500) {
                                onClickListener.onClick(mViewPager.getCurrentItem() % images.size());
                            }
                        }
                        // 抬起开启自动轮播
                        startAutoScroll();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

    }

    private class PictureCarouselAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (images != null) {
                return Integer.MAX_VALUE;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % images.size();
            // pointsPosition： 要加载的位置
            ImageView iv = images.get(position);
            // 用来添加要显示的View的
            mViewPager.addView(iv);
            // 记录缓存标记--return 标记
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 销毁移除item， object:标记
            position = position % images.size();
            ImageView iv = images.get(position);
            mViewPager.removeView(iv);
        }
    }

    public int dp2px(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 获取自动轮播任务
     */
    public AutoScrollTask getAutoScrollTask() {
        if (mAutoScrollTask == null) {
            synchronized (this) {
                if (mAutoScrollTask == null) {
                    mAutoScrollTask = new AutoScrollTask();
                }
            }
        }
        return mAutoScrollTask;
    }

    /**
     * 自动轮播
     */
    private class AutoScrollTask implements Runnable {

        private void start() {
            handler.postDelayed(this, timeDelayed);
        }

        private void stop() {
            handler.removeCallbacks(this);
        }

        @Override
        public void run() {
            int currentItem = mViewPager.getCurrentItem();
            currentItem++;
            mViewPager.setCurrentItem(currentItem);
            start();
        }
    }

    /**
     * 开启自动轮播
     */
    public void startAutoScroll() {
        if (!isScrolling) {
            getAutoScrollTask().start();
            isScrolling = true;
        }
    }

    /**
     * 停止自动轮播
     */
    public void stopAutoScroll() {
        if (isScrolling) {
            getAutoScrollTask().stop();
            isScrolling = false;
        }
    }

    /**
     * 设置文字背景颜色
     */
    public void setTitleBackgroundColor(int color) {
        if (mRl != null) {
            mRl.setBackgroundColor(color);
        }
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * 设置图片轮播间隔
     */
    public void setTimeDelayed(long timeDelayed) {
        this.timeDelayed = timeDelayed > 0 ? timeDelayed : 2000;
    }

    /**
     * 同时设置文字描述和图片数据
     */
    public void addTitlesAndImages(String[] titles, List<ImageView> images) {
        this.titles = titles;
        this.images = images;
        if (titles == null || titles.length == 0) {
            mRl.setVisibility(GONE);
        }
        if (images != null && images.size() > 0) {
            initData();
            initEvent();
        }
    }

    /**
     * 添加轮播图片
     */
    public void addImages(List<ImageView> images) {
        this.images = images;
        if (titles == null || titles.length == 0) {
            mRl.setVisibility(GONE);
        }
        if (images != null && images.size() > 0) {
            initData();
            initEvent();
        }
    }

    /**
     * 设置点的宽
     */
    public void setPointW(int pointW) {
        this.pointW = pointW;
    }

    /**
     * 设置点的高
     */
    public void setPointH(int pointH) {
        this.pointH = pointH;
    }
    /**
     * 设置点的位置
     * @param  pointsPosition RelativeLayout.ALIGN_PARENT_LEFT...
     */
    public void setPointsPosition(int pointsPosition) {
        this.pointsPosition = pointsPosition;
    }

    /**
     * 设置标题的位置
     * @param  titlePosition RelativeLayout.ALIGN_PARENT_RIGHT...
     */
    public void setTitlePosition(int titlePosition) {
        this.titlePosition = titlePosition;
    }

    /**
     * 设置监听
     */
    public void setClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * 自定义监听接口
     */
    public interface OnClickListener {
        void onClick(int position);
    }

}
