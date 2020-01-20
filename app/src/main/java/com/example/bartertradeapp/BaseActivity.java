package com.example.bartertradeapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {


    public static final int PICK_IMAGE = 1;

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(100,181,246));
        }
    }

    public static void showLongToastInCenter(Context ctx, String message) {

        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showShortToast(Context ctx, String message) {

        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLongToast(Context ctx, String message) {

        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLongSnackbar(View view, String message) {

        Snackbar bar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

                .setAction("Dismiss", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        // Handle user action
                    }

                });
        bar.setActionTextColor(Color.RED);
        bar.show();
    }
}
