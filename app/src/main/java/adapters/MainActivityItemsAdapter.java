package adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import objects.Item;
import pi.novobyte.com.pimp.ItemViewActivity;
import pi.novobyte.com.pimp.R;

public class MainActivityItemsAdapter extends RecyclerView.Adapter<MainActivityItemsAdapter.ViewHolder>{
    List<Item> items;
    boolean searchpage;
    Activity activity;
    public MainActivityItemsAdapter(List<Item> items, Activity activity) {
        this.items = items;
        this.activity = activity;
        this.searchpage = false;
    }
    public MainActivityItemsAdapter(List<Item> items, Activity activity,boolean searchpage) {
        this.items = items;
        this.activity = activity;
        this.searchpage = searchpage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Item item = items.get(position);
        String price= item.price+" CFA";
        holder.pricetv.setText(price);
        holder.distancetv.setText(new DecimalFormat("###.00").format(item.distance));
        holder.itemtitletv.setText(item.name);
        holder.intereststv.setText(item.interest_number+"");
        if(item.user_interested){
            holder.likebutton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.theNewPink));
        }

       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   URL url =  new URL( item.images.size() > 0 ?  item.images.get(0) : "");
                   final Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                   activity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           holder.mainImage.setImageBitmap(bitmap);
                       }
                   });
               } catch (MalformedURLException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }).start();
//if(searchpage){
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
   Intent intent  = new Intent(activity, ItemViewActivity.class);
   intent.putExtra("item_data",item);
   activity.startActivity(intent);
        }
    });
/*}else{
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent  = new Intent(activity, ItemViewActivity.class);
            intent.putExtra("item_data",item);
            activity.startActivity(intent);
        }
    });
}*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView pricetv,intereststv,itemtitletv,distancetv;
        ImageView mainImage;
        ImageButton likebutton;
        public ViewHolder(View itemView) {
            super(itemView);
            pricetv = itemView.findViewById(R.id.item_price);
            intereststv = itemView.findViewById(R.id.item_interests);
            itemtitletv = itemView.findViewById(R.id.item_title);
            distancetv = itemView.findViewById(R.id.partner_distance);
            mainImage = itemView.findViewById(R.id.item_thumbnail);
            likebutton = itemView.findViewById(R.id.item_like_button);
        }
    }
}
