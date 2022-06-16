package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.adapters.CommentsAdapter;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    private RecyclerView rvComments;
    private List<Comment> mComments;
    private CommentsAdapter adapter;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // show back arrow in action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        post = getIntent().getParcelableExtra(Post.class.getSimpleName());

        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvDescription = findViewById(R.id.tvDescription);
        ImageView ivImage = findViewById(R.id.ivImage);
        TextView tvTimestamp = findViewById(R.id.tvTimestamp);
        TextView tvLikes = findViewById(R.id.tvLikes);
        ImageButton ibLike = findViewById(R.id.ibLike);
        ImageButton ibComment = findViewById(R.id.ibComment);
        rvComments = findViewById(R.id.rvComments);

        mComments = new ArrayList<>();
        adapter = new CommentsAdapter(this, mComments);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        // load comments for post
        refreshComments();

        // show like count
        tvLikes.setText(post.getLikesCount());

        if (post.isLikedByCurrentUser()) {
            ibLike.setBackgroundResource(R.drawable.ufi_heart_active);
        } else {
            ibLike.setBackgroundResource(R.drawable.ufi_heart);
        }

        try {
            tvUsername.setText(post.getUser().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvDescription.setText(post.getDescription());
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }
        // calculate relative date
        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvTimestamp.setText(timeAgo);

        // comment functionality
        ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to comment activity
                Intent i = new Intent(DetailActivity.this, CommentActivity.class);
                i.putExtra("post", post);
                startActivity(i);
            }
        });

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.isLikedByCurrentUser()) {
                    // unlike
                    post.unlike();
                    ibLike.setBackground(getDrawable(R.drawable.ufi_heart));
                } else {
                    // like
                    post.like();
                    ibLike.setBackground(getDrawable(R.drawable.ufi_heart_active));

                }
                tvLikes.setText(post.getLikesCount());
            }
        });

    }

    private void refreshComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.addAscendingOrder("createdAt");
        query.include(Comment.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e("DetailActivity", "issue with getting posts", e);
                    return;
                }
                mComments.clear();
                mComments.addAll(comments);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onRestart() {
        // fires when we come back from future activities
        super.onRestart();
        refreshComments();
    }

    // go back to feed after clicking back arrow
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}