package pi.novobyte.com.pimp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.annotation.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapters.CommentReviewAdapter;
import adapters.ErrorAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.CookieData;
import data.GetRequestManager;
import objects.Message;
import objects.Post;
import objects.User;

import static commondata.ServerData.COMMENTS_LINK;

public class PostViewActvity extends AppCompatActivity {
RecyclerView recyclerView;
ImageView postimg,partnerimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view_actvity);
        recyclerView = (RecyclerView) findViewById(R.id.comments_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(PostViewActvity.this));

        final Post post = (Post) getIntent().getSerializableExtra("post_object");
        TextView desc = findViewById(R.id.partner_description);
        TextView name = findViewById(R.id.partner_name);
        TextView date = findViewById(R.id.post_date);
        TextView time = findViewById(R.id.post_time);
        TextView title = findViewById(R.id.post_title);
        TextView likes = findViewById(R.id.post_like_number);
        partnerimg = findViewById(R.id.partner_thumbnail);
        postimg =  findViewById(R.id.post_thumbnail);
        final TextView content = findViewById(R.id.post_content);
        desc.setText(post.site.description);
        name.setText(post.site.name);
        date.setText(post.date_time.split(" ")[0]);
        time.setText(post.date_time.split(" ")[1]);
        title.setText(post.title);
        likes.setText("("+post.likes_number+")");
        content.setText(post.content);

        final List<Message> messages =  new ArrayList<>();


        getCommentsDataAndPostImages(post);

        /*
        * partner_thumbnail
        * post_date
        * post_time
        * post_title
        * post_like_number
        * comments_rv
        * post_content
        * post_title
        * partner_name
        * partner_description
        * */
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_comment_layout,null);
        final EditText editText =  view.findViewById(R.id.comments_et);
        sweetAlertDialog.setCustomView(view);
         sweetAlertDialog.setConfirmButton("Comment", new SweetAlertDialog.OnSweetClickListener() {
             @Override
             public void onClick(SweetAlertDialog sweetAlertDialog) {
                 final String string =  editText.getText().toString();
                 if(string.isEmpty()){
                     editText.setError("Should not be empty");
                 }
                else{
                     sweetAlertDialog.cancel();
                     new Thread(new Runnable() {
                         @Override
                         public void run() {
                             String resp = GetRequestManager.getResponse(COMMENTS_LINK+
                             new Uri.Builder().appendQueryParameter("user_id", CookieData.getUserId(PostViewActvity.this))
                             .appendQueryParameter("action","push")
                             .appendQueryParameter("comment",string)
                             .appendQueryParameter("post_id",""+post.id)
                             );
                            try{
                                int rsp = Integer.parseInt(resp);
                                if(rsp==1){
                                    getCommentsDataAndPostImages(post);
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(PostViewActvity.this, "That didnt work", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }catch (NumberFormatException exception){
                                exception.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PostViewActvity.this, "That didnt work", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                         }
                     }).start();
                 }
             }
         });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.show();
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    void getCommentsDataAndPostImages(final Post post){
        final List<Message> messages =  new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {



                final  String resp = GetRequestManager.getResponse(COMMENTS_LINK+ new Uri.Builder()
                        .appendQueryParameter("post_id",post.id+"").build().toString());
                try{
                    JSONArray jsonArray = new JSONArray(resp);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        messages.add(new Message(User.getUserInstanceFromJSONData(jsonObject),
                                jsonObject.getString("comment"),jsonObject.getString("comment_time"),
                                jsonObject.getInt("comment_id"),
                                post.id));
                    }
                    if(messages.isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new ErrorAdapter("No comments yet"));
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new CommentReviewAdapter(messages,CommentReviewAdapter.PAGE_COMMENT));
                            }
                        });
                    }
                }catch (JSONException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            recyclerView.setAdapter(new ErrorAdapter("Error loading data"));
                            Snackbar.make((CoordinatorLayout)findViewById(R.id.coordinator),"Error loading data: "+
                                    GetRequestManager.decodeErrorMessage(resp),Snackbar.LENGTH_INDEFINITE).setAction(
                                    "Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getCommentsDataAndPostImages(post);
                                        }
                                    }
                            );
                        }
                    });
                    e.printStackTrace();
                }
                try {
                    final Bitmap bitmap1 = BitmapFactory.decodeStream( new URL(post.thumbnail).openConnection().getInputStream());
                    final Bitmap bitmap2 = BitmapFactory.decodeStream( new URL(post.site.thumbnail).openConnection().getInputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            partnerimg.setImageBitmap(bitmap2);
                            postimg.setImageBitmap(bitmap1);
                        }
                    });
                } catch (IOException e) {
                   /* runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

//                            recyclerView.setAdapter(new ErrorAdapter("Error loading data"));
                            Snackbar.make((CoordinatorLayout)findViewById(R.id.coordinator),"Error loading images",Snackbar.LENGTH_INDEFINITE).setAction(
                                    "Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getCommentsDataAndPostImages(post);
                                        }
                                    }
                            );
                        }
                    });*/
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
