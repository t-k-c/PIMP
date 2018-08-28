package pi.novobyte.com.pimp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ErrorAdapter;
import adapters.MainActivityPartnersAdapters;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Site;
import objects.User;

public class SiteListActivity extends AppCompatActivity {
    public static final String FOLLOWED_SITES_PAGE=MainActivityPartnersAdapters.SITE_LIST_PAGE_FOLLOWS;
    public static final String MY_SITES_PAGE= MainActivityPartnersAdapters.SITE_LIST_PAGE_MYSITES;
    String page;
    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    public static User utilisateur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_list);
        final User user = (User) getIntent().getSerializableExtra("user_data");
        utilisateur = user;
         page = getIntent().getStringExtra("page");
        TextView page_title = (TextView) findViewById(R.id.ptname);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        swipeRefreshLayout = findViewById(R.id.swiper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coordinatorLayout = findViewById(R.id.coordinator);
        page_title.setText(page.equals(FOLLOWED_SITES_PAGE) ? FOLLOWED_SITES_PAGE : MY_SITES_PAGE);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        if (page.equals(FOLLOWED_SITES_PAGE)) {
            getFollowedSites();

            floatingActionButton.setVisibility(View.GONE);
        } else {
            getMySites();
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =  new Intent(SiteListActivity.this,AddSite.class);
                    intent.putExtra("user_data",user);
                    startActivity(intent);
                }
            });
        }

    }

    public void getFollowedSites() {
        double[] latlng = CookieData.getLastLocation(this);
       loadData(ServerData.SUBSCRIPTIONS_LINK+new Uri.Builder().
       appendQueryParameter("user_id", CookieData.getUserId(this))
               .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
       .build()
       .toString());
    }

    public void getMySites(){
        double[] latlng = CookieData.getLastLocation(this);
        loadData(ServerData.SITES_LINK+new Uri.Builder().
                appendQueryParameter("user_id", CookieData.getUserId(this))
                .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                .build()
                .toString());
    }
    public void loadData(final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                final String resp = GetRequestManager.getResponse(path);

                try{
                    final List<Site> sites =  new ArrayList<>();
                    final JSONArray jsonArray = new JSONArray(resp);

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Site site = Site.getSiteInstanceFromJSONData(jsonArray.getJSONObject(i),
                                User.getUserInstanceFromJSONData(jsonObject));
                        site.setUser_follows(jsonObject.getInt("user_follows") == 1);
                        site.setDistance(jsonObject.getDouble("distance"));
                        site.setSite_visibility(jsonObject.getInt("site_visibility") == 1);
                        site.setSubscriptions_number(jsonObject.getInt("site_follows"));
                        sites.add(site);

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(sites.isEmpty()){
                                recyclerView.setAdapter(new ErrorAdapter("No sites for the moment"));
                            }else{
                                recyclerView.setAdapter(new MainActivityPartnersAdapters(sites,SiteListActivity.this,page));
                            }
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    });
                }catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("pimp",resp);
                    final String r =  resp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new ErrorAdapter("An error occurred"));
                            Snackbar.make(coordinatorLayout, "Error: " + GetRequestManager.decodeErrorMessage(r), Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loadData(path);
                                }
                            }).show();
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    });
                }
            }
        }).start();
    }
}
