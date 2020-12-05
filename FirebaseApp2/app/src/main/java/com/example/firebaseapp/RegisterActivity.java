package com.example.firebaseapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    //views
    EditText mEmailEt, mPasswordEt;
    Button mRegisterBtn, changeLang;
    TextView mHaveAccountTv;

    //progressbar to display while registering user
    ProgressDialog progressDialog;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_register);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.create_account);
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);
        changeLang = findViewById(R.id.changeMyLang);

        //In the onCreate() method, initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.registering_user));

        //handle Change Language Button Click
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Alert Dialog to display list of languages, only one can be selected
                showChangeLanguageDialog();
            }
        });
        //handle register btn click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email, password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //set error and focuss to email edittext
                    mEmailEt.setError(getResources().getString(R.string.invalid_email));
                    mEmailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    //set error and focuss to password edittext
                    mPasswordEt.setError(getResources().getString(R.string.password_length_not_six));
                    mPasswordEt.setFocusable(true);
                } else {
                    registerUser(email, password); //register the user
                }
            }
        });
        //handle login textview click listener
        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });


    }

    //  Create Separate Strings.xml for each language
    private void showChangeLanguageDialog() {
        //Array of languages to display in dialog box
        final String [] listLangs = {"French (Canada)", " Punjabi", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
        mBuilder.setTitle(R.string.choose_languages);
        mBuilder.setSingleChoiceItems(listLangs, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i == 0){
                    //French (Canada)
                    setLocale("fr");
                    recreate();
                }
                else if(i == 1){
                    //Punjabi
                    setLocale("pa");
                    recreate();
                }

                else if(i==2){
                    //English
                    setLocale("en");
                    recreate();
                }

                // dismiss alert dialog
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        // show alert dialog
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }
    // load languages saved in shared preferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    private void registerUser(String email, String password) {
        //email and password pattern is valid, show progress dialog and start registering user
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
                            //Get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            //When user is registered store user info in firebase realtime database too
                            //using HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hasmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", "");
                            hashMap.put("onlineStatus", "online");
                            hashMap.put("typingTo", "noOne");
                            hashMap.put("image", "");
                            hashMap.put("cover", "");

                            //firebase database isntance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, R.string.registered +user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error, dismiss progress dialog and get and show the error message
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}