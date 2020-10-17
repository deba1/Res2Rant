package com.deba1.res2rant.ui.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Cart;
import com.deba1.res2rant.models.Food;
import com.deba1.res2rant.models.Order;
import com.deba1.res2rant.models.OrderState;
import com.deba1.res2rant.ui.common.SwipeToDeleteCallback;
import com.deba1.res2rant.ui.customer.FoodMenuFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyCartFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private CartItemAdapter mAdapter;
    List<Cart.CartItem> itemList = new ArrayList<>();
    private float totalAmount;
    private int itemIndex = 0;
    private float discount = 0;
    private List<Cart.CartItem> cartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_my_cart, container, false);
        final ProgressBar loading = mainView.findViewById(R.id.progressBar);
        final RecyclerView myCart = mainView.findViewById(R.id.myCartView);
        final TextView totalPriceView = mainView.findViewById(R.id.cartPrice);
        final TextView discountView = mainView.findViewById(R.id.cartDiscount);
        final Button confirmButton = mainView.findViewById(R.id.cart_confirm_button);
        myCart.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        myCart.setLayoutManager(layoutManager);

        mAdapter = new CartItemAdapter(itemList);
        db.collection("users")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot.getLong("discount") == null)
                            discount = 0;
                        else
                            discount = snapshot.getLong("discount");
                    }
                });

        db.collection("carts")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Cart cart = snapshot.toObject(Cart.class);
                            assert cart != null;
                            cartItems = cart.items;
                            if (cartItems != null)
                                for (final Cart.CartItem item : cartItems) {
                                    db.collection("foods")
                                            .document(item.foodId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot snapshot) {
                                                    if (snapshot != null) {
                                                        Food food = snapshot.toObject(Food.class);
                                                        assert food != null;
                                                        totalAmount += food.price * item.count;
                                                        mAdapter.add(itemIndex, new Cart.CartItem(food, item.count, item.note, item.table));
                                                        itemIndex++;
                                                        totalPriceView.setText(getResources().getString(R.string.total_price_placeholder, totalAmount));
                                                    }
                                                    if (cartItems.size() == itemIndex) {
                                                        if (discount != 0) {
                                                            totalAmount -= totalAmount * (discount / 100.00);
                                                            discountView.setVisibility(View.VISIBLE);
                                                            discountView.setText(getResources().getString(R.string.discount_tag, discount));
                                                            totalPriceView.setText(getResources().getString(R.string.total_price_placeholder, totalAmount));
                                                        }
                                                        myCart.setAdapter(mAdapter);
                                                        ItemTouchHelper touchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(mAdapter));
                                                        touchHelper.attachToRecyclerView(myCart);
                                                    }
                                                }
                                            });
                                }
                            loading.setVisibility(View.GONE);
                        }
                    }
                });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        Order order = new Order();
                        order.cart = cartItems;
                        order.orderedOn = Timestamp.now();
                        order.status = OrderState.PENDING.toString();
                        order.userId = auth.getUid();
                        order.price = totalAmount;
                        db.collection("orders")
                                .document()
                                .set(order)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        cartItems.clear();
                                        db.document("carts/"+auth.getUid())
                                                .update("items", cartItems);
                                        Snackbar.make(view, R.string.order_placed_success, BaseTransientBottomBar.LENGTH_SHORT).show();
                                        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new FoodMenuFragment()).commit();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(view, R.string.order_placed_fail, BaseTransientBottomBar.LENGTH_SHORT).show();
                                    }
                                });
                        for (Cart.CartItem item:
                             cartItems) {
                            db.collection("foods")
                                    .document(item.foodId)
                                    .update("totalPurchase", FieldValue.increment(item.count));
                        }
                    }
                });
                builder.setNegativeButton(R.string.transaction_fail, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar.make(view, R.string.transaction_fail, BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        return mainView;
    }
}
