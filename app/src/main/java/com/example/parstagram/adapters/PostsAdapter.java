package com.example.parstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.DetailActivity;
import com.example.parstagram.R;
import com.example.parstagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /*** Used for swipe refresh ***/
    // clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // add a list of items
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUsername;
        private TextView tvDescription;
        private ImageView ivImage;
        private TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(Post post) {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }
            // calculate relative date
            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvTimestamp.setText(timeAgo);
        }

        // on click, show DetailActivity for selected post
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.i("PostsAdapter", "clicked on post");
                Post post = posts.get(position);
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(i);
            }
        }
    }
}