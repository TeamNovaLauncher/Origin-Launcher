package com.origin.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final List<ResolveInfo> allApps = new ArrayList<>();
    private GridLayout appGrid;
    private EditText searchBox;
    private PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getPackageManager();
        buildHome();
        loadApps();
    }

    private void buildHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(26), dp(20), dp(12));
        root.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(28, 43, 72), Color.rgb(13, 18, 31), Color.rgb(8, 12, 22)}));

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        TextView title = text("Origin", 30, Color.WHITE, true);
        TextView subtitle = text("Your space", 14, 0xFFB8C5DC, false);
        titles.addView(title);
        titles.addView(subtitle);
        header.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));

        TextView settings = text("⚙", 25, Color.WHITE, true);
        settings.setGravity(Gravity.CENTER);
        settings.setContentDescription("Launcher settings");
        settings.setPadding(dp(12), dp(8), dp(12), dp(8));
        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        header.addView(settings);
        root.addView(header);

        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextColor(Color.WHITE);
        searchBox.setHintTextColor(0xFFB8C5DC);
        searchBox.setHint(getString(R.string.search_hint));
        searchBox.setTextSize(15);
        searchBox.setPadding(dp(16), 0, dp(16), 0);
        searchBox.setBackground(roundRect(0x332FFFFFF, dp(18)));
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(50));
        searchParams.topMargin = dp(24);
        root.addView(searchBox, searchParams);

        TextView section = text("APPS", 12, 0xFFB8C5DC, true);
        section.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams sectionParams = new LinearLayout.LayoutParams(-1, -2);
        sectionParams.topMargin = dp(24);
        sectionParams.bottomMargin = dp(10);
        root.addView(section, sectionParams);

        ScrollView scroll = new ScrollView(this);
        appGrid = new GridLayout(this);
        appGrid.setColumnCount(4);
        appGrid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        scroll.addView(appGrid, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderApps(s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });

        setContentView(root);
    }

    private void loadApps() {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN, null);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        allApps.clear();
        allApps.addAll(packageManager.queryIntentActivities(launcherIntent, 0));
        Collections.sort(allApps, new Comparator<ResolveInfo>() {
            @Override public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(packageManager).toString().compareToIgnoreCase(b.loadLabel(packageManager).toString());
            }
        });
        renderApps("");
    }

    private void renderApps(String query) {
        if (appGrid == null) return;
        appGrid.removeAllViews();
        String filter = query.toLowerCase(Locale.ROOT).trim();
        int index = 0;
        for (ResolveInfo app : allApps) {
            String label = app.loadLabel(packageManager).toString();
            if (!label.toLowerCase(Locale.ROOT).contains(filter)) continue;

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(4), dp(10), dp(4), dp(10));

            ImageView icon = new ImageView(this);
            try {
                icon.setImageDrawable(app.loadIcon(packageManager));
            } catch (RuntimeException ignored) {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            item.addView(icon, new LinearLayout.LayoutParams(dp(48), dp(48)));

            TextView name = text(label, 11, Color.WHITE, false);
            name.setGravity(Gravity.CENTER);
            name.setMaxLines(2);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(-1, dp(34));
            nameParams.topMargin = dp(5);
            item.addView(name, nameParams);

            final String packageName = app.activityInfo.packageName;
            final String className = app.activityInfo.name;
            item.setOnClickListener(v -> {
                Intent launch = new Intent(Intent.ACTION_MAIN);
                launch.addCategory(Intent.CATEGORY_LAUNCHER);
                launch.setClassName(packageName, className);
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(launch);
                } catch (Exception ignored) {
                    loadApps();
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(index % 4, 1f);
            params.rowSpec = GridLayout.spec(index / 4);
            appGrid.addView(item, params);
            index++;
        }
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable roundRect(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
