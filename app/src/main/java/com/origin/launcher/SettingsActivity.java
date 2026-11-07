package com.origin.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(16, 21, 34));

        TextView title = new TextView(this);
        title.setText("Launcher settings");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(title);

        TextView info = new TextView(this);
        info.setText("Origin Launcher settings will appear here. Android system settings remain managed by your device.");
        info.setTextColor(0xFFCAD3E2);
        info.setTextSize(15);
        info.setPadding(0, dp(20), 0, 0);
        root.addView(info);

        setContentView(root);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
