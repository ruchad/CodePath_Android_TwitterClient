package ruchad.codepath.rdtweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.parceler.Parcel;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Parcel(analyze = Tweet.class)
@Table(name = "Tweet")
public class Tweet extends Model{

    public Tweet(){}

    @Column(name = "created_at")
    public String created_at;
    @Column(name="id_str", unique=true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String id_str;
    @Column(name="text")
    public String text;
    @Column(name="UserEntity", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public UserEntity user;
    @Column(name = "retweet_count")
    public int retweet_count;
    @Column(name="favorite_count")
    public int favorite_count;
    @Column(name="ExtendedEntitiesEntity", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public ExtendedEntitiesEntity extended_entities;
    @Column(name="favorited")
    public boolean favorited;
    @Column(name="retweeted")
    public boolean retweeted;
    @Column(name="in_reply_to_screen_name")
    public String in_reply_to_screen_name;

    @Parcel(analyze = UserEntity.class)
    @Table(name="UserEntity")
    public static class UserEntity extends Model{

        public UserEntity(){}

        @Column(name="id_str")
        public String id_str;
        @Column(name="name")
        public String name;
        @Column(name="screen_name")
        public String screen_name;
        @Column(name="profile_image_url")
        public String profile_image_url;
        @Column(name="notifications")
        public boolean notifications;
    }

    @Parcel(analyze = ExtendedEntitiesEntity.class)
    @Table(name="ExtendedEntitites")
    public static class ExtendedEntitiesEntity extends Model {

        public ExtendedEntitiesEntity(){}

        @Column(name="media")
        public List<MediaEntity> media;

        @Parcel(analyze = MediaEntity.class)
        @Table(name="MediaEntity")
        public static class MediaEntity extends Model{

            public MediaEntity(){};

            @Column(name="id_str")
            public String id_str;
            @Column(name="media_url")
            public String media_url;
            @Column(name = "type")
            public String type;
            @Column(name="SizesEntity",onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
            public SizesEntity sizes;

            @Parcel(analyze = SizesEntity.class)
            @Table(name="SizesEntity")
            public static class SizesEntity extends Model{
                public SizesEntity(){};

                @Column(name = "MediumEntity",onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
                public MediumEntity medium;

                @Parcel(analyze = MediaEntity.class)
                @Table(name="MediumEntity")
                public static class MediumEntity extends Model{
                    public MediumEntity(){};
                    @Column(name="w")
                    public int w;
                    @Column(name="h")
                    public int h;
                    @Column(name="resize")
                    public String resize;
                }
            }
        }
    }

    // Utility for relative time
    public String getRelativeTime() {
        final String twitter_date_format = "EEE MMM d HH:mm:ss Z y";
        final long oneWeekInMilliSeconds = 604800000;
        final long oneDayInMilliSeconds = 86400000;
        final long oneHourInMilliSeconds = 3600000;
        final long oneMinuteInMilliSeconds = 60000;
        try {
            SimpleDateFormat df = new SimpleDateFormat(twitter_date_format);
            Date date = df.parse(created_at);
            long timestamp = date.getTime();
            long relativeTime = System.currentTimeMillis() - timestamp;
            if (relativeTime >= oneWeekInMilliSeconds)
                return String.valueOf(relativeTime / oneWeekInMilliSeconds + "w");
            else if (relativeTime >= oneDayInMilliSeconds)
                return String.valueOf(relativeTime / oneDayInMilliSeconds + "d");
            else if (relativeTime >= oneHourInMilliSeconds)
                return String.valueOf(relativeTime / oneHourInMilliSeconds + "h");
            else
                return String.valueOf(relativeTime / oneMinuteInMilliSeconds + "m");
        } catch (ParseException e) {
            Log.e("RDTweets", "ERROR: Unable to parse date! " + e.getMessage());
            return "0m";
        }
    }
}
