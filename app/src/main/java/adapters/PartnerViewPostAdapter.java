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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.annotation.Url;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import commondata.ServerData;
import objects.Post;
import pi.novobyte.com.pimp.PostViewActvity;
import pi.novobyte.com.pimp.R;

public class PartnerViewPostAdapter extends RecyclerView.Adapter<PartnerViewPostAdapter.ViewHolder> {
    private List<Post> postList;
    Activity activity;
    public PartnerViewPostAdapter(List<Post> postList,Activity activity) {
        this.postList = postList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_preview2,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
       final Post post  =  postList.get(position);
        holder.patnernametv.setText(post.site.name);
        String st = "@"+post.site.short_name;
        holder.partnerusernametv.setText(st);
        holder.titletv.setText(post.title);
        holder.likestv.setText(post.likes_number+"");
        holder.commentstv.setText(post.comments_number+"");
        holder.timetv.setText(post.date_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final Bitmap thumbnailBitmap1 = BitmapFactory.decodeStream(new URL(
                           ServerData.SITE_IMAGES_LINK+post.site.thumbnail
                    ).openConnection().getInputStream());

                    final Bitmap thumbnailBitmap2 = BitmapFactory.decodeStream(new URL(
                             post.thumbnail
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
        }).start();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, PostViewActvity.class);
                intent.putExtra("post_object",post);
                activity.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView titletv,likestv,commentstv,timetv,patnernametv,partnerusernametv;
        ImageView partner_thumbnail,post_thumbnail;
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            titletv = itemView.findViewById(R.id.post_title);
            timetv = itemView.findViewById(R.id.post_time);
            likestv  = itemView.findViewById(R.id.post_like_number);
            commentstv = itemView.findViewById(R.id.post_comment_number);
            patnernametv = itemView.findViewById(R.id.post_partner_name);
            partnerusernametv = itemView.findViewById(R.id.post_partner_username);
            partner_thumbnail = itemView.findViewById(R.id.post_partner_image);
            post_thumbnail = itemView.findViewById(R.id.post_thumbnail);
        }
    }
}
