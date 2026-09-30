package com.masjid.timeeditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    static final String[] PRAYERS={"Fajar","Dhuhr","Asr","Maghrib","Isha"};
    static final String[] SPECIAL={"Eid","Eid ul adha","Jummah","Sheri","Iftar"};
    final int[] ah={5,12,4,5,7}, am={8,15,15,58,30};
    final int[] offsets={12,0,30,5,15};
    boolean[] auto={true,false,true,true,true};
    int[] fixedJ={0,13*60+30,0,0,0};
    int[] special={6*60+30,6*60+30,0,4*60+30,18*60+30};
    int jummahAdhan=735,jummahKhutbah=780,jummahJamaat=810;

    LinearLayout root,main;
    SharedPreferences prefs;
    final int BG=Color.rgb(4,27,25), PANEL=Color.rgb(8,43,39), CARD=Color.rgb(10,49,44);
    final int FIELD=Color.rgb(3,29,27), TEXT=Color.rgb(235,244,240), MUTED=Color.rgb(154,177,171);
    final int GOLD=Color.rgb(246,190,105), TEAL=Color.rgb(91,210,185), BORDER=Color.rgb(20,72,65);
    String masjid="Masjid Clock", announcement="";

    @Override public void onCreate(Bundle b){
        super.onCreate(b); getWindow().setNavigationBarColor(BG); getWindow().setStatusBarColor(BG);
        prefs=getSharedPreferences("masjid",MODE_PRIVATE); load(); showDashboard();
    }

    void load(){
        masjid=prefs.getString("masjid", "Masjid Clock");
        announcement=prefs.getString("announcement","");
        for(int i=0;i<5;i++){
            ah[i]=prefs.getInt("ah"+i,ah[i]); am[i]=prefs.getInt("am"+i,am[i]);
            offsets[i]=prefs.getInt("off"+i,offsets[i]); auto[i]=prefs.getBoolean("auto"+i,auto[i]);
            fixedJ[i]=prefs.getInt("j"+i,fixedJ[i]);
        }
        for(int i=0;i<5;i++) special[i]=prefs.getInt("sp"+i,special[i]);
        for(int i=0;i<5;i++){offsets[i]=prefs.getInt("off"+i,offsets[i]);auto[i]=prefs.getBoolean("auto"+i,auto[i]);fixedJ[i]=prefs.getInt("j"+i,fixedJ[i]);}
        jummahAdhan=prefs.getInt("jAdhan",jummahAdhan);jummahKhutbah=prefs.getInt("jKhutbah",jummahKhutbah);jummahJamaat=prefs.getInt("jJamaat",jummahJamaat);
    }
    void save(){
        SharedPreferences.Editor e=prefs.edit().putString("masjid",masjid).putString("announcement",announcement);
        for(int i=0;i<5;i++) e.putInt("ah"+i,ah[i]).putInt("am"+i,am[i]).putInt("off"+i,offsets[i]).putBoolean("auto"+i,auto[i]).putInt("j"+i,fixedJ[i]);
        for(int i=0;i<5;i++) e.putInt("sp"+i,special[i]).putInt("off"+i,offsets[i]).putBoolean("auto"+i,auto[i]).putInt("j"+i,fixedJ[i]);
        e.putInt("jAdhan",jummahAdhan).putInt("jKhutbah",jummahKhutbah).putInt("jJamaat",jummahJamaat);
        e.apply(); Toast.makeText(this,"All changes saved",Toast.LENGTH_SHORT).show();
    }

    GradientDrawable bg(int stroke,int fill,float r){GradientDrawable g=new GradientDrawable();g.setColor(fill);g.setCornerRadius(r);if(stroke!=0)g.setStroke(2,stroke);return g;}
    StateListDrawable focusBg(){StateListDrawable s=new StateListDrawable();s.addState(new int[]{android.R.attr.state_focused},bg(GOLD,Color.rgb(17,67,59),20));s.addState(new int[]{android.R.attr.state_pressed},bg(GOLD,Color.rgb(20,76,66),20));s.addState(new int[]{},bg(BORDER,PANEL,20));return s;}
    TextView tv(String s,float z,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextSize(16);b.setTextColor(TEXT);b.setAllCaps(false);b.setFocusable(true);b.setGravity(Gravity.CENTER);b.setPadding(12,0,12,0);b.setBackground(focusBg());if(android.os.Build.VERSION.SDK_INT>=26)b.setDefaultFocusHighlightEnabled(false);return b;}

    void shell(String title,String subtitle){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.HORIZONTAL);root.setBackgroundColor(BG);root.setPadding(22,18,22,18);
        LinearLayout nav=new LinearLayout(this);nav.setOrientation(LinearLayout.VERTICAL);nav.setPadding(16,14,16,14);nav.setBackground(bg(0,PANEL,26));
        TextView brand=tv("⌂  SANCTUARY\n    Display Engine",18,GOLD);brand.setTypeface(null,1);brand.setPadding(8,8,8,12);nav.addView(brand,new LinearLayout.LayoutParams(-1,78));
        addNav(nav,"◷  Prayer Times & Jamaat",v->showDashboard());
        addNav(nav,"▣  Display Preferences",v->showDisplay());
        addNav(nav,"☷  General Settings",v->showGeneral());
        addNav(nav,"⚑  Announcements & Ticker",v->showAnnouncement());
        Space sp=new Space(this);nav.addView(sp,new LinearLayout.LayoutParams(-1,0,1));
        TextView ver=tv("ANDROID TV\nRemote control ready",12,MUTED);ver.setPadding(8,8,8,8);nav.addView(ver,new LinearLayout.LayoutParams(-1,60));
        root.addView(nav,new LinearLayout.LayoutParams(300,-1));

        LinearLayout right=new LinearLayout(this);right.setOrientation(LinearLayout.VERTICAL);right.setPadding(24,0,0,0);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout titles=new LinearLayout(this);titles.setOrientation(LinearLayout.VERTICAL);
        TextView h=tv(masjid+"  —  Admin & Prayer Schedule",29,TEXT);h.setTypeface(null,1);
        TextView sub=tv("Central Masjid Control Terminal",14,MUTED);titles.addView(h,new LinearLayout.LayoutParams(-1,42));titles.addView(sub,new LinearLayout.LayoutParams(-1,26));
        header.addView(titles,new LinearLayout.LayoutParams(0,70,1));
        TextView live=tv("●  Live & Synchronized",15,TEAL);live.setGravity(Gravity.CENTER);live.setBackground(bg(0,Color.rgb(13,67,59),25));header.addView(live,new LinearLayout.LayoutParams(190,48));
        Button save=btn("✓  SAVE ALL CHANGES");save.setTextColor(BG);save.setBackground(bg(GOLD,GOLD,24));save.setOnClickListener(v->save());header.addView(save,new LinearLayout.LayoutParams(210,52));
        right.addView(header,new LinearLayout.LayoutParams(-1,78));
        main=new LinearLayout(this);main.setOrientation(LinearLayout.VERTICAL);main.setPadding(0,8,0,0);
        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);sc.addView(main);right.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        root.addView(right,new LinearLayout.LayoutParams(0,-1,1));setContentView(root);
    }

    void addNav(LinearLayout nav,String label,View.OnClickListener l){Button b=btn(label);b.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);b.setTextSize(15);b.setPadding(14,0,8,0);b.setOnClickListener(l);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,60);p.setMargins(0,5,0,5);nav.addView(b,p);}

    void title(String a,String b){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);TextView t=tv("●  "+a,27,TEXT);t.setTypeface(null,1);row.addView(t,new LinearLayout.LayoutParams(0,58,1));TextView st=tv(b,14,MUTED);st.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);row.addView(st,new LinearLayout.LayoutParams(250,58));main.addView(row);}
    void section(String s){TextView t=tv(s.toUpperCase(Locale.US),13,GOLD);t.setTypeface(null,1);t.setPadding(8,12,8,4);main.addView(t,new LinearLayout.LayoutParams(-1,38));}

    void showDashboard(){
        shell("Prayer Schedule Configuration","");title("PRAYER SCHEDULE CONFIGURATION","Node #MJ-742");
        LinearLayout cards=new LinearLayout(this);cards.setOrientation(LinearLayout.HORIZONTAL);
        for(int i=0;i<5;i++)cards.addView(prayerCard(i),new LinearLayout.LayoutParams(0,350,1));
        main.addView(cards,new LinearLayout.LayoutParams(-1,365));
        LinearLayout bottom=new LinearLayout(this);bottom.setPadding(0,12,0,0);
        bottom.addView(jummahCard(),new LinearLayout.LayoutParams(0,220,2));
        LinearLayout specialBox=new LinearLayout(this);specialBox.setOrientation(LinearLayout.VERTICAL);specialBox.setPadding(18,14,18,10);specialBox.setBackground(bg(BORDER,CARD,20));
        TextView sh=tv("☀  SPECIAL & CELESTIAL TIMES",18,TEXT);sh.setTypeface(null,1);specialBox.addView(sh,new LinearLayout.LayoutParams(-1,38));
        TextView sn=tv("Eid, Jummah, Sheri, Iftar & Sunrise",13,MUTED);specialBox.addView(sn,new LinearLayout.LayoutParams(-1,28));
        for(int i=0;i<SPECIAL.length;i++) if(i!=2) addSpecialRow(specialBox,i);
        bottom.addView(specialBox,new LinearLayout.LayoutParams(0,220,1));
        main.addView(bottom,new LinearLayout.LayoutParams(-1,232));
        main.postDelayed(()->{if(cards.getChildCount()>0)cards.getChildAt(0).requestFocus();},150);
    }

    View prayerCard(final int i){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(16,12,16,10);c.setBackground(bg(BORDER,CARD,18));
        TextView n=tv(PRAYERS[i],21,TEXT);n.setTypeface(null,1);c.addView(n,new LinearLayout.LayoutParams(-1,35));
        c.addView(tv("Athan",12,MUTED),new LinearLayout.LayoutParams(-1,25));
        Button ath=btn(format12(toMin(i)));ath.setTextSize(22);ath.setOnClickListener(v->timePicker(PRAYERS[i]+" Athan",toMin(i),m->{setPrayerTime(i,m);showDashboard();}));c.addView(ath,new LinearLayout.LayoutParams(-1,58));
        LinearLayout ol=new LinearLayout(this);ol.setGravity(Gravity.CENTER_VERTICAL);
        TextView ot=tv("Jama'at",12,MUTED);ol.addView(ot,new LinearLayout.LayoutParams(0,38,1));
        final TextView off=tv((offsets[i]>=0?"+":"")+offsets[i]+"m",12,GOLD);off.setGravity(Gravity.CENTER);ol.addView(off,new LinearLayout.LayoutParams(48,38));
        Button minus=btn("−"),plus=btn("+");minus.setTextSize(20);plus.setTextSize(20);
        final Button jamaat=btn(jamaatText(i));jamaat.setTextSize(22);jamaat.setTextColor(GOLD);
        minus.setOnClickListener(v->{if(auto[i]){offsets[i]--;off.setText((offsets[i]>=0?"+":"")+offsets[i]+"m");jamaat.setText(jamaatText(i));}});
        plus.setOnClickListener(v->{if(auto[i]){offsets[i]++;off.setText((offsets[i]>=0?"+":"")+offsets[i]+"m");jamaat.setText(jamaatText(i));}});
        ol.addView(minus,new LinearLayout.LayoutParams(44,38));ol.addView(plus,new LinearLayout.LayoutParams(44,38));c.addView(ol,new LinearLayout.LayoutParams(-1,40));
        jamaat.setOnClickListener(v->{if(!auto[i])timePicker(PRAYERS[i]+" Jama'at",fixedJ[i],m->{fixedJ[i]=m;showDashboard();});});c.addView(jamaat,new LinearLayout.LayoutParams(-1,58));
        LinearLayout toggle=new LinearLayout(this);toggle.setGravity(Gravity.CENTER_VERTICAL);TextView at=tv(auto[i]?"Auto Jama'at":"Fixed Schedule",12,MUTED);toggle.addView(at,new LinearLayout.LayoutParams(0,38,1));
        Button sw=btn(auto[i]?"AUTO":"FIXED");sw.setTextColor(auto[i]?TEAL:MUTED);sw.setTextSize(12);sw.setOnClickListener(v->{auto[i]=!auto[i];showDashboard();});toggle.addView(sw,new LinearLayout.LayoutParams(70,38));c.addView(toggle,new LinearLayout.LayoutParams(-1,42));
        return c;
    }

    String jamaatText(int i){return format12(auto[i]?toMin(i)+offsets[i]:fixedJ[i]);}
    int toMin(int i){int h=ah[i]%12;if(i!=0)h+=12;return h*60+am[i];}
    void setPrayerTime(int i,int total){total=(total+1440)%1440;ah[i]=(total/60)%12;if(ah[i]==0)ah[i]=12;am[i]=total%60;}
    String format12(int total){total=(total+1440)%1440;int h=total/60%12;if(h==0)h=12;return String.format(Locale.US,"%02d:%02d %s",h,total%60,total/60<12?"AM":"PM");}

    View jummahCard(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(18,14,18,10);c.setBackground(bg(BORDER,CARD,20));
        TextView h=tv("▣  Jumu'ah (Friday) Schedule",18,TEXT);h.setTypeface(null,1);c.addView(h,new LinearLayout.LayoutParams(-1,38));
        TextView s=tv("Independent Friday schedule",13,MUTED);c.addView(s,new LinearLayout.LayoutParams(-1,30));
        LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);
        addJummahButton(r,"1ST ADHAN",jummahAdhan,0);addJummahButton(r,"KHUTBAH",jummahKhutbah,1);addJummahButton(r,"SALAT (JAMA'AT)",jummahJamaat,2);
        c.addView(r,new LinearLayout.LayoutParams(-1,90));return c;
    }
    void addJummahButton(LinearLayout row,String label,int value,int type){Button b=btn(label+"\\n"+format12(value));b.setTextSize(15);b.setOnClickListener(v->timePicker(label,value,m->{if(type==0)jummahAdhan=m;else if(type==1)jummahKhutbah=m;else jummahJamaat=m;showDashboard();}));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,78,1);p.setMargins(5,0,5,0);row.addView(b,p);}
    void addSpecialRow(LinearLayout box,int i){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);TextView n=tv(SPECIAL[i],13,TEXT);r.addView(n,new LinearLayout.LayoutParams(0,36,1));Button b=btn(format12(special[i]));b.setTextSize(15);b.setOnClickListener(v->timePicker(SPECIAL[i],special[i],m->{special[i]=m;showDashboard();}));r.addView(b,new LinearLayout.LayoutParams(125,38));box.addView(r,new LinearLayout.LayoutParams(-1,38));}

    void showDisplay(){
        shell("Display Preferences","Control what is shown on the mosque TV");
        title("DISPLAY PREFERENCES","Saved on this TV");
        addSettingSwitch("24-hour clock","Use 24-hour format on the display","24h",false);
        addSettingSwitch("Show seconds","Display seconds on the main clock","seconds",false);
        addSettingSwitch("Show weather","Display weather beside the date","weather",true);
        addSettingSwitch("Show sunrise / sunset","Display solar times on the TV","sun",true);
        addSettingSwitch("Dim at night","Reduce display brightness at night","dim",true);
    }
    void addSettingSwitch(String name,String sub,String key,boolean def){
        LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(18,8,18,8);r.setBackground(bg(BORDER,CARD,18));
        LinearLayout t=new LinearLayout(this);t.setOrientation(LinearLayout.VERTICAL);TextView a=tv(name,18,TEXT);a.setTypeface(null,1);TextView b=tv(sub,12,MUTED);t.addView(a,new LinearLayout.LayoutParams(-1,30));t.addView(b,new LinearLayout.LayoutParams(-1,26));r.addView(t,new LinearLayout.LayoutParams(0,64,1));
        boolean on=prefs.getBoolean(key,def);Button sw=btn(on?"ON":"OFF");sw.setTextColor(on?TEAL:MUTED);sw.setOnClickListener(v->{prefs.edit().putBoolean(key,!prefs.getBoolean(key,def)).apply();showDisplay();});r.addView(sw,new LinearLayout.LayoutParams(100,48));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,82);p.setMargins(0,6,0,6);main.addView(r,p);
    }

    void showGeneral(){
        shell("General Settings","Masjid identity, date and location");
        title("GENERAL SETTINGS","Configuration");
        addAction("Masjid name",masjid,v->editName());
        addAction("Hijri date adjustment",prefs.getInt("hijri",0)+" days",v->numberDialog("Hijri date adjustment","hijri",-30,30));
        addAction("Sunrise adjustment",prefs.getInt("sunrise",0)+" min",v->numberDialog("Sunrise adjustment","sunrise",-60,60));
        addAction("Sunset adjustment",prefs.getInt("sunset",0)+" min",v->numberDialog("Sunset adjustment","sunset",-60,60));
        addAction("Coordinates",prefs.getString("lat","0.0000")+" , "+prefs.getString("lon","0.0000"),v->coordinates());
        addAction("Profile image","Choose image from USB / storage",v->{Intent in=new Intent(Intent.ACTION_OPEN_DOCUMENT);in.setType("image/*");in.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(in,22);});
    }
    void addAction(String a,String b,View.OnClickListener l){Button x=btn(a+"\n"+b+"    ›");x.setGravity(Gravity.CENTER_VERTICAL);x.setPadding(20,0,20,0);x.setOnClickListener(l);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,78);p.setMargins(0,5,0,5);main.addView(x,p);}
    void editName(){EditText e=new EditText(this);e.setSingleLine(true);e.setText(masjid);new AlertDialog.Builder(this).setTitle("Masjid name").setView(e).setNegativeButton("CANCEL",null).setPositiveButton("SAVE",(d,w)->{masjid=e.getText().toString();save();showGeneral();}).show();}
    void numberDialog(String title,String key,int min,int max){NumberPicker p=new NumberPicker(this);p.setMinValue(min);p.setMaxValue(max);p.setValue(prefs.getInt(key,0));new AlertDialog.Builder(this).setTitle(title).setView(p).setNegativeButton("CANCEL",null).setPositiveButton("SAVE",(d,w)->prefs.edit().putInt(key,p.getValue()).apply()).show();}
    void coordinates(){LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.VERTICAL);EditText lat=new EditText(this);lat.setHint("Latitude");lat.setText(prefs.getString("lat",""));EditText lon=new EditText(this);lon.setHint("Longitude");lon.setText(prefs.getString("lon",""));b.addView(lat);b.addView(lon);new AlertDialog.Builder(this).setTitle("Coordinates").setView(b).setNegativeButton("CANCEL",null).setPositiveButton("SAVE",(d,w)->prefs.edit().putString("lat",lat.getText().toString()).putString("lon",lon.getText().toString()).apply()).show();}

    void showAnnouncement(){
        shell("Announcements & Ticker","Create the scrolling message shown on the display");
        title("ANNOUNCEMENT & TICKER","Right → left");
        EditText e=new EditText(this);e.setText(announcement);e.setHint("Type announcement here");e.setTextSize(20);e.setTextColor(TEXT);e.setHintTextColor(MUTED);e.setGravity(Gravity.TOP);e.setPadding(20,15,20,15);e.setBackground(bg(BORDER,CARD,18));main.addView(e,new LinearLayout.LayoutParams(-1,150));
        Button save=btn("SAVE ANNOUNCEMENT");save.setTextColor(BG);save.setBackground(bg(GOLD,GOLD,22));save.setOnClickListener(v->{announcement=e.getText().toString();save();});main.addView(save,new LinearLayout.LayoutParams(-1,58));
        section("Ticker speed");SeekBar speed=new SeekBar(this);speed.setProgress(prefs.getInt("speed",50));speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int p,boolean f){prefs.edit().putInt("speed",p).apply();}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});main.addView(speed,new LinearLayout.LayoutParams(-1,65));main.addView(tv("Slow                                             Fast",12,MUTED),new LinearLayout.LayoutParams(-1,30));
    }

    void timePicker(String title,int initial,TimeSave callback){
        LinearLayout b=new LinearLayout(this);b.setGravity(Gravity.CENTER);NumberPicker hp=new NumberPicker(this),mp=new NumberPicker(this),pp=new NumberPicker(this);
        hp.setMinValue(1);hp.setMaxValue(12);mp.setMinValue(0);mp.setMaxValue(59);pp.setMinValue(0);pp.setMaxValue(1);pp.setDisplayedValues(new String[]{"AM","PM"});
        int h=initial/60%24;hp.setValue(h%12==0?12:h%12);mp.setValue(initial%60);pp.setValue(h<12?0:1);
        b.addView(hp,new LinearLayout.LayoutParams(130,220));b.addView(tv(":",30,TEXT),new LinearLayout.LayoutParams(35,220));b.addView(mp,new LinearLayout.LayoutParams(130,220));b.addView(pp,new LinearLayout.LayoutParams(120,220));
        new AlertDialog.Builder(this).setTitle(title).setView(b).setNegativeButton("CANCEL",null).setPositiveButton("OK",(d,w)->{int hh=hp.getValue()%12+(pp.getValue()==1?12:0);callback.done(hh*60+mp.getValue());}).show();
    }
    interface TimeSave{void done(int m);}
}
