package manjeet_hooda.weather_app;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

/**
 * Created by manjeet on 24/4/16.
 */
public class NoDataConnection {

    public static final boolean hasDataConnection(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null) {
            //showDialog(context);
            return false;
        }
        return activeNetwork.isConnected();
    }

    public static void showDialog(Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("No Data Connection availaible. Please connect to Internet");
        alertDialogBuilder.setTitle("Connectivity Issues");
        alertDialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface arg0, int arg1){

                    }
                }

        );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
