/*   
 *  ""
 *    
 */
package com.aeye.db;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aeye.helibao.MyApplication;



/**
 * sql manager 操作入口�? 入读本地数据库openLocal 读取系统数据库open
 * 
 * @author 
 * @Version 1.0
 */
public class SqlManager {

	/** 上下�? */
	private static Context context = null;
	/** sql实例 */
	private SQLiteDatabase db = null;
	/** helper实例 */
	private SqlHelper helper = null;
	/** mamager 实例 */
	private static SqlManager instance;

	/**
	 * 获取实例
	 * 
	 */
	public static SqlManager get() {
		if (instance == null) {
			instance = new SqlManager();
		}
		return instance;
	}

	public void clearHistory(int type) {
		open();
		db.execSQL("delete from history where type = " + type);
		close();
	}

	static {
		context = MyApplication.getApp();
	}

	private SqlManager() {
		if (helper == null) {
			helper = new SqlHelper(context);
		}
	}

	/** 打开 */
	private void open() {
		close();
		if (null == db || !db.isOpen()) {
			db = helper.getWritableDatabase();
		}
	}

	private void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	
	public void clearFinger() {
		ModelFingerDB dto = new ModelFingerDB();
		String tableName = dto.getClass().getSimpleName();
		open();
		db.execSQL("delete   from "+tableName);
		close();
	}

	public synchronized String queryNameByCardId(String id) {
		ModelFingerDB dto = new ModelFingerDB();
		boolean success = false;
		String tableName = dto.getClass().getSimpleName();
		try {
			if ((null == db) || !db.isOpen())
				open();
			StringBuilder sql = new StringBuilder("select name from ");
			sql.append(tableName);
			if (id instanceof String) {
				sql.append(" where idCard = '").append(id).append("'");
			} else {
				sql.append(" where idCard = ").append(id);
			}
			Cursor cursor = db.rawQuery(sql.toString(), null);
			
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				return cursor.getString(cursor.getColumnIndex("name"));
			}
			cursor.close();
			success = true;
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 插入实体�?
	 * @param dto  插入的实体类
	 * @return   ture false
	 */
	public synchronized boolean insertDTO(Serializable dto) {
		boolean success = false;
		String tableName = dto.getClass().getSimpleName();
		try {
			if ((null == db) || !db.isOpen())
				open();
			StringBuilder sql = new StringBuilder();
			// 插入
			sql.setLength(0);
			sql.append("insert into ").append(tableName).append(" ( ");
			Field fs[] = dto.getClass().getDeclaredFields();
			for (Field f : fs) {
				if (f.getType() == String.class || f.getType() == Double.class || f.getType() == Long.class
						|| f.getType() == Integer.class || f.getType() == Date.class) {
					sql.append(f.getName()).append(" ,");
				}
			}
			sql.setCharAt(sql.length() - 1, ' ');
			sql.append(" ) ");
			sql.append(" values ( ");
			for (Field f : fs) {
				f.setAccessible(true);
				if (f.getType() == String.class) {
					sql.append("'").append(f.get(dto) == null ? "" : f.get(dto)).append("' ,");
				} else if (f.getType() == Double.class || f.getType() == Long.class || f.getType() == Integer.class) {
					sql.append(f.get(dto) == null ? 0 : f.get(dto)).append(" ,");
				} else if (f.getType() == Date.class) {
					sql.append(f.get(dto) == null ? System.currentTimeMillis() : ((Date) f.get(dto)).getTime())
							.append(" ,");
				}
			}
			sql.setCharAt(sql.length() - 1, ' ');
			sql.append(" ) ");
			db.execSQL(sql.toString());
			success = true;
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * 返回指静�? 模板信息
	 * @param dto
	 * @return
	 */
	public ArrayList<ModelFingerDB> queryModelFingerList() {
		ModelFingerDB dto = new ModelFingerDB();
		ArrayList list =  new ArrayList<ModelFingerDB>();
		try {
			if ((null == db) || !db.isOpen()) {
			open();
			}
			StringBuilder sql = new StringBuilder("select ");
			String tableName = dto.getClass().getSimpleName();
			Field fs[] = dto.getClass().getDeclaredFields();
			for (Field f : fs) {
				if (f.getType() == String.class
						|| f.getType() == Double.class
						|| f.getType() == Long.class
						|| f.getType() == Integer.class
						|| f.getType() == Date.class) {
					sql.append(f.getName()).append(" , ");
				}
			}
			sql.deleteCharAt(sql.length() - 2);
			sql.append(" from " + tableName);
			
			Cursor cursor = db.rawQuery(sql.toString(), null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					dto = new ModelFingerDB();
					for (Field f : fs) {
						f.setAccessible(true);
						int index = cursor.getColumnIndex(f.getName());
						if (f.getType() == String.class) {
							f.set(dto, cursor.getString(index));
						} else if (f.getType() == Double.class) {
							f.set(dto, cursor.getDouble(index));
						} else if(f.getType() == Long.class){
							f.set(dto, cursor.getLong(index));
						} else if(f.getType() == Integer.class){
							f.set(dto, cursor.getInt(index));
						} else if(f.getType() == Date.class){
							f.set(dto, new Date(cursor.getLong(index)));
						}
					}
					list.add(dto);
					if(dto.getFeatureFinger1() !=null){
						ModelFingerDB dto1 = dto;
						//dto1.setFeatureFinger(dto.getFeatureFinger1());
						list.add(dto1);
					}
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}
	/**
	 * 返回模板的个�?
	 * @param dto
	 * @return
	 */
	public int queryModelSize(Serializable dto) {
		try {
			if ((null == db) || !db.isOpen()) {
			open();
			}
			StringBuilder sql = new StringBuilder("select ");
			String tableName = dto.getClass().getSimpleName();
			Field fs[] = dto.getClass().getDeclaredFields();
			for (Field f : fs) {
				if (f.getType() == String.class
						|| f.getType() == Double.class
						|| f.getType() == Long.class
						|| f.getType() == Integer.class
						|| f.getType() == Date.class) {
					sql.append(f.getName()).append(" , ");
				}
			}
			sql.deleteCharAt(sql.length() - 2);
			sql.append(" from " + tableName);
			
			Cursor cursor = db.rawQuery(sql.toString(), null);
			if (cursor != null && cursor.getCount() > 0) {
				return cursor.getCount();
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return 0;
	}
	
}
