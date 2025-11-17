package com.example.a7436.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.a7436.model.User;
import at.favre.lib.crypto.bcrypt.BCrypt; // Correct import

/**
 * DAO (Data Access Object) for managing Users in the SQLite database.
 */
public class DAOUsers {

    private final SQLiteDatabase db;

    public DAOUsers(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long addUser(String username, String email, String phoneNumber, String password) {
        // Hash the password with a randomly generated salt.
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USERNAME, username);
        values.put(DatabaseHelper.KEY_EMAIL, email);
        values.put(DatabaseHelper.KEY_PHONE_NUMBER, phoneNumber);
        values.put(DatabaseHelper.KEY_PASSWORD_HASH, hashedPassword);

        return db.insert(DatabaseHelper.TABLE_USER, null, values);
    }

    public User checkUserLogin(String email, String password) {
        String[] columns = {
                DatabaseHelper.KEY_USER_ID,
                DatabaseHelper.KEY_USERNAME,
                DatabaseHelper.KEY_EMAIL,
                DatabaseHelper.KEY_PHONE_NUMBER,
                DatabaseHelper.KEY_PASSWORD_HASH
        };
        String selection = DatabaseHelper.KEY_EMAIL + " = ?";
        String[] selectionArgs = {email};
        
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_USER, columns, selection, selectionArgs, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                String storedHash = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_PASSWORD_HASH));

                // Verify the password against the stored hash.
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);
                if (result.verified) {
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USERNAME));
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EMAIL));
                    String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_PHONE_NUMBER));
                    return new User(userId, username, userEmail, phoneNumber);
                }
            }
        }
        return null;
    }

    public boolean checkEmailExists(String email) {
        String[] columns = {DatabaseHelper.KEY_USER_ID};
        String selection = DatabaseHelper.KEY_EMAIL + " = ?";
        String[] selectionArgs = {email};

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_USER, columns, selection, selectionArgs, null, null, null)) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USERNAME, user.getUsername());
        values.put(DatabaseHelper.KEY_EMAIL, user.getEmail());
        values.put(DatabaseHelper.KEY_PHONE_NUMBER, user.getPhoneNumber());

        return db.update(DatabaseHelper.TABLE_USER, values, DatabaseHelper.KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getUserId())});
    }

    public int updatePassword(int userId, String newPassword) {
        String newHashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_PASSWORD_HASH, newHashedPassword);

        return db.update(DatabaseHelper.TABLE_USER, values, DatabaseHelper.KEY_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public void deleteUser(int userId) {
        db.delete(DatabaseHelper.TABLE_USER, DatabaseHelper.KEY_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public User getUser(int userId) {
        String[] columns = {
                DatabaseHelper.KEY_USER_ID,
                DatabaseHelper.KEY_USERNAME,
                DatabaseHelper.KEY_EMAIL,
                DatabaseHelper.KEY_PHONE_NUMBER
        };
        String selection = DatabaseHelper.KEY_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_USER, columns, selection, selectionArgs, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USERNAME));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EMAIL));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_PHONE_NUMBER));
                return new User(userId, username, userEmail, phoneNumber);
            }
        }
        return null;
    }
}
