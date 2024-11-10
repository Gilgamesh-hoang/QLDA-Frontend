package com.example.truyenapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UploadImage {

    public static CompletableFuture<String> uploadImageToFirebase(Context context, Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        //Create a reference to 'images/<FILENAME>'
        StorageReference imageRef = storageReference.child("images/" + UUID.randomUUID().toString());

        // Upload the file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);

        CompletableFuture<String> future = new CompletableFuture<>();

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Got the download URL, you can use it now
                future.complete(uri.toString());
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Toast.makeText(context, "Failed to get download URL: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                future.complete(null);
            });

        }).addOnFailureListener(exception -> {
            Log.e("TAG", "Upload failed: " + exception.getMessage());
            future.complete(null);
        });

        return future;
    }
}
