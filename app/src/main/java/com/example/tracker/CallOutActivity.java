package com.example.tracker;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
public class CallOutActivity extends Activity{
    public static final String EXTRA_CALL_LOGS = "EXTRA_CALL_LOGS";

    private ListView listView;
    private TextView tvEmpty;
    private Button buttonToMap;

    private double latitude;
    private double longitude;
    private String selectePhone; // храним выбранный номер
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_out);

        listView = findViewById(R.id.list_call_out);
        tvEmpty = findViewById(R.id.tv_empty);
        buttonToMap = findViewById(R.id.button_call_out_tomap);

        buttonToMap.setEnabled(false);

        // Получаем данные из Intent
        List<CallLogsManager.CallLogEntry> callLogs = (List<CallLogsManager.CallLogEntry>) getIntent().getSerializableExtra(EXTRA_CALL_LOGS);

        if (callLogs != null && !callLogs.isEmpty()) {
            ArrayAdapter<CallLogsManager.CallLogEntry> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, callLogs);
            listView.setAdapter(adapter);

            // Устанавливаем слушатель нажатий для списка
            listView.setOnItemClickListener((parent, view, position, id) -> {
                // Получаем выбранный номер на основании выбранного элемента списка
                selectePhone = callLogs.get(position).getNumber();
                if (selectePhone != null) {
                    //TODO
                    String code = Params.getUserCodeByPhone(selectePhone);
                    Log.debug("code="+code+", selectePhone="+selectePhone);
                    buttonToMap.setEnabled(false);
                    if (code!=null ) {
                        if ((callLogs.get(position).getLatitude() == null) && (callLogs.get(position).getErrCode() == 0)) {
                            new Thread(() -> {
                                try {
                                    String response = HttpClient.sendGetRequest(Params.getCoordFromUrl(code, selectePhone));
                                    runOnUiThread(() -> {
                                        Log.debug("response=" + response);
                                        // Получаем значение поля "errc"
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            int errc = jsonObject.getInt("errc");
                                            if (errc == 0) {
                                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                                String latitude = dataObject.getString("latitude");
                                                String longitude = dataObject.getString("longitude");
                                                Log.debug("latitude=" + latitude + ", longitude=" + longitude);
                                                this.latitude = Double.parseDouble(latitude);
                                                this.longitude = Double.parseDouble(longitude);

                                                callLogs.set(position, callLogs.get(position).addCoord(latitude, longitude));
                                                buttonToMap.setEnabled(true);
                                            } else {
                                                String errm = jsonObject.getString("errm");
                                                Log.debug("errm=" + errm);
                                                callLogs.set(position, callLogs.get(position).addErr(errc, errm));
                                            }
                                            adapter.notifyDataSetChanged();
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                        // Обновите UI с полученными данными
                                    });

                                } catch (Exception e) {
                                    Log.debug(e.getMessage());
                                    e.printStackTrace();
                                }
                            }).start();
                        } else if (callLogs.get(position).getLatitude() != null) {
                            buttonToMap.setEnabled(true);
                        }
                    }
                }

                //               view.setBackgroundColor(R.color.light_blue_600);

                Log.debug("selectePhone="+selectePhone);

                // Удаление выделения у других элементов (если это необходимо)
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (i != position) {
                        listView.getChildAt(i).setBackgroundColor(0x00000000); // Убираем цвет фона
                    } else {
                        listView.getChildAt(i).setBackgroundColor(R.color.light_blue_600);
                    }
                }
//                Toast.makeText(CallLogListActivity.this, "Selected: " + selectedNumber, Toast.LENGTH_SHORT).show();
            });
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }

        // Устанавливаем обработчик нажатия на кнопку
        buttonToMap.setOnClickListener(v -> {

            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("key_latitude", latitude);
            intent.putExtra("key_longitude", longitude);
            startActivity(intent);
        });

        Button backButton = findViewById(R.id.btBack2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Завершаем текущую активность
            }
        });

 //   }
    }


}
