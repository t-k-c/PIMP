package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import objects.Message;
import pi.novobyte.com.pimp.R;

public class CommentReviewAdapter extends RecyclerView.Adapter<CommentReviewAdapter.ViewHolder>{
    List<Message> messages;
    int page;
    public static int  PAGE_REVIEW = 1024;
    public static int PAGE_COMMENT = 21045;
    public CommentReviewAdapter(List<Message> messages,int page) {
        this.messages = messages;
        this.page = page;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_review_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.datetime.setText(message.date.split(" ")[0]+" @ "+message.date.split(" ")[1]);
        holder.shortname.setText("~"+message.source.username);
        holder.content.setText(message.message);
        if(page == PAGE_REVIEW){
//            you can do anything you want here for treatment
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
//My_user_name, 20/03/1998 22:54
        TextView datetime,content,shortname;
        ImageView icon;
         ViewHolder(View itemView) {
            super(itemView);
            shortname = itemView.findViewById(R.id.shortname);
            content = itemView.findViewById(R.id.review_content);
            datetime = itemView.findViewById(R.id.date_time);
//            icon = itemView.findViewById(R.id.review_icon);
        }
    }
}
