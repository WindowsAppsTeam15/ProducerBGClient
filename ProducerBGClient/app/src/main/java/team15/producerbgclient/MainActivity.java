package team15.producerbgclient;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView title;
    EditText username;
    EditText password;
    EditText confirmPass;
    EditText email;
    Button sumbitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.titleRegisterForm);

        sumbitBtn = (Button) findViewById(R.id.submitButton);
        sumbitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = (EditText) findViewById(R.id.usernameInput);
                password = (EditText) findViewById(R.id.passwordInput);
                confirmPass = (EditText) findViewById(R.id.confirmPassInput);
                email = (EditText) findViewById(R.id.emailInput);

                Pattern pattern = Pattern.compile("([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})");
                Matcher matcher = pattern.matcher(email.getText().toString().trim());

                if (username.getText().toString().length() == 0) {
                    username.setError("Username is required!");
                    return;
                } else if (username.getText().toString().length() < 6) {
                    username.setError("Username should be at least 6 characters long!");
                    return;
                } else if (password.getText().toString().length() == 0) {
                    password.setError("Password is required!");
                    return;
                } else if (password.getText().toString().length() < 6) {
                    password.setError("Password should be at least 6 characters long!");
                    return;
                } else if (!password.getText().toString().equals(confirmPass.getText().toString())) {
                    String pass = password.getText().toString();
                    String conf = confirmPass.getText().toString();
                    confirmPass.setError("Password and confirm pass should be equal!");
                    return;
                } else if (!matcher.matches()) {
                    email.setError("You have entered invalid email!");
                    return;
                }

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject jsonUser = new JSONObject();
                try {
                    jsonUser.put("username", username.getText().toString());
                    jsonUser.put("password", password.getText().toString());
                    jsonUser.put("email", email.getText().toString().trim());
                } catch (Exception e) {
                    // Return exception
                }

                new RegisterUserTask().execute(jsonUser);
            }
        });
    }

    private class RegisterUserTask extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {

            try {
                return registerUser(params[0]);
            } catch (Exception e) {
                String errMessage = e.getMessage();
                Log.e("connection error", "Unable to connect");
                return null;
            }
        }

        private String registerUser(JSONObject userToRegister) throws IOException {
            HttpURLConnection urlConnection = null;

            InputStream is = null;

            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            JSONObject result = new JSONObject();
            String userJsonStr = null;

            try {
                URL baseUrl = new URL("https://murmuring-mountain-9323.herokuapp.com/api/users");
                urlConnection = (HttpURLConnection) baseUrl.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(userToRegister.toString());
                out.close();

                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                Log.d("HTTPTHeFuck", "The response is: " + responseCode);

                is = urlConnection.getInputStream();
                int numberOfChars;
                StringBuffer sb = new StringBuffer();
                while ((numberOfChars = is.read()) != -1) {
                    sb.append((char) numberOfChars);
                }
                return sb.toString();

//                return urlConnection.getResponseMessage();

            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            String result = new String(buffer);
            return result;
        }

        @Override
        protected void onPostExecute(String resultUser) {
            title.setText(resultUser);
        }
    }
}
