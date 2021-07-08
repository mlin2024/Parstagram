package com.example.parstagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.models.Post;
import com.example.parstagram.R;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context context;
    public List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView;
        public ImageView postImageView;
        public TextView captionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
            captionTextView = itemView.findViewById(R.id.captionTextView);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            authorTextView.setText(post.getAuthor().getUsername());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(postImageView);
            }
            captionTextView.setText(post.getCaption());
        }
    }
}