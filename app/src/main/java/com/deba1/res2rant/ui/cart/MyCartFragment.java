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
import android.widget.Toast;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.*;
import com.deba1.res2rant.ui.customer.FoodMenuFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyCartFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView.LayoutManager layoutManager;
    private CartItemAdapter mAdapter;
    final List<Cart.FoodItem> itemList = new ArrayList<>();
    float totalAmount;
    int itemIndex = 0;
    List<Cart.CartItem> cartItems;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_my_cart, container, false);
        final ProgressBar loading = mainView.findViewById(R.id.progressBar);
        final RecyclerView myCart = mainView.findViewById(R.id.myCartView);
        final TextView totalPriceView = mainView.findViewById(R.id.cartPrice);
        final Button confirmButton = mainView.findViewById(R.id.cart_confirm_button);
        myCart.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        myCart.setLayoutManager(layoutManager);

        mAdapter = new CartItemAdapter(itemList, MyCartFragment.this);

        db.collection("carts")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
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
                                                totalAmount += food.price*item.count;
                                                mAdapter.add(itemIndex, new Cart.FoodItem(food, item.count, item.note, item.table));
                                                itemIndex++;
                                                totalPriceView.setText(String.format("Total: ৳ %s", totalAmount));
                                            }
                                            if (cartItems.size() == itemIndex)
                                                myCart.setAdapter(mAdapter);
                                        }
                                    });
                        }
                        loading.setVisibility(View.GONE);
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
                        order.status = OrderState.COOKING.toString();
                        order.userId = auth.getUid();
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
