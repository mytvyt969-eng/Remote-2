package com.masjid.timeeditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    static final String[] PRAYERS = {"Fajar", "Dhuhr", "Asr", "Maghrib", "Isha"};
    static final String[] SPECIAL = {"Eid", "Eid ul adha", "Sheri", "Iftar"};
    final int[] hours = {4, 1, 4, 6, 7};
    final int[] mins  = {30, 30, 30, 15, 45};

    LinearLayout root, content;
    final int BG = Color.rgb(13, 18, 28);
    final int PANEL = Color.rgb(24, 31, 45);
    final int PANEL2 = Color.rgb(30, 38, 54);
    final int TEXT = Color.rgb(241, 245, 249);
    final int MUTED = Color.rgb(155, 166, 184);
    final int ACCENT = Color.rgb(35, 205, 164);
    final int BORDER = Color.rgb(52, 63, 82);
    int beforeAdhan = 0, beforeJamaat = 0;
    String announcement = "";

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setNavigationBarColor(BG);
        getWindow().setStatusBarColor(BG);
        showHome();
    }

    GradientDrawable box(int stroke, int fill, float r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill); g.setCornerRadius(r);
        if (stroke != Color.TRANSPARENT) g.setStroke(2, stroke);
        return g;
    }

    StateListDrawable focusBox() {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, box(ACCENT, Color.rgb(30, 65, 62), 22));
        states.addState(new int[]{android.R.attr.state_pressed}, box(ACCENT, Color.rgb(27, 57, 55), 22));
        states.addState(new int[]{}, box(BORDER, PANEL, 22));
        return states;
    }

    TextView text(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    Button button(String label) {
        Button b = new Button(this);
        b.setText(label); b.setTextSize(19); b.setTextColor(TEXT);
        b.setAllCaps(false); b.setGravity(Gravity.CENTER_VERTICAL);
        b.setPadding(22, 0, 22, 0); b.setFocusable(true);
        b.setBackground(focusBox());
        if (android.os.Build.VERSION.SDK_INT >= 26) b.setDefaultFocusHighlightEnabled(false);
        return b;
    }

    void base(String title, String subtitle) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(48, 30, 48, 28);
        root.setBackgroundColor(BG);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        Button back = button("‹");
        back.setTextSize(38); back.setGravity(Gravity.CENTER);
        back.setTextColor(TEXT);
        back.setOnClickListener(v -> showHome());
        top.addView(back, new LinearLayout.LayoutParams(66, 66));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(18, 0, 0, 0);
        TextView h = text(title, 28, TEXT); h.setTypeface(null, 1);
        TextView sub = text(subtitle, 15, MUTED);
        titles.addView(h, new LinearLayout.LayoutParams(-1, 38));
        titles.addView(sub, new LinearLayout.LayoutParams(-1, 28));
        top.addView(titles, new LinearLayout.LayoutParams(0, 66, 1));

        TextView status = text("●  TV READY", 14, ACCENT);
        status.setGravity(Gravity.CENTER);
        status.setBackground(box(Color.TRANSPARENT, PANEL, 24));
        top.addView(status, new LinearLayout.LayoutParams(140, 48));
        root.addView(top, new LinearLayout.LayoutParams(-1, 76));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, 12, 0, 12);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }

    void showHome() {
        base("ADMIN PAGE", "Masjid TV control centre");
        addHomeCard("01", "Prayer times", "Regular prayers & special salah", "5 regular • 4 special", v -> showPrayerMenu());
        addHomeCard("02", "Announcement", "Scrolling message displayed on TV", "Text & speed", v -> showAnnouncement());
        addHomeCard("03", "Themes", "Main clock and flyer appearance", "Theme • Flyer • Slides", v -> showThemes());
        addHomeCard("04", "Countdown clock", "Fine-tune Adhan and Jamaat timing", "Adhan ± " + beforeAdhan + " min • Jamaat ± " + beforeJamaat + " min", v -> showCountdown());
        addHomeCard("05", "Other settings", "Masjid profile, dates and location", "Name • Image • Location", v -> showOther());
    }

    void addHomeCard(String number, String title, String sub, String meta, View.OnClickListener click) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(22, 12, 22, 12);
        card.setBackground(focusBox());
        card.setFocusable(true);
        card.setClickable(true);
        card.setOnClickListener(click);

        TextView n = text(number, 16, ACCENT);
        n.setGravity(Gravity.CENTER);
        n.setTypeface(null, 1);
        n.setBackground(box(Color.TRANSPARENT, Color.rgb(23, 55, 53), 18));
        card.addView(n, new LinearLayout.LayoutParams(58, 58));

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(18, 0, 10, 0);
        TextView t = text(title, 23, TEXT); t.setTypeface(null, 1);
        TextView s = text(sub, 15, MUTED);
        labels.addView(t, new LinearLayout.LayoutParams(-1, 36));
        labels.addView(s, new LinearLayout.LayoutParams(-1, 28));
        card.addView(labels, new LinearLayout.LayoutParams(0, 82, 1));

        TextView m = text(meta, 13, MUTED);
        m.setGravity(Gravity.CENTER);
        card.addView(m, new LinearLayout.LayoutParams(180, 52));

        TextView arrow = text("›", 32, ACCENT);
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(45, 60));

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, 104);
        p.setMargins(0, 7, 0, 7);
        content.addView(card, p);
    }

    void addLabel(String s) {
        TextView t = text(s, 14, ACCENT);
        t.setTypeface(null, 1); t.setPadding(6, 14, 6, 5);
        content.addView(t, new LinearLayout.LayoutParams(-1, 46));
    }

    void addAction(String title, String value, View.OnClickListener click) {
        Button b = button(title + (value == null ? "" : "\n" + value) + "    ›");
        b.setGravity(Gravity.CENTER_VERTICAL);
        b.setPadding(24, 0, 24, 0);
        b.setOnClickListener(click);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, 76);
        p.setMargins(0, 5, 0, 5);
        content.addView(b, p);
    }

    void showPrayerMenu() {
        base("PRAYER TIMES", "Choose regular prayer or special salah");
        addLabel("REGULAR PRAYER");
        for (int i = 0; i < PRAYERS.length; i++) {
            final int n = i;
            addAction(PRAYERS[i], format(i), v -> showPicker(n));
        }
        addLabel("SPECIAL SALAH");
        for (String s : SPECIAL) {
            final String name = s;
            addAction(name, "Set time", v -> showSpecialTime(name));
        }
    }

    void showSpecialTime(String name) {
        final EditText input = new EditText(this);
        input.setTextSize(22); input.setHint("e.g. 06:30 AM"); input.setSingleLine(true);
        input.setTextColor(TEXT); input.setHintTextColor(MUTED);
        input.setBackground(box(BORDER, PANEL2, 18));
        input.setPadding(20, 0, 20, 0);
        new AlertDialog.Builder(this)
            .setTitle(name + " time")
            .setView(input)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("SAVE", (d, w) -> {})
            .show();
    }

    void showPicker(int index) {
        final DialogPicker d = new DialogPicker(this, index, hours[index], mins[index], hm -> {
            hours[index] = hm[0]; mins[index] = hm[1]; showPrayerMenu();
        });
        d.show();
    }

    String format(int i) {
        return String.format(Locale.US, "%02d:%02d %s", hours[i], mins[i], i == 0 ? "AM" : "PM");
    }

    void showAnnouncement() {
        base("ANNOUNCEMENT", "Message displayed as a right-to-left scrolling banner");
        addLabel("MESSAGE");
        EditText e = new EditText(this);
        e.setText(announcement); e.setHint("Enter announcement text");
        e.setTextSize(21); e.setSingleLine(false); e.setGravity(Gravity.TOP);
        e.setTextColor(TEXT); e.setHintTextColor(MUTED);
        e.setPadding(22, 18, 22, 18); e.setBackground(box(BORDER, PANEL, 20));
        content.addView(e, new LinearLayout.LayoutParams(-1, 150));

        Button save = button("SAVE ANNOUNCEMENT");
        save.setGravity(Gravity.CENTER); save.setTextColor(ACCENT);
        save.setOnClickListener(v -> { announcement = e.getText().toString(); Toast.makeText(this, "Announcement saved", Toast.LENGTH_SHORT).show(); });
        content.addView(save, new LinearLayout.LayoutParams(-1, 66));

        addLabel("TEXT SPEED");
        SeekBar speed = new SeekBar(this); speed.setProgress(50);
        content.addView(speed, new LinearLayout.LayoutParams(-1, 60));
        TextView note = text("SLOW", 13, MUTED); note.setGravity(Gravity.LEFT);
        content.addView(note, new LinearLayout.LayoutParams(-1, 28));
    }

    void showThemes() {
        base("THEMES", "Choose the appearance used on the TV");
        addLabel("MAIN THEME");
        addAction("Regular theme", "Default prayer clock appearance", v -> Toast.makeText(this, "Regular theme selected", Toast.LENGTH_SHORT).show());
        addLabel("FLYER THEME");
        addAction("Flyer upload", "Select a flyer image", v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, 20);
        });
        addAction("Slides speed", "Control flyer slide duration", v -> showSpeedDialog());
    }

    void showSpeedDialog() {
        final SeekBar bar = new SeekBar(this); bar.setProgress(50);
        new AlertDialog.Builder(this).setTitle("Slides speed").setView(bar)
            .setNegativeButton("CANCEL", null).setPositiveButton("OK", null).show();
    }

    void showCountdown() {
        base("COUNTDOWN CLOCK", "Adjust the displayed countdown relative to prayer times");
        addLabel("TIME ADJUSTMENTS");
        addAdjustment("Before Adhan", true);
        addAdjustment("Before Jamaat", false);
    }

    void addAdjustment(String name, boolean adhan) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(22, 0, 22, 0);
        row.setBackground(box(BORDER, PANEL, 22));
        TextView label = text(name, 21, TEXT); label.setTypeface(null, 1);
        row.addView(label, new LinearLayout.LayoutParams(0, 72, 1));

        Button minus = button("−"), value = button("0 min"), plus = button("+");
        minus.setTextSize(27); plus.setTextSize(27); value.setTextSize(17);
        minus.setGravity(Gravity.CENTER); plus.setGravity(Gravity.CENTER); value.setGravity(Gravity.CENTER);
        minus.setOnClickListener(v -> { if (adhan) beforeAdhan--; else beforeJamaat--; value.setText((adhan ? beforeAdhan : beforeJamaat) + " min"); });
        plus.setOnClickListener(v -> { if (adhan) beforeAdhan++; else beforeJamaat++; value.setText((adhan ? beforeAdhan : beforeJamaat) + " min"); });
        row.addView(minus, new LinearLayout.LayoutParams(64, 58));
        row.addView(value, new LinearLayout.LayoutParams(120, 58));
        row.addView(plus, new LinearLayout.LayoutParams(64, 58));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, 84);
        p.setMargins(0, 6, 0, 6); content.addView(row, p);
    }

    void showOther() {
        base("OTHER SETTINGS", "General masjid and location configuration");
        addAction("Masjid name editor", "Change display name", v -> editTextDialog("Masjid name"));
        addAction("Profile image", "Change masjid logo / profile image", v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, 21);
        });
        addAction("Hijri date adjustment", "Adjust Hijri date ± days", v -> numberDialog("Hijri date adjustment"));
        addAction("Sunrise / sunset adjustment", "Fine tune sunrise and sunset", v -> numberDialog("Sunrise / sunset adjustment"));
        addAction("Coordinates adjustment", "Latitude and longitude", v -> coordinatesDialog());
    }

    void editTextDialog(String title) {
        EditText e = new EditText(this); e.setTextSize(22); e.setSingleLine(true);
        new AlertDialog.Builder(this).setTitle(title).setView(e)
            .setNegativeButton("CANCEL", null).setPositiveButton("SAVE", null).show();
    }

    void numberDialog(String title) {
        final NumberPicker p = new NumberPicker(this);
        p.setMinValue(-30); p.setMaxValue(30); p.setValue(0);
        new AlertDialog.Builder(this).setTitle(title).setView(p)
            .setNegativeButton("CANCEL", null).setPositiveButton("OK", null).show();
    }

    void coordinatesDialog() {
        LinearLayout b = new LinearLayout(this);
        b.setOrientation(LinearLayout.VERTICAL); b.setPadding(24, 0, 24, 0);
        EditText lat = new EditText(this); lat.setHint("Latitude");
        EditText lon = new EditText(this); lon.setHint("Longitude");
        b.addView(lat); b.addView(lon);
        Button map = button("PICK UP FROM MAP");
        map.setGravity(Gravity.CENTER);
        map.setOnClickListener(v -> Toast.makeText(this, "Map picker can be connected here", Toast.LENGTH_SHORT).show());
        b.addView(map, new LinearLayout.LayoutParams(-1, 65));
        new AlertDialog.Builder(this).setTitle("Coordinates adjustment").setView(b)
            .setNegativeButton("CANCEL", null).setPositiveButton("SAVE", null).show();
    }
}
