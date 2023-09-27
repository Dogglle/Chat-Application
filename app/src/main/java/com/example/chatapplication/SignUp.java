package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtName = findViewById(R.id.edt_name);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                signUp(name, email, password);
            }
        });
    }

    private void signUp(final String name, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();
                                addUserToDatabase(name, email, uid);
                            }
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close the SignUp activity after successful sign-up
                        } else {
                            // Check the specific error and show an appropriate toast message
                            String errorMessage;
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthInvalidUserException) {
                                errorMessage = "Invalid user or password.";
                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                errorMessage = "Invalid credentials. Please check your email and password.";
                            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                errorMessage = "User with this email already exists.";
                            } else {
                                errorMessage = "Some error occurred. Please try again.";
                            }

                            Toast.makeText(SignUp.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToDatabase(String name, String email, String uid) {
        mDbref = FirebaseDatabase.getInstance().getReference();
        mDbref.child("user").child(uid).setValue(new User(name, email, uid));
    }
}
