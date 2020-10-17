package com.deba1.res2rant;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.deba1.res2rant.ui.cart.MyCartFragment;
import com.deba1.res2rant.ui.customer.FoodMenuFragment;
import com.deba1.res2rant.ui.food.TopFoodFragment;
import com.deba1.res2rant.ui.order.MyOrdersFragment;
import com.deba1.res2rant.ui.user.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

public class CustomerActivity extends AppCompatActivity {

    NavigationView navView;
    FirebaseAuth auth = FirebaseAuth.getInstance();

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
        View headerView = navView.getHeaderView(0);
        TextView navTitle = headerView.findViewById(R.id.nav_user_name);
        TextView navSub = headerView.findViewById(R.id.nav_user_sub);
        navTitle.setText(getIntent().getStringExtra("name"));
        navSub.setText(getIntent().getStringExtra("email"));

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                int itemId = item.getItemId();
                if (itemId == R.id.customer_menu_top) {
                    transaction.replace(R.id.fragment_container, new TopFoodFragment());
                }
                else if (itemId == R.id.customer_menu_foods) {
                    transaction.replace(R.id.fragment_container, new FoodMenuFragment());
                } else if (itemId == R.id.customer_menu_orders) {
                    transaction.replace(R.id.fragment_container, new MyOrdersFragment());
                } else if (itemId == R.id.customer_menu_profile) {
                    transaction.replace(R.id.fragment_container, new ProfileFragment());
                } else if (itemId == R.id.customer_menu_logout) {
                    auth.signOut();
                    finish();
                }
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}