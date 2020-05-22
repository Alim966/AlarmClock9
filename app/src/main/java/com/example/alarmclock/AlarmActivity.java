package com.example.alarmclock;


import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.IOException;

public class AlarmActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    private MediaPlayer player;
    private static Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private DatabaseHelper databaseHelper;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        int idNote = getIntent().getIntExtra("note_id", 0);
        databaseHelper = DatabaseHelper.getInstance(this);
        Note note = databaseHelper.getNoteById(idNote);
        note.setState(false);
        databaseHelper.updateNote(note);

        tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setText(note.getMessage());

        YoYo.with(Techniques.Bounce)
                .duration(500)
                .repeat(60)
                .playOn(findViewById(R.id.image));

        player = new MediaPlayer();
        try {
            player.setDataSource(getApplicationContext(), uri);
            player.setVolume(100, 100);
            player.setLooping(true);
            player.setOnPreparedListener(this);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player.isPlaying()) {
                    player.stop();
                }
            }
        }, 60000);

        findViewById(R.id.btTurnOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.stop();
                }
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }
}
