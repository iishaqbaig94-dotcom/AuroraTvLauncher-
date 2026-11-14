
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
    private ScrollView scroll;
    private SharedPreferences prefs;

    private View initialFocus;

    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(145, 80, 255);
    private final int DARK = Color.rgb(5, 8, 23);
    private final int CARD = Color.rgb(24, 31, 60);

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

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);

        prefs = getSharedPreferences(
            "AuroraWallpaper", MODE_PRIVATE
        );

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(DARK);
        scroll.setFocusable(false);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 30, 35, 40);
        layout.setBackgroundColor(DARK);

        scroll.addView(layout);
        setContentView(scroll);

        addHeader();
        addPreview();

        addHeading("✦  EXCLUSIVE WALLPAPER COLLECTION");

        for (int i = 0; i < names.length; i++) {
            addWallpaper(i);
        }

        addHeading("✦  PERSONAL WALLPAPER");

        addActionButton(
            "＋  CHOOSE FROM GALLERY",
            CYAN,
            v -> openGallery()
        );

        addActionButton(
            "↻  RESTORE DEFAULT",
            PURPLE,
            v -> restoreDefault()
        );

        addActionButton(
            "‹  BACK TO AURORA HOME",
            CYAN,
            v -> finish()
        );

        TextView footer = new TextView(this);
        footer.setText(
            "AURORA TV  ∞  INFINITY X\n" +
            "YOUR WORLD. YOUR STYLE."
        );
        footer.setTextColor(Color.GRAY);
        footer.setGravity(Gravity.CENTER);
        footer.setTextSize(12);
        footer.setPadding(10, 35, 10, 15);

        layout.addView(footer);

        updatePreview();

        requestInitialFocus();
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
        subtitle.setLetterSpacing(0.1f);
        subtitle.setPadding(2, 5, 0, 22);

        layout.addView(subtitle);
    }

    private void addPreview() {

        addHeading("✦  WALLPAPER PREVIEW");

        preview = new ImageView(this);
        preview.setScaleType(ImageView.ScaleType.CENTER_CROP);

        GradientDrawable border = new GradientDrawable();
        border.setColor(CARD);
        border.setCornerRadius(24);
        border.setStroke(3, CYAN);

        preview.setBackground(border);
        preview.setClipToOutline(true);
        preview.setFocusable(false);

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
        heading.setLetterSpacing(0.04f);
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
        card.setPadding(25, 20, 25, 20);
        card.setFocusable(true);
        card.setClickable(true);

        card.setContentDescription(
            names[index] + " wallpaper"
        );

        updateWallpaperCard(card, index);

        card.setOnFocusChangeListener((v, focused) -> {

            updateWallpaperCard(card, index);

            if (focused) {
                ensureVisible(card);
            }
        });

        card.setOnClickListener(v -> {

            prefs.edit()
                .putInt("wallpaper_index", index)
                .remove("custom_wallpaper")
                .apply();

            updatePreview();

            Toast.makeText(
                this,
                names[index] + " selected",
                Toast.LENGTH_SHORT
            ).show();

            updateAllWallpaperCards();
        });

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 85);

        params.setMargins(0, 7, 0, 7);

        layout.addView(card, params);

        if (initialFocus == null &&
            prefs.getInt("wallpaper_index", 0) == index &&
            prefs.getString("custom_wallpaper", null) == null) {

            initialFocus = card;
        }
    }

    private void updateWallpaperCard(
        TextView card,
        int index
    ) {

        boolean selected =
            prefs.getInt("wallpaper_index", 0) == index &&
            prefs.getString("custom_wallpaper", null) == null;

        boolean focused = card.hasFocus();

        GradientDrawable bg = gradient(index);

        bg.setStroke(
            focused ? 4 : (selected ? 3 : 2),
            focused ? Color.WHITE :
                (selected ? CYAN : Color.rgb(65, 90, 155))
        );

        card.setBackground(bg);

        card.setScaleX(focused ? 1.03f : 1f);
        card.setScaleY(focused ? 1.03f : 1f);

        card.setTextColor(
            selected ? CYAN : Color.WHITE
        );
    }

    private void updateAllWallpaperCards() {

        for (int i = 0; i < layout.getChildCount(); i++) {

            View view = layout.getChildAt(i);

            if (view instanceof TextView) {

                TextView text = (TextView) view;

                for (int j = 0; j < names.length; j++) {

                    if (text.getText().toString().contains(
                        names[j]
                    )) {
                        updateWallpaperCard(text, j);
                    }
                }
            }
        }
    }

    private GradientDrawable gradient(int index) {

        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[] {
                Color.rgb(
                    colors[index][0],
                    colors[index][1],
                    colors[index][2]
                ),
                Color.rgb(
                    colors[index][3],
                    colors[index][4],
                    colors[index][5]
                )
            }
        );

        bg.setCornerRadius(22);
        bg.setStroke(2, CYAN);

        return bg;
    }

    private void addActionButton(
        String text,
        int accent,
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

        GradientDrawable normal = actionBackground(
            CARD, accent, 2
        );

        GradientDrawable focused = actionBackground(
            Color.rgb(55, 45, 115), Color.WHITE, 4
        );

        button.setBackground(normal);

        button.setOnFocusChangeListener((v, hasFocus) -> {

            button.setBackground(
                hasFocus ? focused : normal
            );

            button.setScaleX(hasFocus ? 1.03f : 1f);
            button.setScaleY(hasFocus ? 1.03f : 1f);

            if (hasFocus) {
                ensureVisible(button);
            }
        });

        button.setOnClickListener(listener);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 7, 0, 7);

        layout.addView(button, params);

        if (initialFocus == null) {
            initialFocus = button;
        }
    }

    private GradientDrawable actionBackground(
        int color,
        int stroke,
        int strokeWidth
    ) {

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(22);
        bg.setStroke(strokeWidth, stroke);

        return bg;
    }

    private void updatePreview() {

        if (preview == null) return;

        String custom = prefs.getString(
            "custom_wallpaper", null
        );

        if (custom != null) {

            try {
                preview.setImageURI(Uri.parse(custom));

                if (preview.getDrawable() != null) {
                    return;
                }

            } catch (Exception ignored) {}
        }

        int index = prefs.getInt("wallpaper_index", 0);

        if (index < 0 || index >= colors.length) {
            index = 0;
        }

        preview.setImageDrawable(gradient(index));
    }

    private void restoreDefault() {

        prefs.edit().clear().apply();

        updatePreview();
        updateAllWallpaperCards();

        Toast.makeText(
            this,
            "Default wallpaper restored",
            Toast.LENGTH_SHORT
        ).show();
    }

    private void openGallery() {

        Intent intent = new Intent(
            Intent.ACTION_OPEN_DOCUMENT
        );

        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        );

        try {
            startActivityForResult(intent, 100);

        } catch (Exception e) {

            Toast.makeText(
                this,
                "Gallery unavailable",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        );

        if (requestCode == 100 &&
            resultCode == RESULT_OK &&
            data != null &&
            data.getData() != null) {

            Uri uri = data.getData();

            try {

                int flags = data.getFlags() &
                    Intent.FLAG_GRANT_READ_URI_PERMISSION;

                getContentResolver()
                    .takePersistableUriPermission(uri, flags);

            } catch (Exception ignored) {}

            prefs.edit()
                .putString("custom_wallpaper", uri.toString())
                .apply();

            updatePreview();
            updateAllWallpaperCards();

            Toast.makeText(
                this,
                "Custom wallpaper saved!",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void ensureVisible(View view) {

        if (view == null || scroll == null) return;

        view.post(() -> {

            android.graphics.Rect rect =
                new android.graphics.Rect(
                    0, 0,
                    view.getWidth(),
                    view.getHeight()
                );

            view.requestRectangleOnScreen(rect, true);
        });
    }

    private void requestInitialFocus() {

        scroll.post(() -> {

            if (initialFocus != null) {
                initialFocus.requestFocus();
            }
        });
    }
}
