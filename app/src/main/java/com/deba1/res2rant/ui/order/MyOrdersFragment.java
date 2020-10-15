package com.deba1.res2rant.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class MyOrdersFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressBar loading;
    private RecyclerView listContainer;
    private OrderListAdapter mAdapter;
    List<Order> orders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        loading = mainView.findViewById(R.id.my_orders_loading);
        listContainer = mainView.findViewById(R.id.my_orders_list);

        listContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        listContainer.setHasFixedSize(true);

        db.collection("orders")
                .whereEqualTo("userId", auth.getUid())
                .orderBy("orderedOn", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Order order = queryDocumentSnapshot.toObject(Order.class);
                            order.Id = queryDocumentSnapshot.getId();
                            orders.add(order);
                        }
                        mAdapter = new OrderListAdapter(orders, MyOrdersFragment.this);
                        listContainer.setAdapter(mAdapter);
                        loading.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //
                    }
                });
        return mainView;
    }

    private void updateOrder() {
        db.collection("orders")
                .whereEqualTo("userId", auth.getUid())
                .orderBy("orderedOn", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Order order = snapshot.toObject(Order.class);
                            order.Id = snapshot.getId();
                            mAdapter.add(order);
                        }
                    }
                });
    }
}