package com.example.citylistview;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


/*
版权所有：版权所有(C)2013，固派软件
文件名称：com.goopai.selfdrive.ui.custom.CityListView.java
系统编号：
系统名称：SelfDrive
模块编号：
模块名称：
设计文档：
创建日期：2013-12-2 下午11:48:13
作 者：陆键霏
内容摘要：
类中的代码包括三个区段：类变量区、类属性区、类方法区。
文件调用:
 */
public class CityListView extends ListView implements OnTouchListener{

	private static String[] LETTERS = new String[] { "#", "A", "B", "C", "D",
		"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
		"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	
	private final class RemoveWindow implements Runnable {
		public void run() {
			invisibleWindow();
		}
	}
	private int mLettersLength = LETTERS.length;
	
	private HashMap<String, Integer> mAlphaIndexMap = new HashMap<String, Integer>();
	
	private boolean isShowDialogText;
	
	private WindowManager mWindowManager;
	private TextView mDialogText;
	private LayoutInflater inflater;
	private LinearLayout mLinearLayout;
	Handler mHandler = new Handler();
	private RemoveWindow mRemoveWindow = new RemoveWindow();
	
	public CityListView(Context context) {
		this(context,null);
	}

	public CityListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		inflater=LayoutInflater.from(context);
	}

	public void init(LinearLayout right,List<String> data){
	    this.mLinearLayout=right;
		if(null ==mLinearLayout){
			throw new RuntimeException("you must set this right linearLayout for this listview");
		}
		for (int i = 0; i < LETTERS.length; i++) {
			TextView textView = new TextView(getContext());
			textView.setText(LETTERS[i]);
			textView.setTextSize(16);
			textView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 0, 1.0f));
			;
			textView.setPadding(4, 0, 2, 0);
			mLinearLayout.addView(textView);
		}
		mLinearLayout.setOnTouchListener(this);
		setAdapter(new DataAdapter(data));
		mDialogText = (TextView) inflater.inflate(R.layout.found_citylist_activity_textview, null);
		mDialogText.setVisibility(View.INVISIBLE);
		if(!isShowDialogText){
			addWindow();
		}
	}
	private class DataAdapter extends BaseAdapter {
		LayoutInflater inflater;
		String temp = "-1";
		List<String> data=null;
		public DataAdapter(List<String> data) {
			if(null ==data){
				throw new NullPointerException("adapter data is null");
			}
			this.data=data;
			this.inflater = LayoutInflater.from(getContext());
			for (int i = 0; i < this.data.size(); i++) {
				String alpha=PinyinUtils.getFirstLetter(this.data.get(i)).toUpperCase();
				if (isLetter(alpha)) {
					if (!alpha.equals(temp)) {
						mAlphaIndexMap.put(alpha, i);
						temp = alpha;
					}
				} else {
					mAlphaIndexMap.put("#", 0);
				}
			}
		}

		@Override
		public int getCount() {
			return this.data.size();
		}

		@Override
		public String getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.found_citylist_activity_item, null);
				holder.tvAlphaHeader = (TextView) convertView
						.findViewById(R.id.tv_alphabar);
				holder.tvData = (TextView) convertView
						.findViewById(R.id.tv_data);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			 String item_temp  = getItem(position);
             if(item_temp.startsWith("root")){ //特殊情况：定位城市、热门城市
             		holder.tvAlphaHeader.setVisibility(View.VISIBLE);
             		holder.tvAlphaHeader.setText(item_temp.replace("root", ""));
             		holder.tvData.setVisibility(View.GONE);
             }else if(item_temp.startsWith("sub")){ 
             		holder.tvAlphaHeader.setVisibility(View.GONE);
             		holder.tvData.setText(item_temp.replace("sub", ""));
             		holder.tvData.setVisibility(View.VISIBLE);
             }
             else{
             	String alpha=PinyinUtils.getFirstLetter(item_temp).toUpperCase();
             	if (!isLetter(alpha))
             		alpha = "#";
             	if (mAlphaIndexMap.get(alpha) == position) {
             		holder.tvAlphaHeader.setVisibility(View.VISIBLE);
             		holder.tvAlphaHeader.setText(alpha);
             	} else {
             		holder.tvAlphaHeader.setVisibility(View.GONE);
             	}
             	holder.tvData.setText(item_temp);
             }
			return convertView;
		}
	}
	public void addWindow(){
		if(isShowDialogText) return;
		if(mWindowManager!=null&&mDialogText!=null){
			mHandler.post(new Runnable() {
				public void run() {
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT,
							WindowManager.LayoutParams.TYPE_APPLICATION,
							WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
									| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
							PixelFormat.TRANSLUCENT);
					mWindowManager.addView(mDialogText, lp);
				}
			});
			isShowDialogText=true;
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int height = v.getHeight();
		final int alphaHeight = height / mLettersLength;
		final int fingerY = (int) event.getY();
		int selectIndex = fingerY / alphaHeight;
		if (selectIndex < 0 || selectIndex > mLettersLength - 1) {// 防止越界
			mLinearLayout.setBackgroundResource(android.R.color.transparent);
			mHandler.removeCallbacks(mRemoveWindow);
    		mHandler.post(mRemoveWindow); 
			return true;
		}
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			String letter = LETTERS[selectIndex];
			findLocation(letter);
			break;
		case MotionEvent.ACTION_MOVE:
			letter = LETTERS[selectIndex];
			findLocation(letter);
			break;
		case MotionEvent.ACTION_UP:
			mLinearLayout.setBackgroundResource(android.R.color.transparent);
			mHandler.removeCallbacks(mRemoveWindow);
    		mHandler.post(mRemoveWindow);
			break;
		default:
			break;
		}
		return true;
	}
	public void invisibleWindow() {
		if(mDialogText!=null){
			mDialogText.setVisibility(View.INVISIBLE);
		}
    }
	
	public void removeWindow(){
		if(mWindowManager != null){
			mWindowManager.removeView(mDialogText);
			isShowDialogText=false;
		}
	}
	
	private void findLocation(String letter){
		if(mAlphaIndexMap.containsKey(letter)){			
			mLinearLayout.setBackgroundResource(android.R.color.darker_gray);
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText(letter);
			int position = mAlphaIndexMap.get(letter);
			setSelection(position);
		}
	}
	/**
	 * 中文按首个英文字母排序
	 */
	public static class ChineseCharComp implements Comparator {
		public int compare(Object o1, Object o2) {

		Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);

		if (myCollator.compare(o1, o2) < 0){
			return -1;
		}
		else if (myCollator.compare(o1, o2) > 0){
			return 1;
		}
		else
			return 0;

		}
	}
	
	private boolean isLetter(String str) {
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Matcher m = pattern.matcher(str);
		return m.matches();
	}
	static class ViewHolder {
		public TextView tvAlphaHeader;
		public TextView tvData;
	}
}


