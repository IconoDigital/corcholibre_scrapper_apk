package com.iconodigital.corcholibre.Utils;

import android.app.Activity;

/**
 * Created by WorkPlace on 12/16/2017.
 */

public class FacebookUserModel {
    private static FacebookUserModel facebookUserModel = null;
    private String fb_access_token;
    private String fb_id;
    private String fb_first_name;
    private String fb_last_name;
    private String fb_email;
    private String fb_gender ;
    private String fb_profileURL;

    FacebookUserModel() {
    } //no specifying access specifier makes this to be package private(accessible within same package)

    public static FacebookUserModel getFacebookUserSingletonModel(Activity activity) {
        if(facebookUserModel==null){
            PrefFbUtils prefFbUtils=new PrefFbUtils(activity);
            facebookUserModel=prefFbUtils.getFacebookUserModelPrefs();
        }
        return facebookUserModel;
    }

    public String getFb_access_token() {
        return fb_access_token;
    }

    //setters
    public void setFb_access_token(String fb_access_token) {
        this.fb_access_token = fb_access_token;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getFb_first_name() {
        return fb_first_name;
    }

    public void setFb_first_name(String fb_first_name) {
        this.fb_first_name = fb_first_name;
    }

    public String getFb_last_name() {
        return fb_last_name;
    }

    public void setFb_last_name(String fb_last_name) {
        this.fb_last_name = fb_last_name;
    }

    public String getFb_email() {
        return fb_email;
    }

    public void setFb_email(String fb_email) {
        this.fb_email = fb_email;
    }

    public String getFb_gender() {
        return fb_gender;
    }

    public void setFb_gender(String fb_gender) {
        this.fb_gender = fb_gender;
    }

    public String getFb_profileURL() {
        return fb_profileURL;
    }

    public void setFb_profileURL(String fb_profileURL) {
        this.fb_profileURL = fb_profileURL;
    }
}
