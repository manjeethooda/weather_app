package manjeet_hooda.weather_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Double mLat, mLon;
    AnimationDrawable weatherAnimation;

    private Context mContext;

    final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    protected LocationManager locationManager;

    private FetchWeather mFetchWeather;
    private TextView vLocation, vTemp,vDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vLocation = (TextView) findViewById(R.id.Place);
        vTemp = (TextView) findViewById(R.id.current_temperature_field);
        vDetails = (TextView) findViewById(R.id.details_field);
        mContext = this;

        setupGoogleApiClient();
        setupRefresh();

    }

    public void setupRefresh(){
        ImageView imageView = (ImageView)findViewById(R.id.refresh);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Refreshing... Please wait", Toast.LENGTH_SHORT).show();
                fetchWeather();
                Toast.makeText(mContext, "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupGoogleApiClient(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
    }

    public void fetchWeather(){
        mFetchWeather = new FetchWeather(this, this);
        mFetchWeather.execute(mLat, mLon);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLat = lastLocation.getLatitude();
            mLon = lastLocation.getLongitude();
            fetchWeather();
        }
    }

    @Override
    public void onConnectionSuspended(int a){

    }

    @Override
    public void onConnectionFailed(ConnectionResult result){

    }

    public void get_weather(String[] weather){
        vLocation.setText(weather[1]);
        vDetails.setText(weather[3]);
        vTemp.setText(weather[2]);
    }

}
