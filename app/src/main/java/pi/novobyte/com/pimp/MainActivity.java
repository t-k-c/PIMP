package pi.novobyte.com.pimp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Fragments.ItemsFragment;
import Fragments.PostsFragment;
import Fragments.SitesFragment;
import adapters.PartnerViewPostAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import functionalities.LocationListenerClass;
import objects.Item;
import objects.Post;
import objects.Site;
import objects.User;

import static commondata.ServerData.USERS_LINK;

public class MainActivity extends AppCompatActivity {
    final int request_code_location = 102; //any number to be gotten directly in the callback
    DrawerLayout drawerLayout;
     String[] menu_options_string = new String[10];
     int[] menu_options_icons = new int[10];
    SliderLayout sliderLayout;
    SweetAlertDialog loadingDialog;
    TextView newsTitle, likenumber, commentnumber;
    LocationListener locationListener;
    CoordinatorLayout coordinatorLayout;
    LocationManager locationManager;
    Fragment postfragment,itemsfragment,sitefragment;
    RecyclerView postRecycler;ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         swipeRefreshLayout =  (SwipeRefreshLayout) findViewById(R.id.swiper);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNecessaryData();

            }

        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
         listView = (ListView) findViewById(R.id.lv);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        TextView newsfeed = (TextView) findViewById(R.id.pindexerText);
        newsfeed.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa/Comfortaa-Bold.ttf"));
        newsTitle = (TextView) findViewById(R.id.newsTitle);
        commentnumber = (TextView) findViewById(R.id.comments_tv);
        likenumber = (TextView) findViewById(R.id.likes_tv);
        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        newsTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa/Comfortaa-Regular.ttf"));
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
         tabLayout = (TabLayout) findViewById(R.id.tabs);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View v = LayoutInflater.from(this).inflate(R.layout.tab, null);
            ImageView imageView = (ImageView) v.findViewById(R.id.icon);
            TextView textView = (TextView) v.findViewById(R.id.text);
            switch (i) {
                case 0:
                    imageView.setImageResource(R.drawable.ic_recent);
                    textView.setText("RECENT");
                    Log.d("PIMP", "adding for case 0");
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.ic_items);
                    textView.setText("ITEMS");
                    Log.d("PIMP", "adding for case 1");
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.ic_sites);
                    textView.setText("SITES");
                    Log.d("PIMP", "adding for case 2");
                    break;
            }
            assert tab != null;
            tab.setCustomView(v);
            if (tab.getCustomView() == null) Log.d("PIMP", "null");
            else Log.d("PIMP", "not null");
        }



    /*    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    ImageView img = (ImageView) view.findViewById(R.id.icon);
                    img.setColorFilter(Color.rgb(255, 255, 255));
                }
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                if (v != null) {
                ImageView img = (ImageView) v.findViewById(R.id.icon);
                img.setColorFilter(getResources().getColor(R.color.lightwhite));
            }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

*/
    postfragment = new PostsFragment();
    itemsfragment = new ItemsFragment();
    sitefragment = new SitesFragment();
final String[] pages = {"Recent","Items","Sites"};
        viewpager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return postfragment ;
                    case 1:
                        return itemsfragment;
                    case 2:
                        return sitefragment;
                }
                return null;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return pages[position];
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        tabLayout.setupWithViewPager(viewpager,false);
        getLocation(new Runnable() {
            @Override
            public void run() {
                getNecessaryData();
            }
        });
    }

    /**
     * The runnable is the operation to be carried out on successful first localisation
     */
    void getLocation(Runnable runnable) {
        loadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.setContentText("please move a little ...");
        loadingDialog.setCancelable(true);

        if (!CookieData.hasUserLastLocation(this)) {
            loadingDialog.setConfirmButton("Use last known location", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.cancel();
                    Snackbar.make(coordinatorLayout, "Using your last known location", Snackbar.LENGTH_LONG);
                    return;
                }
            });
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListenerClass(runnable, loadingDialog, this);

        if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Snackbar.make(coordinatorLayout, "You need to activate your GPS", Snackbar.LENGTH_INDEFINITE).setAction(
                    "OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Intent i =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package",getPackageName(),null);
