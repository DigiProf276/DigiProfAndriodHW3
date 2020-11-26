// ForgotPassword Header
// Group 13: DigiProf
// Primary Coder: Hieu
// Modifiers: Andy
// Modifications:
// - Added Comments and Code Style
// - Code Review and Testing
// - Implemented ForgotPassword

package com.example.digiprof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * ForgotPassword Class handles when the user forgets their password.
 * It takes an email registered with the system and sends an email to reset the password.
 */
public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText emailAddress;
    private Button Reset;
    private TextView banner;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailAddress = findViewById(R.id.editEmailAddress);

        banner = findViewById(R.id.Banner);
        banner.setOnClickListener(this);

        Reset = findViewById(R.id.resetPassword);
        Reset.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Banner:
                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                break;
            case R.id.resetPassword:
                ClickResetPassword();
                break;
        }
    }

    private void ClickResetPassword() {
        String email = emailAddress.getText().toString();

        if (email.isEmpty()) {
            emailAddress.setError("Please enter your email!");
            emailAddress.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddress.setError("Please enter a valid email!");
            emailAddress.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "Please check your email!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                } else {
                    Toast.makeText(ForgotPassword.this, "Try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}