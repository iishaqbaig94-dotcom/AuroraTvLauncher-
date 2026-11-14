
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(5,8,23));
        getWindow().setNavigationBarColor(Color.rgb(5,8,23));

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(40,25,40,25);
        mainLayout.setBackgroundColor(Color.rgb(5,8,23));

        TextView title = new TextView(this);
        title.setText("∞  AURORA TV");
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.CYAN);
        mainLayout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("INFINITY X  •  THE FUTURE OF ENTERTAINMENT");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(12);
        subtitle.setPadding(0,5,0,20);
        mainLayout.addView(subtitle);

        addHero();

        addSection("YOUR APPS");

        HorizontalScrollView appScroll = new HorizontalScrollView(this);
        appScroll.setHorizontalScrollBarEnabled(false);

        LinearLayout appRow = new LinearLayout(this);
        appRow.setOrientation(LinearLayout.HORIZONTAL);

        String[] apps = {
            "YouTube", "Netflix", "Prime Video",
            "Disney+", "Spotify", "Settings"
        };

        for (String app : apps) {
            addApp(appRow, app);
        }

        appScroll.addView(appRow);
        mainLayout.addView(appScroll);

        addSection("CONTINUE WATCHING");

        addApp(mainLayout, "Continue Watching");

        addSection("EXPLORE");

        addApp(mainLayout, "Movies");
        addApp(mainLayout, "TV Shows");
        addApp(mainLayout, "Picture Studio");

        setContentView(mainLayout);
    }

    private void addHero() {
        TextView hero = new TextView(this);
        hero.setText("WELCOME TO\nAURORA TV ∞\n\nYOUR ENTERTAINMENT. YOUR UNIVERSE.");
        hero.setTextSize(23);
        hero.setTextColor(Color.WHITE);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(30,25,30,25);
        hero.setTypeface(null, Typeface.BOLD);

        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[] {
                Color.rgb(12,35,85),
                Color.rgb(70,25,120)
            }
        );
        bg.setCornerRadius(28);
        bg.setStroke(2, Color.CYAN);
        hero.setBackground(bg);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1,230);
        params.setMargins(0,10,0,20);

        mainLayout.addView(hero,params);
    }

    private void addSection(String name) {
        TextView section = new TextView(this);
        section.setText(name);
        section.setTextSize(19);
        section.setTextColor(Color.rgb(120,160,255));
        section.setTypeface(null, Typeface.BOLD);
        section.setPadding(0,18,0,12);
        mainLayout.addView(section);
    }

    private void addApp(LinearLayout parent, String name) {
        TextView app = new TextView(this);
        app.setText(name + "  ➜");
        app.setTextSize(17);
        app.setTextColor(Color.WHITE);
        app.setGravity(Gravity.CENTER);
        app.setFocusable(true);
        app.setClickable(true);
        app.setPadding(20,18,20,18);

        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.rgb(24,31,60));
        normal.setCornerRadius(18);
        normal.setStroke(1,Color.rgb(45,95,180));

        GradientDrawable focused = new GradientDrawable();
        focused.setColor(Color.rgb(45,40,100));
        focused.setCornerRadius(18);
        focused.setStroke(3,Color.CYAN);

        app.setBackground(normal);
        app.setOnFocusChangeListener((v, hasFocus) ->
            app.setBackground(hasFocus ? focused : normal)
        );

        app.setOnClickListener(v -> openApp(name));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                name.equals("YouTube") ||
                name.equals("Netflix") ||
                name.equals("Prime Video") ||
                name.equals("Disney+") ||
                name.equals("Spotify") ||
                name.equals("Settings") ? 190 : -1,
                -2
            );

        params.setMargins(5,5,12,5);
        parent.addView(app,params);
    }

    private void openApp(String name) {

        if (name.equals("Picture Studio")) {
    Intent intent = new Intent(
        MainActivity.this,
        PictureStudioActivity.class
    );
    startActivity(intent);
    return;
        }

        if (name.equals("Movies") ||
            name.equals("TV Shows") ||
            name.equals("Continue Watching")) {
            Toast.makeText(this,
                name + " coming soon!",
                Toast.LENGTH_SHORT).show();
            return;
        }

        String packageName = null;

        switch(name) {
            case "YouTube":
                packageName = "com.google.android.youtube.tv";
                break;
            case "Netflix":
                packageName = "com.netflix.ninja";
                break;
            case "Prime Video":
                packageName = "com.amazon.amazonvideo.livingroom";
                break;
            case "Disney+":
                packageName = "com.disney.disneyplus";
                break;
            case "Spotify":
                packageName = "com.spotify.tv.android";
                break;
        }

        if (name.equals("Settings")) {
            startActivity(new Intent(
                android.provider.Settings.ACTION_SETTINGS));
            return;
        }

        if (packageName != null) {
            Intent launch = getPackageManager()
                .getLaunchIntentForPackage(packageName);

            if (launch != null) {
                startActivity(launch);
            } else {
                Toast.makeText(this,
                    name + " is not installed",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
}
