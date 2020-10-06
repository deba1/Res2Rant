package com.deba1.res2rant;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.deba1.res2rant.ui.order.OrdersFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

public class ChefActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final DrawerLayout drawer = findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.navigation_drawer_close);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView headerName, headerSub;
        headerName = headerView.findViewById(R.id.nav_user_name);
        headerSub = headerView.findViewById(R.id.nav_user_sub);
        headerName.setText(getIntent().getStringExtra("name"));
        headerSub.setText(getIntent().getStringExtra("email"));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.menu_chef_orders:
                        transaction.replace(R.id.fragment_container, new OrdersFragment());
                        break;
                    case R.id.menu_chef_profile:
                        break;
                    default:
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
}