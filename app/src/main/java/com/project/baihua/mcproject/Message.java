package com.project.baihua.mcproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Message extends Activity {
	
	private ArrayList<JSONObject> src_msg = new ArrayList<JSONObject>();
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messages);
		final ListView listView = (ListView) findViewById(R.id.listview_msg);
		
		Bundle msgBundle = getIntent().getBundleExtra("msgBundle");
		ArrayList<String> src_msg_str = msgBundle.getStringArrayList("msgBundle");
				
		for(String i : src_msg_str) {
			try {
				src_msg.add(new JSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		
		MessageListAdapter adapter = new MessageListAdapter(this, src_msg);
		listView.setAdapter(adapter);
	}
}
