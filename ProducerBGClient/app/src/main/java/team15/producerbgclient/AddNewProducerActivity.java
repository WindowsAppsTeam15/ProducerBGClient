package team15.producerbgclient;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by veso on 1/13/2016.
 */
public class AddNewProducerActivity extends BaseActivity implements View.OnClickListener {
    EditText producerNameInput;
    EditText producerDescriptionInput;
    Spinner producerTypeInput;
    EditText mainProductsInput;
    EditText telephoneInput;

    // I should think about that
    Button adressBtn;

    // I should think about that
    Button logoBtn;

    Button sumbitBtn;
    LinearLayout currentContainer;

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
        return;
    }

    private void uploadProducerLogo() {
        return;
    }

    public void getProducerAddress() {
        return;
    }
}
