package com.deba1.res2rant.ui.order;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Order;
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
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressBar loading;
    private OrderListAdapter mAdapter;
    private final List<Order> orders = new ArrayList<>();

    private final Handler handler = new Handler();
    private Runnable runnable;
    private final long delay = 10000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        loading = mainView.findViewById(R.id.my_orders_loading);
        RecyclerView listContainer = mainView.findViewById(R.id.my_orders_list);

        listContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        listContainer.setHasFixedSize(true);
        mAdapter = new OrderListAdapter(orders, MyOrdersFragment.this);
        listContainer.setAdapter(mAdapter);

        updateOrder();

        return mainView;
    }

    @Override
    public void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                updateOrder();
                Log.d("OrderService", "Fetching new orders");
            }
        }, delay);
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void updateOrder() {
        db.collection("orders")
                .whereEqualTo("userId", auth.getUid())
                .orderBy("orderedOn", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        orders.clear();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Order order = snapshot.toObject(Order.class);
                            order.Id = snapshot.getId();
                            mAdapter.add(order);
                        }
                        loading.setVisibility(View.GONE);
                    }
                });
    }
}