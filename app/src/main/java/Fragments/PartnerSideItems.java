package Fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import adapters.ErrorAdapter;
import adapters.PartnerSideItemsAdapter;
import adapters.PartnerViewPostAdapter;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Item;
import objects.Post;
import objects.Site;
import objects.User;
import pi.novobyte.com.pimp.PartnerViewActivity;
import pi.novobyte.com.pimp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PartnerSideItems extends Fragment {
    RecyclerView recyclerView;
    Context context;

    public PartnerSideItems() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_partner_side_items, container, false);
        recyclerView = theView.findViewById(R.id.rv);
        context = inflater.getContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getNecessaryData();
        return theView;
    }

    void getNecessaryData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = "";

                double[] latlng = CookieData.getLastLocation(context);


                try {
                    /* Loading the news articles */

                    resp = GetRequestManager.getResponse(ServerData.POSTS_LINK + new Uri.Builder()
                            .appendQueryParameter("user_id", CookieData.getUserId(context))
                            .appendQueryParameter("site_id", PartnerViewActivity.site.id + "")
                            .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                            .build().toString()
                    );
                    final List<Item> finalIt = loadItemsData(resp);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new PartnerSideItemsAdapter(finalIt, getActivity()));
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    final String r = resp;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new ErrorAdapter());

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
}