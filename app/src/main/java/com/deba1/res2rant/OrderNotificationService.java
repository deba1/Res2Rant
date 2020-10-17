package com.deba1.res2rant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.deba1.res2rant.models.Order;
import com.deba1.res2rant.models.OrderRaw;
import com.deba1.res2rant.models.OrderState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class OrderNotificationService {
    private Context context;
    public final static String CHANNEL_ID = "OrderNotification";
    public final static int ORDER_NOTIFICATION_INTENT = 2021;
    public static int NOTIFICATION_ID =10011;
    private final List<Order> newOrders, oldOrders;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderNotificationService(Context context) {
        this.context = context;
        this.oldOrders = new ArrayList<>();
        this.newOrders = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("orders")
                .whereEqualTo("userId", auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            if (snapshot.getString("status").equals(OrderState.PENDING.name()) || snapshot.getString("status").equals(OrderState.COOKING.name()))
                                oldOrders.add(new Order(snapshot));
                        }
                    }
                });
    }

    public void checkUpdate() {
        this.newOrders.clear();
        for (final Order order : oldOrders) {
            db.collection("orders")
                    .document(order.Id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Order thisOrder = documentSnapshot.toObject(Order.class);
                            assert thisOrder != null;
                            thisOrder.Id = documentSnapshot.getId();
                            OrderRaw raw = new OrderRaw(thisOrder);
                            if (!order.status.equals(thisOrder.status)) {
                                Intent intent = new Intent(context, OrderDetailsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("order", raw);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, ORDER_NOTIFICATION_INTENT, intent, 0);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_orders)
                                        .setContentTitle("Order Updated")
                                        .setContentText(String.format("Your order has been %s", order.status.toLowerCase()))
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat.from(context)
                                        .notify(NOTIFICATION_ID, builder.build());
                                if (order.status.equals("COOKING")) {
                                    newOrders.add(thisOrder);
                                }
                            }
                            else {
                                newOrders.add(thisOrder);
                            }
                            if (oldOrders.indexOf(order) == oldOrders.size() - 1) {
                                oldOrders.clear();
                                oldOrders.addAll(newOrders);
                            }
                        }
                    });

        }
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Notification";
            String description = "Get notification on change of order status";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
