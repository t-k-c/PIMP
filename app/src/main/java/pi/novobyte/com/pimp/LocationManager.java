package pi.novobyte.com.pimp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.CookieData;
import data.GetRequestManager;
import functionalities.LocationListenerClass;
import functionalities.StringUtils;
import map.RequestManager;
import map.ResponseManager;
import objects.Site;

public class LocationManager extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Site site;
    final int request_code_location = 102; //any number to be gotten directly in the callback
    LocationListener locationListener;
    CoordinatorLayout coordinatorLayout;
    android.location.LocationManager locationManager;
    Location temp;//temporary site location...
    TextView distancetv,speedtv;
    LatLng point;
    MarkerOptions  mk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);
        distancetv =  findViewById(R.id.distance);
        speedtv =  findViewById(R.id.speed);
         point = new LatLng(CookieData.getLastLocation(this)[0],
                CookieData.getLastLocation(this)[1]);

        mk = new MarkerOptions().position(point).title("Where you are");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        site = (Site) getIntent().getSerializableExtra("site_data");

  getLocation();
    }
    Location previousLocation;
public void getLocation() {
    locationManager = (android.location.LocationManager) getSystemService(LOCATION_SERVICE);
    locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Location temp = new Location( android.location.LocationManager.GPS_PROVIDER);
            temp.setLatitude(site.latlng[0]);
            temp.setLongitude(site.latlng[1]);
            float distance = location.distanceTo(temp);
            String val = distance+"m remaining";
            if(distance>1000){
                float distance2=distance/1000;
                 val = distance2+"km remaining";
            }
            distancetv.setText(val);
            String vv = "";
            if(previousLocation!=null){
                double speed = location.distanceTo(previousLocation)/(location.getTime() - previousLocation.getTime());
                speed/=3.6;
                String[] split  =  Double.toString(speed).split("[.]");
                String sp = split.length>0 ? split[0]+"."+(split[1]+"000").substring(0,2) : split[0];
                vv = "Moving at "+sp+" km/h";
                //Moving at 10m/s
            }
                previousLocation = location;

            speedtv.setText(vv);
            mk.position(new LatLng(location.getLatitude(),location.getLongitude()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    if (locationManager == null || !locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
        Snackbar.make((RelativeLayout) findViewById(R.id.rel), "You need to activate your GPS", Snackbar.LENGTH_INDEFINITE).setAction(
                "OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                            Intent i =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package",getPackageName(),null);
//                            i.setData(uri);
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }
        ).show();
        return;
    }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        ActivityCompat.requestPermissions(LocationManager.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, request_code_location);
        return;
    }

    locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0, locationListener);

}
    public void click(View view) {
        try {
            JSONObject jsonObject = new JSONObject(site.contact);
            String website = jsonObject.getString("website");
            String email = jsonObject.getString("email");
            String number = jsonObject.getString("phone");
            switch (view.getId()) {
                case R.id.website:
                    if (website.isEmpty()) {
                        Toast.makeText(this, "No website attached", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String wbsite = website.startsWith("http") ? website : "http://"+website;
                        intent.setData(Uri.parse(website));
                        startActivity(intent);
                    }
                    break;
                case R.id.call:
                    if (number.isEmpty()) {
                        Toast.makeText(this, "No phone number attached", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + number));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);
                    }
                    break;
                case R.id.mail:if (email.isEmpty()) {
                    Toast.makeText(this, "No email attached", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL,email);
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Mail from PIMP");
                    intent.putExtra(Intent.EXTRA_TEXT,"Content here");
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }break;
                case R.id.message:
                    if (number.isEmpty()) {
                        Toast.makeText(this, "No phone number attached", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("tel:" + number));
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(point));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

        final String request = RequestManager.createUrlFromSrcDesLatLng(
                CookieData.getLastLocation(this)[0],
                CookieData.getLastLocation(this)[1],
                site.latlng[0],
                site.latlng[1]
        );
        Log.i("pimp","location request __"+request);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String resp = GetRequestManager.getResponse(request);
                    Log.i("pimp","location response __"+resp);
                    try {
                        JSONObject jsonObject =  new JSONObject(resp);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        String response="";
                        for(int i=0;i<routes.length();i++){
                             JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
                            for (int j=0;j<legs.length();j++){
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
                                for (int k=0;k<steps.length();k++){
                                    response+= steps.getJSONObject(k).getString("html_instructions")+"\n";
                                }
                            }
                            response+="OR\n";
                        }
                        Log.i("pimp","location resp2 == "+ StringUtils.unescapeHtml3(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ResponseManager.drawOnMap(resp,mMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) { //trying to get which permission has been granted
            case request_code_location:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    //permission not at all accepted
                    Snackbar.make(coordinatorLayout, "You need to give PIMP location permission", Snackbar.LENGTH_INDEFINITE).setAction(
                            "OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    i.setData(uri);
                                    startActivity(i);
                                }
                            }
                    );
                    return;
                }
        }
    }
}