//                            i.setData(uri);
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }
            ).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, request_code_location);
            return;
        }

        loadingDialog.show();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) { //trying to get which permission has been granted
            case request_code_location:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getLocation(new Runnable() {
                        @Override
                        public void run() {
                            getNecessaryData();
                        }
                    });
                } else {
                    //permission not at all accepted
                    Snackbar.make(coordinatorLayout, "You need to give PIMP location permission", Snackbar.LENGTH_INDEFINITE).setAction(
                            "OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    i.setData(uri);
                                    startActivity(i);
                                }
                            }
                    );
                    return;
                }
        }
    }

    /**
     * OPen the nav drawer
     */
    public void openLeftMenu(View v) {
        v.setRotationY(180);
        drawerLayout.openDrawer(Gravity.START);
    }

   private   void getNecessaryData() {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            String resp = "";
            @Override
            public void run() {
                try {


                         resp = GetRequestManager.getResponse(USERS_LINK+ new Uri.Builder()
                                .appendQueryParameter("user_id",CookieData.getUserId(MainActivity.this))
                                .build()

                            .toString());

                            JSONObject jsonObject = new JSONObject(resp);
                            final User u = User.getUserInstanceFromJSONData(jsonObject);
                            if (jsonObject.getInt("user_category") == 1){
                                menu_options_icons = new int[]{R.drawable.ic_like,R.drawable.ic_brown_tagged_like, R.drawable.ic_settings,R.drawable.ic_location};
                                menu_options_string = new String[]{"Followed sites", "Interested items", "My Setttings", "My sites"};
                            }else{
                                menu_options_icons = new int[]{R.drawable.ic_like,R.drawable.ic_brown_tagged_like, R.drawable.ic_settings};
                                menu_options_string = new String[]{"Followed sites", "Interested items",  "My Setttings"};
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView tv1 = (TextView) findViewById(R.id.name);
                                    tv1.setText("~"+u.username);
                                    TextView tv2 = (TextView) findViewById(R.id.email);
                                    tv2.setText(u.email);
                                    listView.setAdapter(new DrawerMenuListAdapter(MainActivity.this,R.layout.drawer_list_item));
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            switch (i){
                                                case 0: Intent intent = new Intent(MainActivity.this,SiteListActivity.class);
                                                intent.putExtra("user_data",u);
                                                intent.putExtra("page",SiteListActivity.FOLLOWED_SITES_PAGE);
                                                    startActivity(intent);break;
                                                case 3: intent = new Intent(MainActivity.this,SiteListActivity.class);
                                                    intent.putExtra("user_data",u);
                                                    intent.putExtra("page",SiteListActivity.MY_SITES_PAGE);
                                                    startActivity(intent);break;
                                                case 1: intent = new Intent(MainActivity.this,LikedItems.class);
                                                    startActivity(intent);break;

                                            }
                                        }
                                    });
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });



                     resp = GetRequestManager.getResponse(ServerData.POSTS_LINK + new Uri.Builder()
                            .appendQueryParameter("type", "1").build()
                            .toString()
                    );

                    final List<Post> posts = new ArrayList<>();
                    JSONArray glopalPosts = new JSONArray(resp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);

                            sliderLayout.removeAllSliders();
                        }
                    });

                    for (int i = 0; i < (glopalPosts.length() > 10 ? 10 : glopalPosts.length()); i++) {

                        // the post item
                        JSONObject post = glopalPosts.getJSONObject(i);

                        //likes and comments are index 1 one step inside;


                        //the site info is one step inside with key  0;
                        JSONObject site = post.optJSONObject("0");

                        //the user info is one step inside the site with key 0;
                        JSONObject user = site.optJSONObject("0");

                        User userObject = User.getUserInstanceFromJSONData(user);
                        Site siteObject = Site.getSiteInstanceFromJSONData(site, userObject);

                        final Post postObject = Post.getPostInstanceFromJSONData(post, siteObject, Post.STYLE_DEPRECATED);

                        posts.add(postObject);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                TextSliderView textSliderView = new TextSliderView(getApplicationContext());
                                textSliderView.image(postObject.thumbnail);
                                sliderLayout.addSlider(textSliderView);
                            }
                        });

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {

                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {
                                    if (position< posts.size()){

                                        Post post = posts.get(position);
                                        newsTitle.setText(post.title);
                                        likenumber.setText(post.likes_number + "");
                                        commentnumber.setText(post.comments_number + "");

                                    }
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });

                        }
                    });


                    /* Now check the recent,items and sites */
                    double[] latlng = CookieData.getLastLocation(MainActivity.this);

                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            loadingDialog.cancel();
                        }
                    });*/

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("pimp",resp);
                    final String r =  resp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                           if(loadingDialog!=null) {loadingDialog.cancel();}
                            Snackbar.make(coordinatorLayout, "Error: " + GetRequestManager.decodeErrorMessage(r), Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getNecessaryData();
                                }
                            }).show();

                        }
                    });

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {

                           sliderLayout.removeAllSliders();
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





    class DrawerMenuListAdapter extends ArrayAdapter<String> {

        Context context1;
        int resource;

        public DrawerMenuListAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            context1 = context;
            this.resource = resource;
        }


        @Override
        public int getCount() {
            return menu_options_icons.length;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            return super.getView(position, convertView, parent);
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getLayoutInflater().inflate(resource, null);
            }
            ImageView imageView = view.findViewById(R.id.theicon);
            TextView textView = view.findViewById(R.id.thetext);
            textView.setText(menu_options_string[position]);
            imageView.setImageResource(menu_options_icons[position]);
            return view;
        }
    }
    public void search(View view){
       int pos =  tabLayout.getSelectedTabPosition();
       Intent intent = new Intent(this,SearchActivity.class);
       intent.putExtra("tab",pos);
       startActivity(intent);
    }

}

