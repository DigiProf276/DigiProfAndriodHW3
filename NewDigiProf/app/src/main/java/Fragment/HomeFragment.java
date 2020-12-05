package Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.newdigiprof.AddVideoActivity;
import com.example.newdigiprof.MainActivity;
import com.example.newdigiprof.R;
import com.example.newdigiprof.SendVideoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.AdapterVideo;
import models.ModelVideo;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;

    // UI Views
    FloatingActionButton addVideosBtn;
    private RecyclerView videosRv;
    //ArrayList
    private ArrayList<ModelVideo> videoArrayList;
    //adapter
    private AdapterVideo adapterVideo;
    // Database Reference
    DatabaseReference dataBRef;
    // Activity
    private Activity mActivity;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        //initialize
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize UI views
        addVideosBtn = view.findViewById(R.id.addVideosBtn);
        videosRv = view.findViewById(R.id.videosRv);

        // function call, load videos
        loadVideosFromFirebase();

        //Handle Click Button
        addVideosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Option Dialog
                OptionDialog();
            }
        });

        return view;
    }

    private void checkUserStatus(){
        // get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!= null){
            //user is signed in stay here
            // set email of logged in user
            //mProfileTv.setText(user.getEmail());
        }
        else{
            //user not signed in, go to main Activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);// to show menu option in fragment
        super.onCreate(savedInstanceState);

    }

    // Attach activity to fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;//use this one .. this is MainActivity instance u can use this as MainActivity mMainActivity = (MainActivity)mActivity;
    }

    //inflate options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate menu
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //handle menu item click

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item  id
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    private void OptionDialog() {
        // Available options
        String[] options = {"Send", "Upload"};
        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Video options").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    startActivity(new Intent(getActivity().getApplication(), AddVideoActivity.class));
                } else {
                    startActivity(new Intent(getActivity().getApplication(), SendVideoActivity.class));
                }
            }
        }).show();
    }

    // Loads videos from firebase into the screen
    private void loadVideosFromFirebase() {
        // initialize Array List before adding data into it
        videoArrayList = new ArrayList<>();

        //database reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear list before adding data into it
                videoArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get Data
                    ModelVideo modelVideo = ds.getValue(ModelVideo.class);
                    // add model/data into list
                    videoArrayList.add(modelVideo);
                }
                // setup adapter
                adapterVideo = new AdapterVideo(getContext(), videoArrayList, mActivity.getApplication());
                //set adapter to recyclerView
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}