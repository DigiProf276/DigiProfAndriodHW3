package com.example.newdigiprof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * ProfileActivity extends AppCompatActivity (What does it do?)
 */
public class ProfileActivity extends AppCompatActivity {
    TextView profileTextView;
    ActionBar actionBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        localObjectInitialization();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void localObjectInitialization() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        profileTextView = findViewById(R.id.profileTv);
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        boolean currentUserSignedIn = currentUser != null;

        if(currentUserSignedIn) {
            profileTextView.setText(currentUser.getEmail());
        } else {
            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // TODO What does this do?
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int menuItemID = menuItem.getItemId();
        if(menuItemID == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
