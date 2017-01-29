package com.losfreitasapps.mercadobitcoinalert;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Moisés on 23/04/2016.
 */
public class vendelt extends Fragment {
    private static final NumberFormat numberFormat = NumberFormat.getInstance();
    private double comprei = 0.0;
    private double vender = 0.0;
    private double ganho = 0.0;
    private double passado = 0.0;
    private String Vender;
    private TextView textView5;
    private TextView textView7;
    private TextView textView2;
    ProgressDialog progressDialog;
    private TimerTask task;
    private final Handler handler = new Handler();
    private Timer timerAtual = new Timer();
    MediaPlayer mp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vender, container, false);

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mp = MediaPlayer.create(getActivity(), R.raw.compvend);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        textView5.setText(numberFormat.format(comprei));
        textView7 = (TextView) view.findViewById(R.id.textView7);
        textView7.setText(numberFormat.format(ganho));
        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.addTextChangedListener(editTextWatcher);
        EditText editText1 = (EditText) view.findViewById(R.id.editText1);
        editText1.addTextChangedListener(editText1Watcher);
        Button button3 = (Button) view.findViewById(R.id.button3);
        button3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        task = new TimerTask() {
                            public void run() {
                                handler.post(new Runnable() {
                                    public void run() {
                                        update();
                                    }
                                });
                            }
                        };
                        timerAtual.schedule(task, 1000, 30000);
                    }
                });

        return view;
    }

    private void update() {
        EAsyncTask e = new EAsyncTask();
        e.execute("https://www.mercadobitcoin.net/api/ticker_litecoin/");
    }

    private TextWatcher editText1Watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                ganho = Double.parseDouble(s.toString()) / 100.0;
            } catch (NumberFormatException e) {
                ganho = 0.0;
            }
            textView7.setText(numberFormat.format(ganho));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    };

    private TextWatcher editTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                comprei = Double.parseDouble(s.toString()) / 100.0;
            } catch (NumberFormatException e) {
                comprei = 0.0;
            }
            textView5.setText(numberFormat.format(comprei));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    };

    public class EAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "", "Carregando...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return exemploHttp(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            progressDialog.dismiss();
            if (data != null) {
                lerJson(data);
            }
        }
    }

    private String exemploHttp(String link) throws IOException {
        InputStream inputStream = null;
        String result = "Sem resultado";
        try {
            URL url = new URL(link);
            //System.out.println("URL: " + link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestMethod("GET");
            //System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());

            String response = convertInputStreamToString(in);

            //System.out.println("Response: " + response);

            return result = response;
        } catch (Exception e) {
            return result = "erro";
        }

    }

    public String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }

    public void lerJson(String json){

        try {
            JSONObject bitJson = new JSONObject(json);

            JSONObject ticker = bitJson.getJSONObject("ticker");
            String high = ticker.getString("high");
            String low = ticker.getString("low");
            String vol = ticker.getString("vol");
            String last = ticker.getString("last");
            String buy = ticker.getString("buy");
            String sell = ticker.getString("sell");
            String date = ticker.getString("date");

            vender = Double.parseDouble(buy);
            Vender = buy;
            double vd = vender;
            double c = comprei;
            double g = ganho;
            double lucro;
            double def;
            double pef;
            double cef;
            cef = c*1.007;
            def = vd - cef;
            pef = (def/cef)*100;
            lucro = pef - g - 0.7;
            if (pef > 0) {
                if(pef > 0.7){
                    if (lucro > 0) {
                        if(passado>vd){
                            textView2.setText("Vender!\nPreço de venda: R$" + Vender +"↓"+ "\nLucro: " + (pef-0.7) + "%");
                            mp.start();
                        }else{
                            textView2.setText("Vender!\nPreço de venda: R$" + Vender +"↑"+ "\nLucro: " + (pef-0.7) + "%");
                            mp.start();
                        }
                        if(passado==vd){
                            textView2.setText("Vender!\nPreço de venda: R$" + Vender +"-"+ "\nLucro: " + (pef-0.7) + "%");
                            mp.start();
                        }
                    }else{
                        if(passado>vd){
                            textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"↓"+ "\nLucro: " + (pef-0.7) + "%");
                        }
                        if(passado<vd){
                            textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"↑"+ "\nLucro: " + (pef-0.7) + "%");
                        }
                        if(passado==vd){
                            textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"-"+ "\nLucro: " + (pef-0.7) + "%");
                        }
                    }
                }else{
                    if(passado>vd){
                        textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"↓"+ "\nPrejuízo: " + (0.7-pef) + "%");
                    }
                    if(passado<vd){
                        textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"↑"+ "\nPrejuízo: " + (0.7-pef) + "%");
                    }
                    if(passado==vd){
                        textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"-"+ "\nPrejuízo: " + (0.7-pef) + "%");
                    }
                }
            } else {
                if(passado>vd){
                    textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"↓"+ "\nPrejuízo: " + (0.7-pef) + "%");
                }
                if(passado<vd){
                    textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"↑"+ "\nPrejuízo: " + (0.7-pef) + "%");
                }
                if(passado==vd){
                    textView2.setText("Não vender!\nPreço de venda: R$" + Vender +"-"+ "\nPrejuízo: " + (0.7-pef) + "%");
                }
            }
            passado = vd;

            //System.out.println("high: "+high);
            //System.out.println("low: "+low);
            //System.out.println("vol: "+vol);
            //System.out.println("last: "+last);
            //System.out.println("buy: "+buy);
            //System.out.println("sell: "+sell);
            //System.out.println("date: "+date);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
