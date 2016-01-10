package team15.producerbgclient;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import team15.producerbgclient.registerLogFragments.LoginFragment;
import team15.producerbgclient.registerLogFragments.RegisterFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewPager currentViewPager = (ViewPager) findViewById(R.id.registerPager);
        RegisterAdapter adapter = new RegisterAdapter(getSupportFragmentManager());
        currentViewPager.setAdapter(adapter);
    }

    public class RegisterAdapter extends FragmentPagerAdapter {
        public RegisterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new LoginFragment();
                case 1: return new RegisterFragment();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
