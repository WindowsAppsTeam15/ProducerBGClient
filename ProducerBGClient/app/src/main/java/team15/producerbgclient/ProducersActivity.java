package team15.producerbgclient;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

public class ProducersActivity extends AppCompatActivity {
    private  List<Producer> producers;
    private  ProducerAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producers);

        producers = new ArrayList<Producer>();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        new GetProducersTask().execute();
    }

    private class GetProducersTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return  getAllProducers();
        }

        @Override
        protected void onPostExecute(String producersStr) {
            ParseJsonData(producersStr);

            adapter = new ProducerAdapter(ProducersActivity.this, R.layout.list_item, producers);
            listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(adapter);
        }

        private  void ParseJsonData(String producersStr) {
            JSONArray json = null;
            try {
                json = new JSONArray(producersStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < json.length(); i++) {
                JSONObject producerJson = null;
                try {
                    producerJson  = json.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String name = null;
                try {
                    name = producerJson.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String type = null;
                try {
                    type = producerJson.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Producer producer = new Producer(name, type);
                producers.add(producer);
            }
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
            }
            catch (IOException e) {
                Log.e("ProducersActivity", "Error ", e);
                producersStr = null;
            }
            finally {
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

            return  producersStr;
        }
    }
}
