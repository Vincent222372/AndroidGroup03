package com.example.a7436.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.a7436.R;
import com.example.a7436.data.database.DAOUsers;
import com.example.a7436.model.User;
import com.example.a7436.ui.auth.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView textUsername, textEmail, textPhone, textAddress;
    private CircleImageView profileImage;
    private DAOUsers daoUsers;
    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the listener for the password prompt result
        getParentFragmentManager().setFragmentResultListener(PasswordPromptDialogFragment.REQUEST_KEY, this, (requestKey, bundle) -> {
            boolean confirmed = bundle.getBoolean(PasswordPromptDialogFragment.KEY_CONFIRMED);
            if (confirmed) {
                showUserInfoDialog();
            }
        });
    }

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

        textUsername = view.findViewById(R.id.profile_username_display);
        textEmail = view.findViewById(R.id.profile_email_display);
        textPhone = view.findViewById(R.id.profile_phone_display);
        textAddress = view.findViewById(R.id.profile_address_display);
        profileImage = view.findViewById(R.id.profile_image);

        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (userId != -1) {
            currentUser = daoUsers.getUser(userId);
            if (currentUser != null) {
                textUsername.setText(currentUser.getUsername());
                textEmail.setText(currentUser.getEmail());
                textPhone.setText(currentUser.getPhoneNumber());
                textAddress.setText(currentUser.getAddress());

                String imagePath = currentUser.getProfileImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        profileImage.setImageURI(Uri.parse(imagePath));
                    } catch (Exception e) {
                        profileImage.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_options_menu, menu);
        MenuItem editItem = menu.add(Menu.NONE, R.id.menu_edit, 1, "Chỉnh sửa");
        editItem.setIcon(android.R.drawable.ic_menu_edit);
        editItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit) {
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_editProfileFragment);
            return true;
        } else if (itemId == R.id.menu_change_password) {
            new ChangePasswordDialogFragment().show(getParentFragmentManager(), "ChangePasswordDialog");
            return true;
        } else if (itemId == R.id.menu_user_info) {
            new PasswordPromptDialogFragment().show(getParentFragmentManager(), "PasswordPromptDialog");
            return true;
        } else if (itemId == R.id.menu_logout) {
            handleLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUserInfoDialog() {
        if (currentUser != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_user_info, null);

            ((TextView) view.findViewById(R.id.info_username)).setText(currentUser.getUsername());
            ((TextView) view.findViewById(R.id.info_email)).setText(currentUser.getEmail());
            ((TextView) view.findViewById(R.id.info_phone)).setText(currentUser.getPhoneNumber());
            ((TextView) view.findViewById(R.id.info_address)).setText(currentUser.getAddress());
            ((TextView) view.findViewById(R.id.info_password_hash)).setText("***********");

            builder.setView(view).setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            builder.create().show();
        } else {
             Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLogout() {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(LoginActivity.KEY_USER_ID);
        editor.apply();
        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
