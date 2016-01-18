package team15.producerbgclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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

public class ProducerDetailsActivity extends BaseActivity implements View.OnLongClickListener, View.OnClickListener {
    private ImageView logo;
    private TextView name;
    private TextView type;
    private TextView description;
    private TextView email;
    private TextView phone;
    private TextView mainProducts;
    private ImageButton phoneButton;
    private ImageButton emailButton;
    private ImageButton editButton;
    private ImageButton deleteButton;
    private Button getDirections;
    Producer currentProducer;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_producer_details);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("ProducerId");

        editButton = (ImageButton) findViewById(R.id.ib_edit_producer);
        editButton.setOnClickListener(this);
        deleteButton = (ImageButton) findViewById(R.id.ib_delete_producer);
        deleteButton.setOnClickListener(this);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        new GetProducerDetailsTask().execute(id);
    }

    @Override
    public boolean onLongClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.ib_email: {
                String producerEmail = String.valueOf(email.getText());
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + producerEmail));
                    startActivity(intent);
                }
                catch (Exception e) {
                    return false;
                }
                break;
            }
            case R.id.ib_phone: {
                String producerPhoneNumber = String.valueOf(phone.getText());
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + producerPhoneNumber));
                startActivity(intent);
                break;
            }
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.ib_edit_producer: {
                Intent intent = new Intent(ProducerDetailsActivity.this, AddNewProducerActivity.class);
                intent.putExtra("ProducerId", id);
                startActivity(intent);
                break;
            }
            case R.id.ib_delete_producer: {
                // TODO: Fix
                Intent intent = new Intent(ProducerDetailsActivity.this, AddNewProducerActivity.class);
                intent.putExtra("ProducerId", id);
                startActivity(intent);
                break;
            }
        }
    }

    private class GetProducerDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            return getProducerById(id);
        }

        @Override
        protected void onPostExecute(String producerStr) {
            Gson gson = new Gson();

            try {
                currentProducer = gson.fromJson(producerStr, Producer.class);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentProducer != null) {
                logo = (ImageView) findViewById(R.id.iv_producer_details_logo);
                name = (TextView) findViewById(R.id.tv_producer_details_name);
                type = (TextView) findViewById(R.id.tv_producer_details_type);
                description = (TextView) findViewById(R.id.tv_producer_details_description);
                email = (TextView) findViewById(R.id.tv_producer_details_email);
                phone = (TextView) findViewById(R.id.tv_producer_details_phone);
                mainProducts = (TextView) findViewById(R.id.tv_main_products);
                emailButton = (ImageButton) findViewById(R.id.ib_email);
                emailButton.setOnLongClickListener(ProducerDetailsActivity.this);
                phoneButton = (ImageButton) findViewById(R.id.ib_phone);
                phoneButton.setOnLongClickListener(ProducerDetailsActivity.this);

                if (logo != null) {
                    if (currentProducer.getLogo().length != 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(currentProducer.getLogo(), 0, currentProducer.getLogo().length);
                        logo.setImageBitmap(bitmap);
                    } else {
                        logo.setImageResource(R.drawable.no_logo_available);
                    }
                }
                if (name != null) {
                    name.setText(currentProducer.getName());
                }
                if (type != null) {
                    type.setText(currentProducer.getType());
                }
                if (description != null) {
                    description.setText(currentProducer.getDescription());
                }
                if (email != null) {
                    email.setText(currentProducer.getEmail());
                }
                if (phone != null) {
                    phone.setText(currentProducer.getPhone());
                }
                if (mainProducts != null) {
                    mainProducts.setText(TextUtils.join("\n\n", currentProducer.getProducts()));
                }

                getDirections = (Button) findViewById(R.id.btn_get_directions);
                getDirections.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String latitude = currentProducer.getAddressLatitude();
                        String longitude = currentProducer.getAddressLongitude();
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
            }
        }

        private String getProducerById(String id) {
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

//        private Producer ParseJsonDataToProducer(String producerStr) {
//
//            JSONObject producerJson = null;
//            try {
//                producerJson = new JSONObject(producerStr);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            Producer producer = new Producer();
//
//            JSONArray image = null;
//            try {
//                image = producerJson.getJSONArray("logo");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            byte[] imgByteArr = null;
//            if (image != null) {
//                imgByteArr = new byte[image.length()];
//                for (int i = 0; i < image.length(); i++) {
//                    try {
//                        imgByteArr[i] = (byte) image.getInt(i);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                producer.setLogo(imgByteArr);
//            }
//
//            String id = null;
//            try {
//                id = producerJson.getString("_id");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            producer.setId(id);
//
//            String name = null;
//            try {
//                name = producerJson.getString("name");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            producer.setName(name);
//
//            String type = null;
//            try {
//                type = producerJson.getString("type");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            producer.setType(type);
//
//            String description = null;
//            try {
//                description = producerJson.getString("description");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            producer.setDescription(description);
//
//            String email = null;
//            try {
//                email = producerJson.getString("email");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            producer.setEmail(email);
//
//            String phone = null;
//            try {
//                phone = producerJson.getString("telephone");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            producer.setPhone(phone);
//
//            JSONArray mainProducts = null;
//            try {
//                mainProducts = producerJson.getJSONArray("mainProducts");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            List<String> products = new ArrayList<>();
//            for (int i = 0; i < mainProducts.length(); i++) {
//                try {
//                    products.add(mainProducts.getString(i));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            String[] productsArray = products.toArray(new String[products.size()]);
//            producer.setProducts(productsArray);
//
//            return producer;
//        }
    }
}
