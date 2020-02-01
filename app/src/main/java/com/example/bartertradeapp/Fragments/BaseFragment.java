package com.example.bartertradeapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseFragment extends Fragment {

    public static final int PICK_IMAGE = 1;

    // general intent variable , to use in any fragment
    Intent intent;

    /*Firebase*/
    DatabaseReference updateDatabaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth uploadAuth;




    // Strings
    String pname,pdesc,pest,pexch,pcategory,ptype,pcondition,pimg;

    String productName,productDescription,productEstimatedPrice,productPossibleExchangeWith,productCategory,productType,productCondition,productSingleImage;



    ProgressDialog progressDialog;

    /*Model Class*/
    UserUploadProductModel userUploadProductModel;
    UserModel userModel;


    /*Getting File Extension for single Image Uri*/
    public String getFileExtension(Uri uri){
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // User selecting profile pic (Profilefragment)
    public void selectUserProfileImage() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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
