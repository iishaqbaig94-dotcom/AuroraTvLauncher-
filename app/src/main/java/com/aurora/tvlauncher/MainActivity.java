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
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    private LinearLayout mainLayout;
    private LinearLayout appRow;
    private LinearLayout recentRow;
    private ImageView wallpaperBackground;
    private ScrollView pageScroll;
    private HorizontalScrollView appScroll;
    private HorizontalScrollView recentScroll;

    private String lastFocusedName = null;
    private final Set<String> addedPackages = new HashSet<>();

    private final int DARK = Color.rgb(5, 8, 23);
    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(145, 80, 255);
    private final int WHITE = Color.WHITE;

    private final int[][] wallpaperColors = {
        {5, 8, 23, 15, 55, 130},
        {10, 5, 30, 80, 20, 140},
        {0, 0, 8, 35, 35, 65},
        {0, 25, 45, 0, 125, 180},
        {15, 5, 35, 100, 35, 120},
        {5, 15, 40, 20, 80, 100}
    };

    private int dp(float value) {
        return (int) (value * getResources()
            .getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);
        getWindow().getDecorView().setSystemUiVisibility(0);

        FrameLayout root = new FrameLayout(this);

        wallpaperBackground = new ImageView(this);
        wallpaperBackground.setScaleType(
            ImageView.ScaleType.CENTER_CROP
        );

        root.addView(wallpaperBackground,
            new FrameLayout.LayoutParams(-1, -1));

        View overlay = new View(this);
        overlay.setBackgroundColor(Color.argb(175, 3, 6, 20));
        root.addView(overlay,
            new FrameLayout.LayoutParams(-1, -1));

        pageScroll = new ScrollView(this);
        pageScroll.setFillViewport(true);
        pageScroll.setBackgroundColor(Color.TRANSPARENT);
        pageScroll.setFocusable(false);
        pageScroll.setDescendantFocusability(
            ViewGroup.FOCUS_AFTER_DESCENDANTS
        );

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(dp(40), dp(25), dp(40), dp(45));
        mainLayout.setBackgroundColor(Color.TRANSPARENT);

        pageScroll.addView(mainLayout);
        root.addView(pageScroll,
            new FrameLayout.LayoutParams(-1, -1));

        setContentView(root);

        addWallpaperBackground();
        buildHomeScreen();
    }

    private GradientDrawable gradient(
        int[] colors, float radius,
        int strokeColor, int strokeWidth
    ) {
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, colors
        );
        bg.setCornerRadius(dp(radius));

        if (strokeWidth > 0) {
            bg.setStroke(dp(strokeWidth), strokeColor);
        }
        return bg;
    }

    private void buildHomeScreen() {
        addTopBar();
        addHero();

        addSection("YOUR APPS  ✦");

        appScroll = new HorizontalScrollView(this);
        appScroll.setHorizontalScrollBarEnabled(false);
        appScroll.setFocusable(false);
        appScroll.setClipToPadding(false);
        appScroll.setDescendantFocusability(
            ViewGroup.FOCUS_AFTER_DESCENDANTS
        );

        appRow = new LinearLayout(this);
        appRow.setOrientation(LinearLayout.HORIZONTAL);
        appScroll.addView(appRow);

        mainLayout.addView(appScroll,
            new LinearLayout.LayoutParams(-1, -2));

        loadInstalledApps();

        addSection("RECENTLY OPENED");

        recentScroll = new HorizontalScrollView(this);
        recentScroll.setHorizontalScrollBarEnabled(false);
        recentScroll.setFocusable(false);
        recentScroll.setClipToPadding(false);
        recentScroll.setDescendantFocusability(
            ViewGroup.FOCUS_AFTER_DESCENDANTS
        );

        recentRow = new LinearLayout(this);
        recentRow.setOrientation(LinearLayout.HORIZONTAL);
        recentScroll.addView(recentRow);

        mainLayout.addView(recentScroll,
            new LinearLayout.LayoutParams(-1, -2));

        loadRecentApps();

        addSection("EXPLORE YOUR UNIVERSE");
        addApp(mainLayout, "Movies", null, null, false);
        addApp(mainLayout, "TV Shows", null, null, false);

        addSection("PERSONALISE YOUR EXPERIENCE");
        addApp(mainLayout, "Picture Studio", null, null, false);
        addApp(mainLayout, "Wallpaper Studio", null, null, false);

        addSection("DEVICE CARE");
        addApp(mainLayout, "AURORA INFINITY CLEANER",
            null, null, false);

        addSection("SYSTEM");
        addApp(mainLayout, "Settings", null, null, false);

        TextView footer = new TextView(this);
        footer.setText("AURORA TV  ∞  INFINITY X");
        footer.setTextColor(Color.rgb(120, 145, 190));
        footer.setTextSize(11);
        footer.setLetterSpacing(0.12f);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(30), 0, dp(10));
        mainLayout.addView(footer);
    }

    private void addTopBar() {
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("∞  AURORA TV");
        title.setTextSize(29);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(CYAN);
        title.setLetterSpacing(0.04f);
        brand.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(
            "INFINITY X  •  THE FUTURE OF ENTERTAINMENT"
        );
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(10);
        subtitle.setLetterSpacing(0.10f);
        subtitle.setPadding(dp(2), dp(5), 0, 0);
        brand.addView(subtitle);

        top.addView(brand,
            new LinearLayout.LayoutParams(0, -2, 1));

        TextView status = new TextView(this);
        status.setText("● SYSTEM ONLINE");
        status.setTextColor(CYAN);
        status.setTextSize(11);
        status.setTypeface(null, Typeface.BOLD);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(16), dp(11), dp(16), dp(11));
        status.setBackground(gradient(new int[]{
            Color.rgb(10, 35, 60),
            Color.rgb(20, 25, 65)
        }, 30, CYAN, 1));
        top.addView(status);

        mainLayout.addView(top,
            new LinearLayout.LayoutParams(-1, -2));

        View line = new View(this);
        line.setBackground(gradient(new int[]{
            Color.rgb(0, 220, 255),
            Color.rgb(100, 60, 255),
            Color.TRANSPARENT
        }, 2, Color.TRANSPARENT, 0));

        LinearLayout.LayoutParams lineParams =
            new LinearLayout.LayoutParams(-1, dp(2));
        lineParams.setMargins(0, dp(18), 0, dp(10));
        mainLayout.addView(line, lineParams);
    }

    private void addHero() {
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(32), dp(25), dp(32), dp(25));

        hero.setBackground(gradient(new int[]{
            Color.rgb(5, 35, 90),
            Color.rgb(30, 25, 105),
            Color.rgb(90, 25, 145)
        }, 28, CYAN, 2));
        hero.setElevation(dp(12));

        TextView small = new TextView(this);
        small.setText("✦  WELCOME TO YOUR UNIVERSE");
        small.setTextColor(CYAN);
        small.setTextSize(12);
        small.setTypeface(null, Typeface.BOLD);
        small.setLetterSpacing(0.10f);
        hero.addView(small);

        TextView heading = new TextView(this);
        heading.setText("AURORA TV ∞");
        heading.setTextColor(WHITE);
        heading.setTextSize(36);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setLetterSpacing(0.03f);
        heading.setPadding(0, dp(10), 0, dp(5));
        hero.addView(heading);

        TextView description = new TextView(this);
        description.setText("YOUR ENTERTAINMENT.\nYOUR UNIVERSE.");
        description.setTextColor(Color.rgb(220, 230, 255));
        description.setTextSize(15);
        description.setLineSpacing(dp(3), 1f);
        hero.addView(description);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        actions.setPadding(0, dp(18), 0, 0);

        addActionButton(actions, "EXPLORE MOVIES",
            () -> openMovieHub("MOVIES"));
        addActionButton(actions, "PICTURE STUDIO",
            () -> startActivity(new Intent(
                this, PictureStudioActivity.class
            )));

        hero.addView(actions);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, dp(255));
        params.setMargins(0, dp(12), 0, dp(20));
        mainLayout.addView(hero, params);
    }

    private void addActionButton(
        LinearLayout parent, String label, Runnable action
    ) {
        TextView button = new TextView(this);
        button.setText(label + "  ›");
        button.setTextColor(WHITE);
        button.setTextSize(12);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setClickable(true);
        button.setPadding(dp(16), dp(13), dp(16), dp(13));

        GradientDrawable normal = gradient(new int[]{
            Color.rgb(15, 45, 90),
            Color.rgb(30, 30, 95)
        }, 15, CYAN, 1);

        GradientDrawable focused = gradient(new int[]{
            Color.rgb(70, 45, 155),
            Color.rgb(25, 100, 170)
        }, 15, WHITE, 3);

        button.setBackground(normal);
        applyFocusEffect(button, normal, focused, 1.06f);
        button.setOnClickListener(v -> action.run());

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-2, dp(52));
        p.setMargins(0, 0, dp(12), 0);
        parent.addView(button, p);
    }

    private void addSection(String name) {
        TextView section = new TextView(this);
        section.setText(name);
        section.setTextSize(17);
        section.setTextColor(Color.rgb(145, 190, 255));
        section.setTypeface(null, Typeface.BOLD);
        section.setLetterSpacing(0.06f);
        section.setPadding(0, dp(22), 0, dp(12));
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
        GradientDrawable background = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{
                Color.rgb(c[0], c[1], c[2]),
                Color.rgb(c[3], c[4], c[5])
            }
        );
        wallpaperBackground.setImageDrawable(background);
    }

    private List<ResolveInfo> queryApps(Intent intent) {
        PackageManager pm = getPackageManager();

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            return pm.queryIntentActivities(
                intent, PackageManager.ResolveInfoFlags.of(0)
            );
        }
        return pm.queryIntentActivities(intent, 0);
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
            if ("Settings".equalsIgnoreCase(getAppName(info))) {
    continue;
            }

            if (packageName == null ||
                packageName.equals(getPackageName()) ||
                addedPackages.contains(packageName)) {
                continue;
            }

            addedPackages.add(packageName);

            addApp(appRow, getAppName(info), packageName,
                info.activityInfo.name, true);
            count++;
        }

        addApp(appRow, "Settings", null, null, true);

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No installed apps found");
            empty.setTextColor(Color.LTGRAY);
            empty.setTextSize(16);
            empty.setPadding(dp(10), dp(20), dp(10), dp(20));
            appRow.addView(empty);
        }
    }

    private String getAppName(ResolveInfo info) {
        CharSequence label = info.loadLabel(getPackageManager());
        if (label == null) return info.activityInfo.packageName;
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
        app.setTextSize(horizontal ? 15 : 17);
        app.setTextColor(WHITE);
        app.setGravity(Gravity.CENTER);
        app.setTypeface(null, Typeface.BOLD);
        app.setFocusable(true);
        app.setFocusableInTouchMode(true);
        app.setClickable(true);
        app.setPadding(dp(16), dp(18), dp(16), dp(18));

        app.setImportantForAccessibility(
            View.IMPORTANT_FOR_ACCESSIBILITY_YES
        );

        if (packageName != null) {
            try {
                Drawable icon = getPackageManager()
                    .getApplicationIcon(packageName);
                icon.setBounds(0, 0, dp(52), dp(52));
                app.setCompoundDrawables(null, icon, null, null);
                app.setCompoundDrawablePadding(dp(12));
            } catch (Exception ignored) {
            }
        }

        GradientDrawable normal = gradient(new int[]{
            Color.rgb(15, 30, 65),
            Color.rgb(12, 18, 45)
        }, 20, Color.rgb(40, 90, 170), 1);

        GradientDrawable focused = gradient(new int[]{
            Color.rgb(55, 40, 125),
            Color.rgb(20, 95, 155)
        }, 20, CYAN, 3);

        app.setBackground(normal);
        app.setElevation(dp(4));

        app.setOnFocusChangeListener((view, hasFocus) -> {
            app.animate().cancel();
            app.setBackground(hasFocus ? focused : normal);

            app.animate()
                .scaleX(hasFocus ? 1.07f : 1f)
                .scaleY(hasFocus ? 1.07f : 1f)
                .translationZ(hasFocus ? dp(12) : 0)
                .setDuration(160)
                .setInterpolator(new DecelerateInterpolator())
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
            params = new LinearLayout.LayoutParams(
                dp(190), dp(145)
            );
        } else {
            params = new LinearLayout.LayoutParams(-1, dp(75));
        }

        params.setMargins(dp(5), dp(6), dp(12), dp(8));
        parent.addView(app, params);
    }

    // RECENTLY OPENED APPS

    private void recordRecentApp(String packageName) {
        if (packageName == null ||
            packageName.equals(getPackageName())) {
            return;
        }

        android.content.SharedPreferences prefs =
            getSharedPreferences("AuroraRecentApps", MODE_PRIVATE);

        JSONArray oldList;

        try {
            oldList = new JSONArray(
                prefs.getString("recent_packages", "[]")
            );
        } catch (Exception e) {
            oldList = new JSONArray();
        }

        JSONArray newList = new JSONArray();
        newList.put(packageName);

        for (int i = 0; i < oldList.length(); i++) {
            String oldPackage = oldList.optString(i, "");

            if (!oldPackage.isEmpty() &&
                !oldPackage.equals(packageName) &&
                !oldPackage.equals(getPackageName()) &&
                newList.length() < 8) {
                newList.put(oldPackage);
            }
        }

        prefs.edit()
            .putString("recent_packages", newList.toString())
            .apply();
    }

    private void loadRecentApps() {
        if (recentRow == null) return;

        recentRow.removeAllViews();

        android.content.SharedPreferences prefs =
            getSharedPreferences("AuroraRecentApps", MODE_PRIVATE);

        JSONArray saved;

        try {
            saved = new JSONArray(
                prefs.getString("recent_packages", "[]")
            );
        } catch (Exception e) {
            saved = new JSONArray();
        }

        JSONArray valid = new JSONArray();
        PackageManager pm = getPackageManager();

        for (int i = 0; i < saved.length(); i++) {
            String packageName = saved.optString(i, "");

            if (packageName.isEmpty() ||
                packageName.equals(getPackageName())) {
                continue;
            }

            try {
                Intent launchIntent =
                    pm.getLaunchIntentForPackage(packageName);

                if (launchIntent == null) continue;

                android.content.pm.ApplicationInfo info =
                    pm.getApplicationInfo(packageName, 0);

                CharSequence label = pm.getApplicationLabel(info);
                String appName = label == null
                    ? packageName : label.toString();

                addApp(recentRow, appName, packageName, null, true);
                valid.put(packageName);

            } catch (Exception ignored) {
                // Uninstalled or unavailable app is skipped.
            }
        }

        prefs.edit()
            .putString("recent_packages", valid.toString())
            .apply();

        if (recentRow.getChildCount() == 0) {
            TextView empty = new TextView(this);
            empty.setText("Open any app to see it here");
            empty.setTextColor(Color.LTGRAY);
            empty.setTextSize(14);
            empty.setPadding(dp(12), dp(20), dp(12), dp(20));
            recentRow.addView(empty);
        }
    }

    private void applyFocusEffect(
        View view,
        Drawable normal,
        Drawable focused,
        float scale
    ) {
        view.setFocusableInTouchMode(true);

        view.setOnFocusChangeListener((v, hasFocus) -> {
            view.animate().cancel();
            view.setBackground(hasFocus ? focused : normal);

            view.animate()
                .scaleX(hasFocus ? scale : 1f)
                .scaleY(hasFocus ? scale : 1f)
                .translationZ(hasFocus ? dp(12) : 0)
                .setDuration(160)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        });
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

                if (target.equals(textView.getText().toString())
                    && child.isFocusable()) {
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

            if (target == null &&
                appRow != null &&
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
            startActivity(new Intent(this, CleanerActivity.class));
            return;
        }

        if ("Picture Studio".equals(name)) {
            startActivity(new Intent(this, PictureStudioActivity.class));
            return;
        }

        if ("Wallpaper Studio".equals(name)) {
            startActivity(new Intent(this, WallpaperActivity.class));
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

        if ("Settings".equals(name)) {
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        }

        if (packageName != null) {
            try {
                Intent launch = null;

                if (activityName != null) {
                    launch = new Intent(Intent.ACTION_MAIN);
                    launch.setClassName(packageName, activityName);
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                if (launch == null ||
                    getPackageManager().resolveActivity(
                        launch, PackageManager.MATCH_DEFAULT_ONLY
                    ) == null) {
                    launch = getPackageManager()
                        .getLaunchIntentForPackage(packageName);
                }

                if (launch == null) {
                    Toast.makeText(this,
                        "App launch not available",
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                recordRecentApp(packageName);
                startActivity(launch);

            } catch (Exception e) {
                Toast.makeText(this,
                    "Unable to open " + name,
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openMovieHub(String mode) {
        Intent intent = new Intent(
            MainActivity.this, MovieHubActivity.class
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

            loadRecentApps();
            restoreRemoteFocus();
        }
    }
}
