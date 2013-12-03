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
 版权所有：版权所有(C)2013，固派软件 文件名称：com.goopai.obd.database.DBManager.java 系统编号：
 * 系统名称：OBD 模块编号： 模块名称： 设计文档： 创建日期：2013-8-21 上午1:40:32 作 者：陆键霏 内容摘要：数据库管理
 * 类中的代码包括三个区段：类变量区、类属性区、类方法区。 文件调用:
 */

public class DBManager {
	private String tag = "DBManager";
	private final int BUFFER_SIZE = 1024;
	public static final String DB_NAME = "selfdrive.db"; // 保存的数据库文件名
	public static final String PACKAGE_NAME = "com.example.citylistview"; // 包名
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/databases"; // 在手机里存放数据库的位置
	public static final String TB_TROUBLE_AND_FAULT = "tb_common"; // 故障提醒与故障诊断公用表*
	public static final String TB_REGION = "GPREGION"; // 省市表
	
	/**
	 * 创建SQLITE TABLE
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
	 * 创建数据库
	 */
	public void createDatabase() {
		File file = new File(DB_PATH);
		if (!file.exists()) {
			file.mkdir();
		}
		this.createDatabase(DB_PATH + "/" + DB_NAME);
	}

	/**
	 * 创建IM所需表
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
	 * 打开数据库
	 */
	public SQLiteDatabase openDatabase() {
		/** 用getWriteableDatabase()方法不稳定，有时候会报错 */
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
	 * 关闭数据库
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
			if (!(new File(dbfile).exists())) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream is = this.context.getResources().openRawResource(R.raw.selfdrive); // 欲导入的数据库
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
	 * 执行sql 参数：sql 要执行的sql
	 **/
	public void execSQL(String sql) {
		database.execSQL(sql);
	}

	/**
	 * 执行SQL
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
	 * 插入数据 参数：tableName 表名 initialValues 要插入的列对应值
	 * */
	public long insert(String tableName, ContentValues initialValues) {
		return database.insert(tableName, null, initialValues);
	}

	/**
	 * 删除数据 参数：tableName 表名 deleteCondition 删除的条件 deleteArgs
	 * 如果deleteCondition中有“？”号，将用此数组中的值替换
	 * */
	public boolean delete(String tableName, String deleteCondition, String[] deleteArgs) {
		return database.delete(tableName, deleteCondition, deleteArgs) > 0;
	}

	/**
	 * 更新数据 参数：tableName 表名 initialValues 要更新的列 selection 更新的条件 selectArgs
	 * 如果selection中有“？”号，将用此数组中的值替换
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
	 * 取得一个列表 参数：tableName 表名 columns 返回的列 selection 查询条件 selectArgs
	 * 如果selection中有“？”号，将用此数组中的值替换
	 * */
	public Cursor findList(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return database.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	/**
	 * 取得单行记录 参数：tableName 表名 columns 返回的列 selection 查询条件 selectArgs
	 * 如果selection中有“？”号，将用此数组中的值替换
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
	 * 从数据库里取得所有城市
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