package Fragments;

import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.MainActivityItemsAdapter;
import adapters.MainActivityPostsAdapter;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Item;
import objects.Post;
import objects.Site;
import objects.User;
import pi.novobyte.com.pimp.MainActivity;
import pi.novobyte.com.pimp.R;

/**
 * Created by codename-tkc on 03/04/2018.
 */

public class ItemsFragment extends Fragment {
    RecyclerView recyclerView;
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theview =  inflater.inflate(
                R.layout.items_fragment, null);
        recyclerView = theview.findViewById(R.id.recent_items_rv);
        context = inflater.getContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getNecessaryData();
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
                    if(getActivity().getClass().getName().equals(MainActivity.class.getName()))
                    resp = GetRequestManager.getResponse(ServerData.ITEMS_LINK + new Uri.Builder()
                            .appendQueryParameter("type", "0")
                            .appendQueryParameter("user_id", CookieData.getUserId(context))
                            .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                            .appendQueryParameter("order_by", "date") //could be ordered by item_order, site_distance , item_price;
                            .appendQueryParameter("order", "desc")
                            .appendQueryParameter("distance", CookieData.getViewDistance(context))
                            .build().toString()

                    );
                    else
                        resp = GetRequestManager.getResponse(ServerData.ITEMS_LINK + new Uri.Builder()
                                .appendQueryParameter("type", "0")
                                .appendQueryParameter("user_id", CookieData.getUserId(context))
                                .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                                .appendQueryParameter("order_by", "date") //could be ordered by item_order, site_distance , item_price;
                                .appendQueryParameter("order", "desc")
                                .appendQueryParameter("distance", CookieData.getViewDistance(context))
                                .build().toString()

                        );
                    final List<Item> finalItems = loadItemsData(resp);

                    Log.i("pimp","Items received in itemsFragment: "+finalItems.size());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new MainActivityItemsAdapter(finalItems,getActivity()));
                            Log.i("pimp","Adapter charged");
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    final String r =  resp;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make((CoordinatorLayout) getActivity().findViewById(R.id.coordinator), "Error loading items: " + GetRequestManager.decodeErrorMessage(r), Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
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
}
