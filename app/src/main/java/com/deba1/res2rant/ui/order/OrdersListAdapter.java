package com.deba1.res2rant.ui.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Cart;
import com.deba1.res2rant.models.Order;
import com.deba1.res2rant.models.OrderState;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersListAdapter extends BaseExpandableListAdapter {
    private FirebaseFirestore db;
    private boolean moderate;
    private Context context;
    private List<Order> orders;

    public OrdersListAdapter(Context context, List<Order> orders) {
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.orders = orders;
    }

    public OrdersListAdapter(Context context, List<Order> orders, boolean moderate) {
        this(context, orders);
        this.moderate = moderate;
    }

    @Override
    public int getGroupCount() {
        return orders.size();
    }

    @Override
    public int getChildrenCount(int i) {
        /*if (moderate)
            return orders.get(i).cart.size() + 1;*/
        return orders.get(i).cart.size();
    }

    @Override
    public Order getGroup(int i) {
        return orders.get(i);
    }

    @Override
    public Cart.CartItem getChild(int i, int i1) {
        /*if (moderate)
            return orders.get(i).cart.get(i1-1);*/
        return orders.get(i).cart.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String date = new SimpleDateFormat("dd/MM/yy - hh:mm aa", Locale.ENGLISH).format(getGroup(i).orderedOn.toDate());
        String status = getGroup(i).status;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.component_order_item,  null);
        }
        TextView dateView = view.findViewById(R.id.order_item_date);
        dateView.setText(date);
        TextView statusView = view.findViewById(R.id.order_item_status);
        statusView.setText(status);
        if (getGroup(i).status.equalsIgnoreCase(OrderState.COMPLETED.name()))
            statusView.setTextColor(context.getResources().getColor(R.color.textGreen));
        return view;
    }

    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        /*if (moderate && b) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.component_cart_item, null);
                TextView statusButton = view.findViewById(R.id.order_status_button);
                statusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Order order = getGroup(i);
                        db.collection("orders")
                                .document(order.Id)
                                .update("status", order.status.equalsIgnoreCase("COOKING") ? OrderState.COMPLETED.name() : OrderState.COOKING.name())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        orders.get(i).status = order.status.equalsIgnoreCase("COOKING") ? OrderState.COMPLETED.name() : OrderState.COOKING.name();
                                        notifyDataSetChanged();
                                    }
                                });
                    }
                });
            }
        }
        else {*/
            String name = getChild(i, i1).foodName;
            String note = getChild(i, i1).note;
            String table = getChild(i, i1).table;
            int quantity = getChild(i, i1).count;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.component_cart_item, null);
            }
            TextView nameView = view.findViewById(R.id.cart_item_title);
            TextView noteView = view.findViewById(R.id.cart_item_note);
            TextView tableView = view.findViewById(R.id.cart_item_table);
            TextView qtView = view.findViewById(R.id.cart_item_count);
            nameView.setText(name);
            noteView.setText(note);
            tableView.setText(table);
            qtView.setText(String.format("Quantity: %s", quantity));
        //}
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
