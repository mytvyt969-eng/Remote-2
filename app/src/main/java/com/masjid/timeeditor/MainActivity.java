package com.masjid.timeeditor;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    static final String[] PRAYERS = {"Fajar", "Dhuhr", "Asr", "Maghrib", "Isha"};
    final int[] hours = {4, 1, 4, 6, 7};
    final int[] mins  = {30, 30, 30, 15, 45};
    LinearLayout root;
    int green = Color.rgb(22,184,148), dark = Color.rgb(94,104,120);

    @Override public void onCreate(Bundle b) { super.onCreate(b); buildScreen(); }

    GradientDrawable bg(int stroke, int fill, float r) {
        GradientDrawable g = new GradientDrawable(); g.setColor(fill); g.setCornerRadius(r); g.setStroke(2, stroke); return g;
    }
    TextView text(String s, float size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.CENTER_VERTICAL); return t;
    }
    void buildScreen() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(45,30,45,30); root.setBackgroundColor(Color.WHITE);
        TextView title=text("PRAYER TIME SETTINGS",30,dark); title.setTypeface(null,1); root.addView(title,new LinearLayout.LayoutParams(-1,70));
        TextView hint=text("Press EDIT to change a prayer time",18,Color.GRAY); root.addView(hint,new LinearLayout.LayoutParams(-1,45));
        for(int i=0;i<PRAYERS.length;i++) addRow(i);
        setContentView(root);
    }
    void addRow(final int index) {
        LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(22,8,22,8); row.setBackground(bg(Color.rgb(235,239,242),Color.WHITE,28));
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,85); rp.setMargins(0,8,0,8); root.addView(row,rp);
        TextView name=text(PRAYERS[index],24,dark); name.setTypeface(null,1); row.addView(name,new LinearLayout.LayoutParams(0,-1,1));
        final TextView value=text(format(index),24,green); value.setGravity(Gravity.CENTER); row.addView(value,new LinearLayout.LayoutParams(190,-1));
        Button edit=new Button(this); edit.setText("EDIT"); edit.setTextSize(17); edit.setTextColor(green); edit.setAllCaps(false); edit.setBackground(bg(green,Color.WHITE,22)); edit.setFocusable(true);
        edit.setOnClickListener(v -> showPicker(index,value)); row.addView(edit,new LinearLayout.LayoutParams(140,65));
    }
    String format(int i){ return String.format(Locale.US,"%02d:%02d %s",hours[i],mins[i],i==0?"AM":"PM"); }
    void showPicker(int index, TextView value) {
        final DialogPicker d=new DialogPicker(this,index,hours[index],mins[index],i-> {
            hours[index]=i[0]; mins[index]=i[1]; value.setText(format(index));
        }); d.show();
    }
}
