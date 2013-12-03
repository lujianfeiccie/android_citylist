package com.example.citylistview;

import android.app.Application;

/*
��Ȩ���У���Ȩ����(C)2013���������
�ļ����ƣ�com.example.citylistview.MyApplication.java
ϵͳ��ţ�
ϵͳ���ƣ�citylistview
ģ���ţ�
ģ�����ƣ�
����ĵ���
�������ڣ�2013-12-3 ����1:59:43
�� �ߣ�½����
����ժҪ��
���еĴ�������������Σ���������������������෽������
�ļ�����:
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// ��ʼ�����ݿ⣬û���򴴽�
		DBManager mDBManager = new DBManager(this);
		mDBManager.createDatabase();
		mDBManager.openDatabase();
		mDBManager.createTables();
		mDBManager.closeDatabase();
	}
}


