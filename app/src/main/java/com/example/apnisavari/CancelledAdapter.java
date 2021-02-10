package com.example.apnisavari;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CancelledAdapter extends RecyclerView.Adapter<CancelledAdapter.MyViewHolder> {

    Context context;
   CircleImageView imageUpdate;
    ArrayList<CancelledHistory> history;

    public CancelledAdapter(Context c , ArrayList<CancelledHistory> p)
    {
        context = c;
        history = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview1,parent,false));
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder,int position){
        holder.pickup.setText(history.get(position).getPickUpPoint());
        holder.destination.setText(history.get(position).getDestination());

        holder.Date1.setText(String.valueOf(history.get(position).getDate()));
        holder.BookingId.setText(String.valueOf(history.get(position).getBookingId()));
        FirebaseDatabase.getInstance().getReference("DriverInformation").child(history.get(position).getDriverId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
try {
    Picasso.get()
            .load(data.get("profileAks"))
            .into(imageUpdate);
} catch (Exception e) {
    e.printStackTrace();
}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {

        return history.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView pickup,destination,Date1,BookingId;


        public MyViewHolder(View itemView) {
            super(itemView);
            pickup = (TextView) itemView.findViewById(R.id.pickup1);
            destination = (TextView) itemView.findViewById(R.id.destination1);

            Date1 = (TextView) itemView.findViewById(R.id.Date1);
            BookingId = (TextView) itemView.findViewById(R.id.CancellationId);
           imageUpdate =itemView.findViewById(R.id.driversPic);



        }

    }
}