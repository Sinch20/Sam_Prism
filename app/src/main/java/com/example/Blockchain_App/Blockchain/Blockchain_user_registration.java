package com.example.Blockchain_App.Blockchain;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.Blockchain_App.Model.Request;
import com.example.Blockchain_App.Network.NetworkClient;
import com.example.Blockchain_App.Network.UploadApis;
import com.example.Blockchain_App.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Blockchain_user_registration extends AppCompatActivity
{
    public static final String FLAT_NO = "Flat 101";
    public static FirebaseAuth mAuth;

    String pathToFile;
    private Uri photoURI;
    String fileStorageDir = "Pictures/";
    private Button btn_register, btn_takepic;
    private ImageView imageView;
    private String UserName = "";

    private boolean  flag = false;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    //firebase realtime database reference
    private DatabaseReference mDatabase;

    @RequiresApi(api = Build.VERSION_CODES.M)


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockchain_user_registration);
        mAuth = FirebaseAuth.getInstance();
        init();
        if (Build.VERSION.SDK_INT >= 23)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        btn_takepic.setOnClickListener(view -> dispatchPictureTakerAction());

        btn_register.setOnClickListener(v -> uploadImage());

    }
/*
    TODO:   get  logged in username and replace that with someData

 */

    private void uploadImage()
    {
        // gets file path
        File file = new File(fileStorageDir);
        // get retrofit instance
        Retrofit retrofit = NetworkClient.getRetrofit();
        // form the request body for image
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("newimage", file.getName(), requestBody);
        // get currently logged in users details from firebase
        UserName = (mAuth.getCurrentUser().getDisplayName().isEmpty())?"NULL":mAuth.getCurrentUser().getDisplayName();
        // forrm user requestbody of type  plain text
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), UserName);

        UploadApis uploadApis = retrofit.create(UploadApis.class);
        // make the network API call PARAM : image and username
        Call call = uploadApis.Register(parts, username);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(Blockchain_user_registration.this,""+response.message(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(Blockchain_user_registration.this,""+ t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        //ID of the request
        String reqID = UUID.randomUUID().toString();
        // Defining the child of storageReference
        StorageReference ref
                = storageReference
                .child("images/"
                        + reqID);
        // adding listeners on upload
        // or failure of image
        ref.putFile(photoURI)
                .addOnSuccessListener(
                        taskSnapshot -> {

                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Image Uploaded Successfully",
                                            Toast.LENGTH_LONG)
                                    .show();




                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //building data to upload to db
                                    Request request = new Request(UserName, reqID, String.valueOf(uri), 0);
                                    Log.i("Request", request.toString());
                                    mDatabase.child(FLAT_NO).child("Requests").setValue(request, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Request Submitted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });


                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(getApplicationContext(),
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(
                                    UploadTask.TaskSnapshot taskSnapshot) {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int) progress + "%");
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                Bitmap bt = rotateImage(bitmap);
                imageView.setImageBitmap(bt);
            }
        }
    }

    private Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileStorageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            try {
                File file=createPhotoFile("2");
                fileStorageDir = file.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(
                        getApplicationContext(), "com.example.Blockchain_App.fileprovider", file
                );
                FileOutputStream out;
                out = new FileOutputStream(file);
                bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    private void dispatchPictureTakerAction()
    {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        photoFile = createPhotoFile("1");
        if (photoFile != null) {
            pathToFile = photoFile.getAbsolutePath();
            fileStorageDir = pathToFile;
            photoURI = FileProvider.getUriForFile(
                    getApplicationContext(), "com.example.Blockchain_App.fileprovider", photoFile
            );
            takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePic, 1);
        }
    }

    private File createPhotoFile(String i)
    {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name+i, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Excep : " + e.toString());
        }
        return image;
    }

    private void init()
    {
        btn_takepic = findViewById(R.id.btn_takepic);
        btn_register = findViewById(R.id.btn_blockchain_reg);
        //btn_takepic = findViewById(R.id.btn_takepic);
        imageView = findViewById(R.id.img_clicked);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
}