package com.deba1.res2rant.ui.food;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.deba1.res2rant.AdminDashboard;
import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Food;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodFragment extends Fragment {
    private RecyclerView foodListView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final List<Food> allFoods = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_foods, container, false);
        foodListView = mainView.findViewById(R.id.foodListView);
        foodListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        foodListView.setLayoutManager(layoutManager);

        db.collection("foods")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            allFoods.add(new Food(snapshot));
                        }

                        mainView.findViewById(R.id.foodListLoading).setVisibility(View.GONE);
                        mAdapter = new FoodListAdapter(allFoods, FoodFragment.this);
                        foodListView.setAdapter(mAdapter);
                    }
                });

        return mainView;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.app_bar_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
                Log.d("Option", "OK");
                updateFood();
                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.app_bar_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
                Log.d("Option", "OK");
                updateFood();
                return true;
            }
        });
    }

    public void updateFood() {
        db.collection("foods")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        allFoods.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                            allFoods.add(new Food(snapshot));
                    }
                });
    }
}
