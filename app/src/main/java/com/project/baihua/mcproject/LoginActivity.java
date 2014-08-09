package com.project.baihua.mcproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class LoginActivity extends Activity {

    EditText username, password;
    TextView error;
    Button login;
    private String resp;
    private String errorMsg;

    GoogleCloudMessaging gcm;
    Context context;
    String regId;

    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        username = (EditText) findViewById(R.id.et_un);
        password = (EditText) findViewById(R.id.et_pw);
        login = (Button) findViewById(R.id.btn_login);
        error = (TextView) findViewById(R.id.tv_error);

        if (TextUtils.isEmpty(regId)) {
            regId = registerGCM();
            Log.d("RegisterActivity", "GCM RegId: " + regId);
        }

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /**
                 * According with the new StrictGuard policy, running long tasks
                 * on the Main UI thread is not possible So creating new thread
                 * to create and execute http operations
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                        postParameters.add(new BasicNameValuePair("username",
                                username.getText().toString()));
                        postParameters.add(new BasicNameValuePair("password",
                                password.getText().toString()));

                        String response = null;
                        try {
                            response = SimpleHttpClient
                                    .executeHttpPost(
                                            "http://" + Config.IP + ":8080/MC/DevicePWLogin",//AppServer JSP
                                            postParameters);
                            String res = response.toString();
                            System.out.println(res);
                            resp = res.replaceAll("\\s+", "");

                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMsg = e.getMessage();
                        }
                    }
                }).start();

                try {
                    Thread.sleep(1000);

                    /**
                     * Inside the new thread we cannot update the main thread
                     * Update the main thread outside the new thread
                     */
                    JSONObject jsonObj = new JSONObject(resp);
                    boolean pass = jsonObj.getBoolean("success");
                    if(pass && !TextUtils.isEmpty(regId)) {
                        //error.setText("Login succeeded.");
                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                        Bundle upBundle = new Bundle();
                        upBundle.putString("username", username.getText().toString());
                        upBundle.putString("password", password.getText().toString());
                        i.putExtra("upBundle", upBundle);
                        i.putExtra("regId", regId);
                        startActivity(i);
                    } else {
                        if(!pass) {
                            error.setText("Login failed. Incorrect username and password.");
                            if (null != errorMsg && !errorMsg.isEmpty()) {
                                error.setText(errorMsg);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "RegId is empty! Click to register!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    error.setText(e.getMessage());
                }
            }
        });

    }

    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId
            );
        }
        return regId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

}
