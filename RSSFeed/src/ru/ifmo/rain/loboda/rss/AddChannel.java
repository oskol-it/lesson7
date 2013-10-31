package ru.ifmo.rain.loboda.rss;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class AddChannel extends Activity {
    private Bundle bundle;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addchannel);
        Intent intent = getIntent();
        bundle = intent.getExtras();

        SQLRequester helper = new SQLRequester(this);
        helper.open();
        if(bundle != null && bundle.getInt("id") != 0){
            try {
                Cursor cursor = helper.fetchFeedById(bundle.getInt("id"));
                cursor.moveToNext();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(SQLRequester.KEY_NAME_FEED));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(SQLRequester.KEY_URL_FEED));
                ((TextView)findViewById(R.id.channelurl)).setText(url);
                ((TextView)findViewById(R.id.name)).setText(name);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        (findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = ((EditText)findViewById(R.id.channelurl)).getText().toString().trim();
                String name = ((EditText)findViewById(R.id.name)).getText().toString().trim();
                if("".equals(url)){
                    Toast.makeText(view.getContext(), "URL должен быть не пустым", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    URL check = new URL(url);
                    check.toURI();
                } catch (MalformedURLException e) {
                    Toast.makeText(view.getContext(), "URL имеет неверный формат", Toast.LENGTH_LONG).show();
                    return;
                } catch (URISyntaxException e) {
                    Toast.makeText(view.getContext(), "URL имеет неверный формат", Toast.LENGTH_LONG).show();
                    return;
                }
                if("".equals(name)){
                    Toast.makeText(view.getContext(), "Имя не должно быть пустым", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("url", url);
                intent.putExtra("name", name);
                if(bundle != null && bundle.getInt("id") != 0){
                    intent.putExtra("id", bundle.getInt("id"));
                }
                setResult(200, intent);
                finish();
            }
        });
    }
}