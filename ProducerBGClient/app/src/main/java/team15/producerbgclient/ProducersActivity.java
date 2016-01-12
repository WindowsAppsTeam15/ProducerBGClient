package team15.producerbgclient;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ProducersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private  List<Producer> producers;
    private  ProducerAdapter adapter;
    private ListView listView;

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ProducersActivity.this, ProducerDetailsActivity.class);
        Producer currentProducer = adapter.getItem(position);
        intent.putExtra("ProducerId", currentProducer.getId());
        startActivity(intent);
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
            listView = (ListView)findViewById(R.id.lv_producers);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(ProducersActivity.this);
        }

        private void ParseJsonData(String producersStr) {
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

                Producer producer = new Producer();
                JSONObject image = null;
                try {
                    image = producerJson.getJSONObject("img");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject data = null;
                if (image != null) {
                    try {
                        data = image.getJSONObject("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSONArray dataArr = null;
                if (data != null) {
                    try {
                        dataArr = data.getJSONArray("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                byte[] imgByteArr = null;
                if (dataArr != null) {
                    imgByteArr = new byte[dataArr.length()];
                    for (int j = 0; j < dataArr.length(); j++) {
                        try {
                            imgByteArr[j] = (byte) dataArr.getInt(j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    producer.setLogo(imgByteArr);
                }

                String id = null;
                try {
                    id = producerJson.getString("_id");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                producer.setId(id);

                String name = null;
                try {
                    name = producerJson.getString("name");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                producer.setName(name);

                String type = null;
                try {
                    type = producerJson.getString("type");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                producer.setType(type);

                String description = null;
                try {
                    description = producerJson.getString("description");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                producer.setDescription(description);
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
