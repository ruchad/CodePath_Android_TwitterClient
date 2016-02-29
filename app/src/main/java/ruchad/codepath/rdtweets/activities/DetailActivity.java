package ruchad.codepath.rdtweets.activities;

import com.bumptech.glide.Glide;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.fragments.ComposeFragment;
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

        etReplyToTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeFragment composeFragment = ComposeFragment.getInstance(tweet.user.screen_name);
                composeFragment.show(getSupportFragmentManager(), "fragment_compose_tweet");
                composeFragment.setListener(new ComposeFragment.ComposeFragmentListener() {
                    @Override
                    public void onPostTweet(Tweet postTweet) {
                        postTweet.in_reply_to_status_id = tweet.id_str;
                        postTweet(postTweet);
                    }
                });
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
