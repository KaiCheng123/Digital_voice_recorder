package com.example.a31372.digital_voice_recorder.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import com.example.a31372.digital_voice_recorder.Record.FileUtils;
import com.example.a31372.digital_voice_recorder.util.RecordingItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;
    FileUtils fileUtils = new FileUtils();

    private static final String LOG_TAG = "DBHelper";

    public static final String DATABASE_NAME = "saved_recordings.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DBHelperItem implements BaseColumns {
        public static final String TABLE_NAME = "saved_recordings";

        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBHelperItem.TABLE_NAME + " (" +
                    DBHelperItem._ID + " INTEGER PRIMARY KEY" + "," +
                    DBHelperItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + "," +
                    DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + "," +
                    DBHelperItem.COLUMN_NAME_RECORDING_LENGTH + " integer " + "," +
                    DBHelperItem.COLUMN_NAME_TIME_ADDED + TEXT_TYPE + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBHelperItem.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Toast.makeText(mContext, "创建数据库成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * 获取录音文件的信息,存入数据库
     */
    public void addRecording(){
        File[] files = fileUtils.fileslist();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DBHelperItem.TABLE_NAME,null,null);
        for (File spec : files){
            ContentValues cv = new ContentValues();
            int length = 0;
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(spec.lastModified()));
            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(spec.getAbsolutePath());
                mediaPlayer.prepare();
                length = mediaPlayer.getDuration();
                Log.e("test", length+"");
            } catch (IOException e) {
                e.printStackTrace();
            }
            cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, spec.getName());
            cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, spec.getAbsolutePath());
            cv.put(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH, length/1000);
            cv.put(DBHelperItem.COLUMN_NAME_TIME_ADDED, time);
            db.insert(DBHelperItem.TABLE_NAME, null, cv);
        }
    }

    /**
     * 获取数据库音频文件的数量
     * @return
     */
    public int getRecordCount(){
        int recordCount;
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DBHelperItem._ID,
                DBHelperItem.COLUMN_NAME_RECORDING_NAME,
                DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH,
                DBHelperItem.COLUMN_NAME_RECORDING_LENGTH,
                DBHelperItem.COLUMN_NAME_TIME_ADDED
        };
        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        recordCount = c.getCount();
        return recordCount;
    }

    /**
     * 将音频信息填入列表
     * @param position
     * @return
     */
    public RecordingItem getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DBHelperItem._ID,
                DBHelperItem.COLUMN_NAME_RECORDING_NAME,
                DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH,
                DBHelperItem.COLUMN_NAME_RECORDING_LENGTH,
                DBHelperItem.COLUMN_NAME_TIME_ADDED
        };
        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            RecordingItem item = new RecordingItem();
            item.setId(c.getInt(c.getColumnIndex(DBHelperItem._ID)));
            item.setName(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_NAME)));
            item.setFilePath(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH)));
            item.setLength(c.getInt(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH)));
            item.setTime(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TIME_ADDED)));
            return item;
        }else {
            return null;
        }
    }
}
