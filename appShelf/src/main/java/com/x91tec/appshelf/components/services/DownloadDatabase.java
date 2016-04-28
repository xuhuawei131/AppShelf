package com.x91tec.appshelf.components.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.x91tec.appshelf.storage.IOUtils;
import com.x91tec.appshelf.storage.SQLiteController;

/**
 * Created by oeager on 16-3-6.
 */
public class DownloadDatabase extends SQLiteOpenHelper {

    public final static String DB_NAME = "myDownloads";

    public final static int DB_VERSION = 1;

    public final static String TABLE_NAME = "table_download";

    public final static String COLUMN_ID = "_id";

    public final static String COLUMN_TAG_ID = "taskId";

    public final static String COLUMN_URI = "uri";

    private final SQLiteController.Table table ;

    private DownloadDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        table = new SQLiteController.Table(TABLE_NAME);
        table.addColumn(COLUMN_ID,SQLiteController.TYPE_INTEGER,SQLiteController.buildAutoIncrementPrimaryKey());
        table.addColumn(COLUMN_TAG_ID,SQLiteController.TYPE_INTEGER,SQLiteController.CONSTRAINT_NOT_NULL);
        table.addColumn(COLUMN_URI, SQLiteController.TYPE_TEXT, SQLiteController.CONSTRAINT_UNIQUE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteController.createTable(db, table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SQLiteController.dropTable(db,TABLE_NAME);
        onCreate(db);
    }

    private static DownloadDatabase database = null;

    public static DownloadDatabase get(Context context){
        if(database==null){
            synchronized (DownloadDatabase.class){
                if(database==null){
                    database = new DownloadDatabase(context);
                }
            }
        }
        return database;
    }


    public int matchTaskId(String uri){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor =db.query(true, TABLE_NAME, null, COLUMN_URI + " = ?", new String[]{uri}, null, null, COLUMN_ID+" desc", null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                int taskId = cursor.getInt(cursor.getColumnIndex(COLUMN_TAG_ID));
                IOUtils.closeQuietly(cursor);
                return taskId;
            }
        }
        IOUtils.closeQuietly(cursor);
        return -1;
    }

    public synchronized long insertRecords(long taskId,String uri){
        SQLiteDatabase db = getWritableDatabase();
        int matchId = matchTaskId(uri);
        if(matchId>0){
            db.delete(TABLE_NAME,COLUMN_URI+" = ?",new String[]{uri});
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_TAG_ID, taskId);
        values.put(COLUMN_URI, uri);
        return db.insert(TABLE_NAME,null,values);

    }

    public synchronized int deleteByTaskId(long taskId){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME,COLUMN_TAG_ID+" = ?",new String[]{String.valueOf(taskId)});
    }
}
