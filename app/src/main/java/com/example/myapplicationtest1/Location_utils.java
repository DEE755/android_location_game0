package com.example.myapplicationtest1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Location_utils {

    void put_players_on_client_map()
    {
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference("online_players");

    }

}
