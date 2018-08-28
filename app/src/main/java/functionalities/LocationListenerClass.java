package functionalities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.CookieData;

public  class LocationListenerClass implements LocationListener {
    Runnable r;
    SweetAlertDialog sweetAlertDialog;
    boolean firstTime = true;
    Context context;
    boolean providerState =false ;//false:off, true:on
/**
 * @param  r sweetAlertDialog :
 *           runnable for code to run once ,
 *           sweetAlertDialog to be closed on the first location obtention
 * */
    public LocationListenerClass(@NonNull Runnable r, @Nullable SweetAlertDialog sweetAlertDialog,@NonNull Context context) {
        this.r = r;
        this.sweetAlertDialog = sweetAlertDialog;
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (firstTime){
            if(sweetAlertDialog!=null){
                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.cancel();
            }
            r.run();
            firstTime = false;
        }
        CookieData.updateLocation(location.getLatitude()+","+location.getLongitude(),context);
        Log.i("pimp","new location: ("+location.getLatitude()+","+location.getLongitude()+")");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        if(!providerState){
            Toast.makeText(context, "GPS Provider enabled", Toast.LENGTH_LONG).show();
            providerState=true;
        }
    }

    @Override
    public void onProviderDisabled(String s) {
        if(providerState){
            Toast.makeText(context, "You just disabled your GPS provider, PIMP wouldn't work correctly", Toast.LENGTH_LONG).show();
            providerState = false;
        }
    }
}