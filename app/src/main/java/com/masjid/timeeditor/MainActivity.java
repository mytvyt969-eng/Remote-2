package com.masjid.timeeditor;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
    int green = Color.rgb(22,184,148), dark = Color.rgb(45,55,72);
    int bg = Color.rgb(247,249,252), border = Color.rgb(222,227,235);
    int beforeAdhan = 0, beforeJamaat = 0;
    String announcement = "";

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        showHome();
    }

    GradientDrawable box(int stroke, int fill, float r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill); g.setCornerRadius(r); g.setStroke(2, stroke);
        return g;
    }

    TextView text(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    Button button(String label) {
        Button b = new Button(this);
        b.setText(label); b.setTextSize(20); b.setTextColor(dark);
        b.setAllCaps(false); b.setGravity(Gravity.CENTER);
        b.setFocusable(true); b.setBackground(box(border, Color.WHITE, 24));
        return b;
    }

    void base(String title, String subtitle) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(42,28,42,28);
        root.setBackgroundColor(bg);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        Button back = button("‹");
        back.setTextSize(42);
        back.setTextColor(dark);
        back.setOnClickListener(v -> showHome());
        top.addView(back, new LinearLayout.LayoutParams(80,72));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        TextView h = text(title,30,dark); h.setTypeface(null,1);
        TextView sub = text(subtitle,16,Color.GRAY);
        titles.addView(h,new LinearLayout.LayoutParams(-1,42));
        titles.addView(sub,new LinearLayout.LayoutParams(-1,30));
        top.addView(titles,new LinearLayout.LayoutParams(0,72,1));
        root.addView(top,new LinearLayout.LayoutParams(-1,82));

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    void showHome() {
        base("ADMIN PAGE","Masjid TV control centre");
        addSection("Prayer times","Regular prayer and special salah schedules",v -> showPrayerMenu());
        addSection("Announcement","Text box and scrolling speed",v -> showAnnouncement());
        addSection("Themes","Main theme and flyer theme",v -> showThemes());
        addSection("Countdown clock","Adjust time before Adhan and Jamaat",v -> showCountdown());
        addSection("Other settings","Masjid information, date, location and sunrise/sunset",v -> showOther());
    }

    void addSection(String title, String sub, View.OnClickListener click) {
        Button b = button(title + "\n" + sub + "    ›");
        b.setGravity(Gravity.CENTER_VERTICAL);
        b.setPadding(28,0,28,0);
        b.setOnClickListener(click);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,92);
        p.setMargins(0,7,0,7);
        content.addView(b,p);
    }

    void addLabel(String s) {
        TextView t=text(s,19,Color.GRAY); t.setPadding(8,8,8,4);
        content.addView(t,new LinearLayout.LayoutParams(-1,45));
    }

    void addAction(String title, String value, View.OnClickListener click) {
        Button b=button(title + (value==null?"":"\n"+value) + "    ›");
        b.setGravity(Gravity.CENTER_VERTICAL); b.setPadding(28,0,28,0);
        b.setOnClickListener(click);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,78);
        p.setMargins(0,5,0,5); content.addView(b,p);
    }

    void showPrayerMenu() {
        base("PRAYER TIMES","Choose regular prayer or special salah");
        addLabel("REGULAR PRAYER");
        for(int i=0;i<PRAYERS.length;i++){
            final int n=i;
            addAction(PRAYERS[i],format(i),v->showPicker(n,null));
        }
        addLabel("SPECIAL SALAH");
        for(String s:SPECIAL){
            final String name=s;
            addAction(name,"Set time",v->showSpecialTime(name));
        }
    }

    void showSpecialTime(String name) {
        final EditText input=new EditText(this);
        input.setText(""); input.setTextSize(24); input.setHint("e.g. 06:30 AM");
        input.setSingleLine(true);
        new AlertDialog.Builder(this)
            .setTitle(name+" time")
            .setView(input)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("SAVE",(d,w)->{})
            .show();
    }

    void showPicker(int index, TextView unused) {
        final DialogPicker d=new DialogPicker(this,index,hours[index],mins[index],hm->{
            hours[index]=hm[0]; mins[index]=hm[1];
            if(content!=null) showPrayerMenu();
        });
        d.show();
    }

    String format(int i) {
        return String.format(Locale.US,"%02d:%02d %s",hours[i],mins[i],i==0?"AM":"PM");
    }

    void showAnnouncement() {
        base("ANNOUNCEMENT","Text shown as a right-to-left scrolling announcement");
        addLabel("TEXT BOX");
        EditText e=new EditText(this);
        e.setText(announcement); e.setHint("Enter announcement text");
        e.setTextSize(22); e.setSingleLine(false); e.setGravity(Gravity.TOP);
        e.setPadding(24,20,24,20); e.setBackground(box(border,Color.WHITE,24));
        content.addView(e,new LinearLayout.LayoutParams(-1,150));
        Button save=button("SAVE ANNOUNCEMENT");
        save.setOnClickListener(v->{announcement=e.getText().toString(); Toast.makeText(this,"Announcement saved",Toast.LENGTH_SHORT).show();});
        content.addView(save,new LinearLayout.LayoutParams(-1,70));
        addLabel("TEXT SPEED");
        SeekBar speed=new SeekBar(this); speed.setProgress(50);
        content.addView(speed,new LinearLayout.LayoutParams(-1,70));
        TextView note=text("Slow ←                         → Fast",16,Color.GRAY);
        note.setGravity(Gravity.CENTER); content.addView(note,new LinearLayout.LayoutParams(-1,45));
    }

    void showThemes() {
        base("THEMES","Choose the appearance used on the TV");
        addLabel("MAIN THEME");
        addAction("Regular theme","Default prayer clock appearance",v->Toast.makeText(this,"Regular theme selected",Toast.LENGTH_SHORT).show());
        addLabel("FLYER THEME");
        addAction("Flyer upload","Select a flyer image",v->{
            Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i,20);
        });
        addAction("Slides speed","Control flyer slide duration",v->showSpeedDialog());
    }

    void showSpeedDialog() {
        final SeekBar bar=new SeekBar(this); bar.setProgress(50);
        new AlertDialog.Builder(this).setTitle("Slides speed").setView(bar)
            .setNegativeButton("CANCEL",null).setPositiveButton("OK",null).show();
    }

    void showCountdown() {
        base("COUNTDOWN CLOCK","Adjust the displayed countdown relative to prayer times");
        addLabel("TIME ADJUSTMENTS");
        addAdjustment("Before Adhan",true);
        addAdjustment("Before Jamaat",false);
    }

    void addAdjustment(String name, boolean adhan) {
        LinearLayout row=new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(24,0,24,0);
        row.setBackground(box(border,Color.WHITE,24));
        TextView label=text(name,23,dark); label.setTypeface(null,1);
        row.addView(label,new LinearLayout.LayoutParams(0,78,1));
        Button minus=button("−"); Button value=button("0 min"); Button plus=button("+");
        minus.setTextSize(28); plus.setTextSize(28);
        value.setTextSize(18);
        minus.setOnClickListener(v->{if(adhan) beforeAdhan--; else beforeJamaat--; value.setText((adhan?beforeAdhan:beforeJamaat)+" min");});
        plus.setOnClickListener(v->{if(adhan) beforeAdhan++; else beforeJamaat++; value.setText((adhan?beforeAdhan:beforeJamaat)+" min");});
        row.addView(minus,new LinearLayout.LayoutParams(70,62));
        row.addView(value,new LinearLayout.LayoutParams(130,62));
        row.addView(plus,new LinearLayout.LayoutParams(70,62));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,90); p.setMargins(0,6,0,6);
        content.addView(row,p);
    }

    void showOther() {
        base("OTHER SETTINGS","General masjid and location configuration");
        addAction("Masjid name editor","Change display name",v->editTextDialog("Masjid name"));
        addAction("Profile image","Change masjid logo/profile image",v->{
            Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i,21);
        });
        addAction("Hijri date adjustment","Adjust Hijri date ± days",v->numberDialog("Hijri date adjustment"));
        addAction("Sunrise/sunset adjustment","Adjust sunrise and sunset",v->numberDialog("Sunrise / sunset adjustment"));
        addAction("Coordinates adjustment","Latitude and longitude",v->coordinatesDialog());
    }

    void editTextDialog(String title) {
        EditText e=new EditText(this); e.setTextSize(22); e.setSingleLine(true);
        new AlertDialog.Builder(this).setTitle(title).setView(e)
            .setNegativeButton("CANCEL",null).setPositiveButton("SAVE",null).show();
    }

    void numberDialog(String title) {
        final NumberPicker p=new NumberPicker(this);
        p.setMinValue(-30); p.setMaxValue(30); p.setValue(0);
        new AlertDialog.Builder(this).setTitle(title).setView(p)
            .setNegativeButton("CANCEL",null).setPositiveButton("OK",null).show();
    }

    void coordinatesDialog() {
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(24,0,24,0);
        EditText lat=new EditText(this); lat.setHint("Latitude"); lat.setInputType(2|8192);
        EditText lon=new EditText(this); lon.setHint("Longitude"); lon.setInputType(2|8192);
        box.addView(lat); box.addView(lon);
        Button map=button("PICK UP FROM MAP");
        map.setOnClickListener(v->Toast.makeText(this,"Map picker can be connected here",Toast.LENGTH_SHORT).show());
        box.addView(map,new LinearLayout.LayoutParams(-1,65));
        new AlertDialog.Builder(this).setTitle("Coordinates adjustment").setView(box)
            .setNegativeButton("CANCEL",null).setPositiveButton("SAVE",null).show();
    }
}
