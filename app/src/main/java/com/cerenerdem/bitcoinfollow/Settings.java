package com.cerenerdem.bitcoinfollow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText edt_Saniye;
    EditText edt_Yuzde;
    public static Integer Saniye = 6000;
    public static Integer Oku = 1;
    public static Double Yuzde = 0.01;
    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edt_Saniye = (EditText) findViewById(R.id.edt_Saniye);
        edt_Yuzde = (EditText) findViewById(R.id.edt_Yuzde);
        edt_Saniye.setText(Saniye.toString());
        edt_Yuzde.setText(Yuzde.toString());

    }


    public void Kaydet(View view){

        //Kaç saniyede 1 güncellenecek.
        int saniye = Integer.parseInt(String.valueOf(edt_Saniye.getText()));
        Saniye = saniye;

        double yuzde = Double.parseDouble(String.valueOf(edt_Yuzde.getText()));
        Yuzde = yuzde;

        Toast.makeText(this,"Başarılı",Toast.LENGTH_LONG).show();
        Intent IntentNew = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(IntentNew);
    }
}
