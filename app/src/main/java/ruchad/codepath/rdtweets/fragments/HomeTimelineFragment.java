package ruchad.codepath.rdtweets.fragments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.activeandroid.query.Select;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.restclient.TwitterClient;
import ruchad.codepath.rdtweets.util.EndlessRecyclerViewScrollListener;

public class HomeTimelineFragment extends TimelineFragment {

    private TwitterClient mTwitterClient;
    private int since_id=1;
    private static final int count=8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTwitterClient = TwitterApplication.getRestClient();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);

        //Floating action bar for composing tweet
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeFragment composeFragment = ComposeFragment.getInstance("");
                composeFragment.setTargetFragment(HomeTimelineFragment.this, 200);
                composeFragment.show(getFragmentManager(), "fragment_compose_tweet");
                composeFragment.setListener(new ComposeFragment.ComposeFragmentListener() {
                    @Override
                    public void onPostTweet(Tweet tweet) {
                        if (!isNetworkAvailable() || !isOnline()) {
                            Toast.makeText(getContext(), "No Network.Please try again later.", Toast.LENGTH_SHORT).show();
                        } else {
                            postTweet(tweet);
                            //ToDo: implement method to add single tweet
                            List<Tweet> tweets = new ArrayList<>();
                            tweets.add(tweet);
                            addAllTweets(0, tweets);
                            rvTweets.scrollToPosition(0);
                        }
                    }
                });
            }
        });


        //pull to refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(1, -1);
                swipeContainer.setRefreshing(false);
            }
        });

        //recycler view
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(-1, Long.parseLong(getTweet(getTweetsCount() - 1).id_str));
            }
        });
        return view;
    }

    void populateTimeline(int since_id, long max_id){
        this.since_id = since_id;
        populateTimeline(max_id);
    }


    /**
     * Populate time line tweets
     * @param max_id REST req parameter to get tweets less than specified value.
     *               Will be ignored if set to -1.
     */
    void populateTimeline(long max_id){
        if(!isNetworkAvailable() || !isOnline()) fetchFromDB();
        else {
            mTwitterClient.getHomeTimeline(new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("RDTweets", responseString);
                    int pos = getTweetsCount();
                    Gson gson = new GsonBuilder().create();
                    ArrayList<Tweet> tweets= new ArrayList<>(Arrays.asList(gson.fromJson(responseString, Tweet[].class)));
                    //SaveToDB
                    saveToDB(tweets);
                    addAllTweets(pos, tweets);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    fetchFromDB();
                    Toast.makeText(getContext(), "Cannot connect to Twitter (Rate Limit Exceeded).\nPlease try again in few minutes", Toast.LENGTH_LONG).show();
                    Log.e("RDTweets", "Error while retrieiving home line tweets : " + responseString);
                }
            }, count, since_id, max_id);
        }
    }

    /**
     * Posts the tweet composed by user
     * @param tweet
     */
    private void postTweet(Tweet tweet) {
            mTwitterClient.postTweet(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("RDTweets", "Unable to post the tweet! " + responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("RDTweets", "Successfully posted the tweet!" + responseString);
                }
            }, tweet);
    }

    private void saveToDB(ArrayList<Tweet> tweets) {
        for(Tweet tweet: tweets) {
            tweet.user.save();
            if(tweet.extended_entities!=null) {
                tweet.extended_entities.media.get(0).sizes.medium.save();
                tweet.extended_entities.media.get(0).sizes.save();
                tweet.extended_entities.media.get(0).save();
                tweet.extended_entities.save();
            }
            tweet.save();
        }
    }

    private void fetchFromDB(){
        List<Tweet> tweets = new Select().from(Tweet.class).execute();
        addAllTweets(getTweetsCount(), tweets);
    }
}
