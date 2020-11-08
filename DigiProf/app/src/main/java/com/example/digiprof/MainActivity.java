package com.example.digiprof;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView register, ForgotPassword;
    private Button LOGIN;
    private EditText EmailAddress, Password;
    private TextView theFeedback;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.Register);
        register.setOnClickListener(this);

        ForgotPassword = findViewById(R.id.ForgotPassword);
        ForgotPassword.setOnClickListener(this);

        EmailAddress = findViewById(R.id.editTextInputEmailAddress);
        Password = findViewById(R.id.editTextPassword);
        theFeedback = findViewById(R.id.FeedBack);

        LOGIN = findViewById(R.id.Login);
        LOGIN.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.Register:
                Intent intent = new Intent(this, UserRegistration.class);
                startActivity(intent);
                break;
            case R.id.Login:
                LoginClicks(); break;
            case R.id.ForgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    private void LoginClicks(){
        LOGIN.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.FROYO)
            @Override public void onClick(View view){
                String email = EmailAddress.getText().toString();
                String password = Password.getText().toString();

                //Check user's inputs
                if(email.isEmpty()){
                    EmailAddress.setError("Please enter your Email!");
                    EmailAddress.requestFocus();
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    EmailAddress.setError("Please enter a valid email!");
                    EmailAddress.requestFocus();
                }

                if(password.isEmpty()){
                    Password.setError("Please enter passowrd!");
                    Password.requestFocus();
                }

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            //Email verification
                            if(user.isEmailVerified()){
                                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                                Toast.makeText(MainActivity.this, "Successful Login", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                            else {
                                user.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "Please verify your email!", Toast.LENGTH_LONG).show();
                            }

                        }
                        else{
                            Toast.makeText(MainActivity.this, "Failed to login!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

}