package team15.producerbgclient.registerLogFragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team15.producerbgclient.R;
import team15.producerbgclient.User;

/**
 * Created by veso on 1/10/2016.
 */
public class RegisterFragment extends Fragment {

    TextView title;
    EditText usernameInput;
    EditText passwordInput;
    EditText confirmPassInput;
    EditText emailInput;
    Button sumbitBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        title = (TextView) rootView.findViewById(R.id.tv_title_register_form);
        usernameInput = (EditText) rootView.findViewById(R.id.ed_username_input);
        passwordInput = (EditText) rootView.findViewById(R.id.ed_password_input);
        confirmPassInput = (EditText) rootView.findViewById(R.id.ed_confirm_password_input);
        emailInput = (EditText) rootView.findViewById(R.id.ed_email_input);

        sumbitBtn = (Button) rootView.findViewById(R.id.btn_register);
        sumbitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameString = usernameInput.getText().toString().trim();
                String passString = passwordInput.getText().toString().trim();
                String confirmPassString = confirmPassInput.getText().toString().trim();
                String emailString = emailInput.getText().toString().trim();

                Pattern pattern = Pattern.compile("([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})");
                Matcher matcher = pattern.matcher(emailString);

                if (usernameString.length() == 0) {
                    usernameInput.setError("Username is required!");
                    return;
                } else if (usernameString.length() < 6) {
                    usernameInput.setError("Username should be at least 6 characters long!");
                    return;
                } else if (passString.length() == 0) {
                    passwordInput.setError("Password is required!");
                    return;
                } else if (passString.length() < 6) {
                    passwordInput.setError("Password should be at least 6 characters long!");
                    return;
                } else if (!passString.equals(confirmPassString)) {
                    confirmPassInput.setError("Password and confirm pass should be equal!");
                    return;
                } else if (!matcher.matches()) {
                    emailInput.setError("You have entered invalid email!");
                    return;
                }

                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    Toast.makeText(getActivity().getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                User userToBeRegistered = new User(usernameString, passString, emailString);
                Gson gsonUser = new Gson();
                String jsonStringUser = gsonUser.toJson(userToBeRegistered);

                new RegisterUserTask().execute(jsonStringUser);
            }
        });

        return rootView;
    }

    private class RegisterUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                return registerUser(params[0]);
            } catch (Exception e) {
                String errMessage = e.getMessage();
                Log.e("connection error", "Unable to connect");
                return null;
            }
        }

        private String registerUser(String userToRegister) throws IOException {
            HttpURLConnection urlConnection = null;
            InputStream is = null;

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
                if (responseCode > 201) {
                    return urlConnection.getResponseMessage();
                }

                is = urlConnection.getInputStream();
                int numberOfChars;
                StringBuffer sb = new StringBuffer();
                while ((numberOfChars = is.read()) != -1) {
                    sb.append((char) numberOfChars);
                }

                Gson gson = new Gson();
                User returnedUser = null;

                try {
                    returnedUser = gson.fromJson(sb.toString(), User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return returnedUser.getUsername();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            title.setText(result);
        }
    }
}
