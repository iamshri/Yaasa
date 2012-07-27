package com.shrini.yaasa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

//FILE NOT IN USE
public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "Yaasa/" + DBHelper.class.getSimpleName();
	public static final String DB_NAME = "timeline.db";
	public static final int DB_VERSION = 1;
	public static final String C_ID = BaseColumns._ID; // special
	public static final String C_CREATED_AT = "createdAt";
	public static final String C_TEXT = "ytxt";
	public static final String C_USER = "yusr";
	public static final String TABLE = "statuses";
	Context context;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String
				.format("create table %s (%s INT primary key, %s INT, %s TEXT, %s TEXT)",
						TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT);
		db.execSQL(sql);
		Log.d(TAG, "onCreate: " + sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE);
		Log.d(TAG, "On Upgrade dropped table: " + TABLE);
		this.onCreate(db);
	}

}
