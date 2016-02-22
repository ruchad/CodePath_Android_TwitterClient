package ruchad.codepath.rdtweets.activities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.activeandroid.query.Select;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.adapters.TweetsAdapter;
import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.fragments.ComposeFragment;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.restclient.TwitterClient;
import ruchad.codepath.rdtweets.util.EndlessRecyclerViewScrollListener;
import ruchad.codepath.rdtweets.util.GridDividerDecoration;

public class TimelineActivity extends AppCompatActivity{

    private ArrayList<Tweet> mTweets;
    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.fabCompose)  FloatingActionButton fabCompose;
    private TweetsAdapter mTweetsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private TwitterClient mTwitterClient;
    private static final int since_id=1;
    private static final int count=8;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(TimelineActivity.this);

        //action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_action_twitter_logo_white_48);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Floating Action Bar
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeFragment composeFragment = ComposeFragment.getInstance("Compose Tweet");
                composeFragment.show(getFragmentManager(), "fragment_compose_tweet");
                composeFragment.setListener(new ComposeFragment.ComposeFragmentListener() {
                    @Override
                    public void onPostTweet(Tweet tweet) {
                        if(!isNetworkAvailable()||!isOnline()){
                            Toast.makeText(getApplicationContext(), "No Network.Please try again later.", Toast.LENGTH_SHORT).show();
                        }else {
                            postTweet(tweet);
                            mTweets.add(0, tweet);
                            mTweetsAdapter.notifyItemInserted(0);
                            rvTweets.scrollToPosition(0);
                        }
                    }
                });
            }
        });
        fabCompose.setBackgroundColor(0xffffff);

        // Pull to refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRefreshedTweets(count, since_id, -1);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //Setup recycler view
        mTweets = new ArrayList<>();
        mTweetsAdapter = new TweetsAdapter(mTweets);
        rvTweets.setAdapter(mTweetsAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(mLinearLayoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(count, -1, Long.parseLong(mTweets.get(mTweets.size()-1).id_str));
            }
        });
        rvTweets.addItemDecoration(new GridDividerDecoration(this));
        mTwitterClient = TwitterApplication.getRestClient();

        //For the first set of homeline tweets use since_id
        populateTimeline(count, since_id, -1);
    }

    /**
     * Populate time line tweets
     * @param count Number of tweets to retrieve
     * @param since_id REST req parameter to specify the start id of tweets.
     *                 Will be ignored if set to -1
     * @param max_id REST req parameter to get tweets less than specified value.
     *               Will be ignored if set to -1.
     */
    private void populateTimeline(int count, int since_id, long max_id){
        if(!isNetworkAvailable() || !isOnline()) fetchFromDB();
        else {
            mTwitterClient.getHomeTimeline(new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("RDTweets", responseString);
                    int pos = mTweets.size();
                    Gson gson = new GsonBuilder().create();
                    ArrayList<Tweet> tweets= new ArrayList<>(Arrays.asList(gson.fromJson(responseString, Tweet[].class)));
                    //SaveToDB
                    saveToDB(tweets);
                    mTweets.addAll(tweets);
                    mTweetsAdapter.notifyItemRangeInserted(pos, mTweets.size());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    fetchFromDB();
                    Toast.makeText(getApplicationContext(), "Cannot connect to Twitter (Rate Limit Exceeded).\nPlease try again in few minutes", Toast.LENGTH_LONG).show();
                    Log.e("RDTweets", "Error while retrieiving home line tweets : " + responseString);
                }
            }, count, since_id, max_id);
        }
    }

    /**
     * Retrieve new tweets when the user pulls to referesh.
     * Clears the old tweets and populates with new tweets
     * @param count
     * @param since_id
     * @param max_id
     */
    private void fetchRefreshedTweets(int count, int since_id, long max_id){
        mTweets.clear();
        mTweetsAdapter.notifyDataSetChanged();
        populateTimeline(count, since_id, max_id);
        swipeContainer.setRefreshing(false);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }


    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
        mTweets.addAll(tweets);
        mTweetsAdapter.notifyDataSetChanged();
    }
}
