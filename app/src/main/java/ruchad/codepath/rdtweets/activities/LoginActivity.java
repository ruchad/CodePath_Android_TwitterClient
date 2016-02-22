package ruchad.codepath.rdtweets.activities;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.restclient.TwitterClient;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // OAuth authenticated successfully
    // launch tweets Activity
    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginFailure(Exception e) {
        Toast.makeText(this, "Failed to authenticate to Twitter!", Toast.LENGTH_SHORT).show();
        Log.e("RDTweets", e.toString());
        e.printStackTrace();
    }

    //Click handler for 'Connect To Twitter'
    public void loginToRest(View view){
        getClient().connect();
    }
}
