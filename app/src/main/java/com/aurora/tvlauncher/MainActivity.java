package com.aurora.tvlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(45, 35, 45, 35);
        mainLayout.setBackgroundColor(Color.rgb(5, 8, 23));

        TextView title = new TextView(this);
        title.setText("AURORA TV  ∞");
        title.setTextSize(32);
        title.setTextColor(Color.CYAN);
        title.setGravity(Gravity.CENTER);
        mainLayout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("THE FUTURE OF ENTERTAINMENT");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);
        mainLayout.addView(subtitle);

        addSection("YOUR APPS");
        addApp("YouTube");
        addApp("Netflix");
        addApp("Prime Video");
        addApp("Disney+");
        addApp("Spotify");

        addSection("PERSONALIZE");
        addApp("Picture Studio");
        addApp("Settings");

        setContentView(mainLayout);
    }

    private void addSection(String name) {
        TextView section = new TextView(this);
        section.setText(name);
        section.setTextSize(19);
        section.setTextColor(Color.rgb(130, 150, 255));
        section.setPadding(0, 25, 0, 12);
        mainLayout.addView(section);
    }

    private void addApp(String name) {
        TextView app = new TextView(this);
        app.setText("  " + name + "  ➜");
        app.setTextSize(20);
        app.setTextColor(Color.WHITE);
        app.setFocusable(true);
        app.setFocusableInTouchMode(true);
        app.setPadding(20, 18, 20, 18);
        app.setBackgroundColor(Color.rgb(24, 31, 60));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                -1, -2
            );
        params.setMargins(0, 5, 0, 5);

        mainLayout.addView(app, params);
    }
}
