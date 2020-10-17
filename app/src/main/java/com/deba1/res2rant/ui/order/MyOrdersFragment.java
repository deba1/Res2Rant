package com.deba1.res2rant.ui.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.deba1.res2rant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MyOrdersFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressBar loading;
    private RecyclerView listContainer;
    private RecyclerView.LayoutManager layoutManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        loading = mainView.findViewById(R.id.my_orders_loading);
        listContainer = mainView.findViewById(R.id.my_orders_list);
        layoutManager = new LinearLayoutManager(getContext());
        listContainer.setLayoutManager(layoutManager);
        listContainer.setHasFixedSize(true);
        db.collection("orders")
                .whereEqualTo("userId", auth.getUid())
                .orderBy("orderedOn")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            //
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //
                    }
                });
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}