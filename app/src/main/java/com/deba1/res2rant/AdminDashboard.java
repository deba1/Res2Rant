package com.deba1.res2rant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deba1.res2rant.models.Food;
import com.deba1.res2rant.ui.food.FoodFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AdminDashboard extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    TextView navUserSub, navUserName;
    private AppBarConfiguration mAppBarConfiguration;
    FloatingActionButton fabMain;
    final int SELECT_FOOD_IMAGE = 10011;
    AlertDialog foodAddAlert;
    Uri addFoodImageUri;
    Menu mainMenu;
    int destinationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_foods, R.id.nav_users, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headerView = navigationView.getHeaderView(0);
        navUserSub = headerView.findViewById(R.id.nav_user_sub);
        navUserName = headerView.findViewById(R.id.nav_user_name);
        navUserName.setText(currentUser.getDisplayName());
        navUserSub.setText(currentUser.getEmail());
        fabMain = findViewById(R.id.fabMain);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                firebaseAuth.signOut();
                finish();
                return true;
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                destinationId = destination.getId();
                switch (destinationId) {
                   case R.id.nav_foods:
                       findViewById(R.id.fabMain).setVisibility(View.VISIBLE);
                       mainMenu.findItem(R.id.app_bar_search).setVisible(true);
                       invalidateOptionsMenu();
                       break;
                   default:
                       if (mainMenu!=null)
                           mainMenu.findItem(R.id.app_bar_search).setVisible(false);
                       findViewById(R.id.fabMain).setVisibility(View.GONE);
               }
            }
        });


        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                switch (navController.getCurrentDestination().getId()) {
                    case R.id.nav_foods:
                        //AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AdminDashboard.this, R.style.AppTheme_AlertDialog);
                        builder.setTitle(R.string.add_food);
                        View addFoodAlertLayout = getLayoutInflater().inflate(R.layout.action_food_add, null);
                        builder.setView(addFoodAlertLayout);
                        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Food food = new Food();
                                food.name = ((EditText)foodAddAlert.findViewById(R.id.foodAddName)).getText().toString().trim();
                                food.description = ((EditText)foodAddAlert.findViewById(R.id.foodAddDesc)).getText().toString().trim();
                                food.price = Float.parseFloat(((EditText)foodAddAlert.findViewById(R.id.foodAddPrice)).getText().toString().trim());
                                firebaseFirestore.collection("foods")
                                        .add(food)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(final DocumentReference documentReference) {
                                                if (addFoodImageUri != null) {
                                                    final String imagePath = "foods/"+documentReference.getId();
                                                    firebaseStorage.child(imagePath)
                                                            .putFile(addFoodImageUri)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    documentReference.update("imagePath", imagePath);
                                                                    Snackbar.make(view, R.string.image_upload_success, BaseTransientBottomBar.LENGTH_LONG)
                                                                            .show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Snackbar.make(view, R.string.image_upload_fail, BaseTransientBottomBar.LENGTH_LONG)
                                                                            .show();
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        ((Button)addFoodAlertLayout.findViewById(R.id.foodImageSelectButton)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FOOD_IMAGE);
                            }
                        });
                        foodAddAlert = builder.create();
                        foodAddAlert.show();
                        break;
                    default:
                        findViewById(R.id.fabMain).setVisibility(View.GONE);
                }
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.app_bar_search:
                        break;
                    case R.id.app_bar_refresh:
                        if (destinationId == R.id.nav_foods) {
                            //FoodFragment foodFragment = (FoodFragment)navController.getBackStackEntry(R.id.nav_host_fragment);
                            //foodFragment.updateFood();
                        }
                        break;
                    default:
                }
                return false;
            }
        });

    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FOOD_IMAGE && resultCode == RESULT_OK) {
            ImageView preview = foodAddAlert.findViewById(R.id.foodAddPreview);
            addFoodImageUri = data.getData();
            if (preview!=null)
                preview.setImageURI(addFoodImageUri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mainMenu = menu;

        getMenuInflater().inflate(R.menu.admin_dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}