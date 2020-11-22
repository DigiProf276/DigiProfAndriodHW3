package com.example.newdigiprof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LoginActivity extends AppCompatActivity
 */
public class LoginActivity extends AppCompatActivity {
    EditText emailTextBox, passwordTextBox;
    TextView noAccountTextView, recoverPasswordTextView;
    Button loginButton;
    ProgressDialog progressDialog;
    ActionBar actionBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        localObjectsInitialization();

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> loginButtonClickHandler());

        noAccountTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        recoverPasswordTextView.setOnClickListener(v -> showRecoverPasswordDialog());
    }

    private void localObjectsInitialization() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        emailTextBox = findViewById(R.id.emailEt);
        passwordTextBox = findViewById(R.id.passwordEt);
        noAccountTextView = findViewById(R.id.nothave_accountTv);
        loginButton = findViewById(R.id.loginBtn);
        recoverPasswordTextView = findViewById(R.id.recoverPassTv);
        progressDialog = new ProgressDialog(this);
    }

    private void loginButtonClickHandler() {
        String email = emailTextBox.getText().toString();
        String password = passwordTextBox.getText().toString().trim();
        boolean emailMatch = Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (emailMatch) {
            loginUser(email, password);
        } else {
            emailTextBox.setError("Invalid Email");
            emailTextBox.setFocusable(true);
        }
    }

    // TODO what does this do? should probably be broken apart
    private void showRecoverPasswordDialog() {

        // Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //set Layout Linear Layout
        LinearLayout linearLayout = new LinearLayout(this);

        //views to set in dialog
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        // Set the min width of EditView to fit a text of x  'M' Letters
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        //Recover button
        builder.setPositiveButton("Recover", (dialog, which) -> {
            //input email
            String email = emailEt.getText().toString().trim();
            beginRecovery(email);
        });

        //Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                progressDialog.dismiss();
                String taskStatus = task.isSuccessful() ? "Email sent" : "Failed...";
                Toast.makeText(LoginActivity.this, taskStatus, Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(error -> {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void loginUser(String email, String password) {
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    // TODO where is this user object used?
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(error -> {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}