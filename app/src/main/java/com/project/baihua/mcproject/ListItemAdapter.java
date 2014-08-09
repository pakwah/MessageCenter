package com.project.baihua.mcproject;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kenhuen on 8/8/14.
 */
public class ListItemAdapter extends BaseAdapter {

    private ArrayList<JSONObject> subscriptions = new ArrayList<JSONObject>();
    private final Context context;

    public ListItemAdapter(Context contxt, ArrayList<JSONObject> list) {
        context = contxt;
        subscriptions = list;
    }

    public void updateSubscriptions(ArrayList<JSONObject> newSub) {
        checkOnMainThread();
        subscriptions = newSub;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return subscriptions.size();
    }

    @Override
    public Object getItem(int position) {
        return subscriptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView subIcon;
        TextView topicName;
        TextView topicDes;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            subIcon = (ImageView) convertView.findViewById(R.id.sub_icon);
            topicName = (TextView) convertView.findViewById(R.id.topic_name);
            topicDes = (TextView) convertView.findViewById(R.id.topic_des);
            convertView.setTag(new ViewHolder(subIcon, topicName, topicDes));
        } else {
            ViewHolder vH = (ViewHolder) convertView.getTag();
            subIcon = vH.subIcon;
            topicName = vH.topicName;
            topicDes = vH.topicDes;
        }

        JSONObject sub = (JSONObject) getItem(position);
        subIcon.setImageResource(R.drawable.feed_b);
        try {
            topicName.setText(sub.getString("name"));
            topicDes.setText(sub.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static void checkOnMainThread() {
        if (BuildConfig.DEBUG) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                throw new IllegalStateException("This method should be called from the Main Thread");
            }
        }
    }

    private static class ViewHolder {

        public final ImageView subIcon;
        public final TextView topicName;
        public final TextView topicDes;

        public ViewHolder(ImageView sI, TextView tN, TextView tD) {
            subIcon = sI;
            topicName = tN;
            topicDes = tD;
        }

    }

}


