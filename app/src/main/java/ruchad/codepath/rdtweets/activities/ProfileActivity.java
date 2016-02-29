package ruchad.codepath.rdtweets.activities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bumptech.glide.Glide;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.fragments.UserTimelineFragment;
import ruchad.codepath.rdtweets.models.Tweet;
import ruchad.codepath.rdtweets.restclient.TwitterClient;

public class ProfileActivity extends AppCompatActivity {

    MenuItem miActionProgressItem;

    private TwitterClient mTwitterClient;
    private Tweet.UserEntity mUser;

    @Bind(R.id.ivProfilePicture) ImageView ivProfilePic;
    @Bind(R.id.tvUsername) TextView tvUsername;
    @Bind(R.id.tvTagLine) TextView tvTagLine;
    @Bind(R.id.tvFollwers) TextView tvFollowers;
    @Bind(R.id.tvFollowing) TextView tvFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        //action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String screenName = getIntent().getStringExtra("screen_name");

        //user info
        mTwitterClient = TwitterApplication.getRestClient();
        if(screenName==null) getCurrentUserInformation();
        else getUserInformation(screenName);

        //user timeline
        if(savedInstanceState==null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.getInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }


    private void getCurrentUserInformation(){
        mTwitterClient.getCurrentUserInfo(new TextHttpResponseHandler() {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                miActionProgressItem.setVisible(true);
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("RDTweets", "Failed to get user information! " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                miActionProgressItem.setVisible(false);
                Gson gson = new GsonBuilder().create();
                mUser = gson.fromJson(responseString, Tweet.UserEntity.class);

                //action bar
                getSupportActionBar().setTitle("@" + mUser.screen_name);
                getSupportActionBar().setDisplayShowTitleEnabled(true);

                //Header
                Glide.with(ivProfilePic.getContext()).load(mUser.profile_image_url).into(ivProfilePic);

                tvUsername.setText(mUser.name);

                tvFollowing.setText(mUser.friends_count + " FOLLOWING");

                tvFollowing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), UserListActivity.class);
                        intent.putExtra("type", "following");
                        intent.putExtra("screen_name", mUser.screen_name);
                        startActivity(intent);
                    }
                });
                tvFollowers.setText(mUser.followers_count + " FOLLOWERS");

                tvFollowers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), UserListActivity.class);
                        intent.putExtra("type", "followers");
                        intent.putExtra("screen_name", mUser.screen_name);
                        startActivity(intent);
                    }
                });

                tvTagLine.setText(mUser.description);
            }
        });
    }

    private void getUserInformation(String screenName){
        mTwitterClient.getUserInfo(new TextHttpResponseHandler() {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                miActionProgressItem.setVisible(true);
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("RDTweets", "Failed to get user information! " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                miActionProgressItem.setVisible(false);
                Gson gson = new GsonBuilder().create();
                mUser = gson.fromJson(responseString, Tweet.UserEntity.class);

                //action bar
                getSupportActionBar().setTitle("@" + mUser.screen_name);
                getSupportActionBar().setDisplayShowTitleEnabled(true);

                //Header
                Glide.with(ivProfilePic.getContext()).load(mUser.profile_image_url).into(ivProfilePic);

                tvUsername.setText(mUser.name);

                tvFollowing.setText(mUser.friends_count + " FOLLOWING");
                tvFollowing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                        intent.putExtra("type", "Following");
                        intent.putExtra("screen_name", mUser.screen_name);
                        startActivity(intent);
                    }
                });

                tvFollowers.setText(mUser.followers_count + " FOLLOWERS");
                tvFollowers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                        intent.putExtra("type", "Followers");
                        intent.putExtra("screen_name", mUser.screen_name);
                        startActivity(intent);
                    }
                });

                tvTagLine.setText(mUser.description);
            }
        }, screenName);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }
}
