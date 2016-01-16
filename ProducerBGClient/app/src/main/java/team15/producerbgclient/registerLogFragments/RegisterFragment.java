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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team15.producerbgclient.AddNewProducerActivity;
import team15.producerbgclient.HomeActivity;
import team15.producerbgclient.R;
import team15.producerbgclient.User;

/**
 * Created by veso on 1/10/2016.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    EditText usernameInput;
    EditText passwordInput;
    EditText confirmPassInput;
    EditText emailInput;
    Button sumbitBtn;
    LinearLayout currentContainer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        confirmPassInput = (EditText) rootView.findViewById(R.id.ed_confirm_password_input);
        emailInput = (EditText) rootView.findViewById(R.id.ed_email_input);
        usernameInput = (EditText) rootView.findViewById(R.id.ed_username_input);
        passwordInput = (EditText) rootView.findViewById(R.id.ed_password_input);

        sumbitBtn = (Button) rootView.findViewById(R.id.btn_register);
        sumbitBtn.setOnClickListener(this);

        currentContainer = (LinearLayout) rootView.findViewById(R.id.register_layout);
        currentContainer.setOnClickListener(this);

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
            case R.id.btn_register: {
                registerUser();
                break;
            }
        }
    }

    private void registerUser() {
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

    private class RegisterUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                return registerUser(params[0]);
            } catch (Exception e) {
                String errMessage = e.getMessage();
                return errMessage;
            }
        }

        private String registerUser(String userToRegister) throws IOException {
            URL baseUrl = new URL("https://murmuring-mountain-9323.herokuapp.com/api/users");
            HttpURLConnection urlConnection = (HttpURLConnection) baseUrl.openConnection();

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
            Gson gson = new Gson();
            User returnedUser = null;

            try {
                returnedUser = gson.fromJson(result, User.class);
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                return;
            }

            String username = returnedUser.getUsername();
            String token = returnedUser.getToken();

            SharedPreferences sharedPref = getActivity().getSharedPreferences("producerbg", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", username);
            editor.putString("token", token);

            Toast.makeText(getActivity().getApplicationContext(), username + " sucsessfully registered!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), AddNewProducerActivity.class);
            startActivity(intent);
            return;
        }
    }
}
