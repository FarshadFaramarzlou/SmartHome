package com.example.farshad.smarthome.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.farshad.smarthome.Model.AuthenticationResponseModel;
import com.example.farshad.smarthome.Model.TokenModel;
import com.example.farshad.smarthome.Model.UserModel;
import com.example.farshad.smarthome.R;

/**
 * Created by Farshad on 7/19/2017.
 */

public class AppPreferenceTools {

    private SharedPreferences mPreference;
    private Context mContext;
    public static final String STRING_PREF_UNAVAILABLE = "string preference unavailable";

    public AppPreferenceTools(Context context) {
        this.mContext = context;
        this.mPreference = this.mContext.getSharedPreferences("app_preference", Context.MODE_PRIVATE);
    }


    /**
     * save the user authentication model to pref at sing up || sign in
     *
     * @param authUserModel
     */
    public void saveUserAuthenticationInfo(AuthenticationResponseModel authUserModel) {

        mPreference.edit()
                .putString(this.mContext.getString(R.string.pref_access_token), authUserModel.token.access_token)
                .putString(this.mContext.getString(R.string.pref_user_id), String.valueOf(authUserModel.user_profile.getUserid()))
                .putString(this.mContext.getString(R.string.pref_user_familyid), String.valueOf(authUserModel.user_profile.getFamilyId()))
                .putString(this.mContext.getString(R.string.pref_user_usertype), authUserModel.user_profile.getUserType())
                .putString(this.mContext.getString(R.string.pref_user_name), authUserModel.user_profile.getName())
                .putString(this.mContext.getString(R.string.pref_user_lastname), authUserModel.user_profile.getLastname())
                .putString(this.mContext.getString(R.string.pref_user_email), authUserModel.user_profile.getUsername())
                .putString(this.mContext.getString(R.string.pref_user_password), authUserModel.user_profile.getPassword())


                //.putString(this.mContext.getString(R.string.pref_user_image_url), authUserModel.imageUrl)
                .apply();
    }

    /**
     * save the user model when user profile updated
     *
     * @param userModel
     */
    public void saveUserModel(UserModel userModel) {
        mPreference.edit()
                .putString(this.mContext.getString(R.string.pref_user_id), String.valueOf(userModel.getUserid()))
                .putString(this.mContext.getString(R.string.pref_user_email), userModel.getUsername())
                .putString(this.mContext.getString(R.string.pref_user_name), userModel.getName())
                .putString(this.mContext.getString(R.string.pref_user_image_url), userModel.imageUrl)
                .apply();
    }

    public void saveTokenModel(TokenModel tokenModel) {
        mPreference.edit()
                .putString(this.mContext.getString(R.string.pref_access_token), tokenModel.access_token)
                .putString(this.mContext.getString(R.string.pref_refresh_token), tokenModel.refresh_token)
                /*.putLong(this.mContext.getString(R.string.pref_expire_in_sec), tokenModel.expire_in_sec)
                .putLong(this.mContext.getString(R.string.pref_expire_at), tokenModel.expire_at.getTime())
                .putString(this.mContext.getString(R.string.pref_refresh_token), tokenModel.refresh_token)
                .putString(this.mContext.getString(R.string.pref_app_id), tokenModel.app_id)*/
                .apply();
    }
    /**
     * get access token
     *
     * @return
     */

    public String getAccessToken() {
        return mPreference.getString(this.mContext.getString(R.string.pref_access_token), STRING_PREF_UNAVAILABLE);
    }
    /**
     * detect is user sign in
     *
     * @return
     */
    public boolean isAuthorized() {
        return !getAccessToken().equals(STRING_PREF_UNAVAILABLE);
    }

    /**
     * get user name
     *
     * @return
     */
    public String getUserName() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_name), "");
    }

    public String getImageProfileUrl() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_image_url), "");
    }

    public String getUserId() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_id), "");
    }

    public String getUserType() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_usertype), "");
    }

    public String getFamilyId() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_familyid), "");
    }

    /**
     * get refresh token
     *
     * @return
     */
    public String getRefreshToken() {
        return mPreference.getString(this.mContext.getString(R.string.pref_refresh_token), "");
    }

    /**
     * remove all prefs in logout
     */
    public void removeAllPrefs() {
        mPreference.edit().clear().apply();
    }
}
