// UserRegistration Header
// Group 13: DigiProf
// Primary Coder: Hieu
// Modifiers: Andy
// Modifications:
// - Added Comments and Code Style
// - Code Review and Testing
// - Implemented UserRegistration
package com.example.digiprof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * UserRegistration Class creates the screen for registration and also allows the user to register for the app.
 */
public class UserRegistration extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth auth;

    private EditText emailAddress, Password;
    private TextView theBanner;
    Button RegisterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        auth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.editEmailRegistration);
        Password = findViewById(R.id.editPasswordRegistration);

        theBanner = findViewById(R.id.Banner);
        theBanner.setOnClickListener(this);

        RegisterUser = findViewById(R.id.RegisterUser);
        RegisterUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Banner:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.RegisterUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = emailAddress.getText().toString();
        String password = Password.getText().toString();

        if (email.isEmpty()) {
            emailAddress.setError("Email is required!");
            emailAddress.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddress.setError("Please provide a valid email address!");
            emailAddress.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Password.setError("Password is required!");
            Password.requestFocus();
            return;
        }

        if (password.length() < 6) {
            Password.setError("Password length must be at least 6 characters!");
            Password.requestFocus();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Howdy");
                User user = new User(email, password);

                FirebaseDatabase
                    .getInstance()
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user)
                    .addOnCompleteListener(thetask -> {
                        if (thetask.isSuccessful()) {
                            Toast.makeText(UserRegistration.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UserRegistration.this, MainActivity.class));
                            FirebaseUser newuser = FirebaseAuth.getInstance().getCurrentUser();
                            newuser.sendEmailVerification();
                        } else {
                            Toast.makeText(UserRegistration.this, "Failed to register! Please try again!", Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
    }
}