package com.deba1.res2rant.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DatePickerDialog datePicker;
    private Calendar fromDate, toDate;
    private TextView totalOrders, totalEarnings;
    private int orderCount;
    private long earningAmount;
    EditText fromDateView, toDateView;
    private ProgressBar loading;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel =
        //        ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        fromDateView = root.findViewById(R.id.dashboard_date_from);
        toDateView = root.findViewById(R.id.dashboard_date_to);
        totalOrders = root.findViewById(R.id.dashboard_total_orders);
        totalEarnings = root.findViewById(R.id.dashboard_total_earnings);
        loading = root.findViewById(R.id.dashboard_loading);
        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);
        fromDateView.setText(String.format("%s/%s/%s", day, month+1, year));
        toDateView.setText(String.format("%s/%s/%s", day, month+1, year));

        fromDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker picker, int i, int i1, int i2) {
                        fromDate.set(i, i1, i2, 0, 0);
                        fromDateView.setText(String.format("%s/%s/%s", i2, i1+1, i));
                        CheckData();
                    }
                }, year, month, day);

                datePicker.show();
            }
        });
        toDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        toDate.set(i, i1, i2, 23, 59);
                        toDateView.setText(String.format("%s/%s/%s", i2, i1+1, i));
                        CheckData();
                    }
                }, year, month, day);
                DatePicker picker = datePicker.getDatePicker();
                picker.setMinDate(fromDate.getTimeInMillis());
                datePicker.show();
            }
        });
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        CheckData();
        return root;
    }

    public void CheckData() {
        loading.setVisibility(View.VISIBLE);
        Log.d("Dashboard",  String.format("Checking Report from %s to %s", fromDateView.getText().toString(), toDateView.getText().toString()));
        Date dateFrom = new Date(), dateTo = new Date();
        try {
            dateFrom = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).parse(fromDateView.getText().toString());
            dateFrom.setHours(0);
            dateTo = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).parse(toDateView.getText().toString());
            dateTo.setHours(23);
            dateTo.setMinutes(59);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.collection("orders")
                .whereGreaterThanOrEqualTo("orderedOn", dateFrom)
                .whereLessThanOrEqualTo("orderedOn", dateTo)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        orderCount = queryDocumentSnapshots.size();
                        totalOrders.setText(String.valueOf(orderCount));
                        for (QueryDocumentSnapshot snapshot :
                                queryDocumentSnapshots) {
                            earningAmount += (long) snapshot.get("price");
                        }
                        totalEarnings.setText(String.format("BDT %s", earningAmount));
                        loading.setVisibility(View.INVISIBLE);
                        Log.d("Dashboard", "Received Response");
                    }
                });
    }
}