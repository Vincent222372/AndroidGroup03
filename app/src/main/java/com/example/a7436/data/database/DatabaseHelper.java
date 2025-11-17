package com.example.a7436.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_manager.db";
    private static final int DATABASE_VERSION = 4; // Version incremented to 4

    // Table Names
    public static final String TABLE_USER = "User";
    public static final String TABLE_EXPENSES = "expenses";

    // User Table Columns
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD_HASH = "password_hash";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_ADDRESS = "address";

    // Expense Table Columns
    public static final String COLUMN_EXPENSE_ID = "expense_id";
    public static final String COLUMN_EXPENSE_USER_ID = "user_id";
    public static final String COLUMN_EXPENSE_DESCRIPTION = "description";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";
    public static final String COLUMN_EXPENSE_DATE = "date";
    public static final String COLUMN_EXPENSE_CATEGORY = "category";

    // Create Table Statements
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "( "
            + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_USERNAME + " TEXT NOT NULL, "
            + KEY_PASSWORD_HASH + " TEXT NOT NULL, "
            + KEY_EMAIL + " TEXT NOT NULL UNIQUE, "
            + KEY_PHONE_NUMBER + " TEXT, "
            + KEY_ADDRESS + " TEXT );";

    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + "("
            + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EXPENSE_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_EXPENSE_DESCRIPTION + " TEXT,"
            + COLUMN_EXPENSE_AMOUNT + " REAL NOT NULL,"
            + COLUMN_EXPENSE_DATE + " TEXT NOT NULL,"
            + COLUMN_EXPENSE_CATEGORY + " TEXT NOT NULL,"
            + "FOREIGN KEY(" + COLUMN_EXPENSE_USER_ID + ") REFERENCES " + TABLE_USER + "(" + KEY_USER_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }
}
