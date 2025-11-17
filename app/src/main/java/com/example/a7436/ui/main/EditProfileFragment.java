package com.example.a7436.ui.main;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.a7436.R;
import com.example.a7436.data.database.DAOUsers;
import com.example.a7436.model.User;
import com.example.a7436.ui.auth.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private TextInputEditText editUsername, editEmail, editPhone, editAddress;
    private Button buttonSaveChanges;
    private ImageButton buttonEditImage;
    private CircleImageView profileImage;
    private DAOUsers daoUsers;
    private User currentUser;
    private Uri newImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        newImageUri = result.getData().getData();
                        if (newImageUri != null) {
                            requireActivity().getContentResolver().takePersistableUriPermission(newImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            profileImage.setImageURI(newImageUri);
                        }
                    }
                });

        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        daoUsers = new DAOUsers(getContext());

        MaterialToolbar toolbar = view.findViewById(R.id.edit_profile_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        editUsername = view.findViewById(R.id.profile_edit_username);
        editEmail = view.findViewById(R.id.profile_edit_email);
        editPhone = view.findViewById(R.id.profile_edit_phone);
        editAddress = view.findViewById(R.id.profile_edit_address);
        buttonSaveChanges = view.findViewById(R.id.buttonSaveChanges);
        buttonEditImage = view.findViewById(R.id.buttonEditImage);
        profileImage = view.findViewById(R.id.profile_image_edit);

        loadUserProfile();

        buttonSaveChanges.setOnClickListener(v -> saveChanges());
        buttonEditImage.setOnClickListener(v -> openImagePicker());
    }

    private void loadUserProfile() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (userId != -1) {
            currentUser = daoUsers.getUser(userId);
            if (currentUser != null) {
                editUsername.setText(currentUser.getUsername());
                editEmail.setText(currentUser.getEmail());
                editPhone.setText(currentUser.getPhoneNumber());
                editAddress.setText(currentUser.getAddress());

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

    private void saveChanges() {
        if (currentUser != null) {
            currentUser.setUsername(editUsername.getText().toString().trim());
            currentUser.setEmail(editEmail.getText().toString().trim());
            currentUser.setPhoneNumber(editPhone.getText().toString().trim());
            currentUser.setAddress(editAddress.getText().toString().trim());

            if (newImageUri != null) {
                currentUser.setProfileImagePath(newImageUri.toString());
            }

            int rowsAffected = daoUsers.updateUser(currentUser);
            if (rowsAffected > 0) {
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp(); // Go back to profile screen
            } else {
                Toast.makeText(getContext(), "Cập nhật thất bại.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
}
