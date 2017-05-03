package com.example.pics;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
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

/**
 * @author haopi
 * @创建时间 2016年7月7日 下午9:38:45
 * @描述 TODO
 * 
 * @ 修改提交者:$Author$ @ 提交时间:$Date$ @ 当前版本 $Rev$
 * 
 */
public class PicsView extends RelativeLayout
{
	Context context;
	private ViewPager mViewPager;
	private LinearLayout mPointContainer;
	private TextView mTvTitle;

	List<ImageView> mListDatas = new ArrayList<ImageView>();
	String[] titles;

	private OnLunBoClickListener onLunBoClickListener;
	private LinearLayout mLl;

	Handler handler;
	private AutoScrollTask mAutoScrollTask;
	private MyAdapter mMyAdapter;
	
	private boolean isScrolling = false;

	public PicsView(Context context) {
		super(context, null);
	}

	public PicsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		handler = new Handler();

		initView();
	}

	/** 初始化视图 */
	private void initView() {
		View.inflate(context, R.layout.pics_view, this);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPointContainer = (LinearLayout) findViewById(R.id.point_container);
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		mLl = (LinearLayout) findViewById(R.id.ll);
	}

	/** 初始化数据，前提是设置图片及文字资源，两者数量一致 */
	private void initData() {
		// 初始化数据
		if (mListDatas != null) {
			for (int i = 0; i < mListDatas.size(); i++) {
				// 添加点点
				View point = new View(context);
				point.setBackgroundResource(R.drawable.point_normal);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
				if (i != 0) {
					params.leftMargin = 10;
				} else {
					point.setBackgroundResource(R.drawable.point_selected);

					mTvTitle.setText(titles[i]);
				}
				mPointContainer.addView(point, params);
			}
		}

		mMyAdapter = new MyAdapter();
		mViewPager.setAdapter(mMyAdapter);

		// 设置默认选中中间的item
		int middle = Integer.MAX_VALUE / 2;
		int extra = middle % mListDatas.size();
		int item = middle - extra;
		// 选中第item个
		mViewPager.setCurrentItem(item);
	}

	@SuppressWarnings("deprecation")
	private void initEvent() {
		// 设置viewPager监听器
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			// 回调方法,当viewpager滚动时的回调
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			// 回调方法,当viewpager的某个页面选中时的回调
			@Override
			public void onPageSelected(int position) {
				position = position % mListDatas.size();

				// 设置选中的点的样式
				int count = mPointContainer.getChildCount();
				for (int i = 0; i < count; i++) {
					View view = mPointContainer.getChildAt(i);

					view.setBackgroundResource(position == i ? R.drawable.point_selected : R.drawable.point_normal);
				}
				mTvTitle.setText(titles[position]);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		mViewPager.setOnTouchListener(new OnTouchListener() {

			private float mDownX;
			private float mDownY;
			private long mDownTime;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					System.out.println("按下");
					mDownX = event.getX();
					mDownY = event.getY();
					mDownTime = System.currentTimeMillis();
					stopAutoScroll();
					break;
				case MotionEvent.ACTION_MOVE:
					System.out.println("移动...");
					break;
				case MotionEvent.ACTION_UP:
					float upX = event.getX();
					float upY = event.getY();
					long upTime = System.currentTimeMillis();
					// 设置点击事件
					if (mDownX == upX && mDownY == upY) {
						if (upTime - mDownTime < 500) {
							// 点击
							System.out.println("点击");
							onLunBoClickListener.clickLunbo(mViewPager.getCurrentItem() % mListDatas.size());
						}
					}
					System.out.println("松开");
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

	/** 图片轮播ViewPager适配器 */
	class MyAdapter extends PagerAdapter
	{

		// 页面的数量
		@Override
		public int getCount() {
			if (mListDatas != null) {
				return Integer.MAX_VALUE;
			}
			return 0;
		}

		// 标记方法，用来判断缓存标记
		@Override
		public boolean isViewFromObject(View view, Object object) {
			// view:显示的view，object: 标记
			return view == object;
		}

		// 初始化item
		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			position = position % mListDatas.size();

			// position： 要加载的位置
			ImageView iv = mListDatas.get(position);

			// 用来添加要显示的View的
			mViewPager.addView(iv);

			// 记录缓存标记--return 标记
			return iv;
		}

		// 销毁item条目
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 销毁移除item， object:标记
			position = position % mListDatas.size();

			ImageView iv = mListDatas.get(position);
			mViewPager.removeView(iv);
		}
	}

	/** 获取自动轮播任务 */
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

	/** 自动轮播 */
	class AutoScrollTask implements Runnable
	{

		public void start() {
			handler.postDelayed(this, 1800);
		}

		public void stop() {
			handler.removeCallbacks(this);
		}

		@Override
		public void run() {
			int currentItem = mViewPager.getCurrentItem();
			currentItem++;
			mViewPager.setCurrentItem(currentItem);
			// 递归
			start();
		}
	}

	/** 开启自动轮播 */
	public void startAutoScroll() {
		if(!isScrolling){
			getAutoScrollTask().start();
			isScrolling = true;
		}
	}

	/** 停止自动轮播 */
	public void stopAutoScroll() {
		if(isScrolling){
			getAutoScrollTask().stop();
			isScrolling = false;
		}
	}

	/** 设置背景颜色 */
	public void setLlBackgroundAlph(int color) {
		mLl.setBackgroundColor(color);
	}

	/** 设置文字描述是否可见 */
	public void setTvTitleVisibility(int visibility) {
		mTvTitle.setVisibility(visibility);
	}

	/** 设置文字描述和图片数据 */
	public void setTitlesAndImages(String[] titles, List<ImageView> imgs) {
		this.titles = titles;
		this.mListDatas = imgs;

		initData();
		initEvent();
	}

	/** 设置监听 */
	public void setOnLunBoClickListener(OnLunBoClickListener onLunBoClickListener) {
		this.onLunBoClickListener = onLunBoClickListener;
	}

	/** 自定义监听接口 */
	public interface OnLunBoClickListener
	{
		void clickLunbo(int position);
	}

}
