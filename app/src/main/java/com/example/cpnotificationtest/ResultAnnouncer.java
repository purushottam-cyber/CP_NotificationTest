package com.example.cpnotificationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

// we need this API call :- https://codeforces.com/api/user.status?handle=0NE_MORE_TIME&from=1&count=1

public class ResultAnnouncer extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SubmissionInfo> {

    private TextView userHandel;
    private TextView Rating;
    private TextView Rank;
    private TextView juryVerdict;
    private TextView juryID;
    private TextView poblemName;
    private TextView passedTest;

    /**
     * @param userName = the user handel from main activity
     * @param userRank = the Rank from user
     * @param userRating = the Rating of user
     * @param previousSubmissionId to make sure we alert user only for new submission
     * @param completeUrl the URL for API call of submission
     * @param loaderId to make mutiiple loader callbacks
     * @param UPDATE_AFTER is the delay tiem for timer task;
     */
    private String userName;
    private String userRank;
    private int userRating;
    private static final String PRE_URL = "https://codeforces.com/api/user.status?handle=";
    private static final String POST_URL = "&from=1&count=1";
    private static final int UPDATE_AFTER = 2000;
    private static final int REQUEST_TYPE = 2;
    private int loaderId = 100;
    private MediaPlayer mMediaPlayer;

    private int previousSubmissionId = -1;

    private String completeUrl;

    /**
     * This is the oncompletion media call back method which we are declearing globally so that we dont
     * have to create it again and for every submission
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_announcer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /**
         *                          intent.putExtra("Name",forAnnouncer.getHandel());
         *                         intent.putExtra("MaxRating",forAnnouncer.getMaxRating());
         *                         intent.putExtra("Rank",forAnnouncer.getRank());
         *
         */

        userHandel = (TextView) findViewById(R.id.Name);
        Rating = (TextView) findViewById(R.id.Rating);
        Rank = (TextView) findViewById(R.id.Rank);
        juryVerdict = (TextView) findViewById(R.id.juryVerdict);
        juryID = (TextView) findViewById(R.id.juryID);
        poblemName = (TextView)findViewById(R.id.problemName);
        passedTest = (TextView)findViewById(R.id.passedTest);

        Intent intent = getIntent();

        userName = intent.getStringExtra("Name");
        userRank = intent.getStringExtra("Rank");
        userRating = intent.getIntExtra("MaxRating", -1);


        userHandel.setText(userName);
        Rating.setText(String.valueOf(userRating));
        Rank.setText(userRank);

        completeUrl = getCompleteUrl(userName);

        Timer mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startLoader();
            }
        }, 0, UPDATE_AFTER);

    }

    private void startLoader() {
        // now check for connnectivity
        ++loaderId;
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
            loaderManager.initLoader(loaderId, null, this);

        } else {
            Toast.makeText(this, " No Internet Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCompleteUrl(String userName) {
        String Url = PRE_URL + userName + POST_URL;
        return Url;
    }

    private void respondAccoringToVerdict(SubmissionInfo data) {
        juryVerdict.setText(data.getVerdict());
        juryID.setText(String.valueOf(data.getId()));
        poblemName.setText(data.getProblemName());
        passedTest.setText(String.valueOf(data.getPassedCases()));


        int audioResourceID;

        String juryVerdict = data.getVerdict();

        switch (juryVerdict) {
            case "IDLENESS_LIMIT_EXCEEDED":
                audioResourceID = R.raw.buffer;
                break;
            case "COMPILATION_ERROR":
                audioResourceID = R.raw.compilation_error;
                break;
            case "FAILED":
                audioResourceID = R.raw.failed;
                break;
            case "MEMORY_LIMIT_EXCEEDED":
                audioResourceID = R.raw.mle;
                break;
            case "OK":
                audioResourceID = R.raw.ok;
                break;
            case "RUNTIME_ERROR":
                audioResourceID = R.raw.re;
                break;
            case "SKIPPED":
                audioResourceID = R.raw.skipped;
                break;
            case "TIME_LIMIT_EXCEEDED":
                audioResourceID = R.raw.tle;
                break;
            case "WRONG_ANSWER":
                audioResourceID = R.raw.wa;
                break;
            default:
                audioResourceID = R.raw.unknown_error;
        }

        mMediaPlayer = MediaPlayer.create(ResultAnnouncer.this, audioResourceID);
        mMediaPlayer.start();

        // when the mediaPlayer reaches at the end() state we should clean up the resources
        /**
         * @param mCompletionListener is a mediaPlayer completion callback method which we had setup globally
         */
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            // since we don't need this media player anymore
            mMediaPlayer.release();

            //set the media player back to null, which indicates that media player is currently
            // not ready to play anything
            mMediaPlayer = null;
        }
    }

    @Override
    public Loader<SubmissionInfo> onCreateLoader(int id, Bundle args) {
        return new CodeforcesLoader(this, completeUrl, REQUEST_TYPE);
    }

    @Override
    public void onLoadFinished(Loader<SubmissionInfo> loader, SubmissionInfo data) {
        if (data == null) {
            Toast.makeText(this, "Problem in getting VERDICT", Toast.LENGTH_SHORT).show();
            return;
        }

        String current = data.getVerdict();

        if(current.equalsIgnoreCase("wait"))return;;

        if (current.equalsIgnoreCase("TESTING")) {
            juryVerdict.setText("TESTING...");
        } else if (previousSubmissionId == -1) {
            juryVerdict.setText("wishing you High Ratings");
            previousSubmissionId = data.getId();
        } else if (data.getId() != previousSubmissionId) {
            respondAccoringToVerdict(data);
            previousSubmissionId = data.getId();
        }

        if (loader != null) {    // because after getting into new activity we don't need the current loader
            getLoaderManager().destroyLoader(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<SubmissionInfo> loader) {
        return;
    }
}