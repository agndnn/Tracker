package com.example.tracker;

import android.content.Context;
import android.os.AsyncTask;
;

public class LocationTask extends AsyncTask<Void, Void,Params> {
    private Context context;
    LocationTask(Context context){
        super();
        this.context=context;
    }
    @Override
    protected Params doInBackground(Void... voids) {
        Coord coord = new Coord(context);
        Log.debug("doInBackground - "+ Params.getCoordTxt());
        String urlString = "https://site-www.ru/maptrack/add_point.php?code=ag234678&is_log=1&lat="+ Params.latitude +"&lon="+Params.latitude;
        // textView.setText(urlString);
        try {
            String response = HttpClient.sendGetRequest(urlString);
            Log.debug("response = "+response);
        } catch (Exception e) {
            Log.debug("error "+ e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Params params) {
        // Обработка результата
        if (Params.latitude>0) {
            Log.debug("onPostExecute - "+ Params.getCoordTxt());
            // Действия при получении координат
        } else {
            Log.debug("onPostExecute не определись");
        }
    }
}