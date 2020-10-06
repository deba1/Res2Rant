package com.deba1.res2rant.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Order;
import com.deba1.res2rant.models.OrderState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrdersFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressBar loading;
    private ExpandableListView ordersView;
    List<Order> orders = new ArrayList<>();
    private OrderListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_all_orders, container, false);
        loading = view.findViewById(R.id.orders_loading);
        ordersView = view.findViewById(R.id.orders_list);
        db.collection("orders")
                .orderBy("orderedOn")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Order order = queryDocumentSnapshot.toObject(Order.class);
                            order.Id = queryDocumentSnapshot.getId();
                            orders.add(order);
                        }
                        mAdapter = new OrderListAdapter(getContext(), orders, true);
                        ordersView.setAdapter(mAdapter);
                        ordersView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                final Order order = mAdapter.getGroup(i);
                                db.collection("orders")
                                        .document(order.Id)
                                        .update("status", order.status.equalsIgnoreCase("COOKING") ? OrderState.COMPLETED.name() : OrderState.COOKING.name())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                orders.get(i).status = order.status.equalsIgnoreCase("COOKING") ? OrderState.COMPLETED.name() : OrderState.COOKING.name();
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        });
                                return false;
                            }
                        });
                        loading.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(view, getResources().getText(R.string.network_error), BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
        return view;
    }
}
