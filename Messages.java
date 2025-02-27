package com.example.myapplicationtest1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class Messages {
    String message;

    String destination;

    Player destination_player;

    static String last_message;



    Map<String,String> message_mapping;
    static Database ref= MainActivity.getmDatabase();;

    Messages(){};
    Messages(String message, Player destination_player) {
        this.message = message;
        this.destination_player = destination_player;
        }

        public void send_message()
            {
                ref.add_message_to_db(destination,message);
            }

    public void input_message_and_send(Player player_sender, Player destination_player,Context context) {
        // Create an EditText view to get user input
        final EditText input = new EditText(context);

        String sender_name= player_sender.getName();
        String receiver_key=destination_player.getPlayerKey();
        destination=receiver_key;

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Write Your Message:");
        builder.setMessage("Please enter your message to " + destination_player.getName() + ":");
        Drawable drawable_profile_pic = new BitmapDrawable(context.getResources(), destination_player.getProfile_pic());
        builder.setIcon(drawable_profile_pic);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                message = input.getText().toString() + "\n sent from: " + sender_name;
                send_message();
                Toast.makeText(context, "Message Sent to " + destination_player.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }



    public void start_listening_for_messages()
    {
        ref.get_db_ref().child("online_players").child(MyService.getClientPlayer().getPlayerKey()).child("messages").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                last_message = dataSnapshot.getValue(String.class);
                Log.d("Messages", "New message: " + last_message);
                MainActivity.new_message_flag=true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Messages", "Failed to read value.", error.toException());
            }
        });
    }
    }

