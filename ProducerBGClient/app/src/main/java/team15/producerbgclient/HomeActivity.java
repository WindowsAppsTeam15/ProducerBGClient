package team15.producerbgclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button goToRegisterButton = (Button) findViewById(R.id.goToRegisterButton);
        goToRegisterButton.setOnClickListener(this);
        Button goToAllProducersButton = (Button) findViewById(R.id.goToAllProducersButton);
        goToAllProducersButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.goToRegisterButton: {
                Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.goToAllProducersButton: {
                Intent intent = new Intent(HomeActivity.this, ProducersActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
