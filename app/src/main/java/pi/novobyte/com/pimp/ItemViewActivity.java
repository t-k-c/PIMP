package pi.novobyte.com.pimp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Item;

public class ItemViewActivity extends AppCompatActivity {
     Item item = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        TextView name_tv,short_name_tv,item_nametv,item_descriptiontv,likes_number_tv,distance_tv,item_pricetv;
        final ImageView t1,t2,t3,t4,partner_thumbnail;

        ImageButton imageButton;
            name_tv = findViewById(R.id.site_name);
            short_name_tv = findViewById(R.id.site_short_name);
            item_pricetv  = findViewById(R.id.item_price);
            likes_number_tv = findViewById(R.id.likes_tv);
            distance_tv = findViewById(R.id.distance);
            item_nametv = findViewById(R.id.title);

            item_descriptiontv = findViewById(R.id.item_description);
            t1 = findViewById(R.id.th1);
            t2 = findViewById(R.id.th2);
            t3 = findViewById(R.id.th3);
            t4 = findViewById(R.id.th4);
            partner_thumbnail = findViewById(R.id.partner_thumbnail);
        final ImageView[] imageViews = {t1,t2,t3,t4};
        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setFullScreen(true);
        for(ImageView imageView : imageViews){
            imageView.setClickable(true);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView1 = (ImageView) view;
                    imagePopup.initiatePopup(imageView1.getDrawable());
                    imagePopup.viewPopup();
                }
            });
        }
            imageButton = findViewById(R.id.post_like_button);
         item = (Item) getIntent().getSerializableExtra("item_data");
        TextView title =  (TextView) findViewById(R.id.ptname);
        title.setText(item.name);
        name_tv.setText(item.site.name);
        short_name_tv.setText("@"+item.site.short_name);
        item_nametv.setText(item.name);
        item_descriptiontv.setText(item.description);
        likes_number_tv.setText(item.interest_number+"");
        distance_tv.setText(new DecimalFormat("###.00").format(item.distance)+" km");
        item_pricetv.setText(item.price+" FCFA");
        if(item.user_interested){
            imageButton.setColorFilter(getResources().getColor(R.color.theNewPink));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
               String resp = GetRequestManager.getResponse(ServerData.ASSOCS_LINK + new Uri.Builder()
                        .appendQueryParameter("item_id", item.id+"")
                        .build().toString()
                );
                try {
                    JSONArray jsonArray = new JSONArray(resp);
                    for (int i=0;i<( jsonArray.length() > 4 ? 4 : jsonArray.length() );i++){
                        final Bitmap bp = BitmapFactory.decodeStream(new URL(jsonArray.getJSONObject(0).getString("item_thumbnail")).openConnection().getInputStream());
                        final int j = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageViews[j].setImageBitmap(bp);
                            }
                        });
                    }
                    final Bitmap partner_thumbnailbm = BitmapFactory.decodeStream(new URL(ServerData.SITE_IMAGES_LINK+item.site.thumbnail).openConnection().getInputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            partner_thumbnail.setImageBitmap(partner_thumbnailbm);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ItemViewActivity.this, "Wrong response from server", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    public void click(View view) {
        switch (view.getId()){
            case R.id.map : Intent intent = new Intent(this,LocationManager.class);
                intent.putExtra("site_data",item.site);
                startActivity(intent);
        }
    }
}
