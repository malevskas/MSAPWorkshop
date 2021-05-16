package uk.ac.shef.oak.jobserviceexample;

import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Constants for the various components of the Books API request.
    //
    // Base endpoint URL for the Books API.
    private static final String BOOK_BASE_URL = "http://10.0.2.2:5000/getjobs";

    /**
     * Static method to make the actual query to the Books API.
     *
     * @return the JSON response string from the query.
     */
    static String getInfo() {

        // Set up variables for the try block that need to be closed in the
        // finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JSONString = "null";

        try {
            // Convert the URI to a URL,
            URL requestURL = new URL(BOOK_BASE_URL);

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
        // Write the final JSON response to the log
        Log.d(LOG_TAG, JSONString);
        return JSONString;
    }
}
