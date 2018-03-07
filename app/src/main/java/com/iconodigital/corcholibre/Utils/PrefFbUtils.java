package com.iconodigital.corcholibre.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by WorkPlace on 12/16/2017.
 */

public class PrefFbUtils {
    public static String KEY_fb_access_token = "fb_access_token";
    public static String KEY_fb_id = "fb_id";
    private static String KEY_fb_first_name = "fb_first_name";
    private static String KEY_fb_last_name = "fb_last_name";
    private static String KEY_fb_email = "fb_email";
    private static String KEY_fb_gender = "fb_gender";
    private static String KEY_fb_profileURL = "fb_profileURL";
    SharedPreferences prefs;
    private Activity activity;

    public PrefFbUtils(Activity activity) {
        this.activity = activity;
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public FacebookUserModel getFacebookUserModelPrefs() {
        FacebookUserModel facebookUserModel = new FacebookUserModel();
        facebookUserModel.setFb_access_token(prefs.getString(KEY_fb_access_token, null));
        facebookUserModel.setFb_id(prefs.getString(KEY_fb_id, null));
        facebookUserModel.setFb_first_name(prefs.getString(KEY_fb_first_name, null));
        facebookUserModel.setFb_last_name(prefs.getString(KEY_fb_last_name, null));
        facebookUserModel.setFb_email(prefs.getString(KEY_fb_email, null));
        facebookUserModel.setFb_gender(prefs.getString(KEY_fb_gender, null));
        facebookUserModel.setFb_profileURL(prefs.getString(KEY_fb_profileURL, null));

        return facebookUserModel;
    }

    public void saveFacebookUserModelPrefs(FacebookUserModel facebookUserModel) {
        SharedPreferences.Editor editor = prefs.edit();

        if (facebookUserModel.getFb_access_token() != null)
            editor.putString(KEY_fb_access_token, facebookUserModel.getFb_access_token());
        if (facebookUserModel.getFb_id() != null ||facebookUserModel.getFb_id().equalsIgnoreCase("-1"))
            editor.putString(KEY_fb_id, facebookUserModel.getFb_id());
        if (facebookUserModel.getFb_first_name() != null)
            editor.putString(KEY_fb_first_name, facebookUserModel.getFb_first_name());
        if (facebookUserModel.getFb_last_name() != null)
            editor.putString(KEY_fb_last_name, facebookUserModel.getFb_last_name());
        if (facebookUserModel.getFb_email() != null)
            editor.putString(KEY_fb_email, facebookUserModel.getFb_email());
        if (facebookUserModel.getFb_gender() != null)
            editor.putString(KEY_fb_gender, facebookUserModel.getFb_gender());
        if (facebookUserModel.getFb_profileURL() != null)
            editor.putString(KEY_fb_profileURL, facebookUserModel.getFb_profileURL());

        editor.apply(); // This line is IMPORTANT !!!
    }

    public void clearFacebookUserModelPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_fb_id,null);
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveAccessToken(String accessToken) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_fb_access_token, accessToken);
        editor.apply();
    }

    public void clearToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_fb_access_token, null);
        editor.apply();
    }
}
