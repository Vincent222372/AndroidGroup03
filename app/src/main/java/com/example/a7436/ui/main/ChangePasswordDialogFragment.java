package com.example.a7436.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ChangePasswordDialogFragment extends DialogFragment {

    private DAOUsers daoUsers;
    private User currentUser;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        daoUsers = new DAOUsers(getContext());
        loadCurrentUser();

        final TextInputEditText currentPassword = view.findViewById(R.id.edit_text_current_password);
        final TextInputEditText newPassword = view.findViewById(R.id.edit_text_new_password);
        final TextInputEditText confirmNewPassword = view.findViewById(R.id.edit_text_confirm_new_password);

        builder.setView(view)
                .setPositiveButton("Lưu", (dialog, id) -> {
                    // This is overridden below to prevent the dialog from closing on error
                })
                .setNegativeButton("Hủy", (dialog, id) -> ChangePasswordDialogFragment.this.getDialog().cancel());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String currentPass = currentPassword.getText().toString();
                String newPass = newPassword.getText().toString();
                String confirmPass = confirmNewPassword.getText().toString();

                if (validateInput(currentPass, newPass, confirmPass)) {
                    if (updatePassword(currentPass, newPass)) {
                        Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        return dialog;
    }

    private boolean validateInput(String current, String newPass, String confirm) {
        if (TextUtils.isEmpty(current) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ các trường", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPass.equals(confirm)) {
            Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPass.length() < 6) { // Example: require at least 6 characters
            Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadCurrentUser() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        if (userId != -1) {
            currentUser = daoUsers.getUser(userId);
        }
    }

    private boolean updatePassword(String oldPassword, String newPassword) {
        if (currentUser != null) {
            // We need to re-fetch the user to get the password hash
            User userWithPassword = daoUsers.checkUserLogin(currentUser.getEmail(), oldPassword); 
            if(userWithPassword != null){
                daoUsers.updatePassword(currentUser.getUserId(), newPassword);
                return true;
            }  
        }
        return false;
    }
}