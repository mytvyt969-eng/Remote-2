package com.masjid.timeeditor;

import android.app.*; import android.content.*; import android.os.Bundle; import android.graphics.*; import android.graphics.drawable.GradientDrawable; import android.view.*; import android.widget.*; import java.util.*;

public class DialogPicker extends Dialog {
    interface Save { void done(int[] hm); }
    final int prayer; int hour, minute; final Save save; TextView selected; NumberPicker hp, mp; TextView period;
    int green=Color.rgb(22,184,148), dark=Color.rgb(94,104,120);
    DialogPicker(Context c,int p,int h,int m,Save s){super(c); prayer=p; hour=h; minute=m; save=s;}
    GradientDrawable box(int stroke,int fill,float r){GradientDrawable g=new GradientDrawable();g.setColor(fill);g.setCornerRadius(r);g.setStroke(2,stroke);return g;}
    TextView tv(String s,float z,int c){TextView t=new TextView(getContext());t.setText(s);t.setTextSize(z);t.setTextColor(c);t.setGravity(Gravity.CENTER);return t;}
    @Override protected void onCreate(Bundle b){super.onCreate(b); requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout all=new LinearLayout(getContext());all.setOrientation(LinearLayout.VERTICAL);all.setPadding(38,28,38,24);all.setBackground(box(green,Color.WHITE,38));
        TextView head=tv("SELECTED TIME",25,dark);head.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);head.setTypeface(null,1);all.addView(head,new LinearLayout.LayoutParams(-1,55));
        selected=tv("",45,Color.rgb(8,125,104));selected.setTypeface(null,1);selected.setBackground(box(green,Color.rgb(246,255,252),28));all.addView(selected,new LinearLayout.LayoutParams(-1,90));
        LinearLayout labels=new LinearLayout(getContext());labels.setGravity(Gravity.CENTER); String[] ls={"HOURS","MINUTES","PERIOD"}; for(String x:ls){TextView t=tv(x,18,dark);t.setTypeface(null,1);labels.addView(t,new LinearLayout.LayoutParams(0,55,1));}all.addView(labels);
        LinearLayout wheels=new LinearLayout(getContext());wheels.setGravity(Gravity.CENTER);
        hp=new NumberPicker(getContext()); hp.setMinValue(1);hp.setMaxValue(12);hp.setValue(hour);hp.setWrapSelectorWheel(true); hp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mp=new NumberPicker(getContext()); mp.setMinValue(0);mp.setMaxValue(59);mp.setValue(minute);mp.setFormatter(v->String.format(Locale.US,"%02d",v));mp.setWrapSelectorWheel(true);mp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        wheels.addView(hp,new LinearLayout.LayoutParams(150,250)); wheels.addView(tv(":",38,dark),new LinearLayout.LayoutParams(45,250));wheels.addView(mp,new LinearLayout.LayoutParams(150,250));
        period=tv(prayer==0?"AM":"PM",32,Color.WHITE);period.setTypeface(null,1);period.setBackground(box(green,green,25));wheels.addView(period,new LinearLayout.LayoutParams(150,110));all.addView(wheels,new LinearLayout.LayoutParams(-1,270));
        TextView note=tv(prayer==0?"Fajar uses AM only":"This prayer uses PM only",15,Color.GRAY);all.addView(note,new LinearLayout.LayoutParams(-1,40));
        LinearLayout buttons=new LinearLayout(getContext());buttons.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);Button cancel=new Button(getContext());cancel.setText("CANCEL");cancel.setTextColor(green);cancel.setTextSize(18);cancel.setBackgroundColor(Color.TRANSPARENT);Button ok=new Button(getContext());ok.setText("OK");ok.setTextColor(Color.WHITE);ok.setTextSize(18);ok.setBackground(box(green,green,25));buttons.addView(cancel,new LinearLayout.LayoutParams(170,65));LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(260,65);op.setMargins(15,0,0,0);buttons.addView(ok,op);all.addView(buttons);
        hp.setOnValueChangedListener((p,a,c)->update());mp.setOnValueChangedListener((p,a,c)->update());cancel.setOnClickListener(v->dismiss());ok.setOnClickListener(v->{save.done(new int[]{hp.getValue(),mp.getValue()});dismiss();});setContentView(all);Window w=getWindow(); if(w!=null){w.setBackgroundDrawableResource(android.R.color.transparent);w.setLayout(900,WindowManager.LayoutParams.WRAP_CONTENT);}update(); }
    void update(){if(selected!=null)selected.setText(String.format(Locale.US,"%02d : %02d   %s",hp.getValue(),mp.getValue(),prayer==0?"AM":"PM"));}
}