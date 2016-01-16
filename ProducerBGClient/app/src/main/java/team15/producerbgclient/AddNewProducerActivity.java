package team15.producerbgclient;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddNewProducerActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {
    TextView title;

    EditText producerNameInput;
    EditText producerDescriptionInput;

    Spinner producerTypeInput;
    ArrayAdapter<CharSequence> adapter;

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
    Button deleteBtn;
    Button editBtn;
    LinearLayout currentContainer;

    ProgressDialog dialog;

    String currentProducerId;

    GoogleApiClient mGoogleApiClient;

    GoogleMap currentMap;
    Marker addressMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_producer);

        producerTypeInput = (Spinner) findViewById(R.id.sp_producer_type_input);
        adapter = ArrayAdapter.createFromResource(this, R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        producerTypeInput.setAdapter(adapter);

        producerNameInput = (EditText) findViewById(R.id.ed_producer_name_input);
        producerDescriptionInput = (EditText) findViewById(R.id.et_description_input);
        mainProductsInput = (EditText) findViewById(R.id.et_products_input);
        telephoneInput = (EditText) findViewById(R.id.ed_producer_telephone_input);

        adressBtn = (Button) findViewById(R.id.btn_adress_producer);
        logoBtn = (Button) findViewById(R.id.btn_logo_producer);

        sumbitBtn = (Button) findViewById(R.id.btn_register_producer);
        deleteBtn = (Button) findViewById(R.id.btn_delete_producer);
        editBtn = (Button) findViewById(R.id.btn_edit_producer);

        currentContainer = (LinearLayout) findViewById(R.id.register_producer_layout);

        title = (TextView) findViewById(R.id.tv_title_register_producer);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentProducerId = bundle.getString("ProducerId");

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog = ProgressDialog.show(this, "", "Loading producer details...", true);

            deleteBtn.setOnClickListener(this);
            editBtn.setOnClickListener(this);

            new GetProducerDetailsTask().execute(currentProducerId);
        }

        adressBtn.setOnClickListener(this);
        logoBtn.setOnClickListener(this);
        sumbitBtn.setOnClickListener(this);
        currentContainer.setOnClickListener(this);

