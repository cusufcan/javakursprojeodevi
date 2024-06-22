package com.cusufcan.javakursprojeodevi.helper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;

public class AppHelper {
    public static Bitmap imageSmaller(Bitmap image, int maxSize, boolean filter) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        float bitmapRatio = (float) width / (float) height;
        
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        
        return Bitmap.createScaledBitmap(image, width, height, filter);
    }
    
    public static void toGallery(ActivityResultLauncher<Intent> launcher) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String intentAction = Intent.ACTION_PICK;
        
        Intent intentToGallery = new Intent(intentAction, mediaUri);
        launcher.launch(intentToGallery);
    }
}
