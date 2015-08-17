package com.likefool.apps.aliketweetr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.likefool.apps.aliketweetr.R;
import com.likefool.apps.aliketweetr.RestApplication;
import com.likefool.apps.aliketweetr.adapters.TweetsArrayAdapter;
import com.likefool.apps.aliketweetr.helpers.EndlessScrollListener;
import com.likefool.apps.aliketweetr.helpers.RestClient;
import com.likefool.apps.aliketweetr.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity {

    private RestClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter tweetAdapter;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(tweetAdapter);
        client = RestApplication.getRestClient(); // singleton client
        tweetAdapter.clear();
        populateTimeline(1);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(page);
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                tweetAdapter.clear();
                populateTimeline(1);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void populateTimeline(int i) {
        client.getHomeTimeline(i, new JsonHttpResponseHandler() {
            // Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                tweetAdapter.addAll(Tweet.fromJSONArray(json));
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (response != null) {
                    Log.e("ERROR", "populateTimeline fail, response: " + response.toString());
                }
                Toast.makeText(getApplicationContext(), "Timeline Fail", Toast.LENGTH_SHORT).show();

                // Query ActiveAndroid for list of data
                List<Tweet> localTweets = new Select().from(Tweet.class)
                        .orderBy("CreatedAt DESC").limit(100).execute();
                tweetAdapter.addAll(localTweets);

                swipeContainer.setRefreshing(false);

            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.twitter_post) {
            goPost();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goPost() {
        Intent i = new Intent(this, PostActivity.class);
        startActivityForResult(i, 3458);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            tweetAdapter.clear();
            populateTimeline(1);
        }
    }
}
