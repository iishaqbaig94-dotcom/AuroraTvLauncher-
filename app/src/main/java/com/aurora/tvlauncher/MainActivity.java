
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    private ImageView wallpaperBackground;
    private ScrollView pageScroll;

    private String lastFocusedName = null;

    private final Set<String> addedPackages = new HashSet<>();

    private final int DARK = Color.rgb(5, 8, 23);
    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(145, 80, 255);

    private final int[][] wallpaperColors = {
        {5, 8, 23, 15, 55, 130},
        {10, 5, 30, 80, 20, 140},
        {0, 0, 8, 35, 35, 65},
        {0, 25, 45, 0, 125, 180},
        {15, 5, 35, 100, 35, 120},
        {5, 15, 40, 20, 80, 100}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);

        FrameLayout root = new FrameLayout(this);

        wallpaperBackground = new ImageView(this);
        wallpaperBackground.setScaleType(
            ImageView.ScaleType.CENTER_CROP
        );

        root.addView(
            wallpaperBackground,
            new FrameLayout.LayoutParams(-1, -1)
        );

        View overlay = new View(this);
        overlay.setBackgroundColor(
            Color.argb(145, 3, 6, 20)
        );

        root.addView(
            overlay,
            new FrameLayout.LayoutParams(-1, -1)
        );

        pageScroll = new ScrollView(this);
        pageScroll.setFillViewport(true);
        pageScroll.setBackgroundColor(Color.TRANSPARENT);
        pageScroll.setFocusable(false);
        pageScroll.setDescendantFocusability(
            ViewGroup.FOCUS_AFTER_DESCENDANTS
        );

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(40, 25, 40, 40);
        mainLayout.setBackgroundColor(Color.TRANSPARENT);

        pageScroll.addView(mainLayout);

        root.addView(
            pageScroll,
            new FrameLayout.LayoutParams(-1, -1)
        );

        setContentView(root);

        addWallpaperBackground();
        buildHomeScreen();
    }

    private void buildHomeScreen() {

        addTopBar();
        addHero();

        addSection("YOUR APPS  ✦");

        HorizontalScrollView appScroll =
            new HorizontalScrollView(this);

        appScroll.setHorizontalScrollBarEnabled(false);
        appScroll.setFocusable(false);
        appScroll.setDescendantFocusability(
            ViewGroup.FOCUS_AFTER_DESCENDANTS
        );

        appRow = new LinearLayout(this);
        appRow.setOrientation(LinearLayout.HORIZONTAL);

        appScroll.addView(appRow);
        mainLayout.addView(appScroll);

        loadInstalledApps();

        addSection("CONTINUE WATCHING");

        addApp(
            mainLayout,
            "Continue Watching",
            null,
            null,
            false
        );

        addSection("EXPLORE YOUR UNIVERSE");

        addApp(mainLayout, "Movies", null, null, false);
        addApp(mainLayout, "TV Shows", null, null, false);

        addSection("PERSONALISE YOUR EXPERIENCE");

        addApp(mainLayout, "Picture Studio", null, null, false);
        addApp(mainLayout, "Wallpaper Studio", null, null, false);

        addSection("DEVICE CARE");

        addApp(
            mainLayout,
            "AURORA INFINITY CLEANER",
            null,
            null,
            false
        );

        addSection("SYSTEM");

        addApp(mainLayout, "Settings", null, null, false);
    }

    private void addTopBar() {

        TextView title = new TextView(this);
        title.setText("∞  AURORA TV");
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(CYAN);
        mainLayout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(
            "INFINITY X  •  THE FUTURE OF ENTERTAINMENT"
        );
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(12);
        subtitle.setLetterSpacing(0.08f);
        subtitle.setPadding(2, 6, 0, 20);
        mainLayout.addView(subtitle);
    }

    private void addHero() {

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(30, 25, 30, 25);

        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[] {
                Color.rgb(8, 35, 90),
                Color.rgb(35, 25, 100),
                Color.rgb(90, 25, 135)
            }
        );

        bg.setCornerRadius(28);
        bg.setStroke(2, CYAN);
        hero.setBackground(bg);

        TextView small = new TextView(this);
        small.setText("WELCOME TO YOUR UNIVERSE");
        small.setTextColor(CYAN);
        small.setTextSize(12);
        hero.addView(small);

        TextView heading = new TextView(this);
        heading.setText("AURORA TV ∞");
        heading.setTextColor(Color.WHITE);
        heading.setTextSize(31);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 12, 0, 5);
        hero.addView(heading);

        TextView description = new TextView(this);
        description.setText(
            "YOUR ENTERTAINMENT.\nYOUR UNIVERSE."
        );
        description.setTextColor(Color.LTGRAY);
        description.setTextSize(15);
        hero.addView(description);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 230);

        params.setMargins(0, 10, 0, 20);
        mainLayout.addView(hero, params);
    }

    private void addSection(String name) {

        TextView section = new TextView(this);
        section.setText(name);
        section.setTextSize(18);
        section.setTextColor(Color.rgb(145, 175, 255));
        section.setTypeface(null, Typeface.BOLD);
        section.setLetterSpacing(0.06f);
        section.setPadding(0, 22, 0, 12);

        mainLayout.addView(section);
    }

    private void addWallpaperBackground() {

        android.content.SharedPreferences prefs =
            getSharedPreferences("AuroraWallpaper", MODE_PRIVATE);

        String custom = prefs.getString("custom_wallpaper", null);

        if (custom != null) {
            try {
                wallpaperBackground.setImageURI(Uri.parse(custom));

                if (wallpaperBackground.getDrawable() != null) {
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        int index = prefs.getInt("wallpaper_index", 0);

        if (index < 0 || index >= wallpaperColors.length) {
            index = 0;
        }

        int[] c = wallpaperColors[index];

        GradientDrawable gradient = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[] {
                Color.rgb(c[0], c[1], c[2]),
                Color.rgb(c[3], c[4], c[5])
            }
        );

        wallpaperBackground.setImageDrawable(gradient);
    }

    private void loadInstalledApps() {

        List<ResolveInfo> apps = new ArrayList<>();

        Intent leanbackIntent = new Intent(Intent.ACTION_MAIN);
        leanbackIntent.addCategory(
            "android.intent.category.LEANBACK_LAUNCHER"
        );

        apps.addAll(queryApps(leanbackIntent));

        Intent normalIntent = new Intent(Intent.ACTION_MAIN);
        normalIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        apps.addAll(queryApps(normalIntent));

        Collections.sort(apps, (a, b) ->
            getAppName(a).compareToIgnoreCase(getAppName(b))
        );

        int count = 0;

        for (ResolveInfo info : apps) {

            if (info.activityInfo == null) continue;

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

        app.setText(name + "   ›");
        app.setTextSize(17);
        app.setTextColor(Color.WHITE);
        app.setGravity(Gravity.CENTER);
        app.setTypeface(null, Typeface.BOLD);
        app.setFocusable(true);
        app.setClickable(true);
        app.setPadding(16, 18, 16, 18);

        app.setImportantForAccessibility(
            View.IMPORTANT_FOR_ACCESSIBILITY_YES
        );

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
        normal.setColor(Color.rgb(20, 28, 58));
        normal.setCornerRadius(20);
        normal.setStroke(2, Color.rgb(45, 95, 180));

        GradientDrawable focused = new GradientDrawable();
        focused.setColor(Color.rgb(48, 38, 105));
        focused.setCornerRadius(20);
        focused.setStroke(3, CYAN);

        app.setBackground(normal);

        app.setOnFocusChangeListener((view, hasFocus) -> {

            app.setBackground(hasFocus ? focused : normal);

            app.animate()
                .scaleX(hasFocus ? 1.05f : 1.0f)
                .scaleY(hasFocus ? 1.05f : 1.0f)
                .setDuration(130)
                .start();

            if (hasFocus) {
                lastFocusedName = name;

                app.post(() -> {
                    Rect rect = new Rect(
                        0, 0, app.getWidth(), app.getHeight()
                    );
                    app.requestRectangleOnScreen(rect, true);
                });
            }
        });

        app.setOnClickListener(view ->
            openApp(name, packageName, activityName)
        );

        LinearLayout.LayoutParams params;

        if (horizontal) {
            params = new LinearLayout.LayoutParams(190, 135);
        } else {
            params = new LinearLayout.LayoutParams(-1, 75);
        }

        params.setMargins(5, 6, 12, 8);
        parent.addView(app, params);
    }

    private View findFocusableByName(
        ViewGroup parent,
        String name
    ) {

        if (name == null) return null;

        String target = name + "   ›";

        for (int i = 0; i < parent.getChildCount(); i++) {

            View child = parent.getChildAt(i);

            if (child instanceof TextView) {
                TextView textView = (TextView) child;

                if (target.equals(textView.getText().toString())) {
                    return child;
                }
            }

            if (child instanceof ViewGroup) {
                View result = findFocusableByName(
                    (ViewGroup) child, name
                );

                if (result != null) return result;
            }
        }

        return null;
    }

    private void restoreRemoteFocus() {

        if (mainLayout == null) return;

        mainLayout.post(() -> {

            View target = findFocusableByName(
                mainLayout, lastFocusedName
            );

            if (target == null && appRow != null &&
                appRow.getChildCount() > 0) {
                target = appRow.getChildAt(0);
            }

            if (target != null) {
                target.requestFocus();

                Rect rect = new Rect(
                    0, 0, target.getWidth(), target.getHeight()
                );

                target.requestRectangleOnScreen(rect, true);
            }
        });
    }

    private void openApp(
        String name,
        String packageName,
        String activityName
    ) {

        if ("AURORA INFINITY CLEANER".equals(name)) {
            startActivity(new Intent(
                MainActivity.this,
                CleanerActivity.class
            ));
            return;
        }

        if ("Picture Studio".equals(name)) {
            startActivity(new Intent(
                MainActivity.this,
                PictureStudioActivity.class
            ));
            return;
        }

        if ("Wallpaper Studio".equals(name)) {
            startActivity(new Intent(
                MainActivity.this,
                WallpaperActivity.class
            ));
            return;
        }

        if ("Movies".equals(name)) {
            openMovieHub("MOVIES");
            return;
        }

        if ("TV Shows".equals(name)) {
            openMovieHub("TV SHOWS");
            return;
        }

        if ("Continue Watching".equals(name)) {
            openMovieHub("CONTINUE");
            return;
        }

        if ("Settings".equals(name)) {
            try {
                startActivity(new Intent(
                    android.provider.Settings.ACTION_SETTINGS
                ));
            } catch (Exception e) {
                Toast.makeText(
                    this, "Settings unavailable",
                    Toast.LENGTH_SHORT
                ).show();
            }
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
                    this, "Unable to open " + name,
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void openMovieHub(String mode) {

        Intent intent = new Intent(
            MainActivity.this,
            MovieHubActivity.class
        );

        intent.putExtra("hub_mode", mode);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (wallpaperBackground != null) {
            addWallpaperBackground();
        }

        if (appRow != null) {
            appRow.removeAllViews();
            addedPackages.clear();

            loadInstalledApps();
            restoreRemoteFocus();
        }
    }
             }
