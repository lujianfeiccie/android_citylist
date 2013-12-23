package com.example.citylistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.citylistview.CityListView.ChineseCharComp;

public class MainActivity extends Activity {

	private CityListView mListView;
	private LinearLayout mLinearLayout;
	private List<String> data; //城市名称
	private DBManager dbmanager = null;
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
	}
	
	public void initData() {
		initCityData();
		Collections.sort(this.data, new ChineseCharComp());
		data.add(0, "root定位城市");
		data.add(1, "sub深圳市");
		data.add(2,"root热门城市");
		data.add(3,"sub北京市");
		data.add(4,"sub上海市");
		data.add(5,"sub广州市");
		mListView.init(mLinearLayout,data);
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
