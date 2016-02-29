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

import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.models.Statuses;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.restclient.TwitterClient;
import ruchad.codepath.rdtweets.util.EndlessRecyclerViewScrollListener;

public class SearchFragment extends TimelineFragment {
    private TwitterClient mTwitterClient;
    private static final int count = 8;
    private String query;

    public SearchFragment(){}

    public static SearchFragment getInstance(String query){
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTwitterClient = TwitterApplication.getRestClient();
        query = getArguments().getString("query");
        populateTimeline(-1);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);

        fabCompose.setVisibility(View.INVISIBLE);

        swipeContainer.setRefreshing(false);

        //recycler view
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(Long.parseLong(getTweet(getTweetsCount() - 1).id_str));
            }
        });

        return view;
    }

    /**
     * Populate time line tweets
     * @param max_id   REST req parameter to get tweets less than specified value.
     *                 Will be ignored if set to -1.
     */
    void populateTimeline(long max_id) {
        if (!isNetworkAvailable() || !isOnline()) Toast.makeText(getContext(), "No Network Connectivity", Toast.LENGTH_LONG);
        else {
            mTwitterClient.getTweetsbyQuery(new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("RDTweets", responseString);
                    int pos = getTweetsCount();
                    Gson gson = new GsonBuilder().create();
                    Statuses statuses = gson.fromJson(responseString, Statuses.class);
                    ArrayList<Tweet> tweets = new ArrayList<>(statuses.statuses);
                    addAllTweets(pos, tweets);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getContext(), "Cannot connect to Twitter (Rate Limit Exceeded).\nPlease try again in few minutes", Toast.LENGTH_LONG).show();
                    Log.e("RDTweets", "Error while retrieiving home line tweets : " + responseString);
                }
            }, query);
        }
    }
}
