package team15.producerbgclient;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    TextView producersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startService(new Intent(this, UpdateProducersService.class));

        Button goToRegisterButton = (Button) findViewById(R.id.btn_go_to_register);
        goToRegisterButton.setOnClickListener(this);
        Button goToAllProducersButton = (Button) findViewById(R.id.btn_all_producers);
        goToAllProducersButton.setOnClickListener(this);

        producersCount = (TextView) findViewById(R.id.tv_count_of_producers);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return;
        } else {
            new GetRegisteredProducersTask().execute();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.btn_go_to_register: {
                Intent intent = new Intent(HomeActivity.this, RegisterLoginActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_all_producers: {
                Intent intent = new Intent(HomeActivity.this, ProducersActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private class GetRegisteredProducersTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            try {
                return getCount();
            } catch (Exception e) {
                String errMessage = e.getMessage();
                return null;
            }
        }

        private String getCount() throws IOException {
            URL baseUrl = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers/count");
            HttpURLConnection urlConnection = (HttpURLConnection) baseUrl.openConnection();

            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode > 201) {
                String message = urlConnection.getResponseMessage();
                return null;
            }

            InputStream is = urlConnection.getInputStream();
            int numberOfChars;
            StringBuffer sb = new StringBuffer();
            while ((numberOfChars = is.read()) != -1) {
                sb.append((char) numberOfChars);
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                producersCount.setText(result + " registered producers on pBg");
            }
            return;
        }
    }
}
