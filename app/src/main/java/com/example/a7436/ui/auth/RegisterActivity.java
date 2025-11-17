package com.example.a7436.ui.auth;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.a7436.R;
import com.example.a7436.data.database.DAOUsers;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewGoToLogin;
    private DAOUsers daoUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize DAO
        daoUsers = new DAOUsers(this);

        // Start background animation
        ConstraintLayout constraintLayout = findViewById(R.id.register_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Find views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);

        buttonRegister.setOnClickListener(v -> registerUser());

        textViewGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Rule 1: Check for empty fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rule 2: Username must not contain numbers (Corrected)
        if (username.matches(".*\\d.*")) {
            Toast.makeText(this, "Tên người dùng không được chứa số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rule 3: Phone number validation
        if (!phone.startsWith("0") || phone.length() != 10) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rule 4: Password confirmation
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rule 5: Check if email already exists
        if (daoUsers.checkEmailExists(email)) {
            Toast.makeText(this, "Email này đã được đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        // All checks passed, proceed with registration
        long result = daoUsers.addUser(username, email, phone, password);

        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to login screen
        } else {
            Toast.makeText(this, "Đăng ký thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }
}
