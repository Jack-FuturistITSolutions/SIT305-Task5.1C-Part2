package com.example.task51cpart2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class UserDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "users";
    public static final String COL_ID = "id";
    public static final String COL_FULLNAME = "full_name";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called once upon initial app creation
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create users table
        String userTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FULLNAME + " TEXT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(userTable);

        // Create playlist table
        String playlistTable = "CREATE TABLE user_playlists (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                "video_url TEXT)";
        db.execSQL(playlistTable);
    }

    // Called when database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert user data with provided credentials
    public boolean insertUser(String fullName, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_FULLNAME, fullName);
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Check if username already exists when a user creates a new account
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COL_ID},
                COL_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Authenticate user login when user is logging in
    public boolean validateUserLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COL_ID},
                COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null
        );

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    // Insert a video into a user's playlist with provided username and video url
    public boolean addToPlaylist(String username, String videoUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("video_url", videoUrl);

        long result = db.insert("user_playlists", null, values);
        return result != -1;
    }

    // Retrieve playlist for a user upon loading into the activity
    public Cursor getPlaylist(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                "user_playlists",
                new String[]{"video_url"},
                "username = ?",
                new String[]{username},
                null, null, null
        );
    }

}
