package com.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wallet.Constants;
import com.wallet.R;
import com.wallet.fragment.AllEventFragment;
import com.wallet.fragment.BalanceFragment;
import com.wallet.fragment.NewEventFragment;
import com.wallet.fragment.MainFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment mFragment;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);

        // set default fragment in container
        getSupportFragmentManager().beginTransaction().replace(R.id.custom_fragment, new MainFragment()).commit();

    }

    public void OnDrawerClosed() {
        mDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_main) {
            mFragment = new MainFragment();
            openNewFragment(mFragment, "");
        } else if (id == R.id.nav_add_spending) {
            mFragment = new NewEventFragment();
            openNewFragment(mFragment, Constants.CATEGORY_EVENT_SPEND);
        } else if (id == R.id.nav_add_income) {
            mFragment = new NewEventFragment();
            openNewFragment(mFragment, Constants.CATEGORY_EVENT_INCOME);
        } else if (id == R.id.nav_balance) {
            mFragment = new BalanceFragment();
            openNewFragment(mFragment, "");
        } else if (id == R.id.nav_all_income) {
            mFragment = new AllEventFragment();
            openNewFragment(mFragment, Constants.CATEGORY_EVENT_INCOME);
        } else if (id == R.id.nav_all_spending) {
            mFragment = new AllEventFragment();
            openNewFragment(mFragment, Constants.CATEGORY_EVENT_SPEND);
        } else if (id == R.id.nav_calculator) {
            Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
            startActivity(intent);
        }

        OnDrawerClosed();
        return true;
    }

    private void openNewFragment(Fragment fragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putString("tagEvent", tag);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.custom_fragment, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.custom_fragment);
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            OnDrawerClosed();
        } else if (!(f instanceof MainFragment)){
            openMainFragment();
        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.custom_fragment), R.string.do_you_want_to_exit, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            snackbar.show();
        }
    }

    public void openMainFragment(){
        MainFragment fragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.custom_fragment, fragment).commit();
    }


}
