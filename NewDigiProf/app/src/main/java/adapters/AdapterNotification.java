package adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newdigiprof.R;
import models.ModelNotification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.HolderNotification> {


    private Context context;
    private ArrayList<ModelNotification> notificationsList;

    private FirebaseAuth firebaseAuth;

    public AdapterNotification(Context context, ArrayList<ModelNotification> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view row_notification

        View view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false);

        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderNotification holder, int position) {
        //get and set data to views

        //get data
        final ModelNotification model = notificationsList.get(position);
        String name = model.getsName();
        String notification = model.getNotification();
        String image = model.getsImage();
        final String timestamp = model.getTimestamp();
        String senderUid = model.getsUid();
        final String pId = model.getpId();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //we will get the name, email, image of the user of notification from his uid
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String image = "" + ds.child("image").getValue();
                            String email = "" + ds.child("email").getValue();

                            //add to model
                            model.setsName(name);
                            model.setsEmail(email);
                            model.setsImage(image);

                            //set to views
                            holder.nameTv.setText(name);

                            try {
                                Picasso.get().load(image).placeholder(R.drawable.ic_default_img).into(holder.avatarIv);
                            } catch (Exception e) {
                                holder.avatarIv.setImageResource(R.drawable.ic_default_img);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        holder.notificationTv.setText(notification);
        holder.timeTv.setText(pTime);
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    //holder class for views of row_notifications.xml
    class HolderNotification extends RecyclerView.ViewHolder {

        //declare views
        ImageView avatarIv;
        TextView nameTv, notificationTv, timeTv;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            notificationTv = itemView.findViewById(R.id.notificationTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}