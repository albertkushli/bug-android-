package dsa.upc.edu.listapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dsa.upc.edu.listapp.auth.*;

import dsa.upc.edu.listapp.auth.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnGoToRegister;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername      = findViewById(R.id.etLoginUsername);
        etPassword      = findViewById(R.id.etLoginPassword);
        btnLogin        = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        // Aquí pasas el contexto de la actividad a getClient
        api = ApiClient.getClient(LoginActivity.this).create(ApiService.class);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario login = new Usuario(user, pass);
            api.loginUser(login).enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit().putString("token", response.body().getToken()).apply();
                        Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, PartidasMenuActivity.class);
                        intent.putExtra("nombreUsu", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}

