package com.shrini.yaasa;

import winterwell.jtwitter.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StatusData {
	private static final String TAG = StatusData.class.getSimpleName();
	public static final String C_ID = BaseColumns._ID; // special
	public static final String C_CREATED_AT = "createdAt";
	public static final String C_TEXT = "ytxt";
	public static final String C_USER = "yusr";

	Context context;
	DBHelper dbHelper;

	public StatusData(Context context) {
		this.context = context;
		dbHelper = new DBHelper();
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor query() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(DBHelper.TABLE, null, null, null, null, null,
				C_CREATED_AT + " DESC");

	}

	public long insert(ContentValues values) {
		long ret;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			ret = db.insertOrThrow(DBHelper.TABLE, null, values);
		} catch (SQLException e) {
			return -1;
			//e.printStackTrace();
		} finally {
			db.close();
		}
		return ret;
	}

	// Delete all data
	public void delete() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DBHelper.TABLE, null, null);
		db.close();
	}

	public long insert(Status status) {
		ContentValues values = new ContentValues();
		values.put(StatusData.C_ID, status.getId().longValue());
		values.put(StatusData.C_CREATED_AT, status.createdAt.getTime());
		values.put(StatusData.C_USER, status.user.name);
		values.put(StatusData.C_TEXT, status.text);
		return this.insert(values);
	}

	// Class to handle DB operations
	private class DBHelper extends SQLiteOpenHelper {
		// private static final String TAG = "Yaasa/" +
		// DBHelper.class.getSimpleName();
		public static final String DB_NAME = "timeline.db";
		public static final int DB_VERSION = 1;
		public static final String TABLE = "statuses";

		// Context context;

		public DBHelper() {
			super(context, DB_NAME, null, DB_VERSION);

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

}
