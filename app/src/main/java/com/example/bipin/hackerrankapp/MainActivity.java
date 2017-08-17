package com.example.bipin.hackerrankapp;

import android.app.usage.UsageEvents;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG=MainActivity.class.getSimpleName();

    /** URL to query the KICKSTARTER dataset for information */
    private static final String KICK_REQUEST_URL=
            "http://starlord.hackerearth.com/kickstarter";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KickAsyncTask task =new KickAsyncTask();
        task.execute();
    }

    private void updateUi(Event kick){
        TextView title=(TextView)findViewById(R.id.title);
        title.setText(kick.title);

        TextView pleadge=(TextView)findViewById(R.id.pleadge);
        pleadge.setText(kick.pleadge);

        TextView backers=(TextView)findViewById(R.id.backers);
        backers.setText(kick.backers);

        TextView days=(TextView)findViewById(R.id.days);
        days.setText(kick.days);
    }



    private class KickAsyncTask extends AsyncTask<URL,Void,Event> {


        @Override
        protected Event doInBackground(URL... urls) {
            URL url = createUrl(KICK_REQUEST_URL);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
            }
            Event kick = extractFeatureFromJson(jsonResponse);
            return kick;
        }


        @Override
        protected void onPostExecute(Event kick) {
            if (kick == null) {
                return;
            }

            updateUi(kick);
        }


        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);

            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error while creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;

        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            return output.toString();
        }

        private Event extractFeatureFromJson(String kickJSON) {
            try {
                JSONArray baseArray = new JSONArray(kickJSON);
                if (baseArray.length() > 0) {

                    JSONObject first = baseArray.getJSONObject(0);

                    String title = first.getString("title");
                    String pleadge = first.getString("amt.pledged");
                    String backers = first.getString("num.backers");
                    int days = first.getInt("end.time");

                    return new Event(title, pleadge, backers, days);

                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem occured", e);
            }
            return null;
        }
    }


}
