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


public class MessageListAdapter extends BaseAdapter {

    private ArrayList<JSONObject> msgSub = new ArrayList<JSONObject>();
    private final Context context;

    public MessageListAdapter(Context contxt, ArrayList<JSONObject> list) {
        context = contxt;
        msgSub = list;
    }

    public void updateSubscriptions(ArrayList<JSONObject> newSub) {
        checkOnMainThread();
        msgSub = newSub;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return msgSub.size();
    }

    @Override
    public Object getItem(int position) {
        return msgSub.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView mailIcon;
        TextView msgName;
        TextView msgDes;

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_msg, parent, false);
            mailIcon = (ImageView) convertView.findViewById(R.id.mail_icon);
            msgName = (TextView) convertView.findViewById(R.id.msg_name);
            msgDes = (TextView) convertView.findViewById(R.id.msg_des);
            convertView.setTag(new ViewHolder(mailIcon, msgName, msgDes));
        } else {
            ViewHolder vH = (ViewHolder) convertView.getTag();
            mailIcon = vH.mailIcon;
            msgName = vH.msgName;
            msgDes = vH.msgDes;
        }

        JSONObject sub = (JSONObject) getItem(position);
        mailIcon.setImageResource(R.drawable.mail);
        try {
            msgName.setText(sub.getString("content"));
            msgDes.setText(sub.getString("sendtime"));
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

        public final ImageView mailIcon;
        public final TextView msgName;
        public final TextView msgDes;

        public ViewHolder(ImageView mI, TextView mN, TextView mD) {
            mailIcon = mI;
            msgName = mN;
            msgDes = mD;
        }

    }

}
