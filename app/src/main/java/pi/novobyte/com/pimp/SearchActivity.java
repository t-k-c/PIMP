package pi.novobyte.com.pimp;

import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ErrorAdapter;
import adapters.MainActivityItemsAdapter;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Item;
import objects.Site;
import objects.User;

public class SearchActivity extends AppCompatActivity {
RecyclerView recyclerView;
EditText nameet,amountet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.rec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new ErrorAdapter("Start searching :) "));
        nameet = findViewById(R.id.name);

        amountet = findViewById(R.id.amount);

        nameet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
              if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
                  getNecessaryData();
                  return true;
              }
              return false;

            }
        });

        amountet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
                    getNecessaryData();
                    return true;
                }
                return false;
            }
        });

    }
    void getNecessaryData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = "";
                double[] latlng = CookieData.getLastLocation(SearchActivity.this);

                try{
                    /* loading the sites */
                    resp = GetRequestManager.getResponse(ServerData.SEARCH_LINK + new Uri.Builder()
                            .appendQueryParameter("amount", amountet.getText().toString())
                            .appendQueryParameter("needle", nameet.getText().toString())
                            .appendQueryParameter("user_id", CookieData.getUserId(SearchActivity.this))
                            .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                            .appendQueryParameter("distance", CookieData.getViewDistance(SearchActivity.this))
                            .build().toString()

                    );

                    final List<Item> finalItems = loadItemsData(resp);

                    Log.i("pimp","Items received in itemsFragment: "+finalItems.size());

                    if(finalItems.isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new ErrorAdapter("Search returned no result"));
                                Log.i("pimp","Adapter charged");
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new MainActivityItemsAdapter(finalItems,SearchActivity.this,true));
                                Log.i("pimp","Adapter charged");
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    final String r =  resp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinator), "Error loading items: " + GetRequestManager.decodeErrorMessage(r), Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getNecessaryData();
                                }
                            }).show();

                        }
                    });
                }
            }
        }).start();

    }

    private List<Item> loadItemsData(String resp) throws  JSONException{

        List<Item> itemList = new ArrayList<>();
        JSONArray items = new JSONArray(resp);
        for(int i=0;i<items.length();i++){
            JSONObject itemJson = items.getJSONObject(i);

            User user = User.getUserInstanceFromJSONData(itemJson);

            Item item = Item.getItemInstanceFromJSONData(itemJson, Site.getSiteInstanceFromJSONData(itemJson,user));

            itemList.add(item);
        }
        return  itemList;
    }

    private List<Site> loadSitesData(String resp)  throws  JSONException {
        List<Site> siteList =  new ArrayList<>();
        JSONArray sites  = new JSONArray(resp);
        for(int i = 0;i<sites.length();i++){

            JSONObject siteJson =  sites.getJSONObject(i);

            User user = User.getUserInstanceFromJSONData(siteJson);

            siteList.add(Site.getSiteInstanceFromJSONData(siteJson,user));
        }
        return  siteList;
    }


}
