package pi.novobyte.com.pimp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import data.PostRequestManager;
import objects.Site;

public class AddSite extends AppCompatActivity implements OnMapReadyCallback {
    String encodedImage,fileExtension;
    ImageView image;
    GoogleMap mMap;
    TextView nametv,shortnametv,descriptiontv,phonetv,emailtv,websitetv,workingperiodtv,locationdescriptiontv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);
        image = (ImageView) findViewById(R.id.image);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        nametv= findViewById(R.id.site_name);
        shortnametv= findViewById(R.id.site_short_name);
        descriptiontv= findViewById(R.id.site_description);
        phonetv= findViewById(R.id.site_phone);
        websitetv= findViewById(R.id.site_website);
        emailtv= findViewById(R.id.site_email);
        workingperiodtv= findViewById(R.id.site_working_period);
        locationdescriptiontv= findViewById(R.id.site_location_description);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                image.setImageURI(selectedImage);
                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
//                image_name_tv.setText(filePath);

                try {
                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                        fileExtension = file_extn;
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inSampleSize = 4;
                        options.inPurgeable = true;
                        Bitmap bm = BitmapFactory.decodeFile(filePath,options);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bm.compress(Bitmap.CompressFormat.JPEG,40,baos);


                        // bitmap object

                        byte[] byteImage_photo = baos.toByteArray();

                        //generate base64 string of image

                         encodedImage = Base64.encodeToString(byteImage_photo,Base64.DEFAULT);

                        //send this encoded string to server
                    } else {
                        //NOT IN REQUIRED FORMAT
                        Toast.makeText(this, "Not an image", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
    }
    public void getImage(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
//        imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng position = new LatLng(CookieData.getLastLocation(this)[0], CookieData.getLastLocation(this)[1]);
        mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }
    public void submit(View view){
       // TextView nametv,shortnametv,descriptiontv,phonetv,emailtv,websitetv,workingperiodtv,locationdescriptiontv;
      final String name,shortname,description,phone,email,website,workingperiod,locationdescription;
      name =  nametv.getText().toString();
      shortname =  shortnametv.getText().toString();
      description =  descriptiontv.getText().toString();
      phone =  phonetv.getText().toString();
      email =  emailtv.getText().toString();
      website =  websitetv.getText().toString();
      workingperiod =  workingperiodtv.getText().toString();
      locationdescription =  locationdescriptiontv.getText().toString();
      if(name.isEmpty()||shortname.isEmpty()||description.isEmpty()||workingperiod.isEmpty()||locationdescription.isEmpty()){
          Toast.makeText(this, "Please make sure all obligatory fields are filled", Toast.LENGTH_SHORT).show();
      }else if(encodedImage==null || encodedImage.isEmpty()){
          Toast.makeText(this, "Please  select an image", Toast.LENGTH_SHORT).show();
      }else{
          new Thread(new Runnable() {
              @Override
              public void run() {
                  final String resp = PostRequestManager.getResponse(ServerData.SITES_LINK,
                  new Uri.Builder()
                  .appendQueryParameter("user_id",CookieData.getUserId(AddSite.this))
                  .appendQueryParameter("latlng",CookieData.getLastLocation(AddSite.this)[0]+","+CookieData.getLastLocation(AddSite.this)[0])
                  .appendQueryParameter("site_name", name)
                  .appendQueryParameter("site_short_name", shortname)
                  .appendQueryParameter("site_description", description)
                  .appendQueryParameter("site_contact", "{\"email\":\""+email+"\",\"website\":\""+website+"\",\"phone\":"+phone+"}")
                  .appendQueryParameter("site_working_period", workingperiod)
                  .appendQueryParameter("site_location_description", workingperiod)
                  .appendQueryParameter("site_thumbnail", encodedImage)
                  .appendQueryParameter("thumbnail_ext", fileExtension)
                          .build().toString()
                  );
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          Toast.makeText(AddSite.this, resp+" is your resp;;;;", Toast.LENGTH_SHORT).show();
                      }
                  });
              }
          }).start();;
      }
    }
}
