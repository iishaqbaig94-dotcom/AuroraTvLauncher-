
package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class PictureStudioActivity extends Activity {

    private LinearLayout layout;
    private android.content.SharedPreferences prefs;

    private final Map<String, TextView> modeButtons =
        new LinkedHashMap<>();

    private String selectedMode = "Standard";

    private ImageView previewImage;

    private final int CYAN = Color.rgb(0, 220, 255);
    private final int PURPLE = Color.rgb(139, 92, 255);
    private final int DARK = Color.rgb(5, 8, 23);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);

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
        layout.setPadding(35, 30, 35, 40);
        layout.setBackgroundColor(DARK);

        scroll.addView(layout);

        addHeader();

        addPreview();

        addHeading("✦  PICTURE ADJUSTMENTS");

        addSlider("Brightness", "brightness", 50);
        addSlider("Contrast", "contrast", 50);
        addSlider("Saturation", "saturation", 50);
        addSlider("Color Temperature", "temperature", 50);

        addHeading("✦  CINEMATIC PICTURE MODES");

        addMode("Standard");
        addMode("Cinema");
        addMode("Vivid");
        addMode("Game");

        updateModeButtons();

        addResetButton();

        TextView note = new TextView(this);
        note.setText(
            "AURORA TV  ∞  INFINITY X\n" +
            "Live preview filters apply inside this studio.\n" +
            "Settings are saved on this device."
        );
        note.setTextColor(Color.GRAY);
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        note.setPadding(10, 25, 10, 10);

        layout.addView(note);

        setContentView(scroll);

        updateFilterPreview();
    }

    private void addHeader() {

        TextView title = new TextView(this);
        title.setText("∞ AURORA");
        title.setTextColor(CYAN);
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);

        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("INFINITY PICTURE STUDIO");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(12);
        subtitle.setLetterSpacing(0.12f);
        subtitle.setPadding(2, 8, 0, 20);

        layout.addView(subtitle);
    }

    private void addPreview() {

        addHeading("✦  LIVE PICTURE PREVIEW");

        previewImage = new ImageView(this);
        previewImage.setImageBitmap(createPreviewBitmap());
        previewImage.setScaleType(
            ImageView.ScaleType.CENTER_CROP
        );
        previewImage.setFocusable(false);

        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.rgb(15, 20, 45));
        border.setCornerRadius(22);
        border.setStroke(2, CYAN);

        previewImage.setBackground(border);
        previewImage.setClipToOutline(true);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 210);

        params.setMargins(0, 5, 0, 15);

        layout.addView(previewImage, params);
    }

    private Bitmap createPreviewBitmap() {

        int width = 900;
        int height = 420;

        Bitmap bitmap = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setShader(new LinearGradient(
            0, 0, width, height,
            new int[] {
                Color.rgb(0, 35, 90),
                Color.rgb(55, 20, 120),
                Color.rgb(0, 170, 190)
            },
            null,
            Shader.TileMode.CLAMP
        ));

        canvas.drawRect(0, 0, width, height, paint);

        paint.setShader(new RadialGradient(
            250, 190, 240,
            new int[] {
                Color.rgb(0, 220, 255),
                Color.TRANSPARENT
            },
            null,
            Shader.TileMode.CLAMP
        ));

        canvas.drawCircle(250, 190, 240, paint);

        paint.setShader(new RadialGradient(
            700, 230, 260,
            new int[] {
                Color.rgb(175, 70, 255),
                Color.TRANSPARENT
            },
            null,
            Shader.TileMode.CLAMP
        ));

        canvas.drawCircle(700, 230, 260, paint);

        paint.setShader(null);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7);

        canvas.drawCircle(450, 210, 105, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(54);
        paint.setTypeface(Typeface.create(
            Typeface.DEFAULT, Typeface.BOLD
        ));

        canvas.drawText("INFINITY X", 300, 225, paint);

        return bitmap;
    }

    private void updateFilterPreview() {

        if (previewImage == null || prefs == null) {
            return;
        }

        float brightness =
            prefs.getInt("brightness", 50);

        float contrast =
            prefs.getInt("contrast", 50);

        float saturation =
            prefs.getInt("saturation", 50);

        float temperature =
            prefs.getInt("temperature", 50);

        float saturationScale = saturation / 50f;
        float contrastScale = contrast / 50f;

        float contrastOffset =
            128f * (1f - contrastScale);

        float brightnessOffset =
            (brightness - 50f) * 2.55f;

        float temperatureOffset =
            (temperature - 50f) * 1.2f;

        ColorMatrix matrix = new ColorMatrix();

        matrix.setSaturation(saturationScale);

        ColorMatrix contrastMatrix = new ColorMatrix(
            new float[] {
                contrastScale, 0, 0, 0, contrastOffset,
                0, contrastScale, 0, 0, contrastOffset,
                0, 0, contrastScale, 0, contrastOffset,
                0, 0, 0, 1, 0
            }
        );

        matrix.postConcat(contrastMatrix);

        ColorMatrix brightnessMatrix = new ColorMatrix(
            new float[] {
                1, 0, 0, 0, brightnessOffset,
                0, 1, 0, 0, brightnessOffset,
                0, 0, 1, 0, brightnessOffset,
                0, 0, 0, 1, 0
            }
        );

        matrix.postConcat(brightnessMatrix);

        ColorMatrix temperatureMatrix = new ColorMatrix(
            new float[] {
                1, 0, 0, 0, temperatureOffset,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, -temperatureOffset,
                0, 0, 0, 1, 0
            }
        );

        matrix.postConcat(temperatureMatrix);

        previewImage.setColorFilter(
            new ColorMatrixColorFilter(matrix)
        );
    }

    private void addHeading(String text) {

        TextView heading = new TextView(this);

        heading.setText(text);
        heading.setTextColor(
            Color.rgb(130, 160, 255)
        );
        heading.setTextSize(17);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setLetterSpacing(0.05f);
        heading.setPadding(0, 22, 0, 12);

        layout.addView(heading);
    }

    private void addSlider(
        String name,
        String key,
        int defaultValue
    ) {

        int savedValue = prefs.getInt(
            key, defaultValue
        );

        TextView label = new TextView(this);

        label.setText(name + " : " + savedValue + "%");
        label.setTextColor(Color.WHITE);
        label.setTextSize(16);
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
                            .putString("picture_mode", "Custom")
                            .apply();

                        selectedMode = "Custom";

                        updateModeButtons();
                        updateFilterPreview();
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

            selectedMode = mode;

            applyMode(mode);

            prefs.edit()
                .putString("picture_mode", mode)
                .apply();

            Toast.makeText(
                this,
                mode + " mode activated",
                Toast.LENGTH_SHORT
            ).show();

            recreate();
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
            .putString("picture_mode", mode)
            .apply();
    }

    private void addResetButton() {

        TextView reset = new TextView(this);

        reset.setText("↻  RESET ALL SETTINGS");
        reset.setTextColor(Color.WHITE);
        reset.setTextSize(16);
        reset.setTypeface(null, Typeface.BOLD);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(20, 20, 20, 20);
        reset.setFocusable(true);
        reset.setClickable(true);

        reset.setBackground(
            makeBackground(Color.rgb(45, 35, 100))
        );

        reset.setOnFocusChangeListener((v, focused) -> {
            reset.setBackground(
                makeBackground(
                    focused
                        ? Color.rgb(65, 55, 145)
                        : Color.rgb(45, 35, 100)
                )
            );
        });

        reset.setOnClickListener(v -> resetSettings());

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 25, 0, 15);

        layout.addView(reset, params);
    }

    private void resetSettings() {

        prefs.edit().clear().apply();

        selectedMode = "Standard";

        Toast.makeText(
            this,
            "Picture settings reset",
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
