package com.deba1.res2rant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.deba1.res2rant.ui.cart.MyCartFragment;
import com.deba1.res2rant.ui.customer.FoodMenuFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class CustomerActivity extends AppCompatActivity {

    NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.navigation_drawer_close);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new FoodMenuFragment());
                transaction.commit();
                drawer.closeDrawers();
                return false;
            }
        });

        drawer.addDrawerListener(toggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_app_bar, menu);
        return true;
    }

    public void openCart(MenuItem item) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyCartFragment()).commit();
    }
}