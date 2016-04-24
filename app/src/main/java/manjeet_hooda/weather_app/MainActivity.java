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
import android.widget.RelativeLayout;
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
    final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private GpsTracker gps;
    private RelativeLayout loadingPanel;
    private boolean internetConnection;

    private FetchWeather mFetchWeather;
    private TextView vLocation, vTemp,vDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupVariables();
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                gps.PERMISSION_ACCESS_COARSE_LOCATION);
                    }
        isInternet();
        setupFloatingRefresh();
        fetchWeather();

    }

    private void setupVariables(){
        vLocation = (TextView) findViewById(R.id.Place);
        vTemp = (TextView) findViewById(R.id.current_temperature_field);
        vDetails = (TextView) findViewById(R.id.details_field);
        mContext = this;
        gps = new GpsTracker(this, this);
        loadingPanel = (RelativeLayout)findViewById(R.id.loadingPanel);
        internetConnection = false;
    }

    public void isInternet(){
        internetConnection = NoDataConnection.hasDataConnection(this);
        if(!internetConnection)
            NoDataConnection.showDialog(this);
    }

    public void fetchWeather(){
            if(internetConnection && get_location()) {
                loadingPanel.setVisibility(View.VISIBLE);
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

    public boolean get_location(){
            gps = new GpsTracker(this, this);
            // check if GPS enabled
            if (gps.canGetLocation()) {

                mLat = gps.getLatitude();
                mLon = gps.getLongitude();

                // \n is for new line
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + mLat + "\nLong: " + mLon,
                //        Toast.LENGTH_LONG).show();
                return true;
            }
        gps.showSettingsAlert();
        return false;
    }

    private void setupFloatingRefresh(){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    isInternet();
                    fetchWeather();
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if(NoDataConnection.hasDataConnection(this)) {
            internetConnection = NoDataConnection.hasDataConnection(this);
            get_location();
            fetchWeather();
        }
    }

    public void get_weather(String[] weather){
        vLocation.setText(weather[1]);
        vDetails.setText(weather[3]);
        vTemp.setText(weather[2]);
        loadingPanel.setVisibility(View.INVISIBLE);
    }

}