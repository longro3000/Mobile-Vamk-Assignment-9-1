package com.example.example95;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    public static final String KEY_URL = "url";
    public static final String KEY_CONTENT = "content";

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "urlData.db";
    private static final String TABLE_NAME = "urlContent";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CREATE = "create table " +
            TABLE_NAME +
            " (" + KEY_URL + " text primary key unique not null, "
            + KEY_CONTENT + " text not null)";

    private static final String TABLE_DELETE = "drop table if exists " + TABLE_NAME;

    private Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqlLiteDb;

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    // Define the DatabaseHelper class
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(TABLE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ". All old data will be deleted! ");
            // Remove the table
            db.execSQL(TABLE_DELETE);
            // Create the table again
            onCreate(db);
        }
    }

    // Open the database
    public DBAdapter open() {
        sqlLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    // Close the database
    public void close() {
        dbHelper.close();
    }

    // Add a contact to the database
    public long addUrl(String url, String content) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_CONTENT, content);
        int id = (int) sqlLiteDb.insertWithOnConflict(TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            return sqlLiteDb.update(TABLE_NAME, initialValues, KEY_URL + "=" + url, null);
        }
        return id;
        // return sqlLiteDb.insert(TABLE_NAME, null, initialValues);
    }

    // This method will retrieve all url data
    public Cursor getAllUrl() {
        return sqlLiteDb.query(TABLE_NAME, new String[] {
                KEY_URL,
                KEY_CONTENT
        }, null, null, null, null, null);
    }

    public Cursor getUrl(String url) {
        String[] query = new String[1];
        query[0] = url;
        Cursor cursor = sqlLiteDb.query(true, TABLE_NAME, new String[] {
                        KEY_URL,
                        KEY_CONTENT
                }, KEY_URL+"=?",
                new String[]{url}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    // Update a url
    public boolean updateUrl(String url, String content) {
        ContentValues values = new ContentValues();
        values.put(KEY_URL, url);
        values.put(KEY_CONTENT, content);
        return (sqlLiteDb.update(TABLE_NAME, values, KEY_URL + "=" + url,
                null) > 0);
    }
}
