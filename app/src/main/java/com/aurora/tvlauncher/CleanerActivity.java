
package com.aurora.tvlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CleanerActivity extends Activity {

    private LinearLayout content;
    private ScrollView scroll;

    private TextView storageInfo;
    private TextView ramInfo;
    private TextView cacheInfo;

    private final int BLUE = Color.rgb(0, 190, 255);
    private final int PURPLE = Color.rgb(145, 75, 255);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(170, 180, 205);
    private final int BACKGROUND = Color.rgb(5, 8, 22);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BACKGROUND);
        getWindow().setNavigationBarColor(Color.BLACK);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BACKGROUND);
        scroll.setSmoothScrollingEnabled(true);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(24), dp(22), dp(24), dp(30));

        scroll.addView(content);
        setContentView(scroll);

        buildScreen();
        refreshInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (storageInfo != null) {
            refreshInfo();
        }
    }

    private void buildScreen() {

        TextView brand = text("∞ AURORA", 18, BLUE, true);
        content.addView(brand);

        TextView heading = text("INFINITY CLEANER", 30, WHITE, true);
        heading.setPadding(0, dp(8), 0, dp(4));
        content.addView(heading);

        TextView subtitle = text(
                "DEVICE CARE • STORAGE • MEMORY • APPS",
                12, MUTED, true
        );
        content.addView(subtitle);

        addSpace(22);

        addSection("STORAGE ANALYSIS");

        storageInfo = text("Checking storage...", 18, WHITE, true);
        addCard(storageInfo);

        Button refresh = makeButton("REFRESH DEVICE INFO", BLUE);
        refresh.setOnClickListener(v -> refreshInfo());
        content.addView(refresh);

        addSpace(20);

        addSection("RAM MONITOR");

        ramInfo = text("Checking memory...", 18, WHITE, true);
        addCard(ramInfo);

        TextView ramNote = text(
                "RAM monitor only. Android manages device memory automatically.",
                12, MUTED, false
        );
        content.addView(ramNote);

        addSpace(20);

        addSection("SAFE CACHE CLEANER");

        cacheInfo = text("Checking app cache...", 18, WHITE, true);
        addCard(cacheInfo);

        Button clean = makeButton("CLEAR AURORA CACHE", PURPLE);
        clean.setOnClickListener(v -> clearAuroraCache());
        content.addView(clean);

        TextView cacheNote = text(
                "Only Aurora TV's own cache is cleared. Other apps' private data is not touched.",
                12, MUTED, false
        );
        content.addView(cacheNote);

        Button systemStorage = makeButton(
                "OPEN SYSTEM STORAGE SETTINGS", BLUE
        );
        systemStorage.setOnClickListener(v -> openStorageSettings());
        content.addView(systemStorage);

        addSpace(20);

        addSection("INSTALLED APP MANAGER");

        TextView appNote = text(
                "Choose an app to open its Android settings.",
                12, MUTED, false
        );
        content.addView(appNote);

        loadInstalledApps();

        addSpace(22);

        Button back = makeButton("BACK TO AURORA HOME", PURPLE);
        back.setOnClickListener(v -> finish());
        content.addView(back);
    }

    private void refreshInfo() {

        try {
            StatFs stat = new StatFs(
                    Environment.getDataDirectory().getPath()
            );

            long total = stat.getTotalBytes();
            long free = stat.getAvailableBytes();
            long used = total - free;

            int percent = total > 0
                    ? (int) ((used * 100L) / total)
                    : 0;

            storageInfo.setText(
                    "Used: " + formatSize(used) +
                    "\nAvailable: " + formatSize(free) +
                    "\nTotal: " + formatSize(total) +
                    "\nStorage usage: " + percent + "%"
            );

        } catch (Exception e) {
            storageInfo.setText("Storage information unavailable.");
        }

        try {
            ActivityManager manager =
                    (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            ActivityManager.MemoryInfo memory =
                    new ActivityManager.MemoryInfo();

            if (manager != null) {
                manager.getMemoryInfo(memory);

                long totalRam = memory.totalMem;
                long availableRam = memory.availMem;
                long usedRam = totalRam - availableRam;

                ramInfo.setText(
                        "Used RAM: " + formatSize(usedRam) +
                        "\nAvailable RAM: " + formatSize(availableRam) +
                        "\nTotal RAM: " + formatSize(totalRam)
                );
            }

        } catch (Exception e) {
            ramInfo.setText("Memory information unavailable.");
        }

        updateCacheInfo();
    }

    private void updateCacheInfo() {

        long cache = getCacheSize(getCacheDir());

        File external = getExternalCacheDir();

        if (external != null) {
            cache += getCacheSize(external);
        }

        cacheInfo.setText(
                "Aurora cache size: " + formatSize(cache)
        );
    }

    private void clearAuroraCache() {

        long before = getCacheSize(getCacheDir());

        File external = getExternalCacheDir();

        if (external != null) {
            before += getCacheSize(external);
        }

        deleteContents(getCacheDir());

        if (external != null) {
            deleteContents(external);
        }

        long after = getCacheSize(getCacheDir());

        if (external != null) {
            after += getCacheSize(external);
        }

        long freed = Math.max(0, before - after);

        refreshInfo();

        cacheInfo.setText(
                "Cleanup completed!\nFreed approximately: "
                        + formatSize(freed) +
                "\nRemaining Aurora cache: " + formatSize(after)
        );
    }

    private void openStorageSettings() {

        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS
            ));

        } catch (Exception e) {

            try {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_SETTINGS
                ));
            } catch (Exception ignored) {
                // Settings unavailable on this device.
            }
        }
    }

    private void loadInstalledApps() {

        PackageManager pm = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> results =
                pm.queryIntentActivities(intent, 0);

        Collections.sort(
                results,
                new Comparator<ResolveInfo>() {
                    @Override
                    public int compare(ResolveInfo a, ResolveInfo b) {
                        return String.valueOf(a.loadLabel(pm))
                                .compareToIgnoreCase(
                                        String.valueOf(b.loadLabel(pm))
                                );
                    }
                }
        );

        String ownPackage = getPackageName();
        Set<String> addedPackages = new HashSet<>();

        for (ResolveInfo item : results) {

            if (item.activityInfo == null) {
                continue;
            }

            String packageName =
                    item.activityInfo.packageName;

            if (packageName.equals(ownPackage)
                    || !addedPackages.add(packageName)) {
                continue;
            }

            String appName =
                    String.valueOf(item.loadLabel(pm));

            Button appButton = makeButton(
                    "APP  •  " + appName,
                    Color.rgb(25, 35, 65)
            );

            appButton.setOnClickListener(v -> {

                try {
                    Intent settings = new Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + packageName)
                    );

                    startActivity(settings);

                } catch (Exception ignored) {
                    // App settings unavailable.
                }
            });

            content.addView(appButton);
        }

        if (addedPackages.isEmpty()) {

            TextView empty = text(
                    "No launchable apps found.",
                    14, MUTED, false
            );

            empty.setPadding(0, dp(14), 0, dp(14));
            content.addView(empty);
        }
    }

    private void addSection(String name) {

        TextView heading = text(name, 16, BLUE, true);

        heading.setPadding(0, dp(5), 0, dp(10));

        content.addView(heading);
    }

    private void addCard(TextView view) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = dp(12);

        view.setPadding(
                dp(18), dp(18), dp(18), dp(18)
        );

        view.setBackground(
                panelBackground(Color.rgb(15, 23, 48))
        );

        content.addView(view, params);
    }

    private Button makeButton(String label, int color) {

        Button button = new Button(this);

        button.setText(label);
        button.setTextColor(WHITE);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);

        button.setPadding(
                dp(14), dp(10), dp(14), dp(10)
        );

        button.setBackground(panelBackground(color));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(58));

        params.bottomMargin = dp(10);

        button.setLayoutParams(params);

        button.setOnFocusChangeListener((v, focused) -> {

            button.animate()
                    .scaleX(focused ? 1.04f : 1f)
                    .scaleY(focused ? 1.04f : 1f)
                    .setDuration(130)
                    .start();

            button.setBackground(
                    panelBackground(
                            focused
                                    ? Color.rgb(25, 75, 125)
                                    : color
                    )
            );

            if (focused) {
                scroll.post(() ->
                        scroll.smoothScrollTo(
                                0, button.getTop()
                        )
                );
            }
        });

        return button;
    }

    private GradientDrawable panelBackground(int color) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(16));
        drawable.setStroke(dp(1), BLUE);

        return drawable;
    }

    private TextView text(
            String value,
            int size,
            int color,
            boolean bold
    ) {

        TextView view = new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return view;
    }

    private void addSpace(int value) {

        View space = new View(this);

        content.addView(
                space,
                new LinearLayout.LayoutParams(1, dp(value))
        );
    }

    private long getCacheSize(File file) {

        if (file == null || !file.exists()) {
            return 0;
        }

        long size = 0;

        File[] files = file.listFiles();

        if (files == null) {
            return 0;
        }

        for (File child : files) {

            if (child.isFile()) {
                size += child.length();
            } else {
                size += getCacheSize(child);
            }
        }

        return size;
    }

    private void deleteContents(File file) {

        if (file == null || !file.exists()) {
            return;
        }

        File[] files = file.listFiles();

        if (files == null) {
            return;
        }

        for (File child : files) {

            if (child.isDirectory()) {
                deleteContents(child);
            }

            child.delete();
        }
    }

    private String formatSize(long bytes) {

        if (bytes < 1024) {
            return bytes + " B";
        }

        double kb = bytes / 1024.0;

        if (kb < 1024) {
            return String.format(Locale.US, "%.1f KB", kb);
        }

        double mb = kb / 1024.0;

        if (mb < 1024) {
            return String.format(Locale.US, "%.1f MB", mb);
        }

        double gb = mb / 1024.0;

        return String.format(Locale.US, "%.2f GB", gb);
    }

    private int dp(int value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }
            }
