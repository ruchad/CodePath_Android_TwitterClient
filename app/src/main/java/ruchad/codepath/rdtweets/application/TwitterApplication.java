package ruchad.codepath.rdtweets.application;

import android.content.Context;

import ruchad.codepath.rdtweets.restclient.TwitterClient;

public class TwitterApplication extends com.activeandroid.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterApplication.context=this;
    }

    public static TwitterClient getRestClient(){
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
    }
}
