package com.project.baihua.mcproject;

/**
 * Created by kenhuen on 8/8/14.
 */
public interface Config {

    //used to share GCM regId with application server - using php app server
    //static final String APP_SERVER_URL = "http://192.168.1.17/gcm/gcm.php?shareRegId=1";

    // GCM server using java
    static final String APP_SERVER_URL =
            "http://" + Config.IP_MyTestServer + ":8080/GCM-App-Server/GCMNotification?shareRegId=1";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "401108751399"; //Used

    static final String MESSAGE_KEY = "message";

    static final String IP = "10.67.8.18"; //IP Address of the Application Server

    static final String IP_MyTestServer = "10.67.223.121";

}
