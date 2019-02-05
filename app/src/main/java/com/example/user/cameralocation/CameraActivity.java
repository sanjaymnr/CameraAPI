package com.example.user.cameralocation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnCamera, btnLocation;
    ImageView ivCamera;
    private String image_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamera = findViewById(R.id.btn_camera);
        ivCamera = findViewById(R.id.iv_camera);
        if (btnCamera != null) {
            this.btnCamera.setOnClickListener(this);
        }
        if (this.ivCamera != null) {
            this.ivCamera.setOnClickListener(this);
        }
        if (this.btnLocation != null) {
            this.btnLocation.setOnClickListener(this);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == RESULT_OK) {
            String photoPath = "";

            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            this.ivCamera.setImageBitmap(mImageBitmap);
            btnCamera.setVisibility(View.INVISIBLE);
            ivCamera.setVisibility(View.VISIBLE);
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.ImageColumns.ORIENTATION},
                    MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Uri uri = Uri.parse(cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA)));
                    photoPath = uri.toString();
                } while (cursor.moveToNext());
                cursor.close();
            }

            this.image_url = photoPath;
        }
    }

    @Override
    public void onClick(View view) {
        if (this.btnCamera.getId() == view.getId()) {
            permissionCheckCamera();
        }
        if (this.ivCamera.getId() == view.getId()) {
            Intent takePictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, 666);
        }
    }

    private void permissionCheckCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    1100);

            return;
        } else {
            permissionCheckStorage();
        }
    }

    private void permissionCheckStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1300);

            return;

        } else {
            openCameraIntent();
        }
    }

    private void openCameraIntent() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 666);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    permissionCheckStorage();
                break;
            case 1300:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCameraIntent();
                break;

        }
    }
}

