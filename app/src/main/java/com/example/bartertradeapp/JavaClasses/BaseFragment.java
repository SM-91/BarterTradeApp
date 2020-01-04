package com.example.bartertradeapp.JavaClasses;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.DataModels.UserUploadServiceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseFragment extends Fragment {

    public static final int PICK_IMAGE = 1;

    // general intent variable , to use in any fragment
    Intent intent;

    /*Firebase*/
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth uploadAuth;

    // Strings
    String pname,pdesc,pest,pexch,pimg;

    ProgressDialog progressDialog;

    /*Model Class*/
    UserUploadProductModel userUploadProductModel;
    UserUploadServiceModel userUploadServiceModel;
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



}
