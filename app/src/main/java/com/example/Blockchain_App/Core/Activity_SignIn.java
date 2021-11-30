package com.example.Blockchain_App.Core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Blockchain_App.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class Activity_SignIn extends AppCompatActivity {

    private Button btn_signin;
    private AutoCompleteTextView email,pass;
    private TextView tv_crate_acc;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
        init();

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInExistingUser(email.getText().toString(),pass.getText().toString());
            }
        });

        tv_crate_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CreateAccountIntent = new Intent(Activity_SignIn.this, activity_signup.class);
                startActivity(CreateAccountIntent);
            }
        });
    }


    private void SignInExistingUser(String email,String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent SigninIntent = new Intent(Activity_SignIn.this,HomeActivity.class);
                            startActivity(SigninIntent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Activity_SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void  init()
    {
        btn_signin = findViewById(R.id.btnSignIn);
        tv_crate_acc = findViewById(R.id.tv_createAcc);
        email = findViewById(R.id.atvEmailLog);
        pass = findViewById(R.id.atvPasswordLog);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent home = new Intent(Activity_SignIn.this,HomeActivity.class);
            startActivity(home);
        }
    }
}

