package ruchad.codepath.rdtweets.activities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.adapters.UsersListAdapter;
import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.models.UsersList;
import ruchad.codepath.rdtweets.restclient.TwitterClient;

public class UserListActivity extends AppCompatActivity {

    ArrayList<UsersList.UsersEntity> mUsers;
    UsersListAdapter adapter;
    TwitterClient mTwitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mTwitterClient = TwitterApplication.getRestClient();

        String type = getIntent().getStringExtra("type");
        String screenName = getIntent().getStringExtra("screen_name");

        //action bar
        //ToDo: Why is text color not working?
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_action_twitter_logo_white_48);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(" " + type);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mUsers = new ArrayList<>();
        adapter = new UsersListAdapter(this, mUsers);
        ListView lvUsers = (ListView) findViewById(R.id.lvUsersList);
        lvUsers.setAdapter(adapter);

        //Populate list
        if(type.equalsIgnoreCase("following")) populateFollowingList(screenName);
        else if(type.equalsIgnoreCase("followers")) populateFollowersList(screenName);
    }

    //ToDo: Get cursor from last item in list
    private void populateFollowingList(String screenName) {
        mTwitterClient.getFriendsList(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("RDTweets", "Error while retrieving following user list: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                UsersList usersList = gson.fromJson(responseString, UsersList.class);
                mUsers.addAll(usersList.users);
                adapter.notifyDataSetChanged();
            }
        }, screenName, -1);
    }

    //ToDo: Get cursor from last item in list
    private void populateFollowersList(String screenName) {
        mTwitterClient.getFollowersList(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("RDTweets", "Error while retrieving following user list: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                UsersList usersList = gson.fromJson(responseString, UsersList.class);
                mUsers.addAll(usersList.users);
                adapter.notifyDataSetChanged();
            }
        }, screenName, -1);
    }
}
