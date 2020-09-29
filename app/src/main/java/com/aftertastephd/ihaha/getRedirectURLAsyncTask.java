package com.aftertastephd.ihaha;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class getRedirectURLAsyncTask extends AsyncTask<String, Void, String> {
    private Exception e;

    protected String doInBackground(String... messages){
        String baseUrl = "http://tts.cyzon.us/tts?text=<";
        String finalizedUrl = "http://tts.cyzon.us";
        //again, this api sucks really bad
        try{
            baseUrl += messages[0] + "";
            Log.d("AsyncTask", baseUrl);
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            String header = connection.getHeaderField("Location");
            finalizedUrl+=header;
            connection.disconnect();

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(finalizedUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            return finalizedUrl;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return finalizedUrl;
    }
}
