package com.example.firebaseapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.firebaseapp.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //views
    Button mRegisterBtn, mLoginBtn;
    Button changeLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        // change actionbar title,
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.app_name));

        //init views
        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.login_btn);
        changeLang = findViewById(R.id.changeMyLang);

        //handle Change Language Button Click
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Alert Dialog to display list of languages, only one can be selected
                showChangeLanguageDialog();
            }
        });

        //handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start RegisterActivity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        //handle login button click
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start LoginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
//  Create Separate Strings.xml for each language
    private void showChangeLanguageDialog() {
        //Array of languages to display in dialog box
        final String [] listLangs = {"French (Canada)", " Punjabi", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
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

}
