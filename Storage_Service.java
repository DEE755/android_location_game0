package com.example.myapplicationtest1;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executors;

import com.bumptech.glide.Glide;

// Storage_Service.java
public class Storage_Service {
        private static final String TAG = "StorageService";

        private FirebaseStorage storage;
        private StorageReference storageRef;
        private StorageReference mountainsRef;

        private Context context;

        Storage_Service(Context context) {
                this.context = context;
                try {
                        storage = FirebaseStorage.getInstance("gs://android-location-game0.firebasestorage.app");
                        storageRef = storage.getReference();
                } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage());
                }
        }

        public void getPlayerImageFromStorage(Player player, OnSuccessListener<Bitmap> onSuccessListener) {
                storageRef.child(player.getRef_to_logo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                                downloadImageFromUri(uri, onSuccessListener);
                        }
                }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error downloading image: "  + player.getRef_to_logo() + "url:"   + e.getMessage());
                        }
                });
        }

        private void downloadImageFromUri(Uri uri, OnSuccessListener<Bitmap> onSuccessListener) {
                Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                                Bitmap bitmap = Glide.with(context)
                                        .asBitmap()
                                        .load(uri)
                                        .submit()
                                        .get();
                                new Handler(Looper.getMainLooper()).post(() -> onSuccessListener.onSuccess(bitmap));
                        } catch (Exception e) {
                                Log.e(TAG, "Error downloading image: " + e.getMessage());
                        }
                });
        }

        public void uploadImage(Bitmap imageBitmap, String destinationName) {
                Log.d(TAG, "entering uploadImage");
                if (imageBitmap == null) {
                        Log.e(TAG, "Image is null");
                        return;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                byte[] data = baos.toByteArray();

                mountainsRef = storageRef.child(destinationName);

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                                Log.e(TAG, "Upload failed: " + exception.getMessage());
                        }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                                Log.d(TAG, "Download URL: " + uri);
                                        }
                                });
                        }
                });
        }
}





