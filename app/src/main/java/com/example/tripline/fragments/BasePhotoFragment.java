package com.example.tripline.fragments;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tripline.utility.BitmapScaler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


// any class that extends this fragment with be able to access these methods
public class BasePhotoFragment extends Fragment {

    public static final String TAG = "BasePhotoFragment";
    public File photoFile;

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Issue decoding image(s).", Toast.LENGTH_SHORT).show();

        }
        return image;
    }

    // helper function to get a URI for the image file
    public File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public File resizeFile(Bitmap image, String type, String photoFileName) {
        int width = (type.equals("trip")) ? 366 : 200;
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, width);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File resizedFile = getPhotoFileUri(photoFileName);
        try {
            resizedFile.createNewFile();
            FileOutputStream fos = null;
            fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch(IOException e) {
            Log.e(TAG, "Error creating resized file", e);
        }
        return resizedFile;
    }


}
