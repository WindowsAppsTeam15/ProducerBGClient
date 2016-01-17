package team15.producerbgclient;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

public class ProducersActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private List<ContractProducer> producers;
    private ProducerAdapter adapter;
    private ListView listView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producers);

        producers = new ArrayList<>();
        linearLayout = (LinearLayout)findViewById(R.id.ll_progress);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = getIntent().getExtras();
        String query = null;
        if (bundle != null) {
            query = bundle.getString("Query");
        }

        new GetProducersTask().execute(query);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ProducersActivity.this, ProducerDetailsActivity.class);
        ContractProducer currentProducer = adapter.getItem(position);
        intent.putExtra("ProducerId", currentProducer.getId());
        startActivity(intent);
    }

    private class GetProducersTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return getAllProducers(params[0]);
        }

        @Override
        protected void onPostExecute(String producersStr) {
            ParseJsonData(producersStr);

            adapter = new ProducerAdapter(ProducersActivity.this, R.layout.list_item, producers);
            listView = (ListView) findViewById(R.id.lv_producers);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(ProducersActivity.this);
            linearLayout.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            linearLayout.setVisibility(View.VISIBLE);
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
                    producerJson = json.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ContractProducer producer = new ContractProducer();
                JSONArray logo = null;
                try {
                    logo = producerJson.getJSONArray("logo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                byte[] imgByteArr = null;
                if (logo != null) {
                    imgByteArr = new byte[logo.length()];
                    for (int j = 0; j < logo.length(); j++) {
                        try {
                            imgByteArr[j] = (byte) logo.getInt(j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    producer.setLogo(imgByteArr);
                }

                String id = null;
                try {
                    id = producerJson.getString("_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                producer.setId(id);

                String name = null;
                try {
                    name = producerJson.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                producer.setName(name);

                String type = null;
                try {
                    type = producerJson.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                producer.setType(type);
//
//                String description = null;
//                try {
//                    description = producerJson.getString("description");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                producer.setDescription(description);
                producers.add(producer);
            }
        }

        private String getAllProducers(String query) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String producersStr = null;

            try {
                URL url = null;
                if (query == null) {
                    url = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers");
                } else {
                    url = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers?name=" + query);
                }
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
}
