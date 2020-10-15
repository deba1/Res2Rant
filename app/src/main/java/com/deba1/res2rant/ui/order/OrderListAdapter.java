package com.deba1.res2rant.ui.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    private List<Order> allOrders, ordersFiltered;
    private Context context;
    private Fragment fragment;

    public OrderListAdapter(List<Order> allOrders, Fragment fragment) {
        this.allOrders = allOrders;
        this.ordersFiltered = allOrders;
        this.fragment = fragment;
    }

    public void add(Order order) {
        ordersFiltered.add(order);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View v = inflater.inflate(R.layout.component_order_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Order order = this.ordersFiltered.get(position);
        String date = new SimpleDateFormat("dd/MM/yy - hh:mm aa", Locale.ENGLISH).format(order.orderedOn.toDate());
        holder.dateView.setText(date);
        holder.statusView.setText(order.status);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new OrderItemDialog(order);
                dialog.show(fragment.getParentFragmentManager(), OrderItemDialog.class.getSimpleName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.ordersFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateView, statusView;
        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView;
            this.dateView = itemView.findViewById(R.id.order_item_date);
            this.statusView = itemView.findViewById(R.id.order_item_status);
        }
    }
}
