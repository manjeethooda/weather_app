package manjeet_hooda.weather_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends AppCompatActivity {

    private Double mLat, mLon;
    private Context mContext;

    GpsTracker gps;

    final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private FetchWeather mFetchWeather;
    private TextView vLocation, vTemp,vDetails;

    private boolean isNet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vLocation = (TextView) findViewById(R.id.Place);
        vTemp = (TextView) findViewById(R.id.current_temperature_field);
        vDetails = (TextView) findViewById(R.id.details_field);
        mContext = this;

        get_location();
        isInternet();
        fetchWeather();
        setupRefresh();

    }

    public boolean isInternet(){
        final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null){
            Toast.makeText(this,"No Network Connection",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void setupRefresh(){
        get_location();
        ImageView imageView = (ImageView)findViewById(R.id.refresh);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isInternet()) {
                    Toast.makeText(mContext, "Refreshing... Please wait", Toast.LENGTH_SHORT).show();
                    get_location();
                    fetchWeather();
                    Toast.makeText(mContext, "Refreshed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fetchWeather(){
        if(isInternet()) {
            mFetchWeather = new FetchWeather(this, this);
            mFetchWeather.execute(mLat, mLon);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void get_location(){
        gps = new GpsTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            mLat = gps.getLatitude();
            mLon = gps.getLongitude();

            // \n is for new line
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + mLat + "\nLong: " + mLon,
            //        Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            //get_location();
        }
    }

    public void get_weather(String[] weather){
        vLocation.setText(weather[1]);
        vDetails.setText(weather[3]);
        vTemp.setText(weather[2]);
    }

}