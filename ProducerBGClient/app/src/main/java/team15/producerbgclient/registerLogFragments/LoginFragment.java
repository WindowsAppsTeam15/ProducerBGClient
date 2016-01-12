package team15.producerbgclient.registerLogFragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import team15.producerbgclient.HomeActivity;
import team15.producerbgclient.ProducersActivity;
import team15.producerbgclient.R;
import team15.producerbgclient.RegisterLoginActivity;
import team15.producerbgclient.User;

public class LoginFragment extends Fragment implements View.OnClickListener {
    TextView title;
    EditText usernameInput;
    EditText passwordInput;
    Button logBtn;
    LinearLayout container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        title = (TextView) rootView.findViewById(R.id.tv_title_log_form);
        usernameInput = (EditText) rootView.findViewById(R.id.ed_username_input);
        passwordInput = (EditText) rootView.findViewById(R.id.ed_password_input);

        logBtn = (Button) rootView.findViewById(R.id.btn_login);
        logBtn.setOnClickListener(this);

        container = (LinearLayout) rootView.findViewById(R.id.login_layout);
        container.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_login: {
                loginUser();
                break;
            }
        }
    }

    private void loginUser() {
        String usernameString = usernameInput.getText().toString().trim();
        String passString = passwordInput.getText().toString().trim();

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
        }

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        User userToBeLogged = new User(usernameString, passString, null);
        Gson gsonUser = new Gson();
        String jsonStringUser = gsonUser.toJson(userToBeLogged);

        new LogUserTask().execute(jsonStringUser);
    }

    private class LogUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                return logUser(params[0]);
            } catch (Exception e) {
                String errMessage = e.getMessage();
                Log.e("connection error", errMessage);
                return null;
            }
        }

        private String logUser(String userToLog) throws IOException {
            HttpURLConnection urlConnection = null;
            InputStream is = null;

            try {
                URL baseUrl = new URL("https://murmuring-mountain-9323.herokuapp.com/api/users/token");
                urlConnection = (HttpURLConnection) baseUrl.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(userToLog.toString());
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

                return sb.toString();

            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getActivity().getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                return;
            }
            Gson gson = new Gson();
            User returnedUser = null;

            try {
                returnedUser = gson.fromJson(result, User.class);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            returnedUser.getUsername();

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", returnedUser.getUsername());
            editor.putString("token", returnedUser.getToken());

            Toast.makeText(getActivity().getApplicationContext(), returnedUser.getUsername() + " sucsessfully logged!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);

            return;
        }
    }
}
