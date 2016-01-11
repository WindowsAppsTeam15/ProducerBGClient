package team15.producerbgclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button goToRegisterButton = (Button) findViewById(R.id.btn_go_to_register);
        goToRegisterButton.setOnClickListener(this);
        Button goToAllProducersButton = (Button) findViewById(R.id.btn_all_producers);
        goToAllProducersButton.setOnClickListener(this);
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
}
