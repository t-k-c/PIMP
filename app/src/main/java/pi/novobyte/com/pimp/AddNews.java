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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.ServerData;
import data.PostRequestManager;
import objects.Site;

public class AddNews extends AppCompatActivity {
    String encodedImage,fileExtension;
    ImageView image;
    TextView titletv,contenttv,rangetv;
    Site site;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        image = findViewById(R.id.image);
        titletv = findViewById(R.id.post_title);
        contenttv = findViewById(R.id.post_content);
        rangetv = findViewById(R.id.post_range);
        site = (Site) getIntent().getSerializableExtra("site_data");
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

    public void submit(View view){
        final String title,content,range;
        title = titletv.getText().toString();
        content = contenttv.getText().toString();
        range = rangetv.getText().toString();
        if(range.isEmpty()||title.isEmpty()||content.isEmpty()){
            Toast.makeText(this, "All fields are obligatory", Toast.LENGTH_SHORT).show();
        }else{
            try{
                int r  = Integer.parseInt(range);
                if(r > 150){
                    Toast.makeText(this, "Range should be less than 150Km", Toast.LENGTH_SHORT).show();
                }else{
                   if(encodedImage==null || encodedImage.isEmpty()){
                       Toast.makeText(this, "You must upload an image", Toast.LENGTH_SHORT).show();
                   }else{
                       final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
                       sweetAlertDialog.setTitle("Loading...");
                       sweetAlertDialog.setContentText("Please wait...");
                       sweetAlertDialog.setCancelable(false);
                       sweetAlertDialog.show();
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               final String resp = PostRequestManager.getResponse(ServerData.POSTS_LINK,new Uri.Builder()
                                       .appendQueryParameter("post_content",content)
                                       .appendQueryParameter("post_range",range)
                                       .appendQueryParameter("post_title",title)
                                       .appendQueryParameter("thumbnail_ext",fileExtension)
                                       .appendQueryParameter("post_thumbnail",encodedImage)
                                       .appendQueryParameter("site_id",site.id+"")
                                       .build()
                                       .toString());
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Log.i("pimp","resp: "+resp);
                                   }
                               });
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     try{
                                         int response= Integer.parseInt(resp);
                                         if(response == -2){
                                             sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                             sweetAlertDialog.setTitle("Error");
                                             sweetAlertDialog.setContentText("An unknown error occurred");
                                         }
                                         else if(response == 0){
                                             sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                             sweetAlertDialog.setTitle("Error");
                                             sweetAlertDialog.setContentText("Image was rejected by server");
                                         }else {
                                             sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                             sweetAlertDialog.setTitle("Done");
                                             sweetAlertDialog.setContentText("Operation successful");
                                             sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                                                 @Override
                                                 public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                     sweetAlertDialog.cancel();
                                                     AddNews.super.onBackPressed();
                                                 }
                                             });
                                         }
                                     }catch (NumberFormatException exception){
                                         exception.printStackTrace();
                                         runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 Toast.makeText(AddNews.this, "Server returned incorrect response", Toast.LENGTH_SHORT).show();
                                                 sweetAlertDialog.cancel();
                                             }
                                         });
                                     }

                                 }
                             });

                           }
                       }).start();
                   }
                }
            }catch(NumberFormatException e){
                e.printStackTrace();
                Toast.makeText(this, "NaN", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
