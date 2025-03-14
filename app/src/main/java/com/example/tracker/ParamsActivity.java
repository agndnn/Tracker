package com.example.tracker;

import static com.example.tracker.Params.usersOut;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ParamsActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    EditText userCodeEditText;
    EditText userPhoneEditText;
    EditText userNameEditText;

    private UserAdapter userAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.debug("ParamsActivity started");
        setContentView(R.layout.activity_params);

        databaseHelper = new DatabaseHelper(this);

        userCodeEditText = findViewById(R.id.user_code_edit_text);
        userPhoneEditText = findViewById(R.id.user_phone_edit_text);
        userNameEditText = findViewById(R.id.user_name_edit_text);
        Button saveButton = findViewById(R.id.save_button);
        Button registerButton = findViewById(R.id.register_button);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        userAdapter = new UserAdapter(this, usersOut);

        // Устанавливаем GridLayoutManager, чтобы организовать элементы в сетке
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1); // 2 столбца
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(userAdapter);

        // Устанавливаем кнопку добавления пользователя

        //ImageView addButton = findViewById(R.id.button_add_user);
        //addButton.setOnClickListener(v -> showAddUserDialog());
        // Загружаем параметры в поля ввода
        loadParams();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParams();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerData();
            }
        });
        ///////
        Button buttonClose = findViewById(R.id.backButton);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadParams() {
        userCodeEditText.setText(Params.userCode);
        userPhoneEditText.setText(Params.userPhone);
        userNameEditText.setText(Params.userName);
    }

    private void saveParams() {
        //Params params = new Params();
        Params.userCode = userCodeEditText.getText().toString();
        Params.userPhone = userPhoneEditText.getText().toString();
        Params.userName = userNameEditText.getText().toString();

        // Вставляем или обновляем параметры в базе данных
        databaseHelper.insertParams();


        Toast.makeText(ParamsActivity.this, "Параметры сохранены!", Toast.LENGTH_SHORT).show();
    }

    private void registerData() {
        HttpHelper httpHelper = new HttpHelper();
        Params.userCode = userCodeEditText.getText().toString();
        Params.userPhone = userPhoneEditText.getText().toString();
        Params.userName = userNameEditText.getText().toString();
        String url = Params.getRegisterUrl();

        httpHelper.executeRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, @NonNull IOException e) {
                // Обработать ошибку
                android.util.Log.e("HTTP_ERROR", "Failed to fetch data", e);
//                Toast.makeText(ParamsActivity.this, "Ошибка регистрации: " + e.getMessage(), Toast.LENGTH_LONG).show();
                runOnUiThread(() -> {
                    Toast.makeText(ParamsActivity.this, "Ошибка регистрации: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Обработать успешный ответ
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.debug("responseData="+responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        int errc = jsonObject.getInt("errc");
                        String errm = jsonObject.getString("errm");
                        if (errc==0){
                            runOnUiThread(() -> {
                                Toast.makeText(ParamsActivity.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                            });
                        }
                        else {
                            runOnUiThread(() -> {
                                Toast.makeText(ParamsActivity.this, "Ошибка "+errm, Toast.LENGTH_SHORT).show();
                            });

                        }
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(ParamsActivity.this, "Ошибка: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }
}
