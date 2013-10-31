package ru.ifmo.rain.loboda.rss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.util.Log;

public class SQLRequester {

    private DatabaseHelper helper;
    private SQLiteDatabase database;
    private Context context;
    private boolean isOpened;

    private static final String DATABASE_NAME = "RSSFeedDB";
    private static final String DATABASE_TABLE_FEEDS = "feeds";
    private static final String DATABASE_TABLE_ARTICLES = "articles";
    public static final String KEY_URL_FEED = "url";
    public static final String KEY_NAME_FEED = "name";
    public static final String KEY_ID_FEED = "_id";
    public static final String KEY_TIME_FEED = "time";
    public static final String KEY_ID_ARTICLE = "_id";
    public static final String KEY_ID_FEED_ARTICLE = "id_feed";
    public static final String KEY_TIME_RECEIVE_ARTICLE = "received";
    public static final String KEY_TITLE_ARTICLE = "title";
    public static final String KEY_DESC_ARTICLE = "description";
    private static final int DATABASE_VERSION = 2;
    private static final String[] DATABASE_CREATE =
    {"CREATE TABLE " + DATABASE_TABLE_FEEDS + " (" + KEY_ID_FEED + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_URL_FEED + " TEXT NOT NULL, " + KEY_NAME_FEED + " TEXT NOT NULL, " + KEY_TIME_FEED + " INTEGER);",
     "CREATE TABLE " + DATABASE_TABLE_ARTICLES + " (" + KEY_ID_ARTICLE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
           KEY_ID_FEED_ARTICLE + " NOT NULL, " + KEY_TIME_RECEIVE_ARTICLE + " INTEGER, " + KEY_TITLE_ARTICLE + " TEXT, " +
           KEY_DESC_ARTICLE +" TEXT)"};

    public SQLRequester(Context context){
        this.context = context;
        isOpened = false;
    }

    public void open(){
        helper = new DatabaseHelper(context);
        database = helper.getWritableDatabase();
        isOpened = true;
    }

    public void close(){
        isOpened = false;
        helper.close();
    }

    public Cursor fetchAllFeeds() throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        return database.query(DATABASE_TABLE_FEEDS, null, null, null, null, null, KEY_ID_FEED + " desc", null);
    }

    public void deleteArticles(int feedId) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        database.delete(DATABASE_TABLE_ARTICLES, KEY_ID_FEED_ARTICLE + "=" + (new Integer(feedId)).toString(), null);
    }

    public void delete(int id) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        database.delete(DATABASE_TABLE_FEEDS, KEY_ID_FEED + "=?", new String[]{(new Integer(id)).toString()});
        database.delete(DATABASE_TABLE_ARTICLES, KEY_ID_FEED_ARTICLE + "=" + (new Integer(id)).toString(), null);
    }

    public void insertArticle(int feedId, String title, String description) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        ContentValues values = new ContentValues();
        values.put(KEY_ID_FEED_ARTICLE, feedId);
        values.put(KEY_TITLE_ARTICLE, title);
        values.put(KEY_DESC_ARTICLE, description);
        values.put(KEY_TIME_RECEIVE_ARTICLE, (new Integer((int)(System.currentTimeMillis()/1000))).toString());
        database.insert(DATABASE_TABLE_ARTICLES, null, values);
    }

    public void updateFeed(int id, String name, String url) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_FEED, name);
        values.put(KEY_URL_FEED, url);
        values.put(KEY_TIME_FEED, (new Integer((int)(System.currentTimeMillis()/1000))).toString());
        database.update(DATABASE_TABLE_FEEDS, values, KEY_ID_FEED + "="+(new Integer(id)).toString(), null);
    }

    public void insertFeed(String name, String url) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_FEED, name);
        values.put(KEY_URL_FEED, url);
        values.put(KEY_TIME_FEED, (new Integer((int)(System.currentTimeMillis()/1000))).toString());
        database.insert(DATABASE_TABLE_FEEDS, null, values);
    }

    public Cursor fetchFeedById(int id) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        return database.query(DATABASE_TABLE_FEEDS, null, KEY_ID_FEED + "=" + (new Integer(id)).toString(), null, null, null, null);
    }

    public Cursor fetchArticleById(int id) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        return database.query(DATABASE_TABLE_ARTICLES, null, KEY_ID_ARTICLE + "=" + (new Integer(id)).toString(), null, null, null, null);
    }

    public Cursor fetchArticlesByFeedId(int feedId) throws SQLiteException {
        if(!isOpened){
            throw new SQLiteException("");
        }
        return database.query(DATABASE_TABLE_ARTICLES, null, KEY_ID_FEED_ARTICLE + "=" + (new Integer(feedId)).toString(), null, null, null, KEY_ID_ARTICLE + " asc", null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for(int i = 0; i < DATABASE_CREATE.length; ++i){
                Log.d("TABLE CREATE", DATABASE_CREATE[i]);
                db.execSQL(DATABASE_CREATE[i]);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ARTICLES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FEEDS);
            onCreate(db);
        }
    }
}
