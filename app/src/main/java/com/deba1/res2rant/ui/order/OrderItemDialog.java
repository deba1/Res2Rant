package com.deba1.res2rant.ui.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Order;
import com.deba1.res2rant.ui.cart.CartItemAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderItemDialog extends DialogFragment {
    private final Order order;

    public OrderItemDialog(Order order) {
        this.order = order;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View header = View.inflate(getContext(), R.layout.dialog_title_order_single, null);
        View builderView = getLayoutInflater().inflate(R.layout.fragment_order_single, null);
        TextView dateView = header.findViewById(R.id.orderSingleDate);
        TextView statusView = header.findViewById(R.id.orderSingleStatus);
        //ListView listView = builderView.findViewById(R.id.orderSingleListView);
        //listView.setAdapter(new CartListAdapter(getContext(), R.id.orderSingleListView, order.cart));
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
        return builder.create();
    }
}
