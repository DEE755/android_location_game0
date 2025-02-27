package com.example.myapplicationtest1;

import android.media.MediaPlayer;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import org.osmdroid.views.MapView;

import java.util.Random;

public class Bonus_Objects extends Object_to_collect {

    private int time_in_seconds;


    static private boolean is_set_up= false;

    static public boolean getIsSetUp() {
        return is_set_up;
    }

    static public void setIs_set_up(boolean is_set_up) {
        Bonus_Objects.is_set_up = is_set_up;
    }

    //ovveriding points
    private int points=500;

    //overridint the icon
    int icon = R.drawable.bonusbus500_small;

    //overriding the click_sound:

    int click_sound = R.raw.sound_bonus1;


    public int getTime_in_seconds() {
        return time_in_seconds;
    }

    public void setTime_in_seconds(int time_in_seconds) {
        this.time_in_seconds = time_in_seconds;
    }

    public String format_time(int time_in_sec) {
        int min = time_in_sec / 60;
        time_in_sec /= 60;
        int sec = time_in_sec % 60;
        return min + ":" + sec;

    }

    //cpy constructor
    Bonus_Objects(Object_to_collect object_to_collect) {
        super(object_to_collect);
        this.time_in_seconds = 25;
        super.points = points;
        super.click_sound = click_sound;
        is_set_up = true;

    }

    //OVERRIDING THE METHOD TO SET THE MARKER ON THE MAP AND ADDING THE TIME TO THE MARKER AND A DIFFERENT ICON
    @Override
    public void set_object_marker_on_map(MapView mapview) {
        Handler handler0 = new Handler();
        Random random = new Random();

        int time_of_bonus_appearance = random.nextInt(25*1000) + 5000; //5-30 seconds

        handler0.postDelayed(() -> {
            this.getObjectMarker().setIcon(mapview.getContext().getResources().getDrawable(this.icon));
            this.getObjectMarker().setSubDescription(this.format_time(this.getTime_in_seconds()));

            // Create an ImageView to apply the animation
            ImageView markerIconView = new ImageView(mapview.getContext());
            markerIconView.setImageDrawable(this.getObjectMarker().getIcon());

            // Load the animation
            Animation anim1_zoom = AnimationUtils.loadAnimation(mapview.getContext(), R.anim.button_zoom);
            markerIconView.startAnimation(anim1_zoom);


            MediaPlayer mediaPlayer = MediaPlayer.create(mapview.getContext(), R.raw.sound_bonus1);
            mediaPlayer.start();


            Toast.makeText(mapview.getContext(), "Bonus object appeared on the map!", Toast.LENGTH_LONG).show();
            super.set_object_marker_on_map(mapview);

            handler0.postDelayed(() -> {
               cancel_bonus_object(mapview);
            }, this.getTime_in_seconds()*1000);

        },time_of_bonus_appearance);

    }

    private void cancel_bonus_object(MapView mapview) {
        //cancels the bonus object after the time has passed
        this.getObjectMarker().setIcon(mapview.getContext().getResources().getDrawable(super.icon));

        this.points=super.getPoints();
        MediaPlayer mediaPlayer2 = MediaPlayer.create(mapview.getContext(), R.raw.sound_bonus2);
        mediaPlayer2.start();

        Toast.makeText(mapview.getContext(), "Too Late ! Bonus object is not a bonus anymore", Toast.LENGTH_LONG).show();
        super.set_object_marker_on_map(mapview);
    }
}
