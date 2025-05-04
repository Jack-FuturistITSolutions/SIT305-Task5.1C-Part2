package com.example.task51cpart2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    ListView playlistListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> playlistItems;
    UserDatabase database;
    String currentUsername;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist);

        // Bind data to views
        playlistListView = findViewById(R.id.playlistListView);
        playlistItems = new ArrayList<>();

        // Set the back up
        backButton = findViewById(R.id.backButton);

        // Load the SQLite database
        database = new UserDatabase(this);

        // Listen for back button presses and move back to the MainInterfaceActivity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlaylistActivity.this, MainInterfaceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            // Finish playlist screen
            finish();
        });


        // Get the currently logged in user
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        currentUsername = prefs.getString("username", null);

        // Check if username is valid (should never trigger)
        if (currentUsername == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load playlist with video urls in the user's user_playlists table
        Cursor cursor = database.getPlaylist(currentUsername);
        if (cursor.moveToFirst()) {
            do {
                String videoUrl = cursor.getString(cursor.getColumnIndexOrThrow("video_url"));
                playlistItems.add(videoUrl);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Check if the playlist is empty
        if (playlistItems.isEmpty()) {
            Toast.makeText(this, "No videos in playlist", Toast.LENGTH_SHORT).show();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlistItems);
        playlistListView.setAdapter(adapter);

        // Listen for playlist url presses and load up the video into the YouTubePlayerFragment
        playlistListView.setOnItemClickListener((parent, view, position, id) -> {
            String videoUrl = playlistItems.get(position);
            String videoId = extractVideoId(videoUrl);

            if (videoId != null && !videoId.isEmpty()) {
                YouTubePlayerFragment playerFragment = YouTubePlayerFragment.newInstance(videoId);
                playerFragment.show(getSupportFragmentManager(), "youtube_player");
            } else {
                Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle system window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.playlistContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Extract the videoId from the provided url
    private String extractVideoId(String url) {
        String videoId = null;
        if (url.contains("v=")) {
            int index = url.indexOf("v=") + 2;
            int amp = url.indexOf("&", index);
            videoId = (amp != -1) ? url.substring(index, amp) : url.substring(index);
        } else if (url.contains("youtu.be/")) {
            int index = url.indexOf("youtu.be/") + 9;
            videoId = url.substring(index);
        }
        return videoId;
    }
}