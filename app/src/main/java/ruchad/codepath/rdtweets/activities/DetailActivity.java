package ruchad.codepath.rdtweets.activities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bumptech.glide.Glide;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.restclient.TwitterClient;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.ivUserDetailPic)ImageView ivDetailUserPic;
    @Bind(R.id.tvUserDetailName)TextView tvDetailUsername;
    @Bind(R.id.tvUserDetailHandle) TextView tvDetailUserhandle;
    @Bind(R.id.tvDetailTweet)TextView tvDetailTweet;
    @Bind(R.id.ivDetailImg) ImageView ivDetailImg;
    @Bind(R.id.tvDetailCreationTime)TextView tvCreationTime;
    @Bind(R.id.etReplyToTweet)EditText etReplyToTweet;
    Tweet.UserEntity mUser;
    TwitterClient mTwitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTwitterClient = new TwitterClient(this);
        getCurrentUser();

        //action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_action_twitter_logo_white_48);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Tweet");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        final Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ButterKnife.bind(DetailActivity.this);
        Glide.with(this).load(tweet.user.profile_image_url).into(ivDetailUserPic);
        tvDetailUsername.setText(tweet.user.name);
        tvDetailUserhandle.setText("@" + tweet.user.screen_name);
        tvDetailTweet.setText(tweet.text);
        ivDetailImg.setImageResource(0);
        if(tweet.extended_entities!=null && tweet.extended_entities.media!=null)
            Glide.with(getApplicationContext()).load(tweet.extended_entities.media.get(0).media_url).into(ivDetailImg);
        tvCreationTime.setText(tweet.created_at);
        etReplyToTweet.setHint(" Reply to " + tweet.user.name);
        etReplyToTweet.setOnKeyListener(new View.OnKeyListener() {
            //Post reply when user presses enter
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Tweet tweet1 = new Tweet();
                    tweet1.text = etReplyToTweet.getText().toString();
                    tweet1.in_reply_to_screen_name = tweet.user.screen_name;
                    tweet1.user = mUser;
                    tweet1.created_at = String.valueOf(System.currentTimeMillis());
                    postTweet(tweet1);
                    Toast.makeText(getApplicationContext(), "Replied to " + tweet.user.screen_name, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void getCurrentUser(){
        mTwitterClient.getVerifyCredentails(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("RDTweets", "Cannot get user details");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                mUser = gson.fromJson(responseString, Tweet.UserEntity.class);
            }
        });
    }

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
}
