package com.example.citylistview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

/**
 * 
 ��Ȩ���У���Ȩ����(C)2013��������� �ļ����ƣ�com.goopai.obd.database.DBManager.java ϵͳ��ţ�
 * ϵͳ���ƣ�OBD ģ���ţ� ģ�����ƣ� ����ĵ��� �������ڣ�2013-8-21 ����1:40:32 �� �ߣ�½���� ����ժҪ�����ݿ����
 * ���еĴ�������������Σ���������������������෽������ �ļ�����:
 */

public class DBManager {
	private String tag = "DBManager";
	private final int BUFFER_SIZE = 1024;
	public static final String DB_NAME = "selfdrive.db"; // ��������ݿ��ļ���
	public static final String PACKAGE_NAME = "com.example.citylistview"; // ����
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/databases"; // ���ֻ��������ݿ��λ��
	public static final String TB_TROUBLE_AND_FAULT = "tb_common"; // ���������������Ϲ��ñ�*
	public static final String TB_REGION = "GPREGION"; // ʡ�б�
	
	/**
	 * ����SQLITE TABLE
	 * */
	private static final String SQL_CREATE_TB_TROUBLE_AND_FAULT = "create table if not exists " + TB_TROUBLE_AND_FAULT + " " + "(_vin VCHAR(17) NOT NULL," + " _sessionid LONG PRIMARY KEY NOT NULL,"
			+ " _msgtype VARCHAR(30) NOT NULL," + " _readstate INTEGER NOT NULL," + " _msgtime VARCHAR(30) NOT NULL" + ")";


	private SQLiteDatabase database;
	private Context context;
	private boolean isopened=false;

	public DBManager(Context context) {
		this.context = context;
	}
	
	public boolean isOpened(){
		return this.isopened;
	}

	/**
	 * �������ݿ�
	 */
	public void createDatabase() {
		File file = new File(DB_PATH);
		if (!file.exists()) {
			file.mkdir();
		}
		this.createDatabase(DB_PATH + "/" + DB_NAME);
	}

	/**
	 * ����IM�����
	 */
	public void createTables() {
		if (this.database != null) {
//			try {
//				this.execSQL(SQL_CREATE_TB_TROUBLE_AND_FAULT);
//			}
//			catch (SQLException exception) {
//				exception.printStackTrace();
//			}
		}
	}
	/**
	 * �����ݿ�
	 */
	public SQLiteDatabase openDatabase() {
		/** ��getWriteableDatabase()�������ȶ�����ʱ��ᱨ�� */
		try {
			closeDatabase();
			database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
			if (database == null) throw new NullPointerException("open database is null");
			isopened=true;
		}
		catch (SQLiteException e) {
			System.out.println("can't open the database!");
			this.database = null;
		}
		return database;
	}

	/**
	 * �ر����ݿ�
	 */
	public void closeDatabase() {
		try {
			if (database != null && database.isOpen()) {
				this.database.close();
				isopened=false;
				database = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createDatabase(String dbfile) {
		try {
			if (!(new File(dbfile).exists())) {// �ж����ݿ��ļ��Ƿ���ڣ�����������ִ�е��룬����ֱ�Ӵ����ݿ�
				InputStream is = this.context.getResources().openRawResource(R.raw.selfdrive); // ����������ݿ�
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		}
		catch (FileNotFoundException e) {
			Log.e("Database", "File not found");
			e.printStackTrace();
		}
		catch (IOException e) {
			Log.e("Database", "IO exception");
			e.printStackTrace();
		}
	}

	/**
	 * ִ��sql ������sql Ҫִ�е�sql
	 **/
	public void execSQL(String sql) {
		database.execSQL(sql);
	}

	/**
	 * ִ��SQL
	 * 
	 * @param sql
	 * @param bindArgs
	 */
	public void execSQL(String sql, Object[] bindArgs) {
		try {
			database.execSQL(sql, bindArgs);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �������� ������tableName ���� initialValues Ҫ������ж�Ӧֵ
	 * */
	public long insert(String tableName, ContentValues initialValues) {
		return database.insert(tableName, null, initialValues);
	}

	/**
	 * ɾ������ ������tableName ���� deleteCondition ɾ�������� deleteArgs
	 * ���deleteCondition���С������ţ����ô������е�ֵ�滻
	 * */
	public boolean delete(String tableName, String deleteCondition, String[] deleteArgs) {
		return database.delete(tableName, deleteCondition, deleteArgs) > 0;
	}

	/**
	 * �������� ������tableName ���� initialValues Ҫ���µ��� selection ���µ����� selectArgs
	 * ���selection���С������ţ����ô������е�ֵ�滻
	 * */
	public boolean update(String tableName, ContentValues initialValues, String selection, String[] selectArgs) {
		int returnValue = -1;
		this.openDatabase();
		try {
			if (database != null) {
				returnValue = database.update(tableName, initialValues, selection, selectArgs);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeDatabase();
		}
		return returnValue > 0;
	}

	/**
	 * ȡ��һ���б� ������tableName ���� columns ���ص��� selection ��ѯ���� selectArgs
	 * ���selection���С������ţ����ô������е�ֵ�滻
	 * */
	public Cursor findList(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return database.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	/**
	 * ȡ�õ��м�¼ ������tableName ���� columns ���ص��� selection ��ѯ���� selectArgs
	 * ���selection���С������ţ����ô������е�ֵ�滻
	 * */
	public Cursor findInfo(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, boolean distinct)
			throws SQLException {
		Cursor mCursor = database.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/**
	 * �����ݿ���ȡ�����г���
	 * @return
	 */
	public List<String> getAllCities(){
		List<String> list = new ArrayList<String>();
		Cursor returnCursor = this.findList(DBManager.TB_REGION, new String[] { "region_name" }, "parent_id != 1 and region_type = 2", null, null, null, "region_id desc");
		while (returnCursor.moveToNext()) {
			String region_name = returnCursor.getString(returnCursor.getColumnIndexOrThrow("region_name"));
			log(String.format("region_name=%s", region_name));
			list.add(region_name);
		}
		returnCursor.close();
		return list;
	}
	public void showAllTable(){
        Cursor cursor = null;
        try {
            String sql = "select * from sqlite_master where type ='table'";
            cursor = database.rawQuery(sql, null);
            while(cursor.moveToNext()){
            	String tb_name = cursor.getString(cursor.getColumnIndexOrThrow("tbl_name"));
            	log("tablename="+tb_name);
            }
            cursor.close();
        } catch (Exception e) {
                // TODO: handle exception
        }                
	}
	void log(String msg) {
		Log.d(tag, msg);
	}
}