package com.deba1.res2rant.ui.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditDialogBox extends DialogFragment {
    private String title;
    private String message;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        return super.onCreateDialog(savedInstanceState);
    }
}
