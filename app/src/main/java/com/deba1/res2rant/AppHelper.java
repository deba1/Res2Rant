package com.deba1.res2rant;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AppHelper {
    Context appContext;
    public AppHelper() {}
    public AppHelper(Context context) {
        this.appContext = context;
    }

    public static boolean validateRegex(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    public static void setDrawable(ImageView imageView, String path) {
        Drawable image = Drawable.createFromPath(path);
        imageView.setImageDrawable(image);
    }

    public static void setDialogEvent(final View layout, final FragmentManager manager, final DialogFragment dialog) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(manager, "Food");
            }
        });
    }
}
