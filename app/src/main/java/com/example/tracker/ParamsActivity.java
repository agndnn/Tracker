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

    //EditText latitudeEditText;
    //EditText longitudeEditText;
    CheckBox isForegroundCheckBox;
    CheckBox isAutoRunCheckBox;
    EditText homeUrlEditText;
    EditText userCodeEditText;
    EditText userPhoneEditText;
    EditText userNameEditText;
    EditText apiKeyEditText;

    private UserAdapter userAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.debug("ParamsActivity started");
        setContentView(R.layout.activity_params);

        databaseHelper = new DatabaseHelper(this);

        isForegroundCheckBox = findViewById(R.id.is_foreground_checkbox);
        isAutoRunCheckBox = findViewById(R.id.is_auto_run_checkbox);
        homeUrlEditText = findViewById(R.id.home_url_edit_text);
        userCodeEditText = findViewById(R.id.user_code_edit_text);
        userPhoneEditText = findViewById(R.id.user_phone_edit_text);
        userNameEditText = findViewById(R.id.user_name_edit_text);
        apiKeyEditText = findViewById(R.id.api_key_edit_text);
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
        //Params params = databaseHelper.getParams();
        isForegroundCheckBox.setChecked(Params.IsForeground.equals("Y"));;
        isAutoRunCheckBox.setChecked(Params.IsAutoRun.equals("Y"));
        homeUrlEditText.setText(Params.homeUrl);
        userCodeEditText.setText(Params.userCode);
        userPhoneEditText.setText(Params.userPhone);
        userNameEditText.setText(Params.userName);
        apiKeyEditText.setText(Params.apiKey);
    }

    private void saveParams() {
        //Params params = new Params();
        Params.IsForeground = isForegroundCheckBox.isChecked() ? "Y" : "N";
        Params.IsAutoRun = isAutoRunCheckBox.isChecked() ? "Y" : "N";
        Params.homeUrl = homeUrlEditText.getText().toString();
        Params.userCode = userCodeEditText.getText().toString();
        Params.userPhone = userPhoneEditText.getText().toString();
        Params.userName = userNameEditText.getText().toString();
        Params.apiKey = apiKeyEditText.getText().toString();

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

    // Метод для показа диалога добавления пользователя
    /*
    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View addView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);

        EditText editUserCode = addView.findViewById(R.id.edit_user_code);
        EditText editPhoneNumber = addView.findViewById(R.id.edit_phone_number);

        builder.setTitle(R.string.add_user_header)
                .setView(addView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String newUserId = editUserCode.getText().toString();
                    String newPhoneNumber = editPhoneNumber.getText().toString();

                    // Создаем нового пользователя и добавляем его в список
                    User newUser = new User(newUserId, newPhoneNumber);
                    usersOut.add(newUser);
                    userAdapter.notifyItemInserted(usersOut.size() - 1); // Уведомляем адаптер о добавлении
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

     */
}
