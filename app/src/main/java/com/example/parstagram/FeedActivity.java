package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;
    public static final String TAG = "FeedActivity";
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        rvPosts = findViewById(R.id.rvPosts);
        // lookup swipe container view and setup refresh listener
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // specify what type of data we want to query
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_USER);
                query.setLimit(20);
                // order posts by creation data (newest first)
                query.addDescendingOrder("createdAt");
                // start an asynchronous call for posts
                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "issue with getting posts", e);
                            return;
                        }
                        // for debugging, print every post description to log
                        for (Post post : posts) {
                            Log.i(TAG, "post: " + post.getDescription() + ", username: " +
                                    post.getUser().getUsername());
                        }
                        adapter.clear();
                        adapter.addAll(posts);
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
        // configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize array that will hold posts and create PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(this, allPosts);

        // set adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set layout manager
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // query posts
        queryPosts();
    }

    private void queryPosts() {
        // specify what type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        // order posts by creation data (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts", e);
                    return;
                }

                // for debugging, print every post description to log
                for (Post post : posts) {
                    Log.i(TAG, "post: " + post.getDescription() + ", username: " +
                            post.getUser().getUsername());
                }
                // save received posts to list and notify adapter of data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });

    }
}