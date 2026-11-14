
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class PictureStudioActivity extends Activity {

    private LinearLayout layout;
    private android.content.SharedPreferences prefs;

    private final Map<String, TextView> modeButtons =
        new LinkedHashMap<>();

    private String selectedMode = "Standard";

    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(139, 92, 255);
    private final int DARK = Color.rgb(5, 8, 23);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(
            "AuroraPicture", MODE_PRIVATE
        );

        selectedMode = prefs.getString(
            "picture_mode", "Standard"
        );

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(DARK);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 30, 35, 35);
        layout.setBackgroundColor(DARK);

        scroll.addView(layout);

        TextView title = new TextView(this);
        title.setText("∞ AURORA PICTURE STUDIO");
        title.setTextColor(CYAN);
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(
            "PERSONALIZE YOUR VISUAL EXPERIENCE"
        );
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(12);
        subtitle.setPadding(0, 8, 0, 20);
        layout.addView(subtitle);

        addSlider("Brightness", "brightness", 50);
        addSlider("Contrast", "contrast", 50);
        addSlider("Saturation", "saturation", 50);
        addSlider("Color Temperature", "temperature", 50);

        addHeading("PICTURE MODES");

        addMode("Standard");
        addMode("Cinema");
        addMode("Vivid");
        addMode("Game");

        updateModeButtons();

        TextView reset = new TextView(this);
        reset.setText("↻  RESET ALL SETTINGS");
        reset.setTextColor(Color.WHITE);
        reset.setTextSize(16);
        reset.setTypeface(null, Typeface.BOLD);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(20, 20, 20, 20);
        reset.setFocusable(true);
        reset.setClickable(true);

        GradientDrawable resetBg =
            makeBackground(Color.rgb(45, 35, 100));

        reset.setBackground(resetBg);

        reset.setOnFocusChangeListener((v, focused) -> {
            reset.setBackground(
                makeBackground(
                    focused ? Color.rgb(65, 55, 145)
                            : Color.rgb(45, 35, 100)
                )
            );
        });

        reset.setOnClickListener(v -> resetSettings());

        LinearLayout.LayoutParams rp =
            new LinearLayout.LayoutParams(-1, -2);

        rp.setMargins(0, 25, 0, 15);
        layout.addView(reset, rp);

        TextView note = new TextView(this);
        note.setText(
            "AURORA TV INFINITY X\n" +
            "Your picture preferences are saved on this device."
        );
        note.setTextColor(Color.GRAY);
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        note.setPadding(10, 20, 10, 10);
        layout.addView(note);

        setContentView(scroll);
    }

    private void addHeading(String text) {
        TextView heading = new TextView(this);

        heading.setText(text);
        heading.setTextColor(
            Color.rgb(130, 150, 255)
        );
        heading.setTextSize(19);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 25, 0, 12);

        layout.addView(heading);
    }

    private void addSlider(
        String name,
        String key,
        int defaultValue
    ) {
        int savedValue = prefs.getInt(key, defaultValue);

        TextView label = new TextView(this);
        label.setText(name + " : " + savedValue + "%");
        label.setTextColor(Color.WHITE);
        label.setTextSize(17);
        label.setTypeface(null, Typeface.BOLD);
        label.setPadding(0, 15, 0, 5);

        layout.addView(label);

        SeekBar bar = new SeekBar(this);
        bar.setMax(100);
        bar.setProgress(savedValue);
        bar.setFocusable(true);
        bar.setFocusableInTouchMode(true);

        bar.setProgressTintList(
            ColorStateList.valueOf(CYAN)
        );

        bar.setThumbTintList(
            ColorStateList.valueOf(PURPLE)
        );

        bar.setOnFocusChangeListener((v, focused) -> {
            label.setTextColor(
                focused ? CYAN : Color.WHITE
            );
        });

        bar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(
                    SeekBar seekBar,
                    int progress,
                    boolean fromUser
                ) {
                    label.setText(
                        name + " : " + progress + "%"
                    );

                    if (fromUser) {
                        prefs.edit()
                            .putInt(key, progress)
                            .apply();
                    }
                }

                @Override
                public void onStartTrackingTouch(
                    SeekBar seekBar
                ) {}

                @Override
                public void onStopTrackingTouch(
                    SeekBar seekBar
                ) {}
            }
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 0, 0, 8);
        layout.addView(bar, params);
    }

    private void addMode(String mode) {
        TextView button = new TextView(this);

        button.setText("✦  " + mode.toUpperCase() + " MODE");
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(20, 20, 20, 20);
        button.setFocusable(true);
        button.setClickable(true);

        button.setOnFocusChangeListener((v, focused) -> {
            updateModeButtons();
        });

        button.setOnClickListener(v -> {
            applyMode(mode);

            selectedMode = mode;

            prefs.edit()
                .putString("picture_mode", mode)
                .apply();

            updateModeButtons();

            Toast.makeText(
                this,
                mode + " mode selected",
                Toast.LENGTH_SHORT
            ).show();
        });

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 5, 0, 5);

        layout.addView(button, params);
        modeButtons.put(mode, button);
    }

    private void updateModeButtons() {
        for (Map.Entry<String, TextView> entry :
            modeButtons.entrySet()) {

            String mode = entry.getKey();
            TextView button = entry.getValue();

            boolean selected = mode.equals(selectedMode);
            boolean focused = button.hasFocus();

            int backgroundColor;

            if (focused) {
                backgroundColor = Color.rgb(65, 55, 145);
            } else if (selected) {
                backgroundColor = Color.rgb(25, 65, 110);
            } else {
                backgroundColor = Color.rgb(24, 31, 60);
            }

            GradientDrawable bg =
                makeBackground(backgroundColor);

            bg.setStroke(
                focused ? 3 : (selected ? 2 : 1),
                focused || selected ? CYAN
                    : Color.rgb(80, 100, 220)
            );

            button.setBackground(bg);

            button.setTextColor(
                selected ? CYAN : Color.WHITE
            );
        }
    }

    private void applyMode(String mode) {
        int brightness = 50;
        int contrast = 50;
        int saturation = 50;
        int temperature = 50;

        switch (mode) {
            case "Cinema":
                brightness = 45;
                contrast = 60;
                saturation = 45;
                temperature = 40;
                break;

            case "Vivid":
                brightness = 60;
                contrast = 70;
                saturation = 85;
                temperature = 60;
                break;

            case "Game":
                brightness = 55;
                contrast = 65;
                saturation = 55;
                temperature = 50;
                break;

            case "Standard":
            default:
                break;
        }

        prefs.edit()
            .putInt("brightness", brightness)
            .putInt("contrast", contrast)
            .putInt("saturation", saturation)
            .putInt("temperature", temperature)
            .apply();

        // Refresh controls to show the selected preset.
        recreate();
    }

    private void resetSettings() {
        prefs.edit().clear().apply();

        Toast.makeText(
            this,
            "All picture preferences reset",
            Toast.LENGTH_SHORT
        ).show();

        recreate();
    }

    private GradientDrawable makeBackground(int color) {
        GradientDrawable bg = new GradientDrawable();

        bg.setColor(color);
        bg.setCornerRadius(18);
        bg.setStroke(2, Color.rgb(80, 100, 220));

        return bg;
    }
}
