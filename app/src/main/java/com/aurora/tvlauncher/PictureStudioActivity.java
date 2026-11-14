
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;

public class PictureStudioActivity extends Activity {

    LinearLayout layout;
    android.content.SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("AuroraPicture", MODE_PRIVATE);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 30, 35, 30);
        layout.setBackgroundColor(Color.rgb(5, 8, 23));

        TextView title = new TextView(this);
        title.setText("∞ AURORA PICTURE STUDIO");
        title.setTextColor(Color.CYAN);
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("PERSONALIZE YOUR VISUAL EXPERIENCE");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(12);
        subtitle.setPadding(0, 8, 0, 25);
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

        TextView reset = new TextView(this);
        reset.setText("RESET ALL SETTINGS");
        reset.setTextColor(Color.WHITE);
        reset.setTextSize(16);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(20, 20, 20, 20);
        reset.setFocusable(true);
        reset.setBackground(makeBackground(Color.rgb(45, 35, 100)));

        reset.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Toast.makeText(this,
                "Settings reset! Reopen Picture Studio.",
                Toast.LENGTH_SHORT).show();
            recreate();
        });

        LinearLayout.LayoutParams rp =
            new LinearLayout.LayoutParams(-1, -2);
        rp.setMargins(0, 25, 0, 15);
        layout.addView(reset, rp);

        setContentView(layout);
    }

    private void addHeading(String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextColor(Color.rgb(130, 150, 255));
        heading.setTextSize(19);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 25, 0, 12);
        layout.addView(heading);
    }

    private void addSlider(String name, String key, int defaultValue) {

        TextView label = new TextView(this);
        label.setText(name + " : " +
            prefs.getInt(key, defaultValue) + "%");
        label.setTextColor(Color.WHITE);
        label.setTextSize(17);
        label.setPadding(0, 15, 0, 5);
        layout.addView(label);

        SeekBar bar = new SeekBar(this);
        bar.setMax(100);
        bar.setProgress(prefs.getInt(key, defaultValue));
        bar.setProgressTintList(
            android.content.res.ColorStateList.valueOf(Color.CYAN));
        bar.setThumbTintList(
            android.content.res.ColorStateList.valueOf(Color.rgb(139,92,255)));

        bar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(
                    SeekBar seekBar, int progress, boolean fromUser) {

                    label.setText(name + " : " + progress + "%");

                    if (fromUser) {
                        prefs.edit().putInt(key, progress).apply();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        layout.addView(bar);
    }

    private void addMode(String mode) {

        TextView button = new TextView(this);
        button.setText(mode + " MODE");
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setGravity(Gravity.CENTER);
        button.setPadding(20, 18, 20, 18);
        button.setFocusable(true);
        button.setBackground(makeBackground(Color.rgb(24,31,60)));

        button.setOnClickListener(v -> {
            prefs.edit().putString("picture_mode", mode).apply();
            Toast.makeText(this,
                mode + " mode selected",
                Toast.LENGTH_SHORT).show();
        });

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 5, 0, 5);
        layout.addView(button, params);
    }

    private GradientDrawable makeBackground(int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(18);
        bg.setStroke(2, Color.rgb(80, 100, 220));
        return bg;
    }
}
