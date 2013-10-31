package ru.ifmo.rain.loboda.rss;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MyService extends IntentService {
    public MyService() {
        super("TaramPapPapPam");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RSSParser parser = new RSSParser();
        SQLRequester helper = new SQLRequester(this);
        Bundle bundle = intent.getExtras();
        String toastFlag = bundle.getString("toastFlag");
        helper.open();
        try {
            Cursor cursor = helper.fetchAllFeeds();
            while(cursor.moveToNext()){
                String url = cursor.getString(cursor.getColumnIndexOrThrow(SQLRequester.KEY_URL_FEED));
                int feedId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLRequester.KEY_ID_FEED));
                List<RSSRecord> records = parser.parse((new URL(url).openStream()));
                helper.deleteArticles(feedId);
                for(int i = 0; i < records.size(); ++i){
                    helper.insertArticle(feedId, records.get(i).getAnnotation(), records.get(i).getDescription());
                }
                Intent broadcastIntent = new Intent("ru.ifmo.rain.loboda.ACTION.UPDATE");
                broadcastIntent.putExtra("feedId", feedId);
                sendBroadcast(broadcastIntent);
            }
            if(toastFlag != null && toastFlag.equals("true")){
                Intent broadcastIntent = new Intent("ru.ifmo.rain.loboda.ACTION.UPDATE");
                broadcastIntent.putExtra("toastFlag", "true");
                sendBroadcast(broadcastIntent);
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
