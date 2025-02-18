package com.example.myapplicationtest1;

import static com.example.myapplicationtest1.MyService.getClientPlayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

public class LeaderBoard extends Fragment
{
    private MediaPlayer mediaPlayer;
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup leaderboard,
                             Bundle savedInstanceState) {

        //setContentView(R.layout.fragment_disconnect);
        // Inflate the layout for this fragment
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.coin_catch_music2);
        mediaPlayer.start();

        View view = inflater.inflate(R.layout.fragment_leaderboard, leaderboard, false);
        WebView table_web = view.findViewById(R.id.table_web);
        //GridLayout gridLayout = view.findViewById(R.ids.grid);

        TextView textView = view.findViewById(R.id.rank_text);
        textView.setText("Rank: " + getClientPlayer().getRank());

        table_web.loadUrl("https://android-location-game0.web.app/leaderboard.html");

        table_web.getSettings().setJavaScriptEnabled(true);



        //gridLayout.setColumnCount(3);
        //gridLayout.addView(new LeaderboardItem(getContext(), "1", "Player 1", "1000"));

        return view;

    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
