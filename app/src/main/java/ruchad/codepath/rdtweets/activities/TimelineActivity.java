package ruchad.codepath.rdtweets.activities;

import com.astuetz.PagerSlidingTabStrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.fragments.HomeTimelineFragment;
import ruchad.codepath.rdtweets.fragments.MentionsTimelineFragment;

public class TimelineActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitterlogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //setup viewpager
        ViewPager viewPager = ButterKnife.findById(this, R.id.viewpager);
        viewPager.setAdapter(new TimelinePagerAdapter(getSupportFragmentManager()));

        //PagerSliding TabStrip
        PagerSlidingTabStrip tabStrip = ButterKnife.findById(this, R.id.tabs);
        tabStrip.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public void onProfileView(MenuItem item) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Return order of fragments within the pager
    public class TimelinePagerAdapter extends FragmentPagerAdapter {
        private String[] tab_titles = {"Home", "Mentions"};

        public TimelinePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)return new HomeTimelineFragment();
            else if(position==1) return new MentionsTimelineFragment();
            else return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_titles[position];
        }

        @Override
        public int getCount() {
            return tab_titles.length;
        }
    }
}
