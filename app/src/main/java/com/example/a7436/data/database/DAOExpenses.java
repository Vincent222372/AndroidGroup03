package com.example.a7436.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.a7436.model.Expense;

public class DAOExpenses {

    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public DAOExpenses(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Adds a new expense to the database.
     * @param expense The Expense object to add.
     * @return The ID of the newly inserted row, or -1 if an error occurred.
     */
    public long addExpense(Expense expense) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USER_ID, expense.getUserId());
        values.put(DatabaseHelper.KEY_CATEGORY_ID, expense.getCategoryId());
        values.put("description", expense.getDescription()); // Assuming column name is 'description'
        values.put("date", expense.getDate()); // Assuming column name is 'date'
        values.put("amount", expense.getAmount()); // Assuming column name is 'amount'
        // Add other recurring expense fields later

        return db.insert("Expense", null, values); // Assuming table name is 'Expense'
    }

    // We will add more methods like getExpense, getAllExpenses, updateExpense, deleteExpense later.
}
