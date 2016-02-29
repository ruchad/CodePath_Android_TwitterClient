package ruchad.codepath.rdtweets.adapters;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.parceler.Parcels;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;

import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.activities.DetailActivity;
import ruchad.codepath.rdtweets.activities.ProfileActivity;
import ruchad.codepath.rdtweets.application.TwitterApplication;
import ruchad.codepath.rdtweets.models.Tweet;

public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Tweet> tweets;

    public TweetsAdapter(List<Tweet> tweets){this.tweets = tweets;}

    //Inflate the layout
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetsView = inflater.inflate(R.layout.item_tweet, parent, false);
        viewHolder = new ViewHolder(context, tweetsView);
        return viewHolder;
    }

    //Populate the data from View Holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Tweet tweet = (Tweet) tweets.get(position);

        //Profile Pic
        ImageView ivProfilePic = viewHolder.ivProfilePic;
        String imageUri = tweet.user.profile_image_url;
        Glide.with(viewHolder.ivProfilePic.getContext()).load(imageUri).into(ivProfilePic);

        //Username
        TextView tvUsername = viewHolder.tvUsername;
        tvUsername.setText(tweet.user.name);

        //Userhandle
        TextView tvUserHandle = viewHolder.tvUserHandle;
        tvUserHandle.setText("@" + tweet.user.screen_name);

        //Retweeted`
        TextView tvRetweeted = viewHolder.tvRetweeted;
        if(tweet.retweeted) tvRetweeted.setText("@" + tweet.user.screen_name + " Retweeted");
        else tvRetweeted.setVisibility(View.GONE);

        //Relative Timestamp
        TextView tvCreationTime = viewHolder.tvCreationTime;
        tvCreationTime.setText(tweet.getRelativeTime());

        //Tweet
        TextView tvTweet = viewHolder.tvTweet;
        tvTweet.setText(tweet.text);

        //Image or Video
        ImageView ivEntity = viewHolder.ivEntity;
        VideoView vvVideo = viewHolder.vvVideo;
        ivEntity.setImageResource(0);
        if(tweet.extended_entities!=null && tweet.extended_entities.media!=null) {
            Tweet.ExtendedEntitiesEntity.MediaEntity media = tweet.extended_entities.media.get(0);
            String type = media.type;
            int width = media.sizes.medium.w;
            int height = media.sizes.medium.h;
            if(media.video_info!=null && media.video_info.variants!=null){
                vvVideo.setVideoURI(Uri.parse(media.video_info.variants.get(0).url));
                vvVideo.setZOrderOnTop(true);
                MediaController mediaController = new MediaController(vvVideo.getContext());
                mediaController.setAnchorView(vvVideo);
                vvVideo.setMediaController(mediaController);
                vvVideo.requestFocus();
                vvVideo.start();
            }else if(type.equalsIgnoreCase("photo") || type.equalsIgnoreCase("animated_gif")) {
                Glide.with(viewHolder.ivEntity.getContext()).load(media.media_url).override(width, height).into(ivEntity);
            }
        }

        //Reply
        TextView tvReply = viewHolder.tvReply;

        //Retweet
        TextView tvRetweets = viewHolder.tvRetweets;
        tvRetweets.setText(String.valueOf(tweet.retweet_count));

        //Like
        TextView tvLikes = viewHolder.tvLikes;
        tvLikes.setText(String.valueOf(tweet.favorite_count));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //View Holder for tweets
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivProfilePic;
        public TextView tvRetweeted;
        public TextView tvUsername;
        public TextView tvUserHandle;
        public TextView tvCreationTime;
        public TextView tvTweet;
        public ImageView ivEntity;
        public VideoView vvVideo;
        private TextView tvReply;
        private TextView tvRetweets;
        private TextView tvLikes;
        private Context context;


        public ViewHolder(final Context context, final View itemView){
            super(itemView);
            this.context = context;
            ivProfilePic = ButterKnife.findById(itemView, R.id.ivProfilePic);
            ivProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("screen_name", tvUserHandle.getText());
                    v.getContext().startActivity(intent);
                }
            });
            tvRetweeted = ButterKnife.findById(itemView, R.id.tvRetweeted);
            tvUsername = ButterKnife.findById(itemView, R.id.tvUsername);
            tvUserHandle = ButterKnife.findById(itemView, R.id.tvUserHandle);
            tvCreationTime = ButterKnife.findById(itemView, R.id.tvCreationTime);
            tvTweet = ButterKnife.findById(itemView, R.id.tvTweetText);
            ivEntity = ButterKnife.findById(itemView, R.id.ivTweetEntity);
            vvVideo = ButterKnife.findById(itemView, R.id.vvTweetVideo);
            tvReply = ButterKnife.findById(itemView, R.id.tvReply);
            tvRetweets = ButterKnife.findById(itemView, R.id.tvRetweet);


            //ToDo: Handle response!
            tvRetweets.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(17)
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Tweet tweet = tweets.get(position);
                    TwitterApplication.getRestClient().retweet(new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("RDTweets", "Retweet failure: " + responseString);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Log.d("RDTweets", "Successfully retweeted");
                            tvRetweets.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_retweet_pressed, 0, 0, 0);
                            Toast.makeText(context, "Successfully retweeted!", Toast.LENGTH_SHORT).show();
                        }
                    },tweet.id_str);
                }
            });


            //ToDo: Handle response!
            tvLikes = ButterKnife.findById(itemView, R.id.tvLike);
            tvLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(17)
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Tweet tweet = tweets.get(position);
                    TwitterApplication.getRestClient().favorite(new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.d("RDTweets", "Successfully favorited");
                            Toast.makeText(context, "Successfully liked!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        }
                    }, tweet.id_str, tweet.favorited);
                    if(tweet.favorited == false)
                        tvLikes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like_on, 0, 0, 0);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Tweet tweet = tweets.get(position);
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }


    public void addAll(List<Tweet> input){
        tweets.addAll(input);
    }
}
