package com.iconodigital.corcholibre;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.iconodigital.corcholibre.Utils.FacebookUserModel;
import com.iconodigital.corcholibre.Utils.PrefFbUtils;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * Created by sourav on 22-Nov-16.
 */

public class LoginActivity extends AppCompatActivity {

    final String TAG = "LOGIN_ACTIVITY";
    CallbackManager callbackManager;
    LoginButton loginButton;
    FacebookUserModel facebookUserModel = null;
    PrefFbUtils prefFbUtils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        ///AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();//to be created before setingContentView
        setContentView(R.layout.activity_login);
        prefFbUtils = new PrefFbUtils(this);
        facebookUserModel = FacebookUserModel.getFacebookUserSingletonModel(this);
        getKeyHash();


        if (facebookUserModel.getFb_id() == null || facebookUserModel.getFb_id().equals("-1")) {
            // Stay at the current activity.
        } else {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();

        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();

                facebookUserModel.setFb_access_token(accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                                // Getting FB User Data
                                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("Please wait..");
                                progressDialog.setMessage("Saving your data..");
                                try {
                                    if (progressDialog.isShowing() == false)
                                        progressDialog.show();
                                    String id = "dummyId";
                                    if (jsonObject.has("id")) {
                                        id = jsonObject.getString("id");
                                        facebookUserModel.setFb_id(id);
                                    }

                                    if (jsonObject.has("first_name"))
                                        facebookUserModel.setFb_first_name(jsonObject.getString("first_name"));
                                    if (jsonObject.has("last_name"))
                                        facebookUserModel.setFb_last_name(jsonObject.getString("last_name"));
                                    if (jsonObject.has("email"))
                                        facebookUserModel.setFb_email(jsonObject.getString("email"));
                                    if (jsonObject.has("gender"))
                                        facebookUserModel.setFb_gender(jsonObject.getString("gender"));

                                    URL profile_picURL;
                                    try {
                                        profile_picURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                        String url_str = profile_picURL.toString();
                                        facebookUserModel.setFb_profileURL(url_str);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }

                                    prefFbUtils.saveFacebookUserModelPrefs(facebookUserModel);//save facebook data in preferences

                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();

                                } catch (Exception e) {
                                    String str = e.getMessage();
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                Log.d(TAG, "Login attempt failed.");
                Toast.makeText(LoginActivity.this, "Error while logging in. " + error, Toast.LENGTH_LONG).show();
                deleteAccessToken();
            }
        });
    }

    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null) {
                    //User logged out
                    prefFbUtils.clearToken();
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(R.string.login);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.com.iconodigital.com.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.corcholibre",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}


