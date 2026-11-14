
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    private LinearLayout mainLayout;
    private LinearLayout appRow;
    private final Set<String> addedPackages = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(5, 8, 23));
        getWindow().setNavigationBarColor(Color.rgb(5, 8, 23));

        ScrollView pageScroll = new ScrollView(this);
        pageScroll.setFillViewport(true);
        pageScroll.setBackgroundColor(Color.rgb(5, 8, 23));

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(40, 25, 40, 35);
        mainLayout.setBackgroundColor(Color.rgb(5, 8, 23));

        pageScroll.addView(mainLayout);

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
        subtitle.setPadding(0, 5, 0, 20);
        mainLayout.addView(subtitle);

        addHero();

        addSection("YOUR APPS");

        HorizontalScrollView appScroll = new HorizontalScrollView(this);
        appScroll.setHorizontalScrollBarEnabled(false);
        appScroll.setFocusable(false);

        appRow = new LinearLayout(this);
        appRow.setOrientation(LinearLayout.HORIZONTAL);

        appScroll.addView(appRow);
        mainLayout.addView(appScroll);

        loadInstalledApps();

        addSection("CONTINUE WATCHING");
        addApp(mainLayout, "Continue Watching", null, null, false);

        addSection("EXPLORE");
        addApp(mainLayout, "Movies", null, null, false);
        addApp(mainLayout, "TV Shows", null, null, false);
        addApp(mainLayout, "Picture Studio", null, null, false);

        setContentView(pageScroll);
    }

    private void addHero() {
        TextView hero = new TextView(this);

        hero.setText(
            "WELCOME TO\nAURORA TV ∞\n\n" +
            "YOUR ENTERTAINMENT. YOUR UNIVERSE."
        );

        hero.setTextSize(23);
        hero.setTextColor(Color.WHITE);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(30, 25, 30, 25);
        hero.setTypeface(null, Typeface.BOLD);

        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[] {
                Color.rgb(12, 35, 85),
                Color.rgb(70, 25, 120)
            }
        );

        bg.setCornerRadius(28);
        bg.setStroke(2, Color.CYAN);
        hero.setBackground(bg);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 230);

        params.setMargins(0, 10, 0, 20);
        mainLayout.addView(hero, params);
    }

    private void addSection(String name) {
        TextView section = new TextView(this);

        section.setText(name);
        section.setTextSize(19);
        section.setTextColor(Color.rgb(120, 160, 255));
        section.setTypeface(null, Typeface.BOLD);
        section.setPadding(0, 18, 0, 12);

        mainLayout.addView(section);
    }

    private void loadInstalledApps() {
        List<ResolveInfo> apps = new ArrayList<>();

        // Android TV launcher apps
        Intent leanbackIntent = new Intent(Intent.ACTION_MAIN);
        leanbackIntent.addCategory(
            "android.intent.category.LEANBACK_LAUNCHER"
        );

        apps.addAll(queryApps(leanbackIntent));

        // Also find normal Android launcher apps
        Intent normalIntent = new Intent(Intent.ACTION_MAIN);
        normalIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        apps.addAll(queryApps(normalIntent));

        Collections.sort(apps, (a, b) ->
            getAppName(a).compareToIgnoreCase(getAppName(b))
        );

        int count = 0;

        for (ResolveInfo info : apps) {
            String packageName = info.activityInfo.packageName;

            if (packageName == null ||
                packageName.equals(getPackageName()) ||
                addedPackages.contains(packageName)) {
                continue;
            }

            addedPackages.add(packageName);

            addApp(
                appRow,
                getAppName(info),
                packageName,
                info.activityInfo.name,
                true
            );

            count++;
        }

        // Keep Settings available
        addApp(appRow, "Settings", null, null, true);

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No installed apps found");
            empty.setTextColor(Color.LTGRAY);
            empty.setTextSize(16);
            empty.setPadding(10, 20, 10, 20);
            appRow.addView(empty);
        }
    }

    private List<ResolveInfo> queryApps(Intent intent) {
        PackageManager pm = getPackageManager();

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            return pm.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0)
            );
        } else {
            return pm.queryIntentActivities(intent, 0);
        }
    }

    private String getAppName(ResolveInfo info) {
        CharSequence label = info.loadLabel(getPackageManager());

        if (label == null) {
            return info.activityInfo.packageName;
        }

        return label.toString();
    }

    private void addApp(
        LinearLayout parent,
        String name,
        String packageName,
        String activityName,
        boolean horizontal
    ) {
        TextView app = new TextView(this);

        app.setText(name);
        app.setTextSize(17);
        app.setTextColor(Color.WHITE);
        app.setGravity(Gravity.CENTER);
        app.setTypeface(null, Typeface.BOLD);
        app.setFocusable(true);
        app.setClickable(true);
        app.setPadding(16, 18, 16, 18);

        if (packageName != null) {
            try {
                Drawable icon = getPackageManager()
                    .getApplicationIcon(packageName);

                icon.setBounds(0, 0, 52, 52);
                app.setCompoundDrawables(null, icon, null, null);
                app.setCompoundDrawablePadding(12);
            } catch (Exception ignored) {
            }
        }

        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.rgb(24, 31, 60));
        normal.setCornerRadius(18);
        normal.setStroke(1, Color.rgb(45, 95, 180));

        GradientDrawable focused = new GradientDrawable();
        focused.setColor(Color.rgb(45, 40, 100));
        focused.setCornerRadius(18);
        focused.setStroke(3, Color.CYAN);

        app.setBackground(normal);

        app.setOnFocusChangeListener((view, hasFocus) ->
            app.setBackground(hasFocus ? focused : normal)
        );

        app.setOnClickListener(view ->
            openApp(name, packageName, activityName)
        );

        LinearLayout.LayoutParams params;

        if (horizontal) {
            params = new LinearLayout.LayoutParams(190, 125);
        } else {
            params = new LinearLayout.LayoutParams(-1, -2);
        }

        params.setMargins(5, 5, 12, 8);
        parent.addView(app, params);
    }

    private void openApp(
        String name,
        String packageName,
        String activityName
    ) {
        if ("Picture Studio".equals(name)) {
            Intent intent = new Intent(
                MainActivity.this,
                PictureStudioActivity.class
            );
            startActivity(intent);
            return;
        }

        if ("Movies".equals(name) ||
            "TV Shows".equals(name) ||
            "Continue Watching".equals(name)) {

            Toast.makeText(
                this,
                name + " coming soon!",
                Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if ("Settings".equals(name)) {
            startActivity(new Intent(
                android.provider.Settings.ACTION_SETTINGS
            ));
            return;
        }

        if (packageName != null && activityName != null) {
            try {
                Intent launch = new Intent(Intent.ACTION_MAIN);
                launch.setClassName(packageName, activityName);
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launch);
            } catch (Exception e) {
                Toast.makeText(
                    this,
                    "Unable to open " + name,
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mainLayout != null && appRow != null) {
            appRow.removeAllViews();
            addedPackages.clear();
            loadInstalledApps();
        }
    }
                }

