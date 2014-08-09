package com.project.baihua.mcproject;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainMenu extends TabActivity {

    ShareExternalServer appUtil;
    String regId;
    AsyncTask<Void, Void, String> shareRegidTask;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        appUtil = new ShareExternalServer();

        Bundle upBundle = getIntent().getBundleExtra("upBundle");
        regId = getIntent().getStringExtra("regId");
        final Context context = this;

        shareRegidTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = appUtil.shareRegIdWithAppServer(context, regId);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                shareRegidTask = null;
            }

        };
        shareRegidTask.execute(null, null, null);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = getTabHost();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("Topics");
        Intent i = new Intent(this, Topics.class);
        i.putExtra("upBundle", upBundle);
        tab1.setContent(i);

        tab2.setIndicator("Scan");
        i = new Intent(this, BarcodeScanning.class);
        i.putExtra("upBundle", upBundle);
        tab2.setContent(i);

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        initTabsAppearance(tabHost.getTabWidget());
    }

    private void initTabsAppearance(TabWidget tabWidget) {
        // Change background
        for(int i=0; i < tabWidget.getChildCount(); i++) {
            tabWidget.getChildAt(i).setBackgroundResource(R.drawable.tab_bg);
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

}
