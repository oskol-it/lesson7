package ru.ifmo.rain.loboda.rss;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebView;

public class DetailsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int id = bundle.getInt("id");
        SQLRequester helper = new SQLRequester(this);
        helper.open();
        try {
            Cursor cursor = helper.fetchArticleById(id);
            cursor.moveToNext();
            String title = cursor.getString(cursor.getColumnIndexOrThrow(SQLRequester.KEY_TITLE_ARTICLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(SQLRequester.KEY_DESC_ARTICLE));
            String article = "<h3>" + title + "</h3>" + description;
            ((WebView)findViewById(R.id.webView)).loadData(article, "text/html; charset=UTF-8", null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}