package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ErrorAdapter;
import adapters.MainActivityPostsAdapter;
import adapters.PartnerViewPostAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Post;
import objects.Site;
import objects.User;
import pi.novobyte.com.pimp.MainActivity;
import pi.novobyte.com.pimp.R;

/**
 * Created by codename-tkc on 03/04/2018.
 */

public class PostsFragment extends Fragment {
    Context context ;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theview =  inflater.inflate(
                R.layout.posts_fragment, null);
        recyclerView = theview.findViewById(R.id.recent_articles_rv);
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
                /* Loading the news articles */

                resp = GetRequestManager.getResponse(ServerData.POSTS_LINK + new Uri.Builder()
                        .appendQueryParameter("type", "0")
                        .appendQueryParameter("user_id", CookieData.getUserId(context))
                        .appendQueryParameter("latlng", latlng[0] + "," + latlng[1])
                        .appendQueryParameter("order_by", "post_date") //could be ordered by post_date
                        .appendQueryParameter("order", "desc")
                        .appendQueryParameter("distance", CookieData.getViewDistance(context))
                        .build().toString()
                );
                    final List<Post> finalPosts = loadPostsData(resp);
                    Log.i("pimp","Posts received in postsFragment: "+finalPosts.size());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new MainActivityPostsAdapter(finalPosts,getActivity()));
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
        List<Post> postList =  new ArrayList<>();
        JSONArray jsonArray = new JSONArray(resp);
        for (int i=0;i<jsonArray.length();i++){
            JSONObject postObject =  jsonArray.getJSONObject(i);
            User user = User.getUserInstanceFromJSONData(postObject);
            Post post =  Post.getPostInstanceFromJSONData(postObject,Site.getSiteInstanceFromJSONData(postObject,user),Post.STYLE_MORDERN);
            postList.add(post);
        }
        return  postList;
    }
}
