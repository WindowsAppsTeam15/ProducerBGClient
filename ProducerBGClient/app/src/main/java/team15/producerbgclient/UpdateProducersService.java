package team15.producerbgclient;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

/**
 * Created by veso on 1/17/2016.
 */
public class UpdateProducersService extends IntentService {
    Context context = this;
    Handler handler = null;
    static Runnable runnable = null;
    ProducerDbHelper currentDb = null;

    public UpdateProducersService() {
        super("UpdateProducersService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        currentDb = new ProducerDbHelper(this);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                // Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                currentDb.emptyProducersDb();
                String stringProducers = getAllProducers();
                currentDb.fillProducerDb(stringProducers);

                handler.postDelayed(runnable, 30000);
            }
        };

        handler.postDelayed(runnable, 10);
    }

    @Override
    public void onCreate() {


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    private String getAllProducers() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String producersStr = null;

        try {
            URL url = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                producersStr = null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                producersStr = null;
            }

            producersStr = buffer.toString();
        } catch (IOException e) {
            Log.e("ProducersActivity", "Error ", e);
            producersStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ProducersActivity", "Error closing stream", e);
                }
            }
        }

        return producersStr;
    }

}
