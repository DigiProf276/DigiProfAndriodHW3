package com.example.firebaseapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;

    //views
    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccntTv, mRecoverPassTv;
    Button mLoginBtn;
    Button changeLang;
//    SignInButton mGoogleLoginBtn;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    //progress dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.login);
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        notHaveAccntTv = findViewById(R.id.nothave_accountTv);
        mRecoverPassTv = findViewById(R.id.recoverPassTv);
        mLoginBtn = findViewById(R.id.loginBtn);
        changeLang = findViewById(R.id.changeMyLang);

        //login button click
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input data
                String email = mEmailEt.getText().toString();
                String passw = mPasswordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //invalid email paatern set error
                    mEmailEt.setError(getString(R.string.invalid_email));
                    mEmailEt.setFocusable(true);
                }
                else {
                    //valid email pattern
                    loginUser(email, passw);
                }
            }
        });
        //not have accoun textview click
        notHaveAccntTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        //recover pass textview click
        mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });
        //handle Change Language Button Click
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Alert Dialog to display list of languages, only one can be selected
                showChangeLanguageDialog();
            }
        });
        //init progress dialog
        pd = new ProgressDialog(this);
    }
    //  Create Separate Strings.xml for each language
    private void showChangeLanguageDialog() {
        //Array of languages to display in dialog box
        final String [] listLangs = {"French (Canada)", " Punjabi", "English"};
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);
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
        androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
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



    private void showRecoverPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.recover_password);

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        //views to set in dialog
        final EditText emailEt = new EditText(this);
        emailEt.setHint(R.string.email);
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        /*sets the min width of a EditView to fit a text of n 'M' letters regardless of the actual text
        extension and text size.*/
        emailEt.setMinEms(16);


        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //buttons recover
        builder.setPositiveButton(R.string.recover, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //buttons cancel
        builder.setNegativeButton(R.string.cancell, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        //show progresss dialog
        pd.setMessage(getResources().getString(R.string.sending_email));
        pd.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, R.string.email_sent, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //get and show proper error message
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String passw) {
        //show progresss dialog
        pd.setMessage(getResources().getString(R.string.loggging_in));
        pd.show();
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss progress dialog
                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //user is logged in, so start LoginActivity
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            //dismiss progress dialog
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //dismiss progress dialog
                pd.dismiss();
                //error, get and show error message
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }


}