package com.example.newdigiprof;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * RegisterActivity extends AppCompatActivity
 */
public class RegisterActivity extends AppCompatActivity {
    EditText emailTextBox, passwordTextBox;
    Button registerButton;
    TextView loginTextView;
    ProgressDialog registerUserProgressBar;
    ActionBar actionBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        localObjectInialization();

        registerButton.setOnClickListener(v -> handleRegisterButtonClick());

        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void localObjectInialization() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        emailTextBox = findViewById(R.id.emailEt);
        passwordTextBox = findViewById(R.id.passwordEt);
        registerButton = findViewById(R.id.registerBtn);
        loginTextView = findViewById(R.id.have_accountTv);

        registerUserProgressBar = new ProgressDialog(this);
        registerUserProgressBar.setMessage("Registering User...");

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void handleRegisterButtonClick() {
        String email = emailTextBox.getText().toString().trim();
        String password = passwordTextBox.getText().toString().trim();
        boolean emailMatch = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean passwordValidLength = password.length() < 6;
        boolean emailAndPasswordValid = emailMatch && passwordValidLength;

        if(emailAndPasswordValid) {
            registerUser(email, password);
        } else if (!emailMatch) {
            emailTextBox.setError("Invalid Email");
            emailTextBox.setFocusable(true);
        } else if (!passwordValidLength) {
            passwordTextBox.setError("Password length must be at least 6 characters");
            passwordTextBox.setFocusable(true);
        }
    }

    private void registerUser(String email, String password) {
        registerUserProgressBar.show();

        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                registerUserProgressBar.dismiss();
                if (task.isSuccessful()) {
                    startRegistrationActivity();
                } else {
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(error -> {
                registerUserProgressBar.dismiss();
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void startRegistrationActivity() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Toast.makeText(RegisterActivity.this, "Registration Successful...\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}