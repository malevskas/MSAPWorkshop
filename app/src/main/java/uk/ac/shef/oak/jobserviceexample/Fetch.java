package uk.ac.shef.oak.jobserviceexample;

import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

public class Fetch extends AsyncTask<Void, Void, String> {

    Fetch() {}

    @Override
    protected String doInBackground(Void... strings) {
        String ping = NetworkUtils.getPing();
        try {
            Log.i("tag","rezultat: " + ping);
            JSONArray itemsArray = new JSONArray(ping);
            String date = null;
            String host = null;
            int count = 0;
            int packetSize = 0;
            int jobPeriod = 0;
            String jobType = null;

            JSONObject job = itemsArray.getJSONObject(0);
            try {
                date = job.getString("date");
                host = job.getString("host");
                count = job.getInt("count");
                packetSize = job.getInt("packetSize");
                jobPeriod = job.getInt("jobPeriod");
                jobType = job.getString("jobType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String result = "";
            if(jobType.equals("PING")) {
                // ping -c 3 -s 100 -i 120 10.0.2.2
                try {
                    String pingCMD = "ping -c "+count+" -s "+packetSize+" -i "+jobPeriod+" "+host;
                    Runtime r = Runtime.getRuntime();
                    Process p = r.exec(pingCMD);
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        result += inputLine;
                    }
                    Log.i("tag", result);
                    in.close();
                    Log.i("tag", "in.close");
                    return NetworkUtils.postPing(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // If onPostExecute() does not receive a proper JSON string,
            // update the UI to show failed results.
            e.printStackTrace();
        }
        return "Unexpected error!";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        /*try {
            Log.i("tag","rezultat: "+s);
            // Convert the response into a JSON object.
            //JSONObject jsonObject = new JSONObject(s);
            // Get the JSONArray of book items.
            JSONArray itemsArray = new JSONArray(s);

            // Initialize iterator and results fields.
            int i = 0;
            String date = null;
            String host = null;
            int count = 0;
            int packetSize = 0;
            int jobPeriod = 0;
            String jobType = null;
            // Look for results in the items array, exiting when both the
            // title and author are found or when all items have been checked.
            while (i < itemsArray.length()) {
                // Get the current item information.
                JSONObject job = itemsArray.getJSONObject(i);
                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    date = job.getString("date");
                    host = job.getString("host");
                    count = job.getInt("count");
                    packetSize = job.getInt("packetSize");
                    jobPeriod = job.getInt("jobPeriod");
                    jobType = job.getString("jobType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jobType.equals("PING")) {
                    // ping -c 3 -s 100 -i 120 10.0.2.2
                    try {
                        String pingCMD = "ping -c "+count+" -s "+packetSize+" -i "+jobPeriod+" "+host;
                        String pingResult = "";
                        Runtime r = Runtime.getRuntime();
                        Process p = r.exec(pingCMD);
                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            pingResult += inputLine;
                            Log.i("tag", pingResult);
                            NetworkUtils.postPing(pingResult);
                        }
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Move to the next item.
                i++;
            }
        } catch (Exception e) {
            // If onPostExecute() does not receive a proper JSON string,
            // update the UI to show failed results.
            e.printStackTrace();
        }*/
    }
}