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

import java.io.ByteArrayOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.ServerData;
import data.PostRequestManager;

public class AddingItems extends AppCompatActivity {
    String fileExtension;
    ImageView image;
    TextView titletv,contenttv,pricetv;
    ImageView imageView1,imageView2,imageView3,imageView4;
    String im1,im2,im3,im4,ext1,ext2,ext3,ext4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_items);
        titletv = findViewById(R.id.item_title);
        contenttv = findViewById(R.id.item_description);
        pricetv = findViewById(R.id.item_price);
        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);
        imageView4 = findViewById(R.id.image4);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                analyze(data,requestCode);
            }
    }
    public void analyze(Intent data,int code){
        Uri selectedImage = data.getData();

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

                 switch (code){
                     case 1: imageView1.setImageURI(selectedImage); im1 = Base64.encodeToString(byteImage_photo,Base64.DEFAULT); ext1 = fileExtension; break;
                     case 2: imageView2.setImageURI(selectedImage); im2 = Base64.encodeToString(byteImage_photo,Base64.DEFAULT); ext1 = fileExtension; break;
                     case 3: imageView3.setImageURI(selectedImage);  im3 = Base64.encodeToString(byteImage_photo,Base64.DEFAULT); ext1 = fileExtension; break;
                     case 4: imageView4.setImageURI(selectedImage);  im4 = Base64.encodeToString(byteImage_photo,Base64.DEFAULT); ext1 = fileExtension; break;
                 }

                //send this encoded string to server
            } else {
                //NOT IN REQUIRED FORMAT
                Toast.makeText(this, "Not an image", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }}

    public void getImage(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        switch (view.getId()){
            case R.id.r1: startActivityForResult(photoPickerIntent, 1 ); break;
            case R.id.r2: startActivityForResult(photoPickerIntent, 2 ); break;
            case R.id.r3: startActivityForResult(photoPickerIntent, 3 ); break;
            case R.id.r4: startActivityForResult(photoPickerIntent, 4 ); break;
        }
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
        final String title,description,price;
        title = titletv.getText().toString();
        description = contenttv.getText().toString();
        price = pricetv.getText().toString();
        if(price.isEmpty()||title.isEmpty()||description.isEmpty()){
            Toast.makeText(this, "All fields are obligatory", Toast.LENGTH_SHORT).show();
        }else{
            try{
                int r  = Integer.parseInt(price);
                if(r > 150){
                    Toast.makeText(this, "Range should be less than 150Km", Toast.LENGTH_SHORT).show();
                }else{
                    if(im1==null || im2==null || im3==null || im4==null  || im4.isEmpty() || im3.isEmpty() || im2.isEmpty() || im1.isEmpty()){
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
                                String resp = PostRequestManager.getResponse(ServerData.ITEMS_LINK,new Uri.Builder()
                                        .appendQueryParameter("item_description",description)
                                        .appendQueryParameter("item_price",price)
                                        .appendQueryParameter("item_title",title)
                                        .appendQueryParameter("ext1",ext1)
                                        .appendQueryParameter("ext2",ext2)
                                        .appendQueryParameter("ext3",ext3)
                                        .appendQueryParameter("ext4",ext4)
                                        .appendQueryParameter("pt1",im1)
                                        .appendQueryParameter("pt2",im2)
                                        .appendQueryParameter("pt3",im3)
                                        .appendQueryParameter("pt4",im4)
                                        .build()
                                        .toString());
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
                                                AddingItems.super.onBackPressed();
                                            }
                                        });
                                    }
                                }catch (NumberFormatException exception){
                                    exception.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddingItems.this, "Server returned incorrect response", Toast.LENGTH_SHORT).show();
                                            sweetAlertDialog.cancel();
                                        }
                                    });
                                }

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
