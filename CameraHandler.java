
package com.example.myapplicationtest1;

import static software.amazon.ion.impl.PrivateIonConstants.True;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraHandler {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Context context;

    private Storage_Service storage_service;

    private String saveImageToFile(Bitmap imageBitmap) {
        File imageFile = new File(context.getCacheDir(), "image1.jpg");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile.getAbsolutePath();
    }

    public CameraHandler(Context context) {
        this.context = context;
    }

    public void takePicture(Activity activity) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Log.d("CameraHandler", "Taking picture");
        } else {
            Log.e("CameraHandler", "No camera app found");
        }
    }

    public Bitmap handleActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CameraHandler", "Handling activity result");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            LogIn.setProfile_picture_taken(true);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.d("CameraHandler", "Handling activity result");
            return imageBitmap;

        }

        else return null;
        //TODO MAYBE HANDLE ERROR BUT IT NEVER HAPPENS
    }
}