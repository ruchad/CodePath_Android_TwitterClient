package ruchad.codepath.rdtweets.fragments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bumptech.glide.Glide;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.restclient.TwitterClient;

public class ComposeFragment extends DialogFragment{

    @Bind(R.id.ivClose) ImageView ivClose;
    @Bind(R.id.ivUserPic) ImageView ivUserPic;
    @Bind(R.id.tvInReplyTo)TextView tvInReplyTo;
    @Bind(R.id.etComposeTweet) EditText etComposeTweet;
    @Bind(R.id.tvCharCount) TextView tvCharCount;
    @Bind(R.id.btnTweet) Button btnTweet;
    @Bind(R.id.tvCFusername) TextView tvUsername;
    @Bind(R.id.tvCFuserhandle) TextView tvUserhandle;

    private ComposeFragmentListener listener;
    private TwitterClient mTwitterClient;
    private Tweet.UserEntity mUser;

    //Default constructor for Dialog Fragment
    public ComposeFragment(){
        this.listener=null;
    }

    public static ComposeFragment getInstance(String inReplyTo){
        ComposeFragment composeFragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("in_reply_to", inReplyTo);
        composeFragment.setArguments(args);
        return composeFragment;
    }

    public void setListener(ComposeFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        ButterKnife.bind(this, view);
        mTwitterClient = TwitterApplication.getRestClient();
        if(getArguments().getString("in_reply_to").isEmpty())tvInReplyTo.setVisibility(View.INVISIBLE);
        else {
            tvInReplyTo.setText("Reply: @" + getArguments().getString("in_reply_to"));
        }
        etComposeTweet.addTextChangedListener(mTextWatcher);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tweet tweet = new Tweet();
                tweet.retweeted= false;
                tweet.user = mUser;
                tweet.text = etComposeTweet.getText().toString();
                String twitter_format = "EEE MMM d HH:mm:ss Z y";
                tweet.created_at = String.valueOf(new SimpleDateFormat(twitter_format).format(System.currentTimeMillis()));
                if(listener!=null)listener.onPostTweet(tweet);
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getUserInformation();
    }

    private void getUserInformation(){
        mTwitterClient.getCurrentUserInfo(new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("RDTweets", "Failed to get user information! " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                mUser = gson.fromJson(responseString, Tweet.UserEntity.class);
                Glide.with(ivUserPic.getContext()).load(mUser.profile_image_url).into(ivUserPic);
                tvUsername.setText(mUser.name);
                tvUserhandle.setText("@" + mUser.screen_name);
            }
        });
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int len = s.length();
            if(len<=140) {
                btnTweet.setEnabled(true);
                tvCharCount.setText(String.valueOf(s.length()));
            }
            else {
                tvCharCount.setText(String.valueOf(140-s.length()));
                btnTweet.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public interface ComposeFragmentListener{
        void onPostTweet(Tweet tweet);
    }
}
