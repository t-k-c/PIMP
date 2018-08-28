package adapters;

import android.app.Activity;
import android.content.Context;
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
import java.net.URL;
import java.util.List;

import objects.Post;
import pi.novobyte.com.pimp.PartnerViewActivity;
import pi.novobyte.com.pimp.PostViewActvity;
import pi.novobyte.com.pimp.R;

public class MainActivityPostsAdapter extends RecyclerView.Adapter<MainActivityPostsAdapter.ViewHolder> {
    private List<Post> posts;
    private Activity activity;
    public MainActivityPostsAdapter(List<Post> posts, Activity activity) {
        this.posts = posts;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_preview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

       final  Post post =  posts.get(position);
        holder.itemView.setClickable(true);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, PostViewActvity.class);
                intent.putExtra("post_object",post);
                activity.startActivity(intent);

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap;
                try {

                    bitmap = BitmapFactory.decodeStream(new URL(post.thumbnail).openConnection().getInputStream());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.imageView.setImageBitmap(bitmap);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        holder.datetimetv.setText(post.date_time);
        holder.likestv.setText(post.likes_number+"");
        holder.commentstv.setText(post.comments_number+"");
        if(post.user_liked){
            holder.likebutton.setColorFilter(holder.v.getContext().getResources().getColor(R.color.theNewPink));
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
            TextView titletv,datetimetv,likestv,commentstv;
            ImageView imageView;
            ImageButton likebutton;
            View v;
            ViewHolder(final View itemView) {
            super(itemView);
            v= itemView;

            titletv = itemView.findViewById(R.id.post_title);
            datetimetv = itemView.findViewById(R.id.post_time);
            likestv = itemView.findViewById(R.id.post_like_number);
            commentstv = itemView.findViewById(R.id.post_comment_number);
            imageView = itemView.findViewById(R.id.post_thumbnail);
            likebutton = itemView.findViewById(R.id.post_like_button);
        }
    }
}
