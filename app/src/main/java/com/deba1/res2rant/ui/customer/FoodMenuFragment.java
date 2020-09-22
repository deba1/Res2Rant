package com.deba1.res2rant.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Food;
import com.deba1.res2rant.ui.food.FoodFragment;
import com.deba1.res2rant.ui.food.FoodListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodMenuFragment extends Fragment {
    private RecyclerView foodListView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final List<Food> allFoods = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_food_menu, container, false);
        foodListView = mainView.findViewById(R.id.foodMenuView);
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

                        mainView.findViewById(R.id.loadingBar).setVisibility(View.GONE);
                        mAdapter = new FoodMenuListAdapter(allFoods, FoodMenuFragment.this);
                        foodListView.setAdapter(mAdapter);
                    }
                });

        return mainView;
    }
}
