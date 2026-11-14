
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MovieHubActivity extends Activity {

    private LinearLayout content;
    private View initialFocus;

    private String mode = "MOVIES";
    private String selectedGenre = "All";

    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(105, 55, 210);
    private final int DARK = Color.rgb(5, 8, 23);
    private final int CARD = Color.rgb(16, 23, 48);

    private final String[] genres = {
        "All", "Action", "Comedy", "Sci-Fi",
        "Thriller", "Drama", "Animation", "Adventure"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String received = getIntent().getStringExtra("hub_mode");
        if (received != null) mode = received;

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);

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
    }

    private void render() {

        content.removeAllViews();
        initialFocus = null;

        addTopBar();

        addHero();

        addHeading("EXPLORE YOUR UNIVERSE");

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);

        addTab(tabs, "🎬  MOVIES", "MOVIES");
        addTab(tabs, "📺  TV SHOWS", "TV SHOWS");

        content.addView(tabs);

        if ("CONTINUE".equals(mode)) {

            addHeading("CONTINUE WATCHING");

            addInfo(
                "Your viewing history is not connected yet. " +
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

            addHeading(
                selectedGenre.equals("All")
                    ? "FEATURED " + mode
                    : selectedGenre.toUpperCase() + " " + mode
            );

            addInfo(
                "Explore " + selectedGenre.toLowerCase() +
                " " + mode.toLowerCase() +
                " through your connected streaming services."
            );
        }

        addHeading("STREAMING COLLECTION");

        addInfo(
            "Your installed entertainment apps appear below. " +
            "Select a service to launch it."
        );

        addStreamingApps();

        addHeading("INFINITY EXPERIENCE");

        addFeatureCard(
            "∞  CINEMATIC ENTERTAINMENT",
            "One premium destination for your favourite entertainment apps."
        );

        addFeatureCard(
            "✦  SMART CONTENT HUB",
            "Discover movies and shows through your installed streaming services."
        );

        addInfo(
            "Live catalogues, movie posters, viewing history and playback " +
            "require integration with a supported content provider."
        );

        Button back = makeButton("‹   BACK TO AURORA HOME");
        back.setTextSize(16);
        back.setBackground(makeBackground(CARD, CYAN));
        back.setOnClickListener(v -> finish());

        LinearLayout.LayoutParams backParams =
            new LinearLayout.LayoutParams(-1, 65);
        backParams.setMargins(0, 25, 0, 10);
        content.addView(back, backParams);

        if (initialFocus == null) {
            initialFocus = back;
        }

        content.post(() -> {
            if (initialFocus != null) {
                initialFocus.requestFocus();
            }
        });
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

        bar.addView(brand, new LinearLayout.LayoutParams(
            0, -2, 1
        ));

        TextView status = new TextView(this);
        status.setText("● INFINITY X");
        status.setTextColor(Color.rgb(130, 255, 210));
        status.setTextSize(12);
        status.setTypeface(null, Typeface.BOLD);
        status.setPadding(15, 10, 15, 10);
        status.setBackground(makeBackground(
            Color.rgb(12, 42, 42),
            Color.rgb(35, 180, 150)
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

    private void addHero() {

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(25, 22, 25, 22);

        hero.setBackground(makeHeroBackground());

        TextView tag = new TextView(this);
        tag.setText("✦  WELCOME TO INFINITY");
        tag.setTextColor(CYAN);
        tag.setTextSize(12);
        tag.setTypeface(null, Typeface.BOLD);

        hero.addView(tag);

        TextView heading = new TextView(this);
        heading.setText("ENTERTAINMENT\nWITHOUT LIMITS");
        heading.setTextColor(Color.WHITE);
        heading.setTextSize(29);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 12, 0, 8);

        hero.addView(heading);

        TextView description = new TextView(this);
        description.setText(
            "Movies  •  Series  •  Your favourite apps"
        );
        description.setTextColor(Color.rgb(195, 210, 240));
        description.setTextSize(14);

        hero.addView(description);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 190);
        params.setMargins(0, 10, 0, 15);

        content.addView(hero, params);
    }

    private void addTab(
        LinearLayout parent,
        String label,
        String value
    ) {

        Button button = makeButton(label);

        boolean active = mode.equals(value);

        button.setBackground(makeBackground(
            active ? PURPLE : CARD,
            active ? CYAN : Color.rgb(50, 75, 130)
        ));

        if (active && initialFocus == null) {
            initialFocus = button;
        }

        button.setOnClickListener(v -> {
            mode = value;
            selectedGenre = "All";
            render();
        });

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(0, 65, 1);

        params.setMargins(5, 5, 5, 5);
        parent.addView(button, params);
    }

    private void addGenre(
        LinearLayout parent,
        String genre
    ) {

        Button button = makeButton(genre);

        boolean active = selectedGenre.equals(genre);

        button.setBackground(makeBackground(
            active ? PURPLE : CARD,
            active ? CYAN : Color.rgb(50, 75, 130)
        ));

        if (active && initialFocus == null) {
            initialFocus = button;
        }

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
            {"Spotify TV", "com.spotify.tv.android"},
            {"Spotify", "com.spotify.music"}
        };

        boolean found = false;

        for (String[] app : supported) {

            String appName = app[0];
            String packageName = app[1];

            Intent launch =
                getPackageManager().getLaunchIntentForPackage(
                    packageName
                );

            if (launch == null) continue;

            found = true;

            Button button = makeButton(
                "▶    OPEN " + appName
            );

            button.setTextSize(15);
            button.setBackground(makeBackground(
                CARD,
                Color.rgb(55, 105, 185)
            ));

            if (initialFocus == null) {
                initialFocus = button;
            }

            button.setOnClickListener(v -> {
                try {
                    startActivity(launch);
                } catch (Exception e) {
                    Toast.makeText(
                        this,
                        "Unable to open " + appName,
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
                "Install a supported streaming app to see it here."
            );
        }
    }

    private void addFeatureCard(
        String heading,
        String description
    ) {

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
        heading.setTextColor(Color.rgb(145, 185, 255));
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
            CARD,
            Color.rgb(45, 75, 130)
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
            CARD,
            Color.rgb(45, 95, 180)
        );

        GradientDrawable focused = makeBackground(
            Color.rgb(48, 38, 105),
            CYAN
        );

        button.setBackground(normal);

        button.setOnFocusChangeListener((view, hasFocus) -> {

            button.setBackground(
                hasFocus ? focused : normal
            );

            button.setScaleX(hasFocus ? 1.04f : 1f);
            button.setScaleY(hasFocus ? 1.04f : 1f);

            if (hasFocus) {
                button.post(() -> {
                    Rect rect = new Rect(
                        0, 0,
                        button.getWidth(),
                        button.getHeight()
                    );

                    button.requestRectangleOnScreen(
                        rect, true
                    );
                });
            }
        });

        return button;
    }

    private GradientDrawable makeBackground(
        int color,
        int stroke
    ) {

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
