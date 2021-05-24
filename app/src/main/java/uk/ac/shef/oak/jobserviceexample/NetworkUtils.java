package uk.ac.shef.oak.jobserviceexample;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Constants for the various components of the Books API request.
    //
    // Base endpoint URL for the Books API.
    public static boolean checkDevice() {
        if(Build.FINGERPRINT.contains("generic")) {
            return true; // emulator
        }
        else
            return false; // hardware
    }
    private static final String EMULATOR_URL = "http://10.0.2.2:5000/getjobs/emulator";
    private static final String HARDWARE_URL = "http://192.168.0.104:5000/getjobs/hardware";
    private static final String EMULATOR_POST = "http://10.0.2.2:5000/postresults";
    private static final String HARDWARE_POST = "http://192.168.0.104:5000/postresults";

    /**
     * Static method to make the actual query to the Books API.
     *
     * @return the JSON response string from the query.
     */
    static String getPing() {

        // Set up variables for the try block that need to be closed in the
        // finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JSONString = "null";

        try {
            // Convert the URI to a URL,
            URL requestURL;
            if(checkDevice()) {
                requestURL = new URL(EMULATOR_URL);
            }
            else {
                requestURL = new URL(HARDWARE_URL);
            }

            // Open the network connection.
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Create a buffered reader from that input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a StringBuilder to hold the incoming response.
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                // Add the current line to the string.
                builder.append(line);

                // Since this is JSON, adding a newline isn't necessary (it won't
                // affect parsing) but it does make debugging a *lot* easier
                // if you print out the completed buffer for debugging.
                builder.append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  Exit without parsing.
                return null;
            }

            JSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the connection and the buffered reader.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JSONString;
    }

    static String postPing(String ping) {
        try {
            URL postURL;
            if(checkDevice()) {
                postURL = new URL(EMULATOR_POST);
            }
            else {
                postURL = new URL(HARDWARE_POST);
            }
            HttpURLConnection con = (HttpURLConnection) postURL.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "text/plain; charset=utf-8");
            con.setDoOutput(true);

            JSONObject obj = new JSONObject();
            obj.put("result", ping);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int code = con.getResponseCode();
            Log.i("responseCode", String.valueOf(code));

            if(code == HttpURLConnection.HTTP_OK) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.i("HTTPresponse", response.toString());
                    return response.toString();
                }
            }
            else {
                return "NOK";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Unexpected error!";
    }
}
