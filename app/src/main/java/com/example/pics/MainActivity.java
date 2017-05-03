package com.example.pics;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.example.pics.PicsView.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private int[] images = { R.drawable.img_0, R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4,
			R.drawable.img_5 };
	private String[] titles = { "吾问无为谓", "梦想信息", "忐忐忑忑", "更换合格", "顶顶顶顶", "啊啊啊啊" };
	private PicsView mPicsViewpager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mPicsViewpager = (PicsView) findViewById(R.id.picsviewpager);
		List<ImageView> imgList = new ArrayList<>();
		for(int i = 0; i < images.length; i++){
			ImageView iv = new ImageView(getApplicationContext());
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(images[i]);
			imgList.add(iv);
		}
		// 初始化数据
		mPicsViewpager.addTitlesAndImages(titles, imgList);
		//mPicsViewpager.addImages(imgList);
		// 设置点击事件
		mPicsViewpager.setClickListener(new OnClickListener() {
			@Override
			public void onClick(int position) {
				System.out.println("点击有效");
				Toast.makeText(MainActivity.this, "点击有效，位置为：" + position, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	protected void onPause() {
		// 停止图片轮播
		mPicsViewpager.stopAutoScroll();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// 开启图片轮播
		mPicsViewpager.startAutoScroll();
		super.onResume();
	}
}
