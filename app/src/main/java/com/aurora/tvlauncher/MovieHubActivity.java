
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieHubActivity extends Activity {

    private LinearLayout content;
    private String mode = "MOVIES";
    private String selectedGenre = "All";
    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(145, 80, 255);

    private final String[] genres = {
        "All", "Action", "Comedy", "Sci-Fi",
        "Thriller", "Drama", "Animation", "Adventure"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mode = getIntent().getStringExtra("hub_mode");
        if (mode == null) mode = "MOVIES";

        getWindow().setStatusBarColor(Color.rgb(5, 8, 23));
        getWindow().setNavigationBarColor(Color.rgb(5, 8, 23));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(5, 8, 23));

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(28, 25, 28, 35);

        scroll.addView(content);
        setContentView(scroll);

        render();
    }

    private void render() {
        content.removeAllViews();

        TextView title = new TextView(this);
        title.setText("∞  AURORA ENTERTAINMENT");
        title.setTextColor(CYAN);
        title.setTextSize(25);
        title.setTypeface(null, Typeface.BOLD);
        content.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("INFINITY X  •  YOUR CONTENT UNIVERSE");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(11);
        subtitle.setPadding(0, 7, 0, 20);
        content.addView(subtitle);

        addHeading("EXPLORE");

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);

        addTab(tabs, "MOVIES", "MOVIES");
        addTab(tabs, "TV SHOWS", "TV SHOWS");

        content.addView(tabs);

        if ("CONTINUE".equals(mode)) {
            addHeading("CONTINUE WATCHING");

            addInfo(
                "Your viewing history is not connected yet. " +
                "Choose a streaming app below to continue watching there."
            );
        } else {
            addHeading("BROWSE BY GENRE");

            LinearLayout genreRow = new LinearLayout(this);
            genreRow.setOrientation(LinearLayout.HORIZONTAL);

            for (String genre : genres) {
                addGenre(genreRow, genre);
            }

            ScrollView genreScroll = new ScrollView(this);
            genreScroll.setHorizontalScrollBarEnabled(false);
            genreScroll.setVerticalScrollBarEnabled(false);
            genreScroll.addView(genreRow);

            content.addView(genreScroll);

            addHeading(
                selectedGenre.equals("All")
                    ? "EXPLORE " + mode
                    : selectedGenre.toUpperCase() + " " + mode
            );

            addInfo(
                "Choose a streaming service below to browse " +
                "its available " + mode.toLowerCase() + "."
            );
        }

        addHeading("YOUR STREAMING APPS");
        addStreamingApps();

        addHeading("MORE");
        addInfo(
            "Live movie listings, posters and playback " +
            "require a connected content provider."
        );

        Button back = makeButton("‹  BACK TO AURORA HOME");
        back.setOnClickListener(v -> finish());

        content.addView(back);
    }

    private void addTab(
        LinearLayout parent,
        String label,
        String value
    ) {
        Button button = makeButton(label);

        button.setBackground(makeBackground(
            mode.equals(value) ? PURPLE : Color.rgb(20, 28, 58),
            mode.equals(value) ? CYAN : Color.rgb(45, 95, 180)
        ));

        button.setOnClickListener(v -> {
            mode = value;
            selectedGenre = "All";
            render();
        });

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(0, 58, 1);

        p.setMargins(4, 5, 8, 5);
        parent.addView(button, p);
    }

    private void addGenre(
        LinearLayout parent,
        String genre
    ) {
        Button button = makeButton(genre);

        button.setTextSize(13);
        button.setBackground(makeBackground(
            selectedGenre.equals(genre)
                ? PURPLE : Color.rgb(20, 28, 58),
            selectedGenre.equals(genre)
                ? CYAN : Color.rgb(45, 95, 180)
        ));

        button.setOnClickListener(v -> {
            selectedGenre = genre;
            render();
        });

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-2, 48);

        p.setMargins(4, 5, 8, 5);
        parent.addView(button, p);
    }

    private void addStreamingApps() {

        String[][] supported = {
            {"Netflix", "com.netflix.mediaclient"},
            {"YouTube", "com.google.android.youtube.tv"},
            {"YouTube", "com.google.android.youtube"},
            {"Prime Video", "com.amazon.amazonvideo.livingroom"},
            {"Disney+", "com.disneyplus"},
            {"Spotify", "com.spotify.tv.android"},
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
                "▶   OPEN " + appName
            );

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

            content.addView(button);
        }

        if (!found) {
            addInfo(
                "No supported streaming apps were detected. " +
                "Install a streaming app to see it here."
            );
        }
    }

    private void addHeading(String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextColor(
            Color.rgb(145, 175, 255)
        );
        heading.setTextSize(17);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 23, 0, 10);
        content.addView(heading);
    }

    private void addInfo(String text) {
        TextView info = new TextView(this);
        info.setText(text);
        info.setTextColor(Color.LTGRAY);
        info.setTextSize(14);
        info.setPadding(16, 16, 16, 16);
        info.setBackground(makeBackground(
            Color.rgb(17, 23, 48),
            Color.rgb(45, 95, 180)
        ));

        content.addView(info);
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setFocusable(true);
        button.setBackground(makeBackground(
            Color.rgb(20, 28, 58),
            Color.rgb(45, 95, 180)
        ));

        button.setOnFocusChangeListener((v, focused) -> {
            button.setBackground(makeBackground(
                focused ? Color.rgb(48, 38, 105)
                        : Color.rgb(20, 28, 58),
                focused ? CYAN
                        : Color.rgb(45, 95, 180)
            ));
            button.setScaleX(focused ? 1.03f : 1f);
            button.setScaleY(focused ? 1.03f : 1f);
        });

        return button;
    }

    private GradientDrawable makeBackground(
        int color,
        int stroke
    ) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(18);
        bg.setStroke(2, stroke);
        return bg;
    }
}
