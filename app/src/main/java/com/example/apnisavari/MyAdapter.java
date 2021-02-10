package com.example.apnisavari;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    CircleImageView imageUpdate;
    ArrayList<History> history;

    public MyAdapter(Context c , ArrayList<History> p)
    {
        context = c;
        history = p;
      }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview,parent,false));
    }

        @Override
        public void onBindViewHolder (@NonNull MyViewHolder holder,int position){
            holder.pickup.setText(history.get(position).getPickUpPoint());
            holder.destination.setText(history.get(position).getDestination());
            holder.fare.setText(String.valueOf(history.get(position).getTotalFare())+"rupees");
            holder.BookingId.setText(String.valueOf(history.get(position).getBookingId()));
            holder.Date.setText(history.get(position).getDate());
            if(history.get(position).getDriverId()!=null) {
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
            else
            {
                Toast.makeText(context, "driver is is null", Toast.LENGTH_SHORT).show();
            }


        }

    @Override
    public int getItemCount() {

        return history.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView pickup,destination,fare,Date,BookingId,DrievrId;


        public MyViewHolder(View itemView) {
            super(itemView);
            pickup = (TextView) itemView.findViewById(R.id.pickup);
            destination = (TextView) itemView.findViewById(R.id.destination);
            fare = (TextView) itemView.findViewById(R.id.fare);
            Date = (TextView) itemView.findViewById(R.id.Date);
            BookingId = (TextView) itemView.findViewById(R.id.BookingId);
            imageUpdate =itemView.findViewById(R.id.driversPic);


        }

    }
}