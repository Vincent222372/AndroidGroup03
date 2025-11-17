package com.example.a7436.ui.main;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.a7436.R;
import com.example.a7436.data.database.DAOExpenses;
import com.example.a7436.model.Expense;
import com.example.a7436.ui.auth.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {

    private TextInputEditText editTextAmount, editTextDescription;
    private Spinner spinnerCategory;
    private Button buttonDatePicker, buttonSaveExpense;
    private DAOExpenses daoExpenses;
    private Calendar selectedDate = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        daoExpenses = new DAOExpenses(getContext());

        MaterialToolbar toolbar = view.findViewById(R.id.add_expense_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        editTextAmount = view.findViewById(R.id.edit_text_amount);
        editTextDescription = view.findViewById(R.id.edit_text_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        buttonDatePicker = view.findViewById(R.id.button_date_picker);
        buttonSaveExpense = view.findViewById(R.id.button_save_expense);

        setupCategorySpinner();
        setupDatePicker();

        buttonSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void setupCategorySpinner() {
        // TODO: Load categories from the database later
        String[] categories = {"Ăn uống", "Di chuyển", "Hóa đơn", "Giải trí", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        updateDateButtonText(); // Set initial text
        buttonDatePicker.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            };

            new DatePickerDialog(getContext(), dateSetListener,
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        buttonDatePicker.setText(sdf.format(selectedDate.getTime()));
    }

    private void saveExpense() {
        String amountStr = editTextAmount.getText().toString();
        String description = editTextDescription.getText().toString();
        // This is a temporary way to get category ID. We'll improve this later.
        int categoryId = spinnerCategory.getSelectedItemPosition() + 1;

        if (amountStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền số tiền và mô tả", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

            if (userId != -1) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String date = sdf.format(selectedDate.getTime());

                Expense newExpense = new Expense(userId, categoryId, description, date, amount);
                long result = daoExpenses.addExpense(newExpense);

                if (result != -1) {
                    Toast.makeText(getContext(), "Thêm khoản chi thành công!", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate back or clear the fields
                    clearFields();
                } else {
                    Toast.makeText(getContext(), "Thêm khoản chi thất bại.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextAmount.setText("");
        editTextDescription.setText("");
        spinnerCategory.setSelection(0);
        selectedDate = Calendar.getInstance();
        updateDateButtonText();
        editTextAmount.requestFocus();
    }
}
