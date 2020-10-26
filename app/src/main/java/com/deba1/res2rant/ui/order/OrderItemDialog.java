package com.deba1.res2rant.ui.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Order;
import com.deba1.res2rant.models.OrderState;
import com.deba1.res2rant.ui.cart.CartItemAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderItemDialog extends DialogFragment {
    private final Order order;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderItemDialog(Order order) {
        this.order = order;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View header = View.inflate(getContext(), R.layout.dialog_title_order_single, null);
        View builderView = getLayoutInflater().inflate(R.layout.dialog_order_items, null);
        TextView dateView = header.findViewById(R.id.orderSingleDate);
        TextView statusView = header.findViewById(R.id.orderSingleStatus);

        String date = new SimpleDateFormat("dd/MM/yy - hh:mm aa", Locale.ENGLISH).format(order.orderedOn.toDate());
        dateView.setText(date);
        statusView.setText(order.status);

        RecyclerView cartList = builderView.findViewById(R.id.orderSingleList);
        cartList.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList.setHasFixedSize(true);
        CartItemAdapter itemAdapter = new CartItemAdapter(order.cart);
        cartList.setAdapter(itemAdapter);

        builder.setCustomTitle(header);
        builder.setView(builderView);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.reorder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.confirm_order);
                builder.setMessage(R.string.confirm_message);
                builder.setIcon(R.drawable.ic_cart);
                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(R.string.transaction_success, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Order newOrder = new Order();
                        newOrder.orderedOn = Timestamp.now();
                        newOrder.price = order.price;
                        newOrder.status = OrderState.PENDING.name();
                        newOrder.cart = order.cart;
                        newOrder.userId = order.userId;
                        db.collection("orders")
                                .add(newOrder)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getContext(), R.string.order_placed_success, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(null);
                    }
                });
                builder.show();
            }
        });
        return builder.create();
    }
}
