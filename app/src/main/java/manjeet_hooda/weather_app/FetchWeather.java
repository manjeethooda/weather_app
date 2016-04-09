package manjeet_hooda.weather_app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

/**
 * Created by manjeet on 9/4/16.
 */
public class FetchWeather  extends AsyncTask<Double, Void, Void> {
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    String units = "metric";

    private Context context;
    private JSONObject data;
    MainActivity mMainActivity;

    String weathericon = "";
    String mPlace, mDescription, mHumid, mPressure, mTemp;

    FetchWeather(Context mContext, MainActivity mainActivity) {
        this.context = mContext;
        mMainActivity = mainActivity;
    }

    @Override
    public Void doInBackground(Double... params) {
        try {
            //String city = params[0];
            URL url = new URL(String.format("http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=%s&appid=%s",
                   params[0] , params[1], units, R.string.open_weather_maps_app_id));
            //URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }
            renderWeather(data);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private void renderWeather(JSONObject json){
        try {
            mPlace = json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country");

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            mDescription = details.getString("description").toUpperCase(Locale.US);
            mDescription = details.getString("description").toUpperCase(Locale.US) +
                    "\n" + "Humidity: " + main.getString("humidity") + "%" +
                    "\n" + "Pressure: " + main.getString("pressure") + " hPa";

            mTemp = String.format("%.2f", main.getDouble("temp"))+ " ℃";
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    @Override
    protected void onPostExecute(Void avoid){
        super.onPostExecute(avoid);

        String [] weather = { weathericon, mPlace, mTemp, mDescription};
        mMainActivity.get_weather(weather);
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                weathericon = context.getString(R.string.weather_sunny);
            } else {
                weathericon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    weathericon = context.getString(R.string.weather_thunder);
                    break;
                case 3:
                    weathericon = context.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    weathericon = context.getString(R.string.weather_foggy);
                    break;
                case 8:
                    weathericon = context.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    weathericon = context.getString(R.string.weather_snowy);
                    break;
                case 5:
                    weathericon = context.getString(R.string.weather_rainy);
                    break;
            }
        }
    }

}

