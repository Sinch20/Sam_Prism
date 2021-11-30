package com.example.Blockchain_App.Core;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Blockchain_App.Blockchain.Blockchain_user_registration;
import com.example.Blockchain_App.Blockchain.Blockchain_user_verification;
import com.example.Blockchain_App.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity
{

    private Button btn_reg, btn_verify,btn_logout,btn_requests;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        init();

        btn_reg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent blockchain_reg_intent = new Intent(getApplicationContext(), Blockchain_user_registration.class);
                startActivity(blockchain_reg_intent);

            }
        });


        btn_verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent blockchain_verify_intent = new Intent(getApplicationContext(), Blockchain_user_verification.class);
                startActivity(blockchain_verify_intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });

        btn_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent req_Intent = new Intent(HomeActivity.this,ImagesActivity.class);
                startActivity(req_Intent);
                finish();
            }
        });

    }

    private void init()
  {
       btn_reg = findViewById(R.id.btn_blockchain_reg);
       btn_verify = findViewById(R.id.btn_blockchain_verify);
       btn_logout = findViewById(R.id.btn_logout);
       btn_requests = findViewById(R.id.btn_requests);
   }

   private void Logout()
   {
       FirebaseAuth.getInstance().signOut();
       Intent logoutIntent = new Intent(getApplicationContext(), Activity_SignIn.class);
       startActivity(logoutIntent);
       finish();
   }
}















