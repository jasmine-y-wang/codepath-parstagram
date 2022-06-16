package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.activities.LoginActivity;
import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.adapters.ProfilePostsAdapter;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    public static final String TAG = "FeedActivity";
    public static final String KEY_PROFILE_PIC = "profilePic";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialize array that will hold posts and create PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(getContext(), allPosts);

        // get username
        TextView tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());

        ImageView ivProfilePic = view.findViewById(R.id.ivProfilePic);
        User currentUser = (User) ParseUser.getCurrentUser();
        ParseFile profilePic = currentUser.getPfp();
        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).circleCrop().into(ivProfilePic);
        } else {
            Glide.with(getContext()).load(R.drawable.profile_placeholder).circleCrop().into(ivProfilePic);
        }

        RecyclerView rvPosts = view.findViewById(R.id.rvPosts);
        // set adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set layout manager
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        // query posts
        queryPosts();

        Button btnLogout = view.findViewById(R.id.btnLogout);
        // logout functionality
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Log.i(TAG, "logout");
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }

    protected void queryPosts() {
        // specify what type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
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
            }
        });
    }
}