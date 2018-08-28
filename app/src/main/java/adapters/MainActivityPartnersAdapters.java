package adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;
import objects.Site;
import pi.novobyte.com.pimp.PartnerViewActivity;
import pi.novobyte.com.pimp.PostViewActvity;
import pi.novobyte.com.pimp.R;
import pi.novobyte.com.pimp.SiteListActivity;

public class MainActivityPartnersAdapters extends RecyclerView.Adapter<MainActivityPartnersAdapters.ViewHolder> {

    List<Site> sites;
    Activity activity;
    public static final String MAIN_PAGE = "main-page";
    public static final String SITE_LIST_PAGE_FOLLOWS = "FOLLOWS";
    public static final String SITE_LIST_PAGE_MYSITES = "MY SITES";
    String page;

    public MainActivityPartnersAdapters(List<Site> sites,Activity activity) {
        this.sites = sites;
        this.activity = activity;
        this.page = MAIN_PAGE;
    }
    public MainActivityPartnersAdapters(List<Site> sites,Activity activity,String page) {
        this.sites = sites;
        this.activity = activity;
        this.page = page;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sites_preview,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Site site = sites.get(position);
        holder.partnernametv.setText(site.name);
        holder.partnerdescriptiontv.setText(site.description);
        holder.partnerdistancetv.setText(new DecimalFormat("###.00").format(site.distance)+" KM");
        holder.subscribeduserstv.setText(site.subscriptions_number+"");
        if(site.user_follows){
            holder.subscriptionbuttonib.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.theNewPink));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final Bitmap bp=BitmapFactory.decodeStream(new URL(ServerData.SITE_IMAGES_LINK+site.thumbnail).openConnection().getInputStream());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.partnerthumbnailim.setImageBitmap(bp);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, PartnerViewActivity.class);
                intent.putExtra("site_object",site);
                activity.startActivity(intent);

            }
        });
        if(page.equals(SITE_LIST_PAGE_FOLLOWS)||page.equals(SITE_LIST_PAGE_MYSITES)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activity, PartnerViewActivity.class);
                    i.putExtra("user_data", SiteListActivity.utilisateur);
                    i.putExtra("site_data",site);
                   activity.startActivity(i);
                }
            });
            if(!site.site_visibility){
                holder.itemView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.clickable_pressed));
            }
        }

        if(page.equals(SITE_LIST_PAGE_FOLLOWS)){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
                    ArrayAdapter<String> stringArrayAdapter =  new ArrayAdapter<String>(
                            activity,android.R.layout.simple_list_item_1
                    );
                    stringArrayAdapter.add("UnFollow");
                    builder.setAdapter(stringArrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case 0 :
                                    new Thread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    String resp = GetRequestManager.getResponse(
                                                            ServerData.SUBSCRIPTIONS_LINK +
                                                                    new Uri.Builder().appendQueryParameter("user_id", CookieData.getUserId(activity))
                                                                            .appendQueryParameter("action", "unfollow")
                                                                            .appendQueryParameter("site_id", site.id+"")
                                                    );
                                                    try{
                                                        int r = Integer.parseInt(resp);
                                                        if(r!=1){
                                                            Toast.makeText(activity, "That didn't work", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            final RecyclerView rv = activity.findViewById(R.id.rv);
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    rv.removeView(view);
                                                                }
                                                            });
                                                        }
                                                    }catch (NumberFormatException e){
                                                        e.printStackTrace();
                                                        Toast.makeText(activity, "That didn't work", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                    ).start();
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                    return true;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder{
            TextView partnerdistancetv,partnernametv,partnerdescriptiontv,subscribeduserstv;
            ImageView partnerthumbnailim;
            ImageButton subscriptionbuttonib;

        ViewHolder(View itemView) {
            super(itemView);

            partnerdistancetv = itemView.findViewById(R.id.partner_distance);
            partnernametv = itemView.findViewById(R.id.partner_name);
            partnerdescriptiontv = itemView.findViewById(R.id.partner_description);
            partnerthumbnailim = itemView.findViewById(R.id.partner_thumbnail);
            subscribeduserstv = itemView.findViewById(R.id.subscribed_users);
            subscriptionbuttonib = itemView.findViewById(R.id.subscription_button);
        }
    }
}
