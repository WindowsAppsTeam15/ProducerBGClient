package team15.producerbgclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by veso on 1/12/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem manageProducersBtn = (MenuItem) menu.findItem(R.id.action_manage);
        MenuItem addProducersBtn = (MenuItem) menu.findItem(R.id.action_add);
        MenuItem logoutBtn = (MenuItem) menu.findItem(R.id.action_logout);

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

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        MenuItem searchItem = menu.findItem(R.id.search_menu_item);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        return true;
    }


}
