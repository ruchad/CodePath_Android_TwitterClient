package ruchad.codepath.rdtweets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.adapters.TweetsAdapter;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.util.GridDividerDecoration;

public abstract class TimelineFragment extends Fragment {

    private ArrayList<Tweet> mTweets;
    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.fabCompose) FloatingActionButton fabCompose;
    private TweetsAdapter mTweetsAdapter;
    LinearLayoutManager mLinearLayoutManager;
    SwipeRefreshLayout swipeContainer;

    public TimelineFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTweets = new ArrayList<>();
        mTweetsAdapter = new TweetsAdapter(mTweets);
        populateTimeline(-1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, parent, false);
        ButterKnife.bind(this, view);

        //Compose FAB
        fabCompose.setBackgroundColor(0xffffff);

        //Pull to refresh
        swipeContainer = ButterKnife.findById(view, R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        //Recycler View
        rvTweets.setAdapter(mTweetsAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(mLinearLayoutManager);
        rvTweets.addItemDecoration(new GridDividerDecoration(getActivity()));
        return view;
    }

    public void addAllTweets(int position, List<Tweet> tweets){
        mTweetsAdapter.addAll(tweets);
        mTweetsAdapter.notifyItemRangeInserted(position, tweets.size());
        if(position==0)rvTweets.scrollToPosition(0);
    }

    public Tweet getTweet(int position){
        return mTweets.get(position);
    }

    public int getTweetsCount(){
        return mTweetsAdapter.getItemCount();
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    abstract void populateTimeline(long max_id);
}
