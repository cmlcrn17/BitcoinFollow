package com.cerenerdem.bitcoinfollow;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {


    ListView lst_BtcDegerleri;
    TextView txw_BtcDegeri;
    TextView txw_DegisimYuzdesi;
    int SaniyeDegeri = Settings.Saniye;
    String FormulDegeri="";
    double YuzdeDegeri = Settings.Yuzde;


    StringBuilder builder;
    StringBuilder builder_;
    ArrayList<String> degerler;
    ArrayList<String> degerler_;
    ArrayAdapter<String> veriadaptoru;
    ArrayAdapter<String> veriadaptoru_;
    Handler handler;
    Timer timer;


    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Settings_Activity) {

            Intent IntentNew = new Intent(getApplicationContext(), Settings.class);
            startActivity(IntentNew);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.eyes);
        setContentView(R.layout.activity_main);


        txw_BtcDegeri = (TextView) findViewById(R.id.txw_Btc);
        txw_DegisimYuzdesi = (TextView) findViewById(R.id.txw_DegisimYuzdesi);
        lst_BtcDegerleri = (ListView) findViewById(R.id.lst_BtcDegerleri);
        degerler = new ArrayList<String>();
        degerler_ = new ArrayList<String>();


        handler = new Handler();
        SaniyeDegeri = Settings.Saniye;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {


                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        load();

                    }
                });
            }
        };

        timer = new Timer();
        timer.schedule(timerTask,0,SaniyeDegeri);

    }





    private void load() { //Değerleri Yükle

        Request request = new Request.Builder()
                .url(Settings.BPI_ENDPOINT)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Değer Okurnurken Hata : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        parseBpiResponse(body);
                        parseBpiResponse_Time(body);
                    }
                });
            }
        });

    }

    private void parseBpiResponse_Time(String body) {
        try {
            builder = new StringBuilder();

            JSONObject jsonObject = new JSONObject(body);
            JSONObject timeObject = jsonObject.getJSONObject("time");
            builder.append(timeObject.getString("updated")).append("\n\n");

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            builder.append(usdObject.getString("rate")).append("$").append("\n");

 /*
            JSONObject gbpObject = bpiObject.getJSONObject("GBP");
            builder.append(gbpObject.getString("rate")).append("£").append("\n");

            JSONObject euroObject = bpiObject.getJSONObject("EUR");
            builder.append(euroObject.getString("rate")).append("€").append("\n");
*/


            txw_BtcDegeri.setText(builder.toString());//zaman + usd değerini yazar.

            degerler.add(builder.toString()); //String liste için gerekli
            veriadaptoru = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, degerler);
            lst_BtcDegerleri.setAdapter(veriadaptoru);

            if (veriadaptoru.getCount() > 4) {

                veriadaptoru.clear();
                lst_BtcDegerleri.setAdapter(veriadaptoru);
            }

        } catch (Exception e) {
            e.toString();
        }
    }

    private void parseBpiResponse(String body) {
        try {

            builder_ = new StringBuilder();

            JSONObject jsonObject = new JSONObject(body);
            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            builder_.append(usdObject.getString("rate"));

            degerler_.add(builder_.toString());
            veriadaptoru_ = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, degerler_);
            KontrolEt_(this);

        } catch (Exception e) {
            e.toString();
        }
    }

    public void KontrolEt_(MainActivity view) {
        try {

            txw_DegisimYuzdesi.setText("-");
            double sondeger_son = 0;
            double bironcekideger = 0;

            if (veriadaptoru_.getCount() > 2) {
                int index = veriadaptoru_.getCount();
                String sondeger = veriadaptoru_.getItem(index - 1);
                String bironceki = veriadaptoru_.getItem(index - 2);

                String sondeger_nokta = sondeger.replace(".", "");
                String sondeger_virgul = sondeger_nokta.replace(",", ".");
                sondeger_son = Double.valueOf(sondeger_virgul);


                String bironceki_nokta = bironceki.replace(".", "");
                String bironceki_virgul = bironceki_nokta.replace(",", ".");
                bironcekideger = Double.valueOf(bironceki_virgul);


                //Test örneği
                //double bironcekideger = 6.6800375;
                //double sondeger_son = 6.850375;

                Double formul = ((sondeger_son - bironcekideger) / bironcekideger) * 100;
                txw_DegisimYuzdesi.setText(formul.toString() + " Value:" + bironceki.toString() + " To:"  + sondeger.toString());

                if (sondeger_son > bironcekideger) {
                    txw_DegisimYuzdesi.setText("Rising") ;
                    YuzdeDegeri = Settings.Yuzde;

                    if (formul > YuzdeDegeri){
                        FormulDegeri = "-> % " + String.valueOf(formul.toString());
                    }

                    if (formul >= YuzdeDegeri) { //Bildirim Gönder
                        Intent intent = new Intent(this, MainActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder b = new NotificationCompat.Builder(this);

                        b.setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setTicker("PUMP")
                                .setContentTitle("BTC is Rising " + FormulDegeri.toString())
                                .setContentText("Value:" + bironceki.toString() + " To:"  + sondeger.toString())
                                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                .setContentIntent(contentIntent)
                                .setContentInfo("Info");


                        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(1, b.build());

                    }

                } else if (sondeger_son < bironcekideger){
                    txw_DegisimYuzdesi.setText("Falls");
                } else if (sondeger_son == bironcekideger){

                    txw_DegisimYuzdesi.setText("Stable");
                }
            }
        } catch (Exception ex) {
            ex.toString();
        }
    }
}
