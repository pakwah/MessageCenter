package com.project.baihua.mcproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Topics extends Activity implements AdapterView.OnItemClickListener {

    private String un, pw, resp_topic, resp_msg;
    private ArrayList<JSONObject> src_topic = new ArrayList<JSONObject>();
    private ArrayList<String> src_msg = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);
        final ListView listView = (ListView) findViewById(R.id.listview);

        Bundle upBundle = getIntent().getBundleExtra("upBundle");
        un = upBundle.getString("username");
        pw = upBundle.getString("password");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("username",
                        un));

                String response = null;
                try {
                    response = SimpleHttpClient
                            .executeHttpPost(
                                    "http://" + Config.IP + ":8080/MC/TopicFetch",
                                    postParameters);
                    String res = response.toString();
                    System.out.println(res);
                    resp_topic = res.replaceAll("\\s+", " ");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Thread.sleep(1000);

            /**
             * Inside the new thread we cannot update the main thread
             * Update the main thread outside the new thread
             */
            JSONObject jsonObj = new JSONObject(resp_topic);
            int numOfSubscription = jsonObj.length();
            for(int i = 0; i < numOfSubscription; ++i) {
                src_topic.add(jsonObj.getJSONObject(i + ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListItemAdapter adapter = new ListItemAdapter(this, src_topic);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position,
                            long id) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("username",
                        un));
                postParameters.add(new BasicNameValuePair("password", pw));
                try {
                    postParameters.add(new BasicNameValuePair("topic_id", src_topic.get(position).getString("id")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String response = null;
                try {
                    response = SimpleHttpClient
                            .executeHttpPost(
                                    "http://" + Config.IP + ":8080/MC/MessageFetch",
                                    postParameters);
                    String res = response.toString();
                    System.out.println(res);
                    resp_msg = res.replaceAll("\\s+", " ");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Thread.sleep(1000);

            /**
             * Inside the new thread we cannot update the main thread
             * Update the main thread outside the new thread
             */
            JSONObject jsonObj = new JSONObject(resp_msg);

            for(int i = 0; i < jsonObj.length(); ++i) {
                src_msg.add(jsonObj.getJSONObject(i + "").toString());
            }

        } catch (Exception e) {}

        Bundle msgBundle = new Bundle();
        msgBundle.putStringArrayList("msgBundle", src_msg);
        Intent i = new Intent(this, com.project.baihua.mcproject.Message.class);
        i.putExtra("msgBundle", msgBundle);
        startActivity(i);
    }

}
