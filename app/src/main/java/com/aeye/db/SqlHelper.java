/*   
 *  ""
 *    
 */
package com.aeye.db;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * 鏁版嵁搴撳鐞嗙被
 * 
 * @author 
 * @Version 1.0
 */
public class SqlHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "aeyebeiqi";
	private final static int version = 2;


	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public SqlHelper(Context context, String name, CursorFactory factory, int version) {
		//super(context, name, factory, version);
		super(new CustomPathDatabaseContext(context, getDirPath()), name, factory, version);
	}

	/**
     * 获取db文件在sd卡的路径
     * @return
     */
    private static String getDirPath(){
            //TODO 这里返回存放db的文件夹的绝对路径
    	String dir = null;
        if(Environment.getExternalStorageState().equals("mounted")) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            dir = Environment.getDataDirectory().getAbsolutePath();
        }

        String sdPath = dir + "/" + "md" + "/" ;
        File dest = new File(sdPath);
        if(!dest.exists()) {
            dest.mkdirs();
        }
        
        return sdPath;
    }
    
	/**
	 * @param context
	 */
	public SqlHelper(Context context) {
		this(context, DB_NAME, null, version);
	}

	/**
	 * 寤鸿〃
	 * 
	 * @param db
	 */
	private void createTable(SQLiteDatabase db) {
		db.execSQL(createSql(ModelFingerDB.class));
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 *鏍规嵁class 鐢熸垚sql 璇彞
	 * 
	 *@param dto 绫?
	 *@return
	 */
	public static String createSql (Class c){
		Field fs[] = c.getDeclaredFields();
		StringBuilder sql = new StringBuilder("create table if not exists " + c.getSimpleName() + "( ");
		sql.append("row_id integer primary key autoincrement , ");
		for(Field f : fs)
		{ 
			if(f.getType() == String.class){
				sql.append(f.getName() + " text,");
			}else if(f.getType() == Date.class || f.getType() == Double.class || f.getType() == Long.class || f.getType() == Integer.class){
				sql.append(f.getName() + " integer,");
			}else if(f.getType() instanceof Serializable){
				
			}
			else{
				sql.append(f.getName() + " text");
			}
		}
		if(sql.toString().endsWith(","))
		{
			sql.setCharAt(sql.length()-1, ' ');
		}
		sql.append(")");
		return sql.toString();
	}
}
