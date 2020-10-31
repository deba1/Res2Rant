package com.deba1.res2rant.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Food;
import com.deba1.res2rant.ui.customer.FoodMenuListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TopFoodFragment extends Fragment {
    private RecyclerView topSoldList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<Food> foodList = new ArrayList<>();
    private FoodMenuListAdapter mAdapter;
    private ProgressBar loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_foods, container, false);
        topSoldList = root.findViewById(R.id.top_foods_list);
        loading = root.findViewById(R.id.top_food_progressBar);
        topSoldList.setLayoutManager(new LinearLayoutManager(getContext()));

        db.collection("foods")
                .orderBy("totalPurchase", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot:
                        queryDocumentSnapshots){
                            Food food = new Food(snapshot);
                            foodList.add(food);
                        }
                        mAdapter = new FoodMenuListAdapter(foodList, TopFoodFragment.this);
                        topSoldList.setAdapter(mAdapter);
                        loading.setVisibility(View.GONE);
                    }
                });
        return root;
    }
}
