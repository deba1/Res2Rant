package com.deba1.res2rant.ui.cart;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Cart;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CartListAdapter extends ArrayAdapter<Cart.CartItem> {
    private List<Cart.CartItem> items;
    private Context context;

    public CartListAdapter(@NonNull Context context, int resource, @NonNull List<Cart.CartItem> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Cart.CartItem item = items.get(position);
        //LayoutInflater inflater = context.getLayoutInflater();
        View root = View.inflate(context, R.layout.component_cart_item, null);
        TextView titleView = root.findViewById(R.id.cart_item_title);
        TextView qtView = root.findViewById(R.id.cart_item_count);
        titleView.setText(item.foodName);
        qtView.setText(String.format("Quantity: %s", item.count));

        return root;
    }
}
