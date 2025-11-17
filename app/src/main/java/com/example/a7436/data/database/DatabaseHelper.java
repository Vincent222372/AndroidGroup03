package com.example.a7436.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_manager.db";
    private static final int DATABASE_VERSION = 5; // Version incremented to 5

    // Table Names
    public static final String TABLE_USER = "User";
    public static final String TABLE_EXPENSE = "Expense"; // New Table
    public static final String TABLE_CATEGORY = "Category"; // New Table

    // User Table Columns
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD_HASH = "password_hash";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PROFILE_IMAGE_PATH = "profile_image_path";

    // Category Table Columns
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_CATEGORY_NAME = "category_name";

    // Expense Table Columns
    public static final String KEY_EXPENSE_ID = "expense_id";
    public static final String KEY_EXPENSE_USER_ID = "user_id"; // Foreign Key
    public static final String KEY_EXPENSE_CATEGORY_ID = "category_id"; // Foreign Key
    public static final String KEY_EXPENSE_DESCRIPTION = "description";
    public static final String KEY_EXPENSE_AMOUNT = "amount";
    public static final String KEY_EXPENSE_DATE = "date";
    public static final String KEY_EXPENSE_IS_RECURRING = "is_recurring";
    public static final String KEY_EXPENSE_START_DATE = "start_date";
    public static final String KEY_EXPENSE_END_DATE = "end_date";
    public static final String KEY_EXPENSE_FREQUENCY = "frequency";

    // --- CREATE TABLE STATEMENTS ---
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_USERNAME + " TEXT NOT NULL, "
            + KEY_PASSWORD_HASH + " TEXT NOT NULL, "
            + KEY_EMAIL + " TEXT NOT NULL UNIQUE, "
            + KEY_PHONE_NUMBER + " TEXT, "
            + KEY_ADDRESS + " TEXT, "
            + KEY_PROFILE_IMAGE_PATH + " TEXT);";

    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
            + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CATEGORY_NAME + " TEXT NOT NULL UNIQUE);";

    private static final String CREATE_TABLE_EXPENSE = "CREATE TABLE " + TABLE_EXPENSE + "("
            + KEY_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_EXPENSE_USER_ID + " INTEGER NOT NULL, "
            + KEY_EXPENSE_CATEGORY_ID + " INTEGER NOT NULL, "
            + KEY_EXPENSE_DESCRIPTION + " TEXT, "
            + KEY_EXPENSE_AMOUNT + " REAL NOT NULL, "
            + KEY_EXPENSE_DATE + " TEXT NOT NULL, "
            + KEY_EXPENSE_IS_RECURRING + " INTEGER DEFAULT 0, "
            + KEY_EXPENSE_START_DATE + " TEXT, "
            + KEY_EXPENSE_END_DATE + " TEXT, "
            + KEY_EXPENSE_FREQUENCY + " TEXT, "
            + "FOREIGN KEY(" + KEY_EXPENSE_USER_ID + ") REFERENCES " + TABLE_USER + "(" + KEY_USER_ID + "), "
            + "FOREIGN KEY(" + KEY_EXPENSE_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + KEY_CATEGORY_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_EXPENSE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
