package com.project.baihua.mcproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class BarcodeScanning extends Activity implements View.OnClickListener {

    String un,pw;
    private Button scanBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanning);
        scanBtn = (Button)findViewById(R.id.scan_button);

        scanBtn.setOnClickListener(this);

        Bundle upBundle = getIntent().getBundleExtra("upBundle");
        un = upBundle.getString("username");
        pw = upBundle.getString("password");
    }

    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            final String scanContent = scanningResult.getContents();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                    postParameters.add(new BasicNameValuePair("username",un));
                    postParameters.add(new BasicNameValuePair("password",pw));
                    postParameters.add(new BasicNameValuePair("uuid", scanContent));
                    String response = null;
                    try {
                        response = SimpleHttpClient
                                .executeHttpPost(
                                        "http://" + Config.IP + ":8080/MC/QRScanAndUpload",
                                        postParameters);
                        String res = response.toString();
                        System.out.println(res);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
