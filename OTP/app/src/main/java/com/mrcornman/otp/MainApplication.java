package com.mrcornman.otp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.mrcornman.otp.models.models.MatchItem;
import com.mrcornman.otp.models.models.PhotoItem;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * Created by Jonathan on 5/9/2015.
 */
public class MainApplication extends Application {

    private final static String PARSE_APP_ID = "LW1vmtFBXLdNp5luR1PYKojwYe8lUfm5cRZ3ZNwn";
    private final static String PARSE_CLIENT_KEY = "mHDq5qR9y9Dq52optRUYPubqkH4BF7UoK3PBDq2z";

    @Override
    public void onCreate() {
        super.onCreate();

        // parse
        ParseObject.registerSubclass(MatchItem.class);
        ParseObject.registerSubclass(PhotoItem.class);
        Parse.initialize(getApplicationContext(), PARSE_APP_ID, PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        /*
        // push notifications
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });*/

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }
}