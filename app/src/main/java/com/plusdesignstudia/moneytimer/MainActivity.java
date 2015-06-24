package com.plusdesignstudia.moneytimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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


    private Timer timer;
    private long start_timer;
    private TextView main_tf_work_money;
    private TextView main_tf_work_timer;

    private float hourRate;
    private ArrayAdapter<String> aa1;
    private ArrayList<Currency> currencies;
    private String currentCurrencySign;
    private boolean isSignBefore;


    private long seconds;
    private long minutes;
    private long hours;
    private AdView adView;
    private AdRequest adRequest;
    private AlertDialog alert;
    private MoneyAndTime currentSession;
    private MoneyAndTime todaysSessions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "Onzzz onCreate");
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
        currentSession = new MoneyAndTime(0,0);

        Date d = new Date();
        java.text.DateFormat df = DateFormat.getDateFormat(this);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        Log.v("createdSession", "\n\ncreatedSession.switch="+settings.getBoolean(df.format(d) + "ToggleButton", false));
        if(settings.getBoolean(df.format(d) + "ToggleButton", false)){
             hourRate = settings.getFloat("hour_rate", -1);
            Log.v("createdSession", "createdSession.hr="+hourRate);
            if (hourRate != -1) {
                long time =  (settings.getLong(df.format(d) + "time", 0) + (new Date().getTime() - settings.getLong(df.format(d) + "whenSaved", 0)) / 1000);
                float money = time * hourRate / 3600;
                currentSession = new MoneyAndTime(money, time);
                Log.v("createdSession", "createdSession=\n"+currentSession);
            }
        }

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
                ToggleButton sw = (ToggleButton) findViewById(R.id.switch1);
                if (hourRate != -1) {
                    if (sw != null && sw.isChecked()) {
                        todaysSessions = ManController.getTodaySessions(getApplicationContext());
                        currentSession.time++;// =(new Date().getTime() - start_timer)/1000;
                        long timeForCounter = currentSession.time + todaysSessions.time;
                        hours   = timeForCounter / 3600;
                        minutes = (timeForCounter - hours * 3600) / 60;
                        seconds = (timeForCounter - hours * 3600 - minutes * 60);
                        if (main_tf_work_timer != null)
                            main_tf_work_timer.setText(hours + ":" + minutes + ":" + seconds);
                        if (hourRate != -1) {
                            currentSession.money+= hourRate/3600;
                            float moneyForCounter = currentSession.money + todaysSessions.money;
                            if (main_tf_work_money != null) {
                                String space = " ";
                                if (currentCurrencySign.length() == 1) space = "";
                                if (isSignBefore)
                                    main_tf_work_money.setText(currentCurrencySign + space + String.format("%.2f", moneyForCounter));
                                else
                                    main_tf_work_money.setText(String.format("%.2f", moneyForCounter) + space + currentCurrencySign);
                            }
                        }
                    }
                }
            }
        }
    };

    private  void cerateSessionTimer(){
        Date d = new Date();
        java.text.DateFormat df = DateFormat.getDateFormat(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);

        if(settings.getBoolean("ToggleButton", false)){
            if(currentSession==null) {
                float hourRate = settings.getFloat("hour_rate", -1);
                if (hourRate != -1) {
                    long time = (long) (settings.getLong(df.format(d) + "time", 0) + (new Date().getTime() - settings.getLong(df.format(d) + "whenSaved", 0)) / 1000);
                    float money = time * hourRate / 3600;
                    currentSession = new MoneyAndTime(money, time);
                }
            }
        }
    }
    public void showRateChngerDialog(View v){
        Log.v("seee0", "ShowDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setEms(10);
        input.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_VARIATION_NORMAL |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setLayoutParams(lp);
        input.setTypeface(tf);
        input.setText(""+hourRate);
        builder.setView(input)
                .setTitle(getString(R.string.change_rate))
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startNewSession();
                        hourRate = Float.parseFloat(input.getText().toString());
                        Log.i("newHR","hewHR="+hourRate);
                        SharedPreferences s = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = s.edit();
                        editor.putFloat("hour_rate", hourRate);
                        editor.commit();
                        TextView hr_text= (TextView) findViewById(R.id.main_tf_hourly_rate_value);
                        hr_text.setText(""+hourRate);
                    }
                })
                .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alert = builder.create();
        alert.show();
    }


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

            currentCurrencySign = settings.getString("s2_value", "");
            isSignBefore = settings.getBoolean("ch_value", false);

            Date d = new Date();
            java.text.DateFormat df = DateFormat.getDateFormat(this);
            //Log.v("format", "FORMAT_"+df.format(d));
            if(currentSession==null) {
                currentSession       = new MoneyAndTime(0, 0);
                currentSession.money = settings.getFloat(df.format(d) + "money", (float) 0);
                currentSession.time  = settings.getLong(df.format(d) + "time", 0);
            }
            ToggleButton sw = (ToggleButton) findViewById(R.id.switch1);
            TextView main_tf_houtly_rate = (TextView) findViewById(R.id.main_tf_houtly_rate);
            TextView main_tf_hourly_rate_value = (TextView) findViewById(R.id.main_tf_hourly_rate_value);
            TextView main_tf_work_text = (TextView) findViewById(R.id.main_tf_work_text);
            main_tf_work_money = (TextView) findViewById(R.id.main_tf_work_money);
            main_tf_work_timer = (TextView) findViewById(R.id.main_tf_work_timer);
            Button resetButton = (Button) findViewById(R.id.reset_button);
            ToggleButton main = (ToggleButton) findViewById(R.id.switch1);
            resetButton.setTypeface(tf);
            main.setTypeface(tf);
            sw.setChecked(settings.getBoolean(df.format(d) + "ToggleButton",false));

            main_tf_houtly_rate.setTypeface(tf);
            main_tf_hourly_rate_value.setTypeface(tf);
            main_tf_work_text.setTypeface(tf);
            main_tf_work_money.setTypeface(tf);
            String space = " ";
            if(currentCurrencySign.length()==1) space="";
            if(isSignBefore)
                main_tf_work_money.setText(currentCurrencySign+space+String.format("%.2f",currentSession.money));
            else
                main_tf_work_money.setText(String.format("%.2f",currentSession.money)+space+currentCurrencySign);

            main_tf_work_timer.setTypeface(tf);
            if(main_tf_work_timer != null){
                hours   = currentSession.time / 3600;
                minutes = (currentSession.time - hours * 3600) / 60;
                seconds = (currentSession.time - hours * 3600 - minutes / 60);
                main_tf_work_timer.setText(hours + ":" + minutes + ":" + seconds);
            }
            if(hourRate!=(float)-1.0)
                main_tf_hourly_rate_value.setText("" + hourRate);
            else{
                setContentView(R.layout.settings);
                currentContentView = R.layout.settings;
                initSettingsPage();
            }
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
            AutoCompleteTextView a = (AutoCompleteTextView) findViewById(R.id.autocomplete);
            CheckBox ch = (CheckBox) findViewById(R.id.settings_ch_sign_before);
            hour_rate.setText("" + settings.getFloat("hour_rate", 0));
            hour_rate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    signBeforeClicked(view);
                    return false;
                }
            });

            ch.setChecked(settings.getBoolean("ch_value", true));
            a.setText(settings.getString("s2_value",""));
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
            Set<Currency> currencySet = new HashSet<Currency>();
            Locale[] locs = Locale.getAvailableLocales();

            for(Locale loc : locs) {
                try {
                    Currency tmp_curr=Currency.getInstance( loc );
                    currencySet.add(tmp_curr);
                    String tmp_str="";
                    if(Integer.valueOf(Build.VERSION.SDK_INT)>= Build.VERSION_CODES.KITKAT)
                        tmp_str= tmp_curr.getDisplayName(getResources().getConfiguration().locale);
                    if(aa1.getPosition(tmp_str)==-1) {
                        aa1.add(tmp_str);
                        aa1.add(tmp_curr.getSymbol());
                        if(!tmp_curr.getSymbol().equals(tmp_curr.getCurrencyCode()))
                            aa1.add(tmp_curr.getCurrencyCode());
                        if(!tmp_curr.getSymbol().equals(tmp_curr.getSymbol(getResources().getConfiguration().locale)))
                            aa1.add(tmp_curr.getSymbol(getResources().getConfiguration().locale));
                        currencies.add(tmp_curr);
                    }
                } catch(Exception exc)
                {
                   // Log.i("unsupported", "unsupported " + loc.toString());
                }
            }
            aa1.notifyDataSetChanged();
            AutoCompleteTextView a = (AutoCompleteTextView) findViewById(R.id.autocomplete);
            a.setAdapter(aa1);
            Log.v("adapter","adapter "+a.getAdapter().getCount());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void signBeforeClicked(View v){
        //Log.v("zzzxdsSignBeforeClicked","zzzxds_SignBeforeClicked");

        try {
            TextView example = (TextView) findViewById(R.id.settings_tf_hour_rate_example);
            AutoCompleteTextView a = (AutoCompleteTextView) findViewById(R.id.autocomplete);
            TextView hourRate = (TextView) findViewById(R.id.settings_tf_hour_rate_value);
            String sixteen = "";
            if(hourRate!=null){
                if(!hourRate.getText().equals("0.0"))
                    sixteen=hourRate.getText().toString();
            }else sixteen="16.25";
            String sign="";
            String space = " ";

            sign = (String) a.getText().toString();

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




    public void ToggleButtonClicked(View v){
        ToggleButton sw= (ToggleButton) v;
        if(sw.isChecked()){
            Date d = new Date();
            java.text.DateFormat df = DateFormat.getDateFormat(this);

            setWorkState(true);
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(df.format(d) + "ToggleButton",sw.isChecked());
            editor.commit();

            initMainPage();


        }else{
            setWorkState(false);
            startNewSession();
            saveSettings();

        }
    }

    public void resetButtonClicked(View v){
        start_timer = new Date().getTime();
        SharedPreferences s = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = s.edit();
        Date d = new Date();
        java.text.DateFormat df = DateFormat.getDateFormat(this);
        editor.putLong(df.format(d) + "time", 0);
        editor.putFloat(df.format(d) + "money", 0);
        editor.commit();
        hours=0;
        minutes=0;
        seconds=0;
        ToggleButton main = (ToggleButton) findViewById(R.id.switch1);
        main.setChecked(false);
        TextView main_tf_work_money = (TextView) findViewById(R.id.main_tf_work_money);
        TextView time_text = (TextView) findViewById(R.id.main_tf_work_timer);
        if(time_text!=null)
            time_text.setText("0:0:0");

        if (main_tf_work_money != null) {
            String space = " ";
            if (currentCurrencySign.length() == 1) space = "";

            if (isSignBefore)
                main_tf_work_money.setText(currentCurrencySign + space +  "0");
            else
                main_tf_work_money.setText( "0" + space + currentCurrencySign);
        }
        ManController.clearTodaysSessions(this);
        todaysSessions = new MoneyAndTime(0,0);
        currentSession= new MoneyAndTime(0,0);
    }

    private void setWorkState(boolean sw){
        if(sw) {
            setColorForWorkText(WHITE_COLOR);


        }else{
            setColorForWorkText(BLUE_COLOR);


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
                ToggleButton sw = (ToggleButton) findViewById(R.id.switch1);
                editor.putLong(   df.format(d) + "time",currentSession.time);
                editor.putLong(   df.format(d) + "whenSaved",new Date().getTime());
                editor.putBoolean(df.format(d) + "ToggleButton",sw.isChecked());
                editor.putFloat(  df.format(d) + "money", currentSession.money);
                editor.commit();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(currentContentView==R.layout.settings) {
            try {
                TextView hour_rate = (TextView) findViewById(R.id.settings_tf_hour_rate_value);
                AutoCompleteTextView a = (AutoCompleteTextView) findViewById(R.id.autocomplete);
                CheckBox ch = (CheckBox) findViewById(R.id.settings_ch_sign_before);
                float hr_old = hourRate;
                hourRate = Float.parseFloat(hour_rate.getText().toString());
                if(hr_old!=hourRate&&currentSession.time>0)
                    startNewSession();
                currentCurrencySign = (String) a.getText().toString();
                isSignBefore = ch.isChecked();

                editor.putFloat("hour_rate", hourRate);
                editor.putString("s2_value", currentCurrencySign);
                editor.putBoolean("ch_value", isSignBefore);
                editor.commit();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void  startNewSession(){
        Log.v("startNewSession", "oldTime="+currentSession.time);
        ManController.writeSession(this,hourRate, currentSession.time);
        currentSession = new MoneyAndTime(0,0);
    }
    
    public void okClicked(View v){
        saveSettings();
        setContentView(R.layout.main);
        initMainPage();
        currentContentView = R.layout.main;
    }

    public void clearAllSettings(View v){

        TextView hour_rate = (TextView) findViewById(R.id.settings_tf_hour_rate_value);
        AutoCompleteTextView a = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        CheckBox ch = (CheckBox) findViewById(R.id.settings_ch_sign_before);

        hour_rate.setText("");
        a.setText("");
        a.requestFocus();
        ch.setChecked(true);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();

        ManController.clearStatistic(this);
        currentSession = new MoneyAndTime(0,0);
        todaysSessions = new MoneyAndTime(0,0);
    }

    @Override
    public void onPause() {
        Log.v("onPause", "Onzzz onPause");
        super.onPause();
        saveSettings();
    }

    @Override
    public void onResume() {
        Log.v("onResume", "Onzzz onResume");
        super.onResume();
        initMainPage();
    }


    @Override
    public void onStop() {
        Log.v("onStop", "Onzzz onStop");
        super.onStop();
        saveSettings();
    }

    @Override
    public void onDestroy() {
        Log.v("onDestroy", "Onzzz onDestroy");
        super.onDestroy();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Date d = new Date();
        java.text.DateFormat df = DateFormat.getDateFormat(this);
        ToggleButton sw = (ToggleButton) findViewById(R.id.switch1);
        editor.putLong(   df.format(d) + "time",currentSession.time);
        editor.putLong(   df.format(d) + "whenSaved",new Date().getTime());
        editor.putBoolean(df.format(d) + "ToggleButton",sw.isChecked());
        editor.putFloat(  df.format(d) + "money", currentSession.money);
        editor.commit();
    }


    @Override
    public void onStart() {
        Log.v("onStart", "Onzzz onStart");
        super.onStart();
        cerateSessionTimer();


    }



    @Override
    public void onBackPressed(){
        saveSettings();
        switch (currentContentView){
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
