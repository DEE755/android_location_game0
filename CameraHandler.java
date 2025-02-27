
package com.example.myapplicationtest1;

import static software.amazon.ion.impl.PrivateIonConstants.True;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraHandler {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Context context;



    public CameraHandler(Context context) {
        this.context = context;
    }

    public void takePicture(Activity activity) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT);
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

            int width = 70;
            int height = 70;

            // Resize the bitmap to the desired dimensions
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, true);

            // Create a circular bitmap
            Bitmap circularBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circularBitmap);
            canvas.drawColor(android.graphics.Color.TRANSPARENT); // Clear the canvas
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(resizedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            float radius = Math.min(width, height) / 2.0f;
            canvas.drawCircle(width / 2.0f, height / 2.0f, radius, paint);
            return circularBitmap;
        } else {
            return null;
        }
    }
}