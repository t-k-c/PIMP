package pi.novobyte.com.pimp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import Fragments.PartnerSidePosts;
import commondata.CookieData;
import objects.Site;
import objects.User;

public class PartnerViewActivity extends AppCompatActivity {
    public static  User user;
    public static Site site;
    boolean mysite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_view);

        final Intent intent = getIntent();

         user = (User) intent.getSerializableExtra("user_data");

         site = (Site) intent.getSerializableExtra("site_data");
        ImageButton imageButton = findViewById(R.id.contact);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 =  new Intent(PartnerViewActivity.this,LocationManager.class);
                intent1.putExtra("site_data",site);
                startActivity(intent1);
            }
        });

        final FloatingActionButton floatingActionButton =  findViewById(R.id.fab);
        mysite = (site.user.id+"").equals(CookieData.getUserId(this));
        if(!mysite){
            floatingActionButton.setVisibility(View.GONE);
        }
        TextView textView = findViewById(R.id.ptname);

        textView.setText(site.name);
        final String[] tabs = new String[]{"News","Items","Reviews"};

        final TabLayout tabLayout =  findViewById(R.id.tabs);
        final ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0: return new PartnerSidePosts();
                    case 1: return new PartnerSidePosts();
                    case 2: return new PartnerSidePosts();
                }
                return null;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tabLayout.getSelectedTabPosition();
                if(mysite){
                    floatingActionButton.setVisibility(View.VISIBLE);
                }else{
                    if(pos == 2){
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }else{
                        floatingActionButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = tabLayout.getSelectedTabPosition();
                switch(pos){
                    case 0 : Intent intent1 = new Intent(PartnerViewActivity.this,AddNews.class);
                    intent1.putExtra("site_data",site);startActivity(intent1);break;

                    case 1 : Intent intent2 = new Intent(PartnerViewActivity.this,AddingItems.class);
                    intent2.putExtra("site_data",site);startActivity(intent2);break;

                    case 2 : break;

                }
            }
        });
    }
}
