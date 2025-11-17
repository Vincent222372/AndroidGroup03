package com.example.a7436.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.a7436.R;
import com.example.a7436.data.database.DAOUsers;
import com.example.a7436.model.User;
import com.example.a7436.ui.auth.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class ProfileFragment extends Fragment {

    private TextView profileUsername, profileEmail, profilePhone;
    private DAOUsers daoUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        daoUsers = new DAOUsers(getContext());

        MaterialToolbar toolbar = view.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        profileUsername = view.findViewById(R.id.profile_username);
        profileEmail = view.findViewById(R.id.profile_email);
        profilePhone = view.findViewById(R.id.profile_phone);

        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (userId != -1) {
            User user = daoUsers.getUser(userId);
            if (user != null) {
                profileUsername.setText(user.getUsername());
                profileEmail.setText(user.getEmail());
                profilePhone.setText(user.getPhoneNumber());
            } else {
                Toast.makeText(getContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_user_info) {
            Toast.makeText(getContext(), "Chức năng xem thông tin người dùng", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_change_password) {
            Toast.makeText(getContext(), "Chức năng đổi mật khẩu", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_logout) {
            handleLogout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void handleLogout() {
        // Clear logged-in user data
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(LoginActivity.KEY_USER_ID);
        editor.apply();

        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
