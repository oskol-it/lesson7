package ru.ifmo.rain.loboda.rss;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    SQLRequester helper;

    public static final int DELETE_ID = Menu.FIRST;
    public static final int MODIFY_ID = Menu.FIRST + 1;

    private void startAlarm(int millis){
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("task", "UPDATE");
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent == null){
            pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millis, millis, pendingIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startAlarm(15 * 60 * 1000);

        helper = new SQLRequester(this);
        helper.open();
        filldata();

        ListView listView = (ListView)findViewById(R.id.listView);
        registerForContextMenu(listView);
        registerReceiver(new MyReceiver(), new IntentFilter("ru.ifmo.rain.loboda.ACTION.UPDATE"));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), FeedActivity.class);
                intent.putExtra("feedId", (int)l);
                startActivity(intent);
            }
        });

        (findViewById(R.id.addbutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddChannel.class);
                startActivityForResult(intent, 200);
            }
        });

        (findViewById(R.id.refreshbutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MyService.class);
                intent.putExtra("toastFlag", "true");
                startService(intent);
            }
        });

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, DELETE_ID, 0, "Удалить");
                contextMenu.add(0, MODIFY_ID, 0, "Изменить");
            }
        });
    }

    @Override
    public boolean onMenuItemSelected (int featureId, MenuItem item){
        try {
            switch (item.getItemId()){
                case DELETE_ID:
                    helper.delete((int)((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id);
                    break;
                case MODIFY_ID:
                    Intent intent = new Intent(this, AddChannel.class);
                    intent.putExtra("id", (int)((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id);
                    startActivityForResult(intent, 200);
                    break;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        filldata();
        return true;
    }

    private void filldata(){
        try {
            Cursor cursor = helper.fetchAllFeeds();
            startManagingCursor(cursor);
            String[] from = new String[]{SQLRequester.KEY_NAME_FEED};
            int[] to = new int[]{R.id.textView};
            SimpleCursorAdapter adapter =
                    new SimpleCursorAdapter(this, R.layout.listrow, cursor, from, to);
            ((ListView)findViewById(R.id.listView)).setAdapter(adapter);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        helper.close();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            Bundle bundle = data.getExtras();
            try {
                if(bundle.getInt("id") == 0){
                    helper.insertFeed(bundle.getString("name"), bundle.getString("url"));
                } else {
                    helper.updateFeed(bundle.getInt("id"), bundle.getString("name"), bundle.getString("url"));
                }
                Intent intent = new Intent(this, MyService.class);
                intent.putExtra("task", "UPDATE");
                startService(intent);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            filldata();
        }
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String flag = bundle.getString("toastFlag");
            if(flag != null && flag.equals("true")){
                Toast.makeText(getApplicationContext(), "Каналы обновлены", Toast.LENGTH_LONG).show();
            }
        }
    }
}
