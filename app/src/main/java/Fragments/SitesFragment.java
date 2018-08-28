package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import pi.novobyte.com.pimp.R;
import pi.novobyte.com.pimp.SiteListActivity;

/**
 * Created by codename-tkc on 03/04/2018.
 */

public class SitesFragment extends Fragment {
    RecyclerView recyclerView;
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theview =  inflater.inflate(
                R.layout.sites_fragment, null);
        recyclerView = theview.findViewById(R.id.recent_sites_rv);
        context = inflater.getContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        String resp = "";

        double[] latlng = CookieData.getLastLocation(context);

        loadData(ServerData.SITES_LINK + new Uri.Builder()
                /*      .appendQueryParameter("type", "0")*/
                .appendQueryParameter("user_id", CookieData.getUserId(context))
                .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                .appendQueryParameter("order_by", "site_distance") // or creation date
                .appendQueryParameter("order", "desc") // or creation date
                .appendQueryParameter("distance", CookieData.getViewDistance(getActivity())) // or creation date
                .build().toString());
        return  theview;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);

    }

    void getNecessaryData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = "";

                double[] latlng = CookieData.getLastLocation(context);


                try{
                    /* loading the sites */
                    resp = GetRequestManager.getResponse(ServerData.SITES_LINK + new Uri.Builder()
                      /*      .appendQueryParameter("type", "0")*/
                            .appendQueryParameter("user_id", CookieData.getUserId(context))
                            .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                            .appendQueryParameter("order_by", "site_distance") // or creation date
                            .appendQueryParameter("order", "desc") // or creation date
                            .appendQueryParameter("distance", CookieData.getViewDistance(getActivity())) // or creation date
                            .build().toString()
                    );

                    final List<Site> finalSites = loadSitesData(resp);


                    Log.i("pimp","Items received in itemsFragment: "+finalSites.size());
                    if(finalSites.isEmpty()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new ErrorAdapter("Empty for the moment"));
                            }
                        });
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new MainActivityPartnersAdapters(finalSites,getActivity()));
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    final String r =  resp;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*Snackbar.make((CoordinatorLayout) getActivity().findViewById(R.id.coordinator), "Error: " + GetRequestManager.decodeErrorMessage(r), Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getNecessaryData();
                                }
                            }).show();*/

                        }
                    });
                }
            }
        }).start();

    }

    public void loadData(final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(sites.isEmpty()){
                                recyclerView.setAdapter(new ErrorAdapter("No sites for the moment"));
                            }else{
                                recyclerView.setAdapter(new MainActivityPartnersAdapters(sites,getActivity()));
                            }


                        }
                    });
                }catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("pimp",resp);
                    final String r =  resp;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new ErrorAdapter("An error occurred"));

                        }
                    });
                }
            }
        }).start();
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
