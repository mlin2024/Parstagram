package com.example.parstagram.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.parstagram.BitmapScaler;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ComposeFragment extends Fragment {
    public static final String TAG = "ComposeFragment";
    public final static int CAMERA_IMAGE_CODE = 413;
    public final static int GALLERY_IMAGE_CODE = 612;
    public String photoFileName = "parstagram" + System.currentTimeMillis() + ".png";
    File photoFile;

    public RelativeLayout composeRelativeLayout;
    public EditText captionEditText;
    public Button takePictureButton;
    public Button uploadPictureButton;
    public ImageView postImageView;
    public Button postButton;
    public ProgressDialog loginProgressDialog;

    public ComposeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        composeRelativeLayout = getView().findViewById(R.id.composeRelativeLayout);
        captionEditText = getView().findViewById(R.id.captionEditText);
        takePictureButton = getView().findViewById(R.id.takePictureButton);
        uploadPictureButton = getView().findViewById(R.id.uploadPictureButton);
        postImageView = getView().findViewById(R.id.postImageView);
        postButton = getView().findViewById(R.id.postButton);
        loginProgressDialog = new ProgressDialog(getContext());
        loginProgressDialog.setMessage(getResources().getString(R.string.publishing_post));

        takePictureButton.setOnClickListener(v -> {
            hideSoftKeyboard(composeRelativeLayout);
            launchCamera();
        });

        uploadPictureButton.setOnClickListener(v -> {
            hideSoftKeyboard(composeRelativeLayout);
            pickPhoto();
        });

        postButton.setOnClickListener(v -> {
            hideSoftKeyboard(composeRelativeLayout);
            String caption = captionEditText.getText().toString();
            if (caption.isEmpty()) {
                Toast.makeText(getContext(), getResources().getString(R.string.caption_error), Toast.LENGTH_SHORT).show();
                return;
            }
            else if (photoFile == null || postImageView.getDrawable() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.image_error), Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                 ParseUser currentUser = ParseUser.getCurrentUser();
                 savePost(caption, photoFile, currentUser);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set text to "Retake Picture" if image is already taken
        if (postImageView.getDrawable() != null) takePictureButton.setText(getResources().getString(R.string.retake_picture));
    }

    private void savePost(String caption, File photoFile, ParseUser currentUser) {
        Post post = new Post();
        post.setCaption(caption);
        post.setImage(new ParseFile(photoFile));
        post.setAuthor(currentUser);
        Log.i(TAG, "Saving post by " + currentUser.getUsername());

        loginProgressDialog.show();

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                loginProgressDialog.dismiss();
                if (e != null) { // Saving post failed
                    Log.e(TAG, "Saving post failed", e);
                    Toast.makeText(getContext(), getResources().getString(R.string.error_publishing_post), Toast.LENGTH_SHORT).show();
                }
                else { // Saving post succeeded
                    Log.i(TAG, "Saving post succeeded");
                    Toast.makeText(getContext(), getResources().getString(R.string.published), Toast.LENGTH_SHORT).show();
                    // Set everything back to default
                    captionEditText.setText("");
                    postImageView.setImageResource(0);
                    takePictureButton.setText(getResources().getString(R.string.take_picture));
                }
            }
        });
    }

    // Trigger camera starting to take photo
    private void launchCamera() {
        Log.i(TAG, "launchCamera() called");

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.parstagram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAMERA_IMAGE_CODE);
        }
        else {
            Log.e(TAG, "No app can handle this intent");
            Toast.makeText(getContext(), getResources().getString(R.string.no_camera), Toast.LENGTH_SHORT).show();
        }
    }

    // Trigger gallery selection for a photo
    public void pickPhoto() {
        Log.i(TAG, "pickPhoto() called");

        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, GALLERY_IMAGE_CODE);
        }
        else {
            Log.e(TAG, "No app can handle this intent");
            Toast.makeText(getContext(), getResources().getString(R.string.no_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_CODE) { // If a photo was taken from the camera
            Log.i(TAG, "Photo was taken from camera " + Uri.fromFile(getPhotoFileUri(photoFileName)));
            if (resultCode == Activity.RESULT_OK) { // Result succeeded
                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName));
                // By this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                rawTakenImage = rotateBitmapOrientation(takenPhotoUri.getPath());
                try { // Try to resize the image and save it to the disk
                    // Resize bitmap
                    Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 1000);
                    // Configure byte output stream
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // Compress the image further
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    // Create a new file for the resized bitmap
                    File resizedFile = getPhotoFileUri(photoFileName + "_resized");
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    // Write the bytes of the bitmap to file
                    fos.write(bytes.toByteArray());
                    fos.close();
                    // Load the resized image into the ImageView
                    postImageView.setImageBitmap(resizedBitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to save resized bitmap to disk", e);
                    // Load the original taken image into the ImageView
                    postImageView.setImageBitmap(rawTakenImage);
                }
            }
            else { // Result failed
                Toast.makeText(getContext(), getResources().getString(R.string.error_camera_photo), Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == GALLERY_IMAGE_CODE) { // If a photo was chosen from the gallery
            Log.i(TAG, "Photo was chosen from gallery " + data.getDataString());
            if (resultCode == Activity.RESULT_OK) { // Result succeeded
                Uri chosenPhotoUri = data.getData();
                Bitmap rawChosenImage = loadFromUri(chosenPhotoUri);
                try { // Try to resize the image and save it to the disk
                    // Resize bitmap
                    Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawChosenImage, 1000);
                    // Configure byte output stream
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // Compress the image further
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    // Create a new file for the resized bitmap
                    File resizedFile = getPhotoFileUri(photoFileName + "_resized");
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    // Write the bytes of the bitmap to file
                    fos.write(bytes.toByteArray());
                    fos.close();
                    // Load the resized image into the ImageView
                    postImageView.setImageBitmap(resizedBitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to save resized bitmap to disk", e);
                    // Load the original taken image into the ImageView
                    postImageView.setImageBitmap(rawChosenImage);
                }
            }
            else { // Result failed
                Toast.makeText(getContext(), getResources().getString(R.string.error_gallery_photo), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

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
        }
        return image;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // Minimizes the soft keyboard
    public void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}