package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.R;
import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    public static final String TAG = "FeedActivity";
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        // initialize array that will hold posts and create PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);

        // set adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayoutManager);

        // lookup swipe container view and setup refresh listener
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // specify what type of data we want to query
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_USER);
                query.include(Post.KEY_LIKED_BY);
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

        // endless scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allPosts.get(allPosts.size() - 1).getCreatedAt());
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
    }

    protected void queryPosts(Date startDate) {
        // specify what type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_LIKED_BY);
        query.setLimit(20);
        if (startDate != null) {
            query.whereLessThan(Post.KEY_CREATED_AT, startDate);
        }
        // order posts by creation data (newest first)
        query.addDescendingOrder(Post.KEY_CREATED_AT);
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
                swipeContainer.setRefreshing(false); // hide the loading spinner for refresh
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FeedFragment", "onResume");
        queryPosts(null);
    }
}