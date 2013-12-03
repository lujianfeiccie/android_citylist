package com.example.citylistview;

import android.app.Application;

/*
版权所有：版权所有(C)2013，固派软件
文件名称：com.example.citylistview.MyApplication.java
系统编号：
系统名称：citylistview
模块编号：
模块名称：
设计文档：
创建日期：2013-12-3 上午1:59:43
作 者：陆键霏
内容摘要：
类中的代码包括三个区段：类变量区、类属性区、类方法区。
文件调用:
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化数据库，没有则创建
		DBManager mDBManager = new DBManager(this);
		mDBManager.createDatabase();
		mDBManager.openDatabase();
		mDBManager.createTables();
		mDBManager.closeDatabase();
	}
}


