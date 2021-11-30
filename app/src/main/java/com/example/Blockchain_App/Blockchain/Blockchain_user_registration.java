package com.example.Blockchain_App.Blockchain;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.Blockchain_App.Network.NetworkClient;
import com.example.Blockchain_App.Network.UploadApis;
import com.example.Blockchain_App.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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
    private FirebaseAuth mAuth;

    String pathToFile;
    String filefinal;
    String filePath = "Pictures/";
    private Button btn_register, btn_takepic;
    private ImageView imageView;
    private String UserDetails = "";

    private boolean  flag = false;

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

        btn_takepic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchPictureTakerAction();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                uploadImage();
            }
        });




    }
/*
    TODO:   get  logged in username and replace that with someData

 */

    private void uploadImage()
    {
        // gets file path
        File file = new File(filefinal);
        // get retrofit instance
        Retrofit retrofit = NetworkClient.getRetrofit();
        // form the request body for image
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("newimage", file.getName(), requestBody);
        // get currently logged in users details from firebase
        UserDetails = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        // forrm user requestbody of type  plain text
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), UserDetails);

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
            exif = new ExifInterface(filePath);
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
                filefinal=file.getAbsolutePath();
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
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile("1");
            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                filePath=pathToFile;
                Uri photoURI = FileProvider.getUriForFile(
                        getApplicationContext(), "com.example.Blockchain_App.fileprovider", photoFile
                );
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }
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
    }
}