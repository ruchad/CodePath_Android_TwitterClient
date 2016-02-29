package ruchad.codepath.rdtweets.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Statuses {

    public Statuses(){}

    @SerializedName("statuses")
    public List<Tweet> statuses;
}
