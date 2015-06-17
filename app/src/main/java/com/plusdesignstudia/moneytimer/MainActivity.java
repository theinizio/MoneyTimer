package com.plusdesignstudia.moneytimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MoneyTimesSettings" ;
    public static int BLUE_COLOR;
    public static int WHITE_COLOR;
    public static int GREEN_COLOR;
    public static int RED_COLOR;
    private Typeface tf;
    private int currentContentView=0;
    private int tp_hour=-1;
    private int tp_minute=-1;
    private TextView selected_tv;

    private Timer timer;
    private long start_timer;
    private TextView main_tf_work_money;
    private TextView main_tf_work_timer;

    private float hourRate;
    private ArrayAdapter<String> aa1;
    private ArrayAdapter<String> aa2;
    private ArrayList<Currency> currencies;
    private String currentCurrencySign;
    private boolean isSignBefore;

    private float money;
    private long savedTime;
    private long seconds;
    private long minutes;
    private long hours;
    private String currentCurrency;
    private AdView adView;
    private AdRequest adRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        currentContentView = R.layout.main;

        BLUE_COLOR  = getResources().getColor(R.color.color_text_blue);
        WHITE_COLOR = getResources().getColor(R.color.color_text_white);
        GREEN_COLOR = getResources().getColor(R.color.color_text_green);
        RED_COLOR   = getResources().getColor(R.color.color_text_red);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans.ttf");


        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.BANNER);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout4ads);
        if(layout!=null)
            layout.addView(adView);
        adRequest = new AdRequest.Builder().build();


        timer = new Timer();
        TimerTask updateTime = new UpdateTimeTask();
        timer.schedule(updateTime, 0, 1000);
        initMainPage();

    }



    class UpdateTimeTask extends TimerTask {
        public void run(){
            TimerMethod();
        }
    }
    private void TimerMethod(){
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {
          //  Log.v("timerTask","timerTask "+savedTime);
            if (currentContentView == R.layout.main) {
                Switch sw = (Switch) findViewById(R.id.switch1);
                if (hourRate != -1) {
                    if (sw != null && sw.isChecked()) {

                        long time = new Date().getTime() - start_timer+savedTime;

                        hours   = time / 1000 / 3600;
                        minutes = (time - hours * 1000 * 3600) / 1000 / 60;
                        seconds = (time - hours * 1000 * 3600 - minutes * 1000 * 60) / 1000;
                        Log.v("times", ""+savedTime);//+"|"+(hours*60*60+minutes*60+seconds)*1000);
                        if (main_tf_work_timer != null)
                            main_tf_work_timer.setText(hours + ":" + minutes + ":" + seconds);
                        if (hourRate != -1) {
                            money = (float) time * hourRate / 1000 / 3600;
                            if (main_tf_work_money != null) {
                                String space = " ";
                                if (currentCurrencySign.length() == 1) space = "";

                                if (isSignBefore)
                                    main_tf_work_money.setText(currentCurrencySign + space + String.format("%.2f", money));
                                else
                                    main_tf_work_money.setText(String.format("%.2f", money) + space + currentCurrencySign);
                            }
                        }

                    }
                }
            }
        }
    };


    private void initMainPage(){
        //Log.v("initMainPage","zzzxds_initMainPage");
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            hourRate = settings.getFloat("hour_rate", (float) -1.0);
            if(hourRate==-1){
                setContentView(R.layout.settings);
                currentContentView = R.layout.settings;
                initSettingsPage();
            }
            currentCurrency = settings.getString("s1_value", "");
            currentCurrencySign = settings.getString("s2_value", "");
            isSignBefore = settings.getBoolean("ch_value", false);

            Date d = new Date();
            java.text.DateFormat df = DateFormat.getDateFormat(this);
            //Log.v("format", "FORMAT_"+df.format(d));

            money = settings.getFloat(df.format(d) + "money",(float)0);


            Switch sw = (Switch) findViewById(R.id.switch1);
            TextView main_tf_houtly_rate = (TextView) findViewById(R.id.main_tf_houtly_rate);
            TextView main_tf_hourly_rate_value = (TextView) findViewById(R.id.main_tf_hourly_rate_value);
            TextView main_tf_work_text = (TextView) findViewById(R.id.main_tf_work_text);
            main_tf_work_money = (TextView) findViewById(R.id.main_tf_work_money);
            main_tf_work_timer = (TextView) findViewById(R.id.main_tf_work_timer);
            sw.setChecked(settings.getBoolean("switch",false));
            main_tf_houtly_rate.setTypeface(tf);
            main_tf_hourly_rate_value.setTypeface(tf);
            main_tf_work_text.setTypeface(tf);
            main_tf_work_money.setTypeface(tf);
            String space = " ";
            if(currentCurrencySign.length()==1) space="";

            if(isSignBefore)
                main_tf_work_money.setText(currentCurrencySign+space+String.format("%.2f",money));
            else
                main_tf_work_money.setText(String.format("%.2f",money)+space+currentCurrencySign);

            main_tf_work_timer.setTypeface(tf);

            savedTime = settings.getLong(df.format(d)+"time",-1);
            if(main_tf_work_timer != null)
            if(savedTime!=-1){
                hours   = savedTime / 1000 / 3600;
                minutes = (savedTime - hours * 1000 * 3600) / 1000 / 60;
                seconds = (savedTime - hours * 1000 * 3600 - minutes / 1000 / 60) / 1000;

                    main_tf_work_timer.setText(hours + ":" + minutes + ":" + seconds);
            }

            if(hourRate!=(float)-1.0)
                main_tf_hourly_rate_value.setText("" + hourRate);
            else{
                setContentView(R.layout.settings);
                currentContentView = R.layout.settings;
                initSettingsPage();
            }
            start_timer=new Date().getTime();

        }catch (Exception e){
            e.printStackTrace();
        }
        adView.loadAd(adRequest);
    }


    private void initSettingsPage(){
        //Log.v("initSettings","zzzxds_initSettings");
        fillSpinners();
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            TextView hour_rate = (TextView) findViewById(R.id.settings_tf_hour_rate_value);
            Spinner s1 = (Spinner) findViewById(R.id.settings_spinner_currencies);

            Spinner s2 = (Spinner) findViewById(R.id.settings_spinner_signs);
            CheckBox ch = (CheckBox) findViewById(R.id.settings_ch_sign_before);

            hour_rate.setText("" + settings.getFloat("hour_rate", 0));
            hour_rate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    signBeforeClicked(view);
                    return false;
                }
            });


            s1.setSelection(aa1.getPosition(settings.getString("s1_value", "")));
            s2.setSelection(aa2.getPosition(settings.getString("s2_value", "")));
            ch.setChecked(settings.getBoolean("ch_value", true));

            s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    aa2.clear();
                    aa2.add(currencies.get(position).getSymbol());
                    if (!currencies.get(position).getSymbol().equals(currencies.get(position).getCurrencyCode()))
                        aa2.add(currencies.get(position).getCurrencyCode());
                    aa2.notifyDataSetChanged();
                    signBeforeClicked(null);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.i("spinner1", "nothing is selected");
                }
            });

            s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    signBeforeClicked(null);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.i("spinner1", "nothing is selected");
                }
            });
            signBeforeClicked(null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void fillSpinners(){
        //Log.v("fillSpinners", "zzzxds_fillSpinners");
        try{

            currencies = new ArrayList<>();
            aa1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
            aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            aa2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
            aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Set<Currency> currencySet = new HashSet<Currency>();
            Locale[] locs = Locale.getAvailableLocales();

            for(Locale loc : locs) {
                try {
                    Currency tmp_curr=Currency.getInstance( loc );
                    currencySet.add(tmp_curr);
                    String tmp_str = tmp_curr.getDisplayName(getResources().getConfiguration().locale);
                    if(aa1.getPosition(tmp_str)==-1) {
                        aa1.add(tmp_str);
                        currencies.add(tmp_curr);
                    }
                } catch(Exception exc)
                {
                   // Log.i("unsupported", "unsupported " + loc.toString());
                }
            }

            /*
            Set<Currency> s = Currency.getAvailableCurrencies();
            for(Currency loc : s) {
                try {
                    String tmp = loc.getDisplayName();
                    if(aa1.getPosition(tmp)==-1) {
                        aa1.add(tmp);
                        currencies.add(loc);
                    }
                }catch (IllegalArgumentException e){
                    Log.i("unsupported", "unsupported " + loc.toString());
                }
            }

*/
            Locale myLocale = getResources().getConfiguration().locale;
            Currency c = Currency.getInstance(myLocale);
            aa2.add(c.getSymbol());
            if(!c.getSymbol().equals(c.getCurrencyCode()))
                aa2.add(c.getCurrencyCode());
            /*
            aa1.sort(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });*/

            aa1.notifyDataSetChanged();
            aa2.notifyDataSetChanged();

            Spinner cs1= (Spinner) findViewById(R.id.settings_spinner_currencies);

            Spinner cs2= (Spinner) findViewById(R.id.settings_spinner_signs);
            cs1.setAdapter(aa1);

            cs2.setAdapter(aa2);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void signBeforeClicked(View v){
        //Log.v("zzzxdsSignBeforeClicked","zzzxds_SignBeforeClicked");

        try {
            TextView example = (TextView) findViewById(R.id.settings_tf_hour_rate_example);
            Spinner spinner = (Spinner) findViewById(R.id.settings_spinner_signs);
            TextView hourRate = (TextView) findViewById(R.id.settings_tf_hour_rate_value);
            String sixteen = "";
            if(hourRate!=null){
                if(!hourRate.getText().equals("0.0"))
                    sixteen=hourRate.getText().toString();
            }else sixteen="16.25";
            String sign="";
            String space = " ";

            if(aa2.getCount()==1) {
                sign=aa2.getItem(0);
            }else{
                sign = (String) spinner.getSelectedItem();
            }
            if(sign.length()==1) space="";
            if (sign.length() > 1) sign += " ";
            CheckBox signBefore = (CheckBox) findViewById(R.id.settings_ch_sign_before);
            if (signBefore.isChecked())
                example.setText(sign + space + sixteen);
            else
                example.setText(sixteen + space + sign);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showTimePickerDialog(View v){
        selected_tv = (TextView) v;
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        LinearLayout view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.time_picker_layout, null);
        builder2.setView(view)
                .setCancelable(true)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
                        if(tp!=null) {
                            tp_hour = tp.getCurrentHour();
                            tp_minute = tp.getCurrentMinute();

                            //TODO TIME FORMAT FROM LOCALE
                            String am_pm = "";
                            if (tp_hour > 12) am_pm = "PM";
                            else am_pm = "AM";
                            selected_tv.setText("" + tp_hour + ":" + tp_minute);
                        }
                    }
                })
                .setTitle(getString(R.string.choose_time));
        AlertDialog alert = builder2.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
                if(tp!=null)
                    tp.setCurrentHour(Integer.parseInt(selected_tv.getText().toString()));
            }
        });
        alert.show();
    }



    public void switchClick(View v){
        Switch sw= (Switch) v;
        if(sw.isChecked()){
            setWorkState(true);
            Log.v("Switch", "sw is checked");
            start_timer=new Date().getTime();
        }else{
            setWorkState(false);
            saveSettings();
            Log.v("Switch", "sw is UNchecked");
        }
    }


    private void setWorkState(boolean sw){
        if(sw) {
            setColorForWorkText(WHITE_COLOR);

            setLeftManColor(BLUE_COLOR);
            setRightManColor(WHITE_COLOR);
        }else{
            setColorForWorkText(BLUE_COLOR);

            setLeftManColor(WHITE_COLOR);
            setRightManColor(BLUE_COLOR);
        }
    }

    private void setColorForWorkText(int color){
        try{
            TextView work = (TextView) findViewById(R.id.main_tf_work_text);
            TextView money = (TextView) findViewById(R.id.main_tf_work_money);
            TextView timer = (TextView) findViewById(R.id.main_tf_work_timer);
            work.setTextColor(color);
            money.setTextColor(color);
            timer.setTextColor(color);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    private void setLeftManColor(int color){
        try {
            ImageView man = (ImageView) findViewById(R.id.left_man);
            if (color == BLUE_COLOR)
                man.setImageResource(R.drawable.icon_rest_blue);
            if (color == WHITE_COLOR)
                man.setImageResource(R.drawable.icon_rest_white);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setRightManColor(int color){
        try {
            ImageView man = (ImageView) findViewById(R.id.right_man);
            if (color == BLUE_COLOR)
                man.setImageResource(R.drawable.icon_work_blue);
            if (color == WHITE_COLOR)
                man.setImageResource(R.drawable.icon_work_white);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveSettings();
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            setContentView(R.layout.settings);
            initSettingsPage();
            currentContentView = R.layout.settings;
            return true;
        }
        if(id == R.id.action_statistics){
            setContentView(R.layout.statistics);
            currentContentView = R.layout.statistics;
            return true;
        }
        if(id == R.id.action_about){
            setContentView(R.layout.about);
            currentContentView = R.layout.about;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveSettings(){
        //Log.v("saveSettings","saveSettings");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        if(currentContentView==R.layout.main) {
            try {
                Date d = new Date();
                java.text.DateFormat df = DateFormat.getDateFormat(this);
                //Log.v("format", "FORMAT_" + df.format(d));
                Switch sw = (Switch) findViewById(R.id.switch1);
                savedTime = (hours*60*60+minutes*60+seconds)*1000;
                editor.putLong(df.format(d) + "time", savedTime);
                editor.putBoolean("switch",sw.isChecked());
                editor.putFloat(df.format(d) + "money", money);
                editor.commit();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(currentContentView==R.layout.settings) {
            try {
                TextView hour_rate = (TextView) findViewById(R.id.settings_tf_hour_rate_value);
                Spinner s1 = (Spinner) findViewById(R.id.settings_spinner_currencies);
                Spinner s2 = (Spinner) findViewById(R.id.settings_spinner_signs);
                CheckBox ch = (CheckBox) findViewById(R.id.settings_ch_sign_before);

                hourRate = Float.parseFloat(hour_rate.getText().toString());
                currentCurrency = (String) s1.getItemAtPosition(s1.getSelectedItemPosition());
                currentCurrencySign = (String) s2.getItemAtPosition(s2.getSelectedItemPosition());
                isSignBefore = ch.isChecked();

                editor.putFloat("hour_rate", hourRate);
                editor.putString("s1_value", (String) s1.getSelectedItem());
                editor.putString("s2_value", (String) s2.getSelectedItem());
                editor.putBoolean("ch_value", isSignBefore);
                editor.commit();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void clearAllSettings(View v){
        savedTime = 0;
        money = 0;
        hourRate = 0;
        start_timer = new Date().getTime();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onPause() {
        Log.v("onPause", "onPause");
        super.onPause();
        saveSettings();
    }


    @Override
    public void onBackPressed(){
        saveSettings();
        switch(currentContentView){
            case R.layout.main:
            default:
                finish();
                break;
            case R.layout.settings:
            case R.layout.statistics:
            case R.layout.about:
                setContentView(R.layout.main);
                initMainPage();
                currentContentView = R.layout.main;
                break;
        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


}
