package ruchad.codepath.rdtweets.restclient;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import ruchad.codepath.rdtweets.models.Tweet;


public class TwitterClient extends OAuthBaseClient{

    public static final Class<? extends Api> REST_API_CLASS= TwitterApi.class;
    public static final String REST_URL="https://api.twitter.com/1.1/";
    public static final String REST_CONSUMER_KEY="gCLjTllJknBkoKspn7ptHUgFq";
    public static final String REST_CONSUMER_SECRET="SmDY4SWtQHCPfxiH9wRDnL1YwPdMwcBMixd7Mh5TCRiYGT5VL1";
    public static final String REST_CALLBACK_URL="oauth://cprdtweets";

    public static final String HOME_TIMELINE_URL = "statuses/home_timeline.json";
    public static final String VERIFY_CREDENTIALS = "account/verify_credentials.json";
    public static final String POST_TWEET = "statuses/update.json";

    public TwitterClient(Context c) {
        super(c, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    //Method for getting home timeline
    public void getHomeTimeline(AsyncHttpResponseHandler handler, int count, int since_id, long max_id){
        String apiURL = getApiUrl(HOME_TIMELINE_URL);
        RequestParams params = new RequestParams();
        params.put("count", count);
        if(since_id!=-1) params.put("since_id", since_id);
        if(max_id!=-1) params.put("max_id", max_id);
        getClient().get(apiURL, params, handler);
    }

    //Method to get user details
    public void getVerifyCredentails(AsyncHttpResponseHandler handler){
        String apiUrL= getApiUrl(VERIFY_CREDENTIALS);
        getClient().get(apiUrL, null, handler);
    }

    //Method for composing a tweet
    public void postTweet(AsyncHttpResponseHandler handler, Tweet tweet){
        String apiURL = getApiUrl(POST_TWEET);
        RequestParams params = new RequestParams();
        params.put("status", tweet.text);
        getClient().post(apiURL, params, handler);
    }
}
