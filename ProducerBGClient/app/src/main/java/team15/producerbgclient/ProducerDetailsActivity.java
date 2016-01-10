package team15.producerbgclient;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProducerDetailsActivity extends AppCompatActivity {
    private String id;
    private TextView name;
    private TextView type;
    private TextView description;
    private TextView email;
    private ListView mainProducts;
    private List<String> sampleProductsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_details);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("ProducerId");

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        new GetProducerDetailsTask().execute();
    }

    private class GetProducerDetailsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return getProducerById();
        }

        @Override
        protected void onPostExecute(String producerStr) {
            Producer producer = ParseJsonData(producerStr);
            if (producer != null) {
                name = (TextView)findViewById(R.id.tv_producer_details_name);
                type = (TextView)findViewById(R.id.tv_producer_details_type);
                description = (TextView)findViewById(R.id.tv_producer_details_description);
                email = (TextView)findViewById(R.id.tv_producer_details_email);
                mainProducts = (ListView)findViewById(R.id.lv_main_products);
                sampleProductsList = new ArrayList<>();
                sampleProductsList.add("Product 1");
                sampleProductsList.add("Product 2");
                sampleProductsList.add("Product 3");
                sampleProductsList.add("Product 4");
                sampleProductsList.add("Product 5");
                sampleProductsList.add("Product 6");
                sampleProductsList.add("Product 7");

                if (name != null) {
                    name.setText(producer.getName());
                }
                if (type != null) {
                    type.setText(producer.getType());
                }
                if (description != null) {
                    description.setText(producer.getDescription());
                }
                if (email != null) {
                    email.setText(producer.getEmail());
                }
                if (mainProducts != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            ProducerDetailsActivity.this,
                            android.R.layout.simple_list_item_1,
                            sampleProductsList);
                    mainProducts.setAdapter(adapter);
                }
            }
        }

        private String getProducerById() {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String producerStr = null;

            try {
                URL url = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers/" + id);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    producerStr = null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    producerStr = null;
                }

                producerStr = buffer.toString();
            } catch (IOException e) {
                Log.e("ProducerDetailsActivity", "Error ", e);
                producerStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ProducerDetailsActivity", "Error closing stream", e);
                    }
                }
            }

            return producerStr;
        }

        private Producer ParseJsonData(String producerStr) {
            JSONObject producerJson = null;
            try {
                producerJson = new JSONObject(producerStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String id = null;
            try {
                id = producerJson.getString("_id");
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
            String description = null;
            try {
                description = producerJson.getString("description");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String email = null;
            try {
                email = producerJson.getString("email");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Producer producer = new Producer();
            producer.setId(id);
            producer.setName(name);
            producer.setType(type);
            producer.setDescription(description);
            producer.setEmail(email);
            return producer;
        }
    }
}
