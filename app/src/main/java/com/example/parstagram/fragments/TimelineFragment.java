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
import com.example.parstagram.adapters.PostAdapter;
import com.example.parstagram.models.Post;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimelineFragment extends Fragment {
    public static final String TAG = "TimelineFragment";

    private LinearLayoutManager linearLayoutManager;
    private PostAdapter postAdapter;
    public List<Post> timeline;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView timelineRecyclerView;
    private EndlessRecyclerViewScrollListener scrollListener;

    public TimelineFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayoutManager = new LinearLayoutManager(getContext());
        timeline = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), timeline);
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);
        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(() -> {
            queryPosts();
            scrollListener.resetState();
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        timelineRecyclerView = getView().findViewById(R.id.timelineRecyclerView);
        // set the adapter on the recycler view
        timelineRecyclerView.setAdapter(postAdapter);
        // set the layout manager on the recycler view
        timelineRecyclerView.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMorePosts(timeline.get(timeline.size()-1).getCreatedAt());
            }
        };
        timelineRecyclerView.addOnScrollListener(scrollListener);

        // query posts from Parstagram
        queryPosts();
    }

    private void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_AUTHOR);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) { // Query has failed
                    Log.e(TAG, "Query failed", e);
                    return;
                }
                else {
                    //for (Post post: posts) Log.i(TAG, "Post: " + post.getCaption() + " by " + post.getAuthor().getUsername());
                    // Signal refresh has finished
                    swipeRefreshLayout.setRefreshing(false);
                    // Clear out old items before appending in the new ones
                    postAdapter.clear();
                    // save received posts to list and notify adapter of new data
                    postAdapter.addAll(posts);
                }
            }
        });
    }

    private void loadMorePosts(Date lastDate) {
        Log.e(TAG, "loading more posts older than " + lastDate);
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_AUTHOR);
        // limit query to latest 20 items
        query.setLimit(20);
        // query only posts that are older than lastDate
        query.whereLessThan("createdAt", lastDate);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) { // Query has failed
                    Log.e(TAG, "Query failed", e);
                    return;
                }
                else {
                    for (Post post: posts) Log.i(TAG, "Post: " + post.getCaption() + " by " + post.getAuthor().getUsername());
                    // Signal refresh has finished
                    swipeRefreshLayout.setRefreshing(false);
                    // save received posts to list and notify adapter of new data
                    postAdapter.addAll(posts);
                }
            }
        });
    }
}