package team15.producerbgclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by veso on 1/12/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItem manageProducersBtn = (MenuItem) menu.findItem(R.id.action_manage);
        MenuItem addProducersBtn = (MenuItem) menu.findItem(R.id.action_add);
        MenuItem goHomeBtn = (MenuItem) menu.findItem(R.id.action_goHome);
        MenuItem logoutBtn = (MenuItem) menu.findItem(R.id.action_logout);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                Intent intent = new Intent(getApplication(), ProducersActivity.class);
                if (query != ""){
                    intent.putExtra("Query", query);
                }
                startActivity(intent);

                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        manageProducersBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplication().getApplicationContext(), ManageProducersActivity.class);
                startActivity(intent);
                return true;
            }
        });

        addProducersBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplication().getApplicationContext(), AddNewProducerActivity.class);
                startActivity(intent);
                return true;
            }
        });

        goHomeBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplication().getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                return true;
            }
        });

        logoutBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences sharedPref = getApplication().getSharedPreferences("producerbg", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", null);
                editor.putString("token", null);

                Toast.makeText(getApplication().getApplicationContext(), "Sucsessfully loggged out!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplication().getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
