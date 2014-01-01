package com.example.citylistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.citylistview.CityListView.ChineseCharComp;

public class MainActivity extends Activity {

	private CityListView mListView;
	private LinearLayout mLinearLayout;
	private List<String> data; //城市名称
	private DBManager dbmanager = null;
	private TextView txt_loading = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		initEvent();
	}
	public void initView() {
		dbmanager = new DBManager(this);
		mListView = (CityListView)findViewById(R.id.lv_citylist);
		mLinearLayout = (LinearLayout) findViewById(R.id.ll_indicator);
		txt_loading = (TextView)findViewById(R.id.txt_loading);
	}
	
	public void initData() {
		txt_loading.setVisibility(View.VISIBLE);
		new Thread()
		{
			public void run() {
				initCityData();
				Collections.sort(data, new ChineseCharComp());
				data.add(0, "root定位城市");
				data.add(1, "sub深圳市");
				data.add(2,"root热门城市");
				data.add(3,"sub北京市");
				data.add(4,"sub上海市");
				data.add(5,"sub广州市");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						txt_loading.setVisibility(View.GONE);
						mListView.init(mLinearLayout,data);
					}
				});
			};
		}.start();
	}
	
	public void initEvent() {
		//mListView.setOnItemClickListener(this);
	}
	/**
	 * 初始化汽车品牌筛选界面所需数据 
	 */
	private void initCityData(){
		data=new ArrayList<String>();
		dbmanager.openDatabase();
		data = dbmanager.getAllCities();
		dbmanager.closeDatabase();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mListView !=null){
			mListView.invisibleWindow();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mListView !=null){
			mListView.removeWindow();
		}
	}
}
