package com.example.a7436.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.a7436.model.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DAOExpenses {
    private final DatabaseHelper dbHelper;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public DAOExpenses(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EXPENSE_USER_ID, expense.getUserId());
        values.put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, expense.getDescription());
        values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, dateFormat.format(expense.getDate()));
        values.put(DatabaseHelper.COLUMN_EXPENSE_CATEGORY, expense.getCategory());

        long id = db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
        return id;
    }

    public List<Expense> getExpensesForUser(int userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_EXPENSES,
                new String[]{DatabaseHelper.COLUMN_EXPENSE_ID, DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, DatabaseHelper.COLUMN_EXPENSE_AMOUNT, DatabaseHelper.COLUMN_EXPENSE_DATE, DatabaseHelper.COLUMN_EXPENSE_CATEGORY},
                DatabaseHelper.COLUMN_EXPENSE_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, DatabaseHelper.COLUMN_EXPENSE_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID)));
                expense.setUserId(userId);
                expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT)));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY)));
                try {
                    expense.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE))));
                } catch (ParseException e) {
                    expense.setDate(new Date()); // fallback to current date
                }
                expenses.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }

    // Methods for updating and deleting expenses can be added here
}
