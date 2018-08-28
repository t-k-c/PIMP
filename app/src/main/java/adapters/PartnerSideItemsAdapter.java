package adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import commondata.ServerData;
import objects.Item;
import pi.novobyte.com.pimp.R;

public class PartnerSideItemsAdapter  extends  RecyclerView.Adapter<PartnerSideItemsAdapter.ViewHolder>{
    List<Item> itemList;
    Activity activity;

    public PartnerSideItemsAdapter(List<Item> itemList, Activity activity) {
        this.itemList = itemList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.fragment_partner_side_items,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item  =  itemList.get(position);
        holder.name_tv.setText(item.site.name);
        holder.short_name_tv.setText("@"+item.site.short_name);
        holder.item_nametv.setText(item.name);
        holder.item_descriptiontv.setText(item.description);
        holder.likes_number_tv.setText(item.interest_number+"");
        holder.distance_tv.setText(item.distance+"");
        holder.distance_tv.setText(item.price+"");
        holder.distance_tv.setText(item.price+"");
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final Bitmap thumbnailBitmap1 = BitmapFactory.decodeStream(new URL(
                           ServerData.SITE_IMAGES_LINK+"item_"+item.id+"-1"
                    ).openConnection().getInputStream());

                    final Bitmap thumbnailBitmap2 = BitmapFactory.decodeStream(new URL(
                            post.thumbnail.contains("http")? post.thumbnail : ServerData.POST_IMAGES_LINK+post.thumbnail
                    ).openConnection().getInputStream());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.partner_thumbnail.setImageBitmap(thumbnailBitmap1);
                            holder.post_thumbnail.setImageBitmap(thumbnailBitmap2);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name_tv,short_name_tv,item_nametv,item_descriptiontv,likes_number_tv,distance_tv,item_pricetv;
        ImageView t1,t2,t3,t4;
        ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.site_name);
            short_name_tv = itemView.findViewById(R.id.site_short_name);
            item_pricetv  = itemView.findViewById(R.id.item_price);
            likes_number_tv = itemView.findViewById(R.id.likes_tv);
            distance_tv = itemView.findViewById(R.id.distance);
            item_nametv = itemView.findViewById(R.id.title);
            item_descriptiontv = itemView.findViewById(R.id.item_description);
            t1 = itemView.findViewById(R.id.th1);
            t2 = itemView.findViewById(R.id.th2);
            t3 = itemView.findViewById(R.id.th3);
            t4 = itemView.findViewById(R.id.th4);
            imageButton = itemView.findViewById(R.id.post_like_button);

        }
    }
}
