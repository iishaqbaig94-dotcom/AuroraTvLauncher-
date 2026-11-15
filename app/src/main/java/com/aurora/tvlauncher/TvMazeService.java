
package com.aurora.tvlauncher;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TvMazeService {

    public static class Show {
        public String title;
        public String genre;
        public String year;
        public String description;
        public String posterUrl;

        public Show(String title, String genre, String year,
                    String description, String posterUrl) {
            this.title = title;
            this.genre = genre;
            this.year = year;
            this.description = description;
            this.posterUrl = posterUrl;
        }
    }

    public interface Callback {
        void onSuccess(ArrayList<Show> shows);
        void onError(String message);
    }

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    public static void loadShows(Callback callback) {

        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                    "https://api.tvmaze.com/shows?page=0"
                );

                connection = (HttpURLConnection)
                        url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(20000);
                connection.setRequestProperty(
                    "User-Agent", "AuroraTvLauncher/1.0"
                );

                int responseCode = connection.getResponseCode();

                if (responseCode != 200) {
                    throw new Exception(
                        "Server response: " + responseCode
                    );
                }

                InputStream stream =
                        connection.getInputStream();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, "UTF-8")
                );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                stream.close();

                JSONArray array =
                        new JSONArray(result.toString());

                ArrayList<Show> shows = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);

                    String title = item.optString(
                            "name", "Unknown Show"
                    );

                    String genre = "Drama";
                    JSONArray genres = item.optJSONArray("genres");

                    if (genres != null && genres.length() > 0) {
                        genre = genres.optString(0, "Drama");
                    }

                    String premiered =
                            item.optString("premiered", "");

                    String year = "";

                    if (premiered.length() >= 4) {
                        year = premiered.substring(0, 4);
                    }

                    String description =
                            item.optString("summary", "");

                    description = description
                            .replaceAll("<[^>]*>", "")
                            .replace("&amp;", "&")
                            .replace("&quot;", "\"")
                            .replace("&#39;", "'")
                            .trim();

                    String posterUrl = "";

                    JSONObject image = item.optJSONObject("image");

                    if (image != null) {
                        posterUrl = image.optString(
                                "medium", ""
                        );
                    }

                    shows.add(new Show(
                            title,
                            genre,
                            year,
                            description,
                            posterUrl
                    ));
                }

                mainHandler.post(() ->
                        callback.onSuccess(shows)
                );

            } catch (Exception e) {
                String message = e.getMessage();

                mainHandler.post(() ->
                        callback.onError(
                            message == null
                                ? "Unable to load TV shows"
                                : message
                        )
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}
