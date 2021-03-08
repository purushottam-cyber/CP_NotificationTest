package com.example.cpnotificationtest;

import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * utility class to retrive required data as a list of string from given url
 */
public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * jsut a utility class having static methods , so need of constructors
     */

    private QueryUtils() {
    }

    public static User fetchUserInfo(String mUrl) {


        URL url = createURL(mUrl);

        String JasonResponse = null;

        try {
            JasonResponse = makeHttpRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User userInfo = extractInfoFromJson(JasonResponse);

        return userInfo;
    }

    public static SubmissionInfo fetchSubmissionInfo(String mUrl) {
        URL url = createURL(mUrl);

        String JasonResponse = null;

        try {
            JasonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SubmissionInfo submissionInfo = extractVerdictFromJson(JasonResponse);

        return submissionInfo;
    }

    private static SubmissionInfo extractVerdictFromJson(String jasonResponse) {
        if (TextUtils.isEmpty(jasonResponse))
            return null;
        /*
         JASON STRUCTURE
 {
   "status":"OK",
   "result":[
      {
         "id":108955528,
         "contestId":1494,
         "verdict":"WRONG_ANSWER",
         "testset":"TESTS",
         "passedTestCount":1,
         "timeConsumedMillis":15,
         "memoryConsumedBytes":0
      }
   ]
}
         */

        SubmissionInfo submissionInfo = new SubmissionInfo();

        try {
            JSONObject baseJasonResponse = new JSONObject(jasonResponse);
            //status
            String isSuccessful = baseJasonResponse.getString("status");
            Log.i("success", isSuccessful);

            if (isSuccessful.equalsIgnoreCase("FAILED")) {
                String reason = baseJasonResponse.getString("comment");

                Log.i("VERDICT FAILED ", reason);

                return submissionInfo;
            } else {
                JSONArray resultArray = baseJasonResponse.getJSONArray("result");

                JSONObject inside = resultArray.getJSONObject(0);

                JSONObject problem = inside.getJSONObject("problem");
                String verdict = "wait";

                if (inside.has("verdict") == false) {
                    return new SubmissionInfo();
                } else {
                    verdict = inside.getString("verdict");
                }
                int id = inside.getInt("id");
                int correctCount = inside.getInt("passedTestCount");
                String problemName = problem.getString("name");

                submissionInfo.setId(id);
                submissionInfo.setPassedCases(correctCount);
                submissionInfo.setVerdict(verdict);
                submissionInfo.setProblemName(problemName);
            }


        } catch (Exception e) {
            Log.e("QueryUtils", "Problem parsing the VERDICT JSON results", e);
        }

        return submissionInfo;
    }

    private static User extractInfoFromJson(String jasonResponse) {
        if (TextUtils.isEmpty(jasonResponse))
            return null;

        User userInfo = new User();
        try {

            JSONObject baseJasonResponse = new JSONObject(jasonResponse);
            //status
            String isSuccessful = baseJasonResponse.getString("status");
            Log.i("success", isSuccessful);
            userInfo.setStatus(isSuccessful);

            if (isSuccessful.equalsIgnoreCase("FAILED")) {
                String reason = baseJasonResponse.getString("comment");

                userInfo.setComment(reason);
                return userInfo;
            } else {
                JSONArray infoArray = baseJasonResponse.getJSONArray("result");

                // we have only info one 1 user

                /*
                {
         "contribution":0,
         "lastOnlineTimeSeconds":1614945066,
         "rating":1254,
         "friendOfCount":2,
         "titlePhoto":"//userpic.codeforces.com/1825902/title/2f3504194ae087bb.jpg",
         "rank":"pupil",
         "handle":"0NE_MORE_TIME",
         "maxRating":1362,
         "avatar":"//userpic.codeforces.com/1825902/avatar/c194cb6699fada90.jpg",
         "registrationTimeSeconds":1608059839,
         "maxRank":"pupil"
      }*/

                JSONObject currentInfo = infoArray.getJSONObject(0);

                userInfo.setContribution(currentInfo.getInt("contribution"));
                userInfo.setHandel(currentInfo.getString("handle"));
                userInfo.setMaxRating(currentInfo.getInt("maxRating"));
                userInfo.setRank(currentInfo.getString("rank"));

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the User JSON results", e);
        }

        // Return the list of userInfo
        Log.i("Tagger", userInfo.getHandel());
        return userInfo;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        if (url == null)
            return "";
        String JasonResponse = "";

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                JasonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryUtils", " Problem in retriving Jason Reasponse " + (urlConnection.getResponseCode()));
                return null;
            }


        } catch (IOException e) {
            Log.e("QUTIL ", "Problem in making connection ", e);
        } finally {

            if (urlConnection != null)
                urlConnection.disconnect();

            // Closing the input stream could throw an IOException, which is why
            // the makeHttpRequest(URL url) method signature specifies than an IOException
            // could be thrown.
            if (inputStream != null)
                inputStream.close();
        }

        return JasonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();

            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }
        }

        return result.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */

    private static URL createURL(String stringURL) {
        URL url = null;

        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
