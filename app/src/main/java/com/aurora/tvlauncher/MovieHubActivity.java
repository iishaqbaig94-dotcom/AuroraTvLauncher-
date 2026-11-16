package com.aurora.tvlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieHubActivity extends Activity {

    private LinearLayout content;
    private View initialFocus;

    private String mode = "MOVIES";
    private String selectedGenre = "All";
    private String searchQuery = "";

    private final int CYAN = Color.rgb(220, 225, 232);
private final int PURPLE = Color.rgb(75, 80, 88);
private final int DARK = Color.rgb(12, 14, 17);
private final int CARD = Color.rgb(30, 33, 38);

    private static final ExecutorService posterExecutor =
        Executors.newFixedThreadPool(3);

    private static final Handler mainHandler =
        new Handler(Looper.getMainLooper());

    private final String[] genres = {
        "All", "Action", "Comedy", "Sci-Fi",
        "Thriller", "Drama", "Animation", "Adventure"
    };

    private static class MediaItem {
        String title;
        String type;
        String genre;
        String year;
        String description;
        String posterUrl = "";

        MediaItem(String title, String type, String genre,
                  String year, String description) {
            this.title = title;
            this.type = type;
            this.genre = genre;
            this.year = year;
            this.description = description;
        }
    }

    private final ArrayList<MediaItem> catalogue = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String received = getIntent().getStringExtra("hub_mode");
        if (received != null) mode = received;

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);

        loadCatalogue();

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(DARK);
        scroll.setFocusable(false);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(35, 25, 35, 35);

        scroll.addView(content);
        setContentView(scroll);

        render();
        loadOnlineShows();
    }

    private void loadCatalogue() {
        catalogue.clear();

        catalogue.add(new MediaItem(
            "The Dark Knight", "MOVIES", "Action", "2008",
            "A masked hero faces a dangerous criminal mastermind in Gotham City."
        ));
        catalogue.add(new MediaItem(
            "Inception", "MOVIES", "Sci-Fi", "2010",
            "A skilled dream thief takes on an extraordinary mission."
        ));
        catalogue.add(new MediaItem(
            "Interstellar", "MOVIES", "Sci-Fi", "2014",
            "Explorers travel beyond Earth in search of a future for humanity."
        ));
        catalogue.add(new MediaItem(
            "The Matrix", "MOVIES", "Action", "1999",
            "A computer hacker discovers that reality is not what it seems."
        ));
        catalogue.add(new MediaItem(
            "Joker", "MOVIES", "Drama", "2019",
            "A troubled comedian struggles to find his place in society."
        ));
        catalogue.add(new MediaItem(
            "Spider-Man: Into the Spider-Verse", "MOVIES",
            "Animation", "2018",
            "A young hero discovers a world of alternate Spider-people."
        ));
        catalogue.add(new MediaItem(
            "The Martian", "MOVIES", "Adventure", "2015",
            "An astronaut fights to survive after being stranded on Mars."
        ));
        catalogue.add(new MediaItem(
            "John Wick", "MOVIES", "Action", "2014",
            "A retired hitman is pulled back into a dangerous underworld."
        ));
        catalogue.add(new MediaItem(
            "The Hangover", "MOVIES", "Comedy", "2009",
            "Friends try to piece together a wild night in Las Vegas."
        ));
        catalogue.add(new MediaItem(
            "A Quiet Place", "MOVIES", "Thriller", "2018",
            "A family must stay silent to survive a mysterious threat."
        ));

        catalogue.add(new MediaItem(
            "Stranger Things", "TV SHOWS", "Sci-Fi", "2016",
            "A group of friends uncover mysterious events in their town."
        ));
        catalogue.add(new MediaItem(
            "Sherlock", "TV SHOWS", "Drama", "2010",
            "A brilliant detective solves unusual cases in modern London."
        ));
        catalogue.add(new MediaItem(
            "The Last of Us", "TV SHOWS", "Adventure", "2023",
            "Two survivors travel through a dangerous post-apocalyptic world."
        ));
        catalogue.add(new MediaItem(
            "Wednesday", "TV SHOWS", "Comedy", "2022",
            "A clever young student investigates strange mysteries."
        ));
        catalogue.add(new MediaItem(
            "Breaking Bad", "TV SHOWS", "Drama", "2008",
            "A chemistry teacher enters a dangerous criminal world."
        ));
        catalogue.add(new MediaItem(
            "Dark", "TV SHOWS", "Thriller", "2017",
            "A disappearance reveals secrets connecting four families."
        ));
        catalogue.add(new MediaItem(
            "The Mandalorian", "TV SHOWS", "Adventure", "2019",
            "A lone bounty hunter travels through a distant galaxy."
        ));
        catalogue.add(new MediaItem(
            "One Piece", "TV SHOWS", "Action", "1999",
            "A young pirate and his crew search for a legendary treasure."
        ));
    }

    private void render() {
    String previousFocusKey = getFocusKey(getCurrentFocus());

    content.removeAllViews();
    initialFocus = null;

        addTopBar();

        addHeading("EXPLORE YOUR UNIVERSE");

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);

        addTab(tabs, "🎬  MOVIES", "MOVIES");
        addTab(tabs, "📺  TV SHOWS", "TV SHOWS");

        content.addView(tabs);

        Button search = makeButton(
            "⌕   SEARCH MOVIES & TV SHOWS"
        );
        search.setTextSize(16);
        search.setBackground(makeBackground(CARD, CYAN));
        search.setOnClickListener(v -> showSearch());

        LinearLayout.LayoutParams searchParams =
            new LinearLayout.LayoutParams(-1, 60);
        searchParams.setMargins(0, 12, 0, 8);
        content.addView(search, searchParams);

        if (!searchQuery.isEmpty()) {
            addInfo("Search: " + searchQuery);

            Button clear = makeButton("✕  CLEAR SEARCH");
            clear.setOnClickListener(v -> {
                searchQuery = "";
                render();
            });
            content.addView(clear);
        }

        if ("CONTINUE".equals(mode)) {
            addHeading("CONTINUE WATCHING");
            addInfo(
                "Viewing history is not connected yet. " +
                "Open a streaming service below to continue watching."
            );
        } else {
            addHeading("BROWSE BY GENRE");

            LinearLayout genreRow = new LinearLayout(this);
            genreRow.setOrientation(LinearLayout.HORIZONTAL);

            for (String genre : genres) {
                addGenre(genreRow, genre);
            }

            HorizontalScrollView genreScroll =
                new HorizontalScrollView(this);
            genreScroll.setHorizontalScrollBarEnabled(false);
            genreScroll.setFocusable(false);
            genreScroll.addView(genreRow);
            content.addView(genreScroll);

            String heading = selectedGenre.equals("All")
                ? "FEATURED " + mode
                : selectedGenre.toUpperCase() + " " + mode;

            addHeading(heading);
            addCatalogueItems();

            if ("TV SHOWS".equals(mode)) {
                addInfo("TV show data and images provided by TVmaze.");
            }
        }

        addHeading("STREAMING COLLECTION");
        addInfo(
            "Open your installed entertainment apps directly from Aurora."
        );
        addStreamingApps();

        addHeading("INFINITY EXPERIENCE");

        addFeatureCard(
            "∞  CINEMATIC ENTERTAINMENT",
            "One premium destination for your favourite entertainment apps."
        );

        addFeatureCard(
            "✦  SMART CONTENT HUB",
            "Browse your offline catalogue and discover TV shows online."
        );

        addInfo(
            "Movies use the built-in sample catalogue. TV show listings " +
            "and posters load from TVmaze when internet is available."
        );

        Button back = makeButton("‹   BACK TO AURORA HOME");
        back.setTextSize(16);
        back.setBackground(makeBackground(CARD, CYAN));
        back.setOnClickListener(v -> finish());

        LinearLayout.LayoutParams backParams =
            new LinearLayout.LayoutParams(-1, 65);
        backParams.setMargins(0, 25, 0, 10);
        content.addView(back, backParams);

        if (initialFocus == null) initialFocus = back;

        content.post(() -> {
    View restore = findFocusableByKey(
        content, previousFocusKey
    );

    if (restore != null) {
        restore.requestFocus();
    } else if (initialFocus != null) {
        initialFocus.requestFocus();
    }
});
}

    
private void loadOnlineShows() {
    TvMazeService.loadShows(new TvMazeService.Callback() {

        @Override
        public void onSuccess(ArrayList<TvMazeService.Show> shows) {

            View focusedView = getCurrentFocus();
            String focusKey = getFocusKey(focusedView);

            for (int i = catalogue.size() - 1; i >= 0; i--) {
                if ("TV SHOWS".equals(catalogue.get(i).type)) {
                    catalogue.remove(i);
                }
            }

            int limit = Math.min(shows.size(), 60);

            for (int i = 0; i < limit; i++) {
                TvMazeService.Show show = shows.get(i);

                MediaItem item = new MediaItem(
                    show.title,
                    "TV SHOWS",
                    show.genre,
                    show.year,
                    show.description
                );

                item.posterUrl = show.posterUrl;
                catalogue.add(item);
            }

            if ("TV SHOWS".equals(mode)) {
                render();

                if (focusKey != null) {
                    content.post(() -> {
                        View restore = findFocusableByKey(
                            content, focusKey
                        );

                        if (restore != null) {
                            restore.requestFocus();
                        }
                    });
                }
            }
        }

        @Override
        public void onError(String message) {
            if ("TV SHOWS".equals(mode)) {
                Toast.makeText(
                    MovieHubActivity.this,
                    "Offline catalogue loaded",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    });
}
    
private String getFocusKey(View view) {
    if (view == null) return null;

    Object tag = view.getTag();

    if (tag instanceof String) {
        return "TAG:" + tag;
    }

    if (view instanceof TextView) {
        return "TEXT:" +
            ((TextView) view).getText().toString();
    }

    return null;
}

private View findFocusableByKey(
    ViewGroup parent,
    String key
) {
    if (parent == null || key == null) return null;

    for (int i = 0; i < parent.getChildCount(); i++) {
        View child = parent.getChildAt(i);

        String childKey = getFocusKey(child);

        if (key.equals(childKey) && child.isFocusable()) {
            return child;
        }

        if (child instanceof ViewGroup) {
            View result = findFocusableByKey(
                (ViewGroup) child, key
            );

            if (result != null) return result;
        }
    }

    return null;
}
    

    private void addCatalogueItems() {
        int count = 0;

        for (MediaItem item : catalogue) {
            if (!item.type.equals(mode)) continue;

            if (!selectedGenre.equals("All")
                    && !item.genre.equals(selectedGenre)) {
                continue;
            }

            if (!searchQuery.isEmpty()) {
                String query = searchQuery.toLowerCase(Locale.ROOT);

                if (!item.title.toLowerCase(Locale.ROOT).contains(query)
                        && !item.genre.toLowerCase(Locale.ROOT).contains(query)) {
                    continue;
                }
            }

            count++;
            addMediaCard(item, count);
        }

        if (count == 0) {
            addInfo(
                "No matching titles found. Try another genre or search."
            );
        }
    }


           private void addMediaCard(MediaItem item, int number) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(14, 12, 14, 12);
        card.setFocusable(true);
        card.setClickable(true);
        card.setTag(item.title);
        card.setBackground(makeBackground(CARD, PURPLE));

        ImageView poster = new ImageView(this);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        poster.setBackground(makeHeroBackground());
        poster.setTag(item.posterUrl);

        LinearLayout.LayoutParams posterParams =
            new LinearLayout.LayoutParams(82, 112);
        posterParams.setMargins(0, 0, 16, 0);
        card.addView(poster, posterParams);

        loadPoster(item.posterUrl, poster, number);

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText(item.title);
        title.setTextColor(Color.WHITE);
        title.setTextSize(17);
        title.setTypeface(null, Typeface.BOLD);
        details.addView(title);

        TextView meta = new TextView(this);
        meta.setText(item.genre + "  •  " + item.year);
        meta.setTextColor(CYAN);
        meta.setTextSize(12);
        meta.setPadding(0, 7, 0, 7);
        details.addView(meta);

        TextView hint = new TextView(this);
        hint.setText("SELECT TO VIEW DETAILS  ›");
        hint.setTextColor(Color.LTGRAY);
        hint.setTextSize(11);
        details.addView(hint);

        card.addView(details, new LinearLayout.LayoutParams(
            0, -2, 1
        ));

        card.setOnClickListener(v -> showDetails(item));

        card.setOnFocusChangeListener((v, focused) -> {
    card.animate().cancel();

    card.setBackground(makeBackground(
        focused ? Color.rgb(30, 85, 145) : CARD,
        focused ? Color.WHITE : Color.rgb(75, 80, 88)
    ));

    card.animate()
        .scaleX(focused ? 1.04f : 1f)
        .scaleY(focused ? 1.04f : 1f)
        .setDuration(150)
        .start();

    if (focused) {
        Rect rect = new Rect(
            0, 0, card.getWidth(), card.getHeight()
        );
        card.requestRectangleOnScreen(rect, true);
    }
});
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 7, 0, 7);
        content.addView(card, params);

        if (initialFocus == null) initialFocus = card;
    }

    private void loadPoster(String imageUrl, ImageView imageView, int number) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageView.setImageDrawable(null);
            return;
        }

        imageView.setTag(imageUrl);

        posterExecutor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setInstanceFollowRedirects(true);

                Bitmap bitmap = BitmapFactory.decodeStream(
                    connection.getInputStream()
                );

                if (bitmap != null) {
                    mainHandler.post(() -> {
                        if (imageUrl.equals(imageView.getTag())) {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }

            } catch (Exception ignored) {
                // Poster unavailable: keep the placeholder background.
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void showDetails(MediaItem item) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(22, 18, 22, 10);

        TextView title = new TextView(this);
        title.setText(item.title);
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        panel.addView(title);

        TextView info = new TextView(this);
        info.setText(
            item.type + "  •  " + item.genre + "  •  " + item.year
        );
        info.setTextColor(CYAN);
        info.setTextSize(13);
        info.setPadding(0, 12, 0, 12);
        panel.addView(info);

        TextView description = new TextView(this);
        description.setText(item.description);
        description.setTextColor(Color.WHITE);
        description.setTextSize(15);
        panel.addView(description);

        new AlertDialog.Builder(this)
            .setView(panel)
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void showSearch() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(searchQuery);
        input.setHint("Enter movie or show name");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.LTGRAY);

        LinearLayout box = new LinearLayout(this);
        box.setPadding(22, 10, 22, 0);
        box.addView(input);

        new AlertDialog.Builder(this)
            .setTitle("SEARCH AURORA")
            .setView(box)
            .setPositiveButton("SEARCH", (dialog, which) -> {
                searchQuery = input.getText().toString().trim();
                render();
            })
            .setNegativeButton("CANCEL", null)
            .show();

        input.requestFocus();
    }

    private void addTopBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);

        TextView brand = new TextView(this);
        brand.setText("∞  AURORA ENTERTAINMENT");
        brand.setTextColor(CYAN);
        brand.setTextSize(24);
        brand.setTypeface(null, Typeface.BOLD);

        bar.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        TextView status = new TextView(this);
        status.setText("● INFINITY X");
        status.setTextColor(Color.rgb(130, 255, 210));
        status.setTextSize(12);
        status.setTypeface(null, Typeface.BOLD);
        status.setPadding(15, 10, 15, 10);
        status.setBackground(makeBackground(
            Color.rgb(12, 42, 42), Color.rgb(35, 180, 150)
        ));

        bar.addView(status);
        content.addView(bar);

        TextView subtitle = new TextView(this);
        subtitle.setText("YOUR PREMIUM ENTERTAINMENT DESTINATION");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(11);
        subtitle.setPadding(2, 8, 0, 15);
        content.addView(subtitle);
    }


    private void addTab(LinearLayout parent, String label, String value) {
        Button button = makeButton(label);
        boolean active = mode.equals(value);

        button.setBackground(makeBackground(
            active ? PURPLE : CARD,
            active ? CYAN : Color.rgb(50, 75, 130)
        ));

        if (active && initialFocus == null) initialFocus = button;

        button.setOnClickListener(v -> {
            mode = value;
            selectedGenre = "All";
            searchQuery = "";
            render();
        });

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(0, 65, 1);
        params.setMargins(5, 5, 5, 5);
        parent.addView(button, params);
    }

    private void addGenre(LinearLayout parent, String genre) {
        Button button = makeButton(genre);
        boolean active = selectedGenre.equals(genre);

        button.setBackground(makeBackground(
            active ? PURPLE : CARD,
            active ? CYAN : Color.rgb(50, 75, 130)
        ));

        if (active && initialFocus == null) initialFocus = button;

        button.setOnClickListener(v -> {
            selectedGenre = genre;
            render();
        });

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-2, 52);
        params.setMargins(5, 5, 8, 5);
        parent.addView(button, params);
    }

    private void addStreamingApps() {
        String[][] supported = {
            {"Netflix", "com.netflix.mediaclient"},
            {"YouTube TV", "com.google.android.youtube.tv"},
            {"YouTube", "com.google.android.youtube"},
            {"Prime Video", "com.amazon.amazonvideo.livingroom"},
            {"Disney+", "com.disneyplus"},
            {"JioHotstar", "in.startv.hotstar"},
            {"JioHotstar", "com.hotstar.android"},
            {"Spotify TV", "com.spotify.tv.android"},
            {"Spotify", "com.spotify.music"}
        };

        boolean found = false;
        boolean jioHotstarAdded = false;

        for (String[] app : supported) {
            String appName = app[0];
            String packageName = app[1];

            Intent launch =
                getPackageManager().getLaunchIntentForPackage(packageName);

            if (launch == null) continue;

            if (appName.equals("JioHotstar")) {
                if (jioHotstarAdded) continue;
                jioHotstarAdded = true;
            }

            found = true;

            Button button = makeButton("▶    OPEN " + appName);
            button.setTextSize(15);
            button.setBackground(makeBackground(
                CARD, Color.rgb(55, 105, 185)
            ));

            if (initialFocus == null) initialFocus = button;

            button.setOnClickListener(v -> {
                try {
                    recordRecentApp(packageName);
                    startActivity(launch);
                } catch (Exception e) {
                    Toast.makeText(
                        this, "Unable to open " + appName,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            });

            LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, 62);
            params.setMargins(0, 5, 0, 5);
            content.addView(button, params);
        }

        if (!found) {
            addInfo(
                "No supported streaming apps were detected. " +
                "Install a supported app to see it here."
            );
        }
    }

    private void recordRecentApp(String packageName) {
        if (packageName == null ||
                packageName.equals(getPackageName())) {
            return;
        }

        android.content.SharedPreferences prefs =
            getSharedPreferences("AuroraRecentApps", MODE_PRIVATE);

        org.json.JSONArray oldList;

        try {
            oldList = new org.json.JSONArray(
                prefs.getString("recent_packages", "[]")
            );
        } catch (Exception e) {
            oldList = new org.json.JSONArray();
        }

        org.json.JSONArray newList = new org.json.JSONArray();
        newList.put(packageName);

        for (int i = 0; i < oldList.length(); i++) {
            String oldPackage = oldList.optString(i, "");

            if (!oldPackage.isEmpty()
                    && !oldPackage.equals(packageName)
                    && !oldPackage.equals(getPackageName())
                    && newList.length() < 8) {
                newList.put(oldPackage);
            }
        }

        prefs.edit()
            .putString("recent_packages", newList.toString())
            .apply();
    }

    private void addFeatureCard(String heading, String description) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);
        card.setBackground(makeBackground(CARD, PURPLE));

        TextView title = new TextView(this);
        title.setText(heading);
        title.setTextColor(CYAN);
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        card.addView(title);

        TextView info = new TextView(this);
        info.setText(description);
        info.setTextColor(Color.LTGRAY);
        info.setTextSize(13);
        info.setPadding(0, 8, 0, 0);
        card.addView(info);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 6, 0, 6);
        content.addView(card, params);
    }

    private void addHeading(String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextColor(Color.rgb(235, 238, 242));
        heading.setTextSize(17);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 22, 0, 10);
        content.addView(heading);
    }

    private void addInfo(String text) {
        TextView info = new TextView(this);
        info.setText(text);
        info.setTextColor(Color.LTGRAY);
        info.setTextSize(14);
        info.setPadding(18, 16, 18, 16);
        info.setBackground(makeBackground(
            CARD, Color.rgb(45, 75, 130)
        ));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 5, 0, 8);
        content.addView(info, params);
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setFocusable(true);
        button.setClickable(true);
        button.setPadding(12, 5, 12, 5);

        GradientDrawable normal = makeBackground(
    CARD, Color.rgb(75, 80, 88)
);

GradientDrawable focused = makeBackground(
    Color.rgb(65, 70, 78), Color.WHITE
);

        button.setBackground(normal);

        button.setOnFocusChangeListener((view, hasFocus) -> {
            button.setBackground(hasFocus ? focused : normal);
            button.setScaleX(hasFocus ? 1.04f : 1f);
            button.setScaleY(hasFocus ? 1.04f : 1f);

            if (hasFocus) {
                button.post(() -> {
                    Rect rect = new Rect(
                        0, 0, button.getWidth(), button.getHeight()
                    );
                    button.requestRectangleOnScreen(rect, true);
                });
            }
        });

        return button;
    }

    private GradientDrawable makeBackground(int color, int stroke) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(20);
        bg.setStroke(2, stroke);
        return bg;
    }

    private GradientDrawable makeHeroBackground() {
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[] {
                Color.rgb(22, 30, 75),
                Color.rgb(55, 25, 105),
                Color.rgb(8, 45, 75)
            }
        );

        bg.setCornerRadius(26);
        bg.setStroke(2, Color.rgb(40, 150, 230));
        return bg;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
        }
      
