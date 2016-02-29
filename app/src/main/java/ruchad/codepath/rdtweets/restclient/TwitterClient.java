package ruchad.codepath.rdtweets.restclient;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

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
    public static final String MENTIONS_TIMELINE_URL = "statuses/mentions_timeline.json";
    public static final String VERIFY_CREDENTIALS = "account/verify_credentials.json";
    public static final String GET_USER = "users/show.json";
    public static final String USER_TIMELINE = "statuses/user_timeline.json";
    public static final String POST_TWEET = "statuses/update.json";
    public static final String FOLLOWERS_LIST = "followers/list.json";
    public static final String FRIENDS_LIST = "friends/list.json";
    public static final String SEARCH_TWEETS_URL = "search/tweets.json";
    public static final String RETWEET_BASE = "statuses/retweet/";
    public static final String FAVORITE_URL = "favorites/create.json";
    public static final String REMOVE_FAVORITE_URL = "favorites/destroy.json";


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

    //Method for mentions timeline
    public void getMentionsTimeline(TextHttpResponseHandler handler, int count, long max_id) {
        String apiURL = getApiUrl(MENTIONS_TIMELINE_URL);
        RequestParams params = new RequestParams();
        params.put("count", count);
        if(max_id!=-1)params.put("max_id", max_id);
        getClient().get(apiURL, params, handler);
    }

    //Method to get current user details
    public void getCurrentUserInfo(AsyncHttpResponseHandler handler){
        String apiUrL= getApiUrl(VERIFY_CREDENTIALS);
        getClient().get(apiUrL, null, handler);
    }

    //Method to get user details
    public void getUserInfo(AsyncHttpResponseHandler handler, String screenName) {
        String apiUrl = getApiUrl(GET_USER);
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    //Method to get user timeline
    public void getUserTimeline(AsyncHttpResponseHandler handler, int count, String screenName, long max_id){
        String apiUrl = getApiUrl(USER_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", count);
        if(screenName!=null)params.put("screen_name", screenName);
        if(max_id!=-1)params.put("max_id", max_id);
        getClient().get(apiUrl, params, handler);
    }

    //Method for composing a tweet
    public void postTweet(AsyncHttpResponseHandler handler, Tweet tweet){
        String apiURL = getApiUrl(POST_TWEET);
        RequestParams params = new RequestParams();
        params.put("status", tweet.text);
        getClient().post(apiURL, params, handler);
    }

    //Method for Followers list
    public void getFollowersList(AsyncHttpResponseHandler handler, String screen_name, int cursor){
        String apiURL = getApiUrl(FOLLOWERS_LIST);
        RequestParams params = new RequestParams();
        params.put("screen_name", screen_name);
        params.put("cursor", cursor);
        getClient().get(apiURL, params, handler);
    }

    //Method for Friends list
    public void getFriendsList(AsyncHttpResponseHandler handler, String screen_name, int cursor){
        String apiURL = getApiUrl(FRIENDS_LIST);
        RequestParams params = new RequestParams();
        params.put("screen_name", screen_name);
        params.put("cursor", cursor);
        getClient().get(apiURL, params, handler);
    }

    //Method for searching tweets
    public void getTweetsbyQuery(AsyncHttpResponseHandler handler, String searchQuery){
        String apiURL = getApiUrl(SEARCH_TWEETS_URL);
        RequestParams params = new RequestParams();
        params.put("q", searchQuery);
        getClient().get(apiURL, params, handler);
    }

    //Method for retweet
    public void retweet(AsyncHttpResponseHandler handler, String id){
        String apiURL = getApiUrl(RETWEET_BASE + id + ".json");
        getClient().post(apiURL, null, handler);
    }

    //Method for favorite
    public void favorite(AsyncHttpResponseHandler handler, String id, boolean favorited) {
        String api_url;
        if (favorited)
            api_url = getApiUrl(REMOVE_FAVORITE_URL);
        else
            api_url = getApiUrl(FAVORITE_URL);

        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(api_url, params, handler);
    }
}