//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

            case R.id.btn_delete_producer: {
                deleteProducer();
                break;
            }

            case R.id.btn_edit_producer: {
                submitEditedProducer();
                break;
            }
        }
    }

    private void deleteProducer() {
        Boolean availableConnection = checkConnection();
        if (!availableConnection) {
            return;
        }

        dialog = ProgressDialog.show(this, "", "Deleting producer...", true);
        new DeleteProducerTask().execute(currentProducerId);
    }

    private void submitEditedProducer() {
        Boolean availableConnection = checkConnection();
        if (!availableConnection) {
            return;
        }

        String changedProducerDetails = gatherDetails();
        dialog = ProgressDialog.show(this, "", "Editing producer...", true);
        new EditProducerTask().execute(changedProducerDetails);
    }

    private void registerProducer() {
        Boolean availableConnection = checkConnection();
        if (!availableConnection) {
            return;
        }

        String jsonStringProducer = gatherDetails();
        dialog = ProgressDialog.show(this, "", "Registering producer...", true);
        new RegisterProducerTask().execute(jsonStringProducer);
    }

    private String gatherDetails() {
        String nameString = producerNameInput.getText().toString().trim();
        String descriptionString = producerDescriptionInput.getText().toString().trim();
        String typeString = producerTypeInput.getSelectedItem().toString().trim();
        String productsString = mainProductsInput.getText().toString().trim();
        String[] productsArray = productsString.split(" ");
        String telephone = telephoneInput.getText().toString().trim();

        Producer producer;
        if (addressLongitude == 0.0) {
            producer = new Producer(nameString, descriptionString, typeString, productsArray, telephone, logo);
        } else {
            producer = new Producer(nameString, descriptionString, typeString, productsArray, telephone, logo, addressLongitude, addressLatitude);
        }

        Gson gsonProducer = new Gson();
        String jsonStringProducer = gsonProducer.toJson(producer);

        return jsonStringProducer;
    }

    private Boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, stream);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
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
                Toast.makeText(getApplicationContext(), "Unable to upload logo.", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

            logo = getBytesFromBitmap(yourSelectedImage);

            dialog.hide();
        } else if (requestCode == 2 && resultCode == -1) {
            Uri selectedImageUri = data.getData();

            InputStream imageStream = null;

        }
    }

    private void uploadProducerLogo() {
        Intent intent = new Intent();
        intent.setType("image/png");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }

    public void getProducerAddress() {

        // To be refactored - it will directs you to select pont from google maps
        Uri gmmIntentUri = Uri.parse("geo:42.676009,23.358279?q=42.676009,23.358279");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivityForResult(mapIntent, 2);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            addressLatitude = 42.676009;
            addressLongitude = 23.358279;
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            addressLatitude = mLastLocation.getLatitude();
            addressLongitude = mLastLocation.getLongitude();
        } else {
            addressLatitude = 42.676009;
            addressLongitude = 23.358279;
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        currentMap = map;
        currentMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addressLatitude = latLng.latitude;
                addressLongitude = latLng.longitude;
                currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addressLatitude, addressLongitude), 15));
                addressMarker.setPosition(latLng);
            }
        });
        currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addressLatitude, addressLongitude), 15));
        addressMarker = currentMap.addMarker(new MarkerOptions()
                .position(new LatLng(addressLatitude, addressLongitude))
                .title("Producer address"));
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
            Producer returnedProducer = null;

            try {
                returnedProducer = gson.fromJson(producerStr, Producer.class);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (returnedProducer != null) {
                title.setText("Edit \'" + returnedProducer.getName() + "\'");

                producerNameInput.setText(returnedProducer.getName());
                producerDescriptionInput.setText(returnedProducer.getDescription());

                int spinnerPosition = adapter.getPosition(returnedProducer.getType());
                producerTypeInput.setSelection(spinnerPosition);

                StringBuffer sbf = new StringBuffer();
                String[] arrayOfProducts = returnedProducer.getProducts();
                if (arrayOfProducts.length > 0) {
                    sbf.append(arrayOfProducts[0]);
                    for (int i = 1; i < arrayOfProducts.length; i++) {
                        sbf.append(" ").append(arrayOfProducts[i]);
                    }
                }
                String scringProducts = sbf.toString();
                mainProductsInput.setText(scringProducts);

                telephoneInput.setText(returnedProducer.getPhone());

                addressLatitude = returnedProducer.getAddressLatitude();
                addressLongitude = returnedProducer.getAddressLongitude();

                deleteBtn.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                sumbitBtn.setVisibility(View.GONE);

                dialog.hide();
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
    }

    private class EditProducerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                return editProducer(params[0]);
            } catch (Exception e) {
                String errMessage = e.getMessage();
                return errMessage;
            }
        }

        private String editProducer(String editedProducer) throws IOException {
            URL baseUrl = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers/" + currentProducerId);
            HttpURLConnection urlConnection = (HttpURLConnection) baseUrl.openConnection();

            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer pOm5W1CQ2OMKA5Qe");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(editedProducer.toString());
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

            Intent intent = new Intent(AddNewProducerActivity.this, ProducerDetailsActivity.class);
            intent.putExtra("ProducerId", currentProducerId);
            startActivity(intent);
            return;
        }
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
                String message = urlConnection.getResponseMessage();
                return message;
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

            Intent intent = new Intent(AddNewProducerActivity.this, HomeActivity.class);
            startActivity(intent);
            return;
        }
    }


    private class DeleteProducerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return deleteProducer(params[0]);
            } catch (Exception e) {
                String errMessage = e.getMessage();
                return null;
            }
        }

        private String deleteProducer(String id) throws IOException {
            URL baseUrl = new URL("https://murmuring-mountain-9323.herokuapp.com/api/producers/" + id);
            HttpURLConnection urlConnection = (HttpURLConnection) baseUrl.openConnection();

            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer pOm5W1CQ2OMKA5Qe");
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
            dialog.hide();
            if (result == null) {
                Toast.makeText(getApplicationContext(), "You are unauthorized to delete this entry!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Producer successfully deleted!", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(AddNewProducerActivity.this, HomeActivity.class);
            startActivity(intent);
            return;
        }
    }
}
