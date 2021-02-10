package com.example.apnisavari;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryCustomerSide extends Fragment {
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<History> list;
    MyAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)

    { ViewGroup rootview=(ViewGroup)inflater.inflate(R.layout.fragment_item_list,container,false);
        recyclerView = (RecyclerView)rootview. findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        database=FirebaseDatabase.getInstance();
        ref=database.getReference("TripHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list = new ArrayList<History>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    History p = dataSnapshot1.getValue(History.class);
                    list.add(p);
                }
                adapter = new MyAdapter(getActivity(),list);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return rootview;
    }
}
