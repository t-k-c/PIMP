package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ErrorAdapter;
import adapters.MainActivityPostsAdapter;
import adapters.PartnerViewPostAdapter;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Post;
import objects.Site;
import objects.User;
import pi.novobyte.com.pimp.PartnerViewActivity;
import pi.novobyte.com.pimp.R;
public class PartnerSidePosts extends android.support.v4.app.Fragment {
    RecyclerView recyclerView;
    Context context;
    public PartnerSidePosts(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View theView  =  inflater.inflate(R.layout.fragment_partner_side_posts,container,false);
        recyclerView = theView.findViewById(R.id.rv);
        context = inflater.getContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getNecessaryData();
        return  theView;
    }
    void getNecessaryData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = "";

                double[] latlng = CookieData.getLastLocation(context);


                try{
                    /* Loading the news articles */

                    resp = GetRequestManager.getResponse(ServerData.POSTS_LINK + new Uri.Builder()
                            .appendQueryParameter("user_id", CookieData.getUserId(context))
                            .appendQueryParameter("site_id", PartnerViewActivity.site.id+"")
                            .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                            .build().toString()
                    );
                    final List<Post> finalPosts = loadPostsData(resp);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new PartnerViewPostAdapter(finalPosts,getActivity()));
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    final String r =  resp;
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
    private List<Post> loadPostsData(String resp) throws  JSONException{
        final List<Post> postList =  new ArrayList<>();
        final JSONArray jsonArray = new JSONArray(resp);

        for (int i=0;i<jsonArray.length();i++){
            JSONObject postObject =  jsonArray.getJSONObject(i);
            Post post =  Post.getPostInstanceFromJSONData(postObject, PartnerViewActivity.site,Post.STYLE_MORDERN);
            postList.add(post);
        }
        return  postList;
    }
}
