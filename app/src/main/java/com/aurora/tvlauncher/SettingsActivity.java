
package com.aurora.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private LinearLayout content;
    private ScrollView scroll;

    private final int BLUE = Color.rgb(0, 190, 255);
    private final int PURPLE = Color.rgb(105, 55, 210);
    private final int DARK = Color.rgb(5, 8, 23);
    private final int CARD = Color.rgb(16, 23, 48);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(Color.BLACK);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(DARK);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(25), dp(22), dp(25), dp(30));

        scroll.addView(content);
        setContentView(scroll);

        buildScreen();
    }

    private void buildScreen() {

        TextView brand = text("∞ AURORA", 20, BLUE, true);
        content.addView(brand);

        TextView heading = text(
                "SYSTEM SETTINGS", 30, Color.WHITE, true
        );
        heading.setPadding(0, dp(10), 0, dp(5));
        content.addView(heading);

        TextView subtitle = text(
                "PERSONALISE YOUR INFINITY EXPERIENCE",
                12, Color.LTGRAY, true
        );
        content.addView(subtitle);

        addSection("DEVICE SETTINGS");

        addOption("DISPLAY SETTINGS",
                "Brightness, screen and display options",
                Settings.ACTION_DISPLAY_SETTINGS);

        addOption("SOUND SETTINGS",
                "Audio and volume preferences",
                Settings.ACTION_SOUND_SETTINGS);

        addOption("NETWORK & WI-FI",
                "Manage internet and network connections",
                Settings.ACTION_WIFI_SETTINGS);

        addOption("BLUETOOTH SETTINGS",
                "Manage Bluetooth connections",
                Settings.ACTION_BLUETOOTH_SETTINGS);

        addOption("DATE & TIME",
                "Set device date, time and timezone",
                Settings.ACTION_DATE_SETTINGS);

        addSection("APPLICATION SETTINGS");

        addOption("INSTALLED APPS",
                "Manage installed applications",
                Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);

        addOption("SYSTEM SETTINGS",
                "Open Android device settings",
                Settings.ACTION_SETTINGS);

        addSection("ABOUT");

        LinearLayout about = new LinearLayout(this);
        about.setOrientation(LinearLayout.VERTICAL);
        about.setPadding(dp(18), dp(18), dp(18), dp(18));
        about.setBackground(background(CARD, PURPLE));

        TextView name = text(
                "AURORA TV – INFINITY X",
                18, BLUE, true
        );
        about.addView(name);

        TextView version = text(
                "Premium Android TV Launcher\nVersion 1.0",
                14, Color.LTGRAY, false
        );
        version.setPadding(0, dp(10), 0, 0);
        about.addView(version);

        content.addView(about);

        addSpace(22);

        Button back = makeButton(
                "‹  BACK TO AURORA HOME", PURPLE
        );

        back.setOnClickListener(v -> finish());
        content.addView(back);

        // Initial TV remote focus on first OPEN SETTINGS button
        content.post(() -> {
            View firstCard = content.getChildAt(4);

            if (firstCard instanceof LinearLayout) {
                View firstButton =
                        ((LinearLayout) firstCard).getChildAt(2);

                if (firstButton != null) {
                    firstButton.requestFocus();
                }
            }
        });
    }

    private void addSection(String title) {
        TextView heading = text(
                title, 16, BLUE, true
        );
        heading.setPadding(0, dp(24), 0, dp(10));
        content.addView(heading);
    }

    private void addOption(
            String title,
            String description,
            String action
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        card.setFocusable(false);
        card.setBackground(background(CARD, BLUE));

        TextView name = text(
                title, 15, Color.WHITE, true
        );
        card.addView(name);

        TextView info = text(
                description, 12, Color.LTGRAY, false
        );
        info.setPadding(0, dp(5), 0, 0);
        card.addView(info);

        Button open = makeButton("OPEN SETTINGS", BLUE);
        open.setOnClickListener(v -> openSystemSetting(action));

        card.addView(open);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(5), 0, dp(10));

        content.addView(card, params);
    }

    private void openSystemSetting(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            try {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            } catch (Exception ignored) {
                Toast.makeText(
                        this,
                        "Settings unavailable on this device",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private Button makeButton(String label, int color) {

        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setBackground(background(color, BLUE));

        button.setOnFocusChangeListener((v, focused) -> {
            button.setBackground(background(
                    focused ? Color.rgb(35, 65, 125) : color,
                    focused ? Color.WHITE : BLUE
            ));

            button.animate()
                    .scaleX(focused ? 1.04f : 1f)
                    .scaleY(focused ? 1.04f : 1f)
                    .setDuration(120)
                    .start();

            if (focused) {
                button.post(() -> {
                    android.graphics.Rect rect =
                            new android.graphics.Rect(
                                    0, 0,
                                    button.getWidth(),
                                    button.getHeight()
                            );
                    button.requestRectangleOnScreen(rect, true);
                });
            }
        });

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(52));
        params.topMargin = dp(10);
        button.setLayoutParams(params);

        return button;
    }

    private GradientDrawable background(
            int color, int stroke
    ) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(16));
        bg.setStroke(dp(1), stroke);
        return bg;
    }

    private TextView text(
            String value, int size, int color, boolean bold
    ) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(
                    Typeface.DEFAULT, Typeface.BOLD
            );
        }

        return view;
    }

    private void addSpace(int value) {
        View space = new View(this);
        content.addView(space,
                new LinearLayout.LayoutParams(1, dp(value)));
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }
}
