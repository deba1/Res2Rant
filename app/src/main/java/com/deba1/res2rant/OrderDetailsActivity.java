package com.deba1.res2rant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.deba1.res2rant.models.OrderRaw;
import com.deba1.res2rant.ui.cart.CartItemAdapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetailsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_single);

        Intent intent = getIntent();
        if (!intent.hasExtra("order"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.orders);
            builder.setMessage(R.string.order_exist_false);
            builder.setCancelable(false);

            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.create().show();
        }
        else {
            TextView dateView = findViewById(R.id.orderDate);
            TextView statusView = findViewById(R.id.orderStatus);
            RecyclerView cartList = findViewById(R.id.orderList);

            OrderRaw order = (OrderRaw) intent.getSerializableExtra("order");
            assert order != null;
            dateView.setText(order.orderedOn);
            statusView.setText(order.status);
            cartList.setLayoutManager(new LinearLayoutManager(this));
            cartList.setHasFixedSize(true);
            CartItemAdapter itemAdapter = new CartItemAdapter(order.cart);
            cartList.setAdapter(itemAdapter);
        }


    }
}
