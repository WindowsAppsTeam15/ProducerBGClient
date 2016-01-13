package team15.producerbgclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by veso on 1/13/2016.
 */
public class AddNewProducerActivity extends BaseActivity implements View.OnClickListener {
    EditText producerNameInput;
    EditText producerDescriptionInput;
    Spinner producerTypeInput;
    EditText mainProductsInput;
    EditText telephoneInput;

    double addressLatitude;
    double addressLongitude;

    byte[] logo;

    // I should think about that
    Button adressBtn;

    // I should think about that
    Button logoBtn;

    Button sumbitBtn;
    LinearLayout currentContainer;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_producer);

        producerTypeInput = (Spinner) findViewById(R.id.sp_producer_type_input);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        producerTypeInput.setAdapter(adapter);

        producerNameInput = (EditText) findViewById(R.id.ed_producer_name_input);
        producerDescriptionInput = (EditText) findViewById(R.id.et_description_input);
        mainProductsInput = (EditText) findViewById(R.id.et_products_input);
        telephoneInput  = (EditText) findViewById(R.id.ed_producer_telephone_input);
        adressBtn = (Button) findViewById(R.id.btn_adress_producer);
        logoBtn = (Button) findViewById(R.id.btn_logo_producer);
        sumbitBtn = (Button) findViewById(R.id.btn_register_producer);;
        currentContainer = (LinearLayout) findViewById(R.id.register_producer_layout);

        adressBtn.setOnClickListener(this);
        logoBtn.setOnClickListener(this);
        sumbitBtn.setOnClickListener(this);
        currentContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_adress_producer: {
                getProducerAddress();
                break;
            }
            case R.id.btn_logo_producer: {
                uploadProducerLogo();
                break;
            }
            case R.id.btn_register_producer: {
                registerProducer();
                break;
            }
        }
    }

    private void registerProducer() {
        String nameString = producerNameInput.getText().toString().trim();
        String descriptionString = producerDescriptionInput.getText().toString().trim();
        String typeString = producerTypeInput.getSelectedItem().toString().trim();
        String productsString = mainProductsInput.getText().toString().trim();


        String[] productsArray = productsString.split(" ");
        String telephone = telephoneInput.getText().toString().trim();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        Producer producerToRegister;
        if (addressLongitude == 0.0) {
            producerToRegister = new Producer(nameString, descriptionString, typeString, productsArray, telephone, logo);
        } else {
            producerToRegister = new Producer(nameString, descriptionString, typeString, productsArray, telephone, logo, addressLongitude, addressLatitude);
        }

        Gson gsonProducer = new Gson();
        String jsonStringProducer = gsonProducer.toJson(producerToRegister);

        dialog = ProgressDialog.show(this, "", "Registering producer...", true);
        new RegisterProducerTask().execute(jsonStringProducer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == -1) {
            dialog = ProgressDialog.show(this, "", "Uploading logo...", true);
            Uri selectedImageUri = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImageUri);
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Unable to upload log.", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

            int bytes = yourSelectedImage.getByteCount();

            ByteBuffer buffer = ByteBuffer.allocate(bytes);
            yourSelectedImage.copyPixelsToBuffer(buffer);

            logo = buffer.array();
            for (int i = 0; i < logo.length; i++) {
                if (logo[i] != 0) {
                    int current = logo[i];
                }
            }
            String logoString = logo.toString();
            dialog.hide();
        }
    }

    private void uploadProducerLogo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }

    public void getProducerAddress() {
        // To be refactored - it will directs you to select pont from google maps
        addressLatitude = 42.676009;
        addressLongitude = 23.358279;
        return;
    }


    private class RegisterProducerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                return registerProducer(params[0]);
            } catch (Exception e) {
                String errMessage = e.getMessage();
                return errMessage;
            }
        }

        private String registerProducer(String producerToRegister) throws IOException {
            URL baseUrl = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers");
            HttpURLConnection urlConnection = (HttpURLConnection) baseUrl.openConnection();

            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer pOm5W1CQ2OMKA5Qe");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(producerToRegister.toString());
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
            dialog.hide();

            Gson gson = new Gson();
            Producer returnedProducer = null;

            try {
                returnedProducer = gson.fromJson(result, Producer.class);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                return;
            }

            String producerName = returnedProducer.getName();

            Toast.makeText(getApplicationContext(), producerName + " sucsessfully registered!", Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent(AddNewProducerActivity.this, HomeActivity.class);
//            startActivity(intent);
            return;
        }
    }
}
