
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

public class WallpaperActivity extends Activity {

    private LinearLayout layout;
    private ImageView preview;
    private SharedPreferences prefs;

    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(145, 80, 255);
    private final int DARK = Color.rgb(5, 8, 23);

    private final int[][] colors = {
        {5, 8, 23, 15, 55, 130},
        {10, 5, 30, 80, 20, 140},
        {0, 0, 8, 35, 35, 65},
        {0, 25, 45, 0, 125, 180},
        {15, 5, 35, 100, 35, 120},
        {5, 15, 40, 20, 80, 100}
    };

    private final String[] names = {
        "INFINITY BLUE",
        "NEON VIOLET",
        "MIDNIGHT BLACK",
        "CYBER CYAN",
        "GALAXY PURPLE",
        "DEEP SPACE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(
            "AuroraWallpaper", MODE_PRIVATE
        );

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(DARK);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 30, 35, 40);

        scroll.addView(layout);

        addHeader();
        addPreview();
        addHeading("✦  EXCLUSIVE WALLPAPER COLLECTION");

        for (int i = 0; i < names.length; i++) {
            addWallpaper(i);
        }

        addHeading("✦  PERSONAL WALLPAPER");

        addButton("＋  CHOOSE FROM GALLERY", CYAN, v -> openGallery());

        addButton("↻  RESTORE DEFAULT", PURPLE, v -> {
            prefs.edit().clear().apply();
            updatePreview();
            Toast.makeText(this,
                "Default restored", Toast.LENGTH_SHORT).show();
        });

        TextView footer = new TextView(this);
        footer.setText("AURORA TV  ∞  INFINITY X\nYOUR WORLD. YOUR STYLE.");
        footer.setTextColor(Color.GRAY);
        footer.setGravity(Gravity.CENTER);
        footer.setTextSize(12);
        footer.setPadding(10, 35, 10, 15);
        layout.addView(footer);

        setContentView(scroll);
        updatePreview();
    }

    private void addHeader() {
        TextView title = new TextView(this);
        title.setText("∞  AURORA");
        title.setTextColor(CYAN);
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("INFINITY WALLPAPER STUDIO");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(13);
        subtitle.setPadding(2, 5, 0, 22);
        layout.addView(subtitle);
    }

    private void addPreview() {
        preview = new ImageView(this);
        preview.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 230);

        params.setMargins(0, 5, 0, 20);

        layout.addView(preview, params);
    }

    private void addHeading(String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextColor(Color.rgb(150, 170, 255));
        heading.setTextSize(17);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 22, 0, 12);
        layout.addView(heading);
    }

    private void addWallpaper(int index) {
        TextView card = new TextView(this);

        card.setText("✦  " + names[index] + "   ➜");
        card.setTextColor(Color.WHITE);
        card.setTextSize(16);
        card.setTypeface(null, Typeface.BOLD);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(25, 25, 25, 25);
        card.setFocusable(true);
        card.setClickable(true);

        GradientDrawable bg = gradient(index);
        card.setBackground(bg);

        card.setOnFocusChangeListener((v, focused) -> {
            GradientDrawable focus = gradient(index);
            focus.setStroke(
                focused ? 4 : 2,
                focused ? Color.WHITE : CYAN
            );
            card.setBackground(focus);
        });

        card.setOnClickListener(v -> {
            prefs.edit()
                .putInt("wallpaper_index", index)
                .remove("custom_wallpaper")
                .apply();

            updatePreview();

            Toast.makeText(this,
                names[index] + " selected",
                Toast.LENGTH_SHORT).show();
        });

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 85);

        params.setMargins(0, 7, 0, 7);
        layout.addView(card, params);
    }

    private GradientDrawable gradient(int index) {
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[] {
                Color.rgb(colors[index][0],
                          colors[index][1],
                          colors[index][2]),
                Color.rgb(colors[index][3],
                          colors[index][4],
                          colors[index][5])
            }
        );

        bg.setCornerRadius(22);
        bg.setStroke(2, CYAN);
        return bg;
    }

    private void addButton(
        String text, int accent,
        View.OnClickListener listener
    ) {
        TextView button = new TextView(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(15);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(20, 22, 20, 22);
        button.setFocusable(true);
        button.setClickable(true);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(24, 31, 60));
        bg.setCornerRadius(22);
        bg.setStroke(2, accent);
        button.setBackground(bg);

        button.setOnFocusChangeListener((v, focused) -> {
            GradientDrawable focus = new GradientDrawable();
            focus.setColor(
                focused ? Color.rgb(55, 45, 115)
                        : Color.rgb(24, 31, 60)
            );
            focus.setCornerRadius(22);
            focus.setStroke(
                focused ? 4 : 2,
                focused ? Color.WHITE : accent
            );
            button.setBackground(focus);
        });

        button.setOnClickListener(listener);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 7, 0, 7);
        layout.addView(button, params);
    }

    private void updatePreview() {
        String custom = prefs.getString(
            "custom_wallpaper", null
        );

        if (custom != null) {
            try {
                preview.setImageURI(Uri.parse(custom));
                return;
            } catch (Exception ignored) {}
        }

        int index = prefs.getInt("wallpaper_index", 0);

        if (index < 0 || index >= colors.length) {
            index = 0;
        }

        preview.setImageDrawable(gradient(index));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Toast.makeText(this,
                "Gallery unavailable",
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 &&
            resultCode == RESULT_OK &&
            data != null &&
            data.getData() != null) {

            Uri uri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {}

            prefs.edit()
                .putString("custom_wallpaper", uri.toString())
                .apply();

            updatePreview();

            Toast.makeText(this,
                "Custom wallpaper saved!",
                Toast.LENGTH_SHORT).show();
        }
    }
}
