package ruchad.codepath.rdtweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.fragments.SearchFragment;

public class SearchActivity extends AppCompatActivity {

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        query = getIntent().getStringExtra("query");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SearchFragment searchFragment = SearchFragment.getInstance(query);
        ft.replace(R.id.flSearchResults, searchFragment);
        ft.commit();
    }
}
