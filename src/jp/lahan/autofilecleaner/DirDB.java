package jp.lahan.autofilecleaner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class DirDB {
	public interface DataColumns extends BaseColumns{
		public static final String DISPLAY_NAME = "_display_name";
		public static final String DIR = "dir";
		public static final String _ID = "_id";
		public static final String FILE_NUM = "file_num";		
	}
	
	private SQLiteDatabase dirDB;
	private static final String DATABASE_TABLE = "dirs";
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		private static final String DATABASE_NAME = "Directries";
		private static final int DATABASE_VERSION = 2;
		private static final String DATABASE_CREATE = "create table " 
					+ DATABASE_TABLE + " ("
					+ DataColumns._ID + " integer primary key autoincrement, "
					+ DataColumns._COUNT + " integer, "
					+ DataColumns.DISPLAY_NAME + " text, "
					+ DataColumns.DIR + " text not null, "
					+ DataColumns.FILE_NUM + " integer"
					+ ");";
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);			
		}			
		
		@Override
		public void onCreate(SQLiteDatabase db) {		
			System.out.println(DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Content provider database",
					"Upgrading database from version " + oldVersion + " to " + newVersion + ", wich will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}			
	}
	
	public DirDB(Context context) {	
		DatabaseHelper helper = new DatabaseHelper(context);
		dirDB = helper.getWritableDatabase();
	}
	
	public int delete(long id, String selection, String[] selectionArgs){
		int count = 0;
		// selectionが空かどうか判定して条件文字列を追加する
		count = dirDB.delete(DATABASE_TABLE,
								DataColumns._ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), 
								selectionArgs);
		return count;
	}
	
	public long insert(ContentValues values){
		long rowID = dirDB.insert(DATABASE_TABLE, "", values);
		if(rowID > 0){
			return rowID;
		}
		throw new SQLException("Failed to insert row");
	}
	
	public long insertWithCheck(ContentValues values){
		if(!hasDirectory(values.getAsString(DataColumns.DIR)))
			return insert(values);
		return -1;
	}
	
	public boolean hasDirectory(String dir){	
		Cursor c = dirDB.query(DATABASE_TABLE, null, DataColumns.DIR + " = '" + dir + "'", null, null, null, DataColumns._ID);
		System.out.println(c.getCount());
		return (c.getCount() > 0);
	}
	
	public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder){
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(DATABASE_TABLE);
		
		if(sortOrder == null || sortOrder.equals("")){
			sortOrder = DataColumns._ID;
		}
		
		Cursor c = sqlBuilder.query(dirDB, projection, selection, selectionArgs, null, null, sortOrder);
		
		return c;
	}
	
	public int update(long id, ContentValues values, String selection, String[] selectionArgs){		
		int count = 0;
		count = dirDB.update(DATABASE_TABLE, values, 
								DataColumns._ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
								selectionArgs);
		return count;
	}
}
