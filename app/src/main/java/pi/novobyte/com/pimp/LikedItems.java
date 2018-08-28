package pi.novobyte.com.pimp;

import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ErrorAdapter;
import adapters.MainActivityItemsAdapter;
import adapters.MainActivityPartnersAdapters;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Item;
import objects.Site;
import objects.User;

public class LikedItems extends AppCompatActivity {
SwipeRefreshLayout refreshLayout ;
RecyclerView recyclerView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_items);
        refreshLayout = findViewById(R.id.swiper);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        loadData();
    }
    public void loadData(){
        refreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
               final String resp =  GetRequestManager.getResponse(ServerData.INTERERSTS_LINK+new Uri.Builder()
                .appendQueryParameter("user_id", CookieData.getUserId(LikedItems.this))
                .appendQueryParameter("latlng",CookieData.getLastLocation(
                        LikedItems.this)[0]+","+
                        CookieData.getLastLocation(LikedItems.this)[1])
                .build()
                .toString());
                try {
                    final List<Item> items =  new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(resp);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject itemsObject = jsonArray.getJSONObject(i);
                        items.add(Item.getItemInstanceFromJSONData(itemsObject,Site.getSiteInstanceFromJSONData(itemsObject,User.getUserInstanceFromJSONData(itemsObject))));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(items.isEmpty()){
                                recyclerView.setAdapter(new ErrorAdapter("No items for the moment"));
                            }else{
                                recyclerView.setAdapter(new MainActivityItemsAdapter(items,LikedItems.this));
                            }
                            refreshLayout.setRefreshing(false);

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new ErrorAdapter("Couldn't load data"));
                            Toast.makeText(LikedItems.this, "Wrong response from server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();
    }
}
