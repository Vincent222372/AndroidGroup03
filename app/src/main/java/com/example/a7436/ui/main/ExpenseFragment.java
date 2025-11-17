package com.example.a7436.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a7436.R;
import com.example.a7436.data.database.DAOExpenses;
import com.example.a7436.model.Expense;
import com.example.a7436.ui.auth.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseFragment extends Fragment {

    private EditText editTextDescription, editTextAmount, editTextDate, editTextCategory;
    private Button buttonSaveExpense;
    private RecyclerView recyclerViewExpenses;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList = new ArrayList<>();
    private DAOExpenses daoExpenses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        daoExpenses = new DAOExpenses(getContext());

        editTextDescription = view.findViewById(R.id.edit_text_description);
        editTextAmount = view.findViewById(R.id.edit_text_amount);
        editTextDate = view.findViewById(R.id.edit_text_date);
        editTextCategory = view.findViewById(R.id.edit_text_category);
        buttonSaveExpense = view.findViewById(R.id.button_save_expense);
        recyclerViewExpenses = view.findViewById(R.id.recycler_view_expenses);

        setupRecyclerView();
        loadExpenses();

        buttonSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void setupRecyclerView() {
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseAdapter = new ExpenseAdapter(expenseList);
        recyclerViewExpenses.setAdapter(expenseAdapter);
    }

    private void loadExpenses() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        if (userId != -1) {
            List<Expense> expenses = daoExpenses.getExpensesForUser(userId);
            expenseAdapter.setExpenses(expenses);
        }
    }

    private void saveExpense() {
        String description = editTextDescription.getText().toString();
        String amountStr = editTextAmount.getText().toString();
        String dateStr = editTextDate.getText().toString();
        String category = editTextCategory.getText().toString();

        if (description.isEmpty() || amountStr.isEmpty() || dateStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr);

            SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

            if (userId != -1) {
                Expense expense = new Expense();
                expense.setUserId(userId);
                expense.setDescription(description);
                expense.setAmount(amount);
                expense.setDate(date);
                expense.setCategory(category);

                daoExpenses.addExpense(expense);

                Toast.makeText(getContext(), "Expense saved!", Toast.LENGTH_SHORT).show();

                // Clear input fields
                editTextDescription.setText("");
                editTextAmount.setText("");
                editTextDate.setText("");
                editTextCategory.setText("");

                loadExpenses(); // Refresh the list
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid date format (use YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
        }
    }
}
