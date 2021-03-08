package com.example.cpnotificationtest;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.util.Log;


public class CodeforcesLoader extends AsyncTaskLoader {

    private String mUrl;
    private int requestType;

    /**
     * Constructs a new {@link CodeforcesLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     * @param request to identify which type of jason to load , 1 = userinfo and 2 = submission info
     */

    public CodeforcesLoader(@NonNull Context context , String url , int request) {
        super(context);
        this.mUrl = url;
        this.requestType = request;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Object loadInBackground() {
        if(mUrl == null)
        {
            return  null;
        }


        if(requestType == 1)
        {
            // todo : get the userinfo from here

            User userInfo =  QueryUtils.fetchUserInfo(mUrl);
            return userInfo;
        }else
        {
            // todo : creating a submission class and fetching its data in new activity
            SubmissionInfo  verdictInfo =  QueryUtils.fetchSubmissionInfo(mUrl);
            Log.i("Loader class  Verdic",verdictInfo.getVerdict());
            return verdictInfo;
        }

    }
}
