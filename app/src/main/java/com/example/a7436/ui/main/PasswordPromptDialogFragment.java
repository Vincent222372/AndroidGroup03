package com.example.a7436.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.a7436.R;
import com.example.a7436.data.database.DAOUsers;
import com.example.a7436.model.User;
import com.example.a7436.ui.auth.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

public class PasswordPromptDialogFragment extends DialogFragment {

    public static final String REQUEST_KEY = "password_prompt_request";
    public static final String KEY_CONFIRMED = "password_confirmed";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_prompt_password, null);

        final TextInputEditText passwordInput = view.findViewById(R.id.edit_text_prompt_password);

        builder.setView(view)
                .setPositiveButton("Xác nhận", null) // We override this to control dialog closing
                .setNegativeButton("Hủy", (dialog, id) -> dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String password = passwordInput.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (verifyPassword(password)) {
                    // Send result back to the calling fragment
                    Bundle result = new Bundle();
                    result.putBoolean(KEY_CONFIRMED, true);
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return dialog;
    }

    private boolean verifyPassword(String password) {
        DAOUsers daoUsers = new DAOUsers(getContext());
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (userId != -1) {
            User currentUser = daoUsers.getUser(userId);
            if (currentUser != null) {
                // Use the existing checkUserLogin method, which handles BCrypt verification
                User verifiedUser = daoUsers.checkUserLogin(currentUser.getEmail(), password);
                return verifiedUser != null;
            }
        }
        return false;
    }
}
