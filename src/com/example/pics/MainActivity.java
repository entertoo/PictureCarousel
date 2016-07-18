package com.example.pics;

import java.util.ArrayList;
import java.util.List;

import com.example.pics.PicsView.OnLunBoClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class MainActivity extends Activity {

	int[] imgs = { R.drawable.img_0, R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4,
			R.drawable.img_5 };
	String[] titles = { "为", "梦", "想", "坚", "持", "呀" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		PicsView picsviewpager = (PicsView) findViewById(R.id.picsviewpager);

		List<ImageView> imgList = new ArrayList<ImageView>();
		for(int i = 0; i < imgs.length; i++){
			ImageView iv = new ImageView(getApplicationContext());
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(imgs[i]);
			imgList.add(iv);
		}
		// 初始化数据
		picsviewpager.setTitlesAndImages(titles, imgList);
		
		// 设置点击事件
		picsviewpager.setOnLunBoClickListener(new OnLunBoClickListener() {
			
			@Override
			public void clickLunbo(int position) {
				System.out.println("点击有效");
				Toast.makeText(MainActivity.this, "点击有效，位置为：" + position, Toast.LENGTH_SHORT).show();
			}
		});

		//picsviewpager.setLlBackgroundAlph(color.transparent);
		//picsviewpager.setTvTitleVisibility(View.GONE);

	}
}
