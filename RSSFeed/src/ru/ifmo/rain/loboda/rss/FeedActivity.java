package ru.ifmo.rain.loboda.rss;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Map;

public class FeedActivity extends Activity {
    SQLRequester helper;
    int feedId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotations);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        helper = new SQLRequester(this);
        helper.open();
        feedId = bundle.getInt("feedId");
        filldata(feedId);
        registerReceiver(new MyReceiver(), new IntentFilter("ru.ifmo.rain.loboda.ACTION.UPDATE"));
        ((ListView)findViewById(R.id.records)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra("id", (int) l);
                startActivity(intent);
            }
        });
    }

    private void filldata(int feedId){
        try {
            Cursor cursor = helper.fetchArticlesByFeedId(feedId);
            startManagingCursor(cursor);
            String[] from = new String[]{SQLRequester.KEY_TITLE_ARTICLE};
            int[] to = new int[]{R.id.textView};
            SimpleCursorAdapter adapter =
                    new SimpleCursorAdapter(this, R.layout.record_row, cursor, from, to);
            ((ListView)findViewById(R.id.records)).setAdapter(adapter);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        helper.close();
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int gettedId = bundle.getInt("feedId");
            if(gettedId == feedId){
                filldata(feedId);
            }
            String flag = bundle.getString("toastFlag");
            if(flag != null && flag.equals("true")){
                Toast.makeText(getApplicationContext(), "Каналы обновлены", Toast.LENGTH_LONG).show();
            }
        }
    }
}