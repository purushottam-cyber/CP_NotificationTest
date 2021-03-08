package com.example.cpnotificationtest;

import android.app.LoaderManager;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Implementing the LoaderCallbacks in our activity is a little more complex.
 * First we need to say that EarthquakeActivity implements the LoaderCallbacks interface,
 * along with a generic parameter specifying what the loader will return (in this case an Earthquake).
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<User> {

    private TextView textView;
    private EditText userHandle;
    private Button getInButton;

    /**
     * @param userName is codeforces handel.
     * @param prefixUrl is a part of requested api call.
     * @param userInfoUrl is the proper API to get the user url.
     * @param REQUEST_TYPE for distuinguishing the type of API call , 1 here for user info
     */

    private static int USER_INFO_LOADER_ID = 1;
    private static final int REQUEST_TYPE = 1;

    private String userName;
    private String lastName = null;
    private final String prefixUrl = "https://codeforces.com/api/user.info?handles=";
    private String userInfoUrl;
    public User forAnnouncer = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        userHandle = (EditText) findViewById(R.id.userhandel);
        getInButton = (Button) findViewById(R.id.getinbutton);

        getInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userHandle.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(getApplicationContext(), "Enter a valid UserName", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lastName != userName) {

                    lastName = userName;
                    userInfoUrl = constructUserInfoUrl(userName);
                    USER_INFO_LOADER_ID++;
                    startLoader();

                }

            }
        });

        Log.i("After Loader ", String.valueOf(forAnnouncer.getMaxRating()));

    }

    private void startLoader() {
        // now check for connnectivity
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            android.app.LoaderManager loaderManager = getLoaderManager();
            /**
             *  Pass in this activity for the LoaderCallbacks parameter (3rd parameter) (which is valid
             *  because this activity implements the LoaderCallbacks interface).
             */
            loaderManager.initLoader(USER_INFO_LOADER_ID, null, this);
            Log.i("inside  Loader ", "Nullaaa");
        } else {
            Toast.makeText(this, " No Internet Connection ", Toast.LENGTH_SHORT);
        }
    }


    private String constructUserInfoUrl(String user) {

        String url = null;

        url = prefixUrl + user;

        return url;
    }


    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        return new CodeforcesLoader(this, userInfoUrl, REQUEST_TYPE);
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User data) {
        // if we have a valid user data then show it

        if (data == null) {
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(getApplicationContext(), data.getHandel(), Toast.LENGTH_SHORT).show();
        forAnnouncer = data;

        if (forAnnouncer.getMaxRating() != -1) {
            Intent intent = new Intent(getApplicationContext(), ResultAnnouncer.class);
            intent.putExtra("Name", forAnnouncer.getHandel());
            intent.putExtra("MaxRating", forAnnouncer.getMaxRating());
            intent.putExtra("Rank", forAnnouncer.getRank());
            startActivity(intent);

            Log.i("NAME", forAnnouncer.getHandel());
            Log.i("MaxRating", String.valueOf(forAnnouncer.getMaxRating()));
            Log.i("Status", "Got Here ");
        }

        if (loader != null) {    // because after getting into new activity we don't need the current loader
            getLoaderManager().destroyLoader(loader.getId());
        }

    }

    @Override
    public void onLoaderReset(Loader<User> loader) {
        return;
    }
}