package com.example.task51cpart2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainInterfaceActivity extends AppCompatActivity {

    EditText youtubeUrlField;
    Button playButton, addToPlaylistButton, myPlaylistButton, logoutButton;

    UserDatabase database;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_interface);

        // Set fields
        youtubeUrlField = findViewById(R.id.youtubeUrlField);
        playButton = findViewById(R.id.playButton);
        addToPlaylistButton = findViewById(R.id.addToPlaylistButton);
        myPlaylistButton = findViewById(R.id.myPlaylistButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Get current user from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        currentUsername = prefs.getString("username", null);

        // Check if a user is logged in (should never trigger)
        if (currentUsername == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load the SQLite database
        database = new UserDatabase(this);

        // Listen for play button presses by the user, load the YouTube Player into a fragment
        playButton.setOnClickListener(v -> {
            String url = youtubeUrlField.getText().toString().trim();
            String videoId = extractVideoId(url);

            if (videoId != null && !videoId.isEmpty()) {
                YouTubePlayerFragment playerFragment = YouTubePlayerFragment.newInstance(videoId);
                playerFragment.show(getSupportFragmentManager(), "youtube_player");
            } else {
                Toast.makeText(this, "Please enter a valid YouTube URL", Toast.LENGTH_SHORT).show();
            }
        });

        // Listen for add to playlist button presses and add the url to the user's user_playlists table
        addToPlaylistButton.setOnClickListener(v -> {
            String url = youtubeUrlField.getText().toString().trim();

            // Ensure a valid url is provided
            if (url.isEmpty()) {
                Toast.makeText(this, "Enter a URL to add", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the video url was added to the user's playlist
            boolean success = database.addToPlaylist(currentUsername, url);
            if (success) {
                Toast.makeText(this, "Added to playlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add to playlist", Toast.LENGTH_SHORT).show();
            }
        });

        // Listen for my playlist button presses to load up the user's playlist in another activity
        myPlaylistButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaylistActivity.class);
            startActivity(intent);
        });

        // Listen for logout button presses and clear sharedpreferences data, load back to the MainActivity screen
        logoutButton.setOnClickListener(v -> {

            // Clear SharedPreferences
            getSharedPreferences("app", MODE_PRIVATE)
                    .edit()
                    .remove("username")
                    .apply();

            // Redirect to login screen
            Intent intent = new Intent(MainInterfaceActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear activity stack
            startActivity(intent);
            finish();
        });

        // Insets padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInterface), (v, insets) -> {
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
