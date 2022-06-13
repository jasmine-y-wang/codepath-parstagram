package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parstagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private Post post;
    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivImage;
    private TextView tvTimestamp;
    public static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.i(TAG, "showing details for post: " + post.getDescription() + ", username: " +
                post.getUser().getUsername());

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);
        tvTimestamp = findViewById(R.id.tvTimestamp);

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }
        // calculate relative date
        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvTimestamp.setText(timeAgo);

    }
}