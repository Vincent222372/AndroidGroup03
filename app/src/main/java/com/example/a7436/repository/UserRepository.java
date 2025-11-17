package com.example.a7436.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.a7436.data.database.DatabaseHelper;

// NOTE: This is a simplified implementation for demonstration.
// In a real app, you should handle password hashing securely.
public class UserRepository {

    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Adds a new user to the database.
     * In a real app, the password should be securely hashed.
     * @return true if the user was added successfully, false otherwise.
     */
    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USERNAME, username);
        values.put(DatabaseHelper.KEY_EMAIL, email);
        // IMPORTANT: Storing plain text passwords is a major security risk.
        // This is just for demonstration. Use a proper hashing library like BCrypt.
        values.put(DatabaseHelper.KEY_PASSWORD_HASH, password); // Store password directly for now

        // Inserting Row
        long result = db.insert(DatabaseHelper.TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Checks if a user with the given email and password exists.
     * @return true if credentials are valid, false otherwise.
     */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.KEY_USER_ID};
        String selection = DatabaseHelper.KEY_EMAIL + " = ?" + " AND " + DatabaseHelper.KEY_PASSWORD_HASH + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }
}