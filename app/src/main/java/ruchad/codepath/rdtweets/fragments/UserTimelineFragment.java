package ruchad.codepath.rdtweets.fragments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.restclient.TwitterClient;
import ruchad.codepath.rdtweets.util.EndlessRecyclerViewScrollListener;

public class UserTimelineFragment extends TimelineFragment {
    private TwitterClient mTwitterClient;
    private static final int count = 8;
    String screen_name;

    public static UserTimelineFragment getInstance(String screen_name){
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTwitterClient = TwitterApplication.getRestClient();
        screen_name = getArguments().getString("screen_name");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        fabCompose.setVisibility(View.INVISIBLE);

        //recycler view
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(screen_name, Long.parseLong(getTweet(totalItemsCount-1).id_str));
            }
        });
        return view;
    }


    void populateTimeline(String screen_name, long max_id) {
        this.screen_name = screen_name;
        populateTimeline(max_id);
    }

    /**
     * Populate time line tweets
     */
    void populateTimeline(long max_id) {
        if (!isNetworkAvailable() || !isOnline()) Toast.makeText(getContext(), "No Network Connectivity", Toast.LENGTH_LONG).show();
        else {
            mTwitterClient.getUserTimeline(new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("RDTweets", responseString);
                    int pos = getTweetsCount();
                    Gson gson = new GsonBuilder().create();
                    ArrayList<Tweet> tweets = new ArrayList<>(Arrays.asList(gson.fromJson(responseString, Tweet[].class)));
                    addAllTweets(pos, tweets);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getContext(), "Cannot connect to Twitter (Rate Limit Exceeded).\nPlease try again in few minutes", Toast.LENGTH_LONG).show();
                    Log.e("RDTweets", "Error while retrieiving user timeline tweets : " + responseString);
                }
            }, count, screen_name, max_id);
        }
    }
}
