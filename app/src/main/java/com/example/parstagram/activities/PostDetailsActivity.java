package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.models.Post;

public class PostDetailsActivity extends AppCompatActivity {
    public static final String TAG = "PostDetailsActivity";

    Post post;

    Toolbar detailsToolbar;
    TextView authorTextViewDetail;
    ImageView postImageViewDetail;
    TextView captionTextViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        detailsToolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(detailsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        authorTextViewDetail = findViewById(R.id.authorTextViewDetail);
        postImageViewDetail = findViewById(R.id.postImageViewDetail);
        captionTextViewDetail = findViewById(R.id.captionTextViewDetail);

        // Unwrap the post that was passed in by the intent
        post = (Post) getIntent().getExtras().getSerializable(Post.class.getSimpleName());
        Log.d(TAG, "Showing details for post by " + post.getAuthor() + " " + post.getImage());

        authorTextViewDetail.setText(post.getAuthor().getUsername());
        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(postImageViewDetail);
        captionTextViewDetail.setText(post.getCaption());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}