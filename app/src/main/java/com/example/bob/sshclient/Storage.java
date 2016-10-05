package com.example.bob.sshclient;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    private static final String SHARED_PREF_NAME="auth_data_share_pref";

    private static final String HOST_KEY="host_key";
    private static final String LOGIN_KEY="login_key";
    private static final String PASSWORD_KEY="password_key";

    private final SharedPreferences mData;

    private String mHost;
    private String mLogin;
    private String mPassword;

    public Storage(Context context){
        mData =context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        mHost=mData.getString(HOST_KEY,"");
        mLogin=mData.getString(LOGIN_KEY,"");
        mPassword=mData.getString(PASSWORD_KEY,"");
    }

    public String getHost(){
        return mHost;
    }

    public String getLogin(){
        return mLogin;
    }

    public String getPassword(){
        return mPassword;
    }

    public void update(String host, String login, String password) {
        if (mHost.equals(host) && mLogin.equals(login) && mPassword.equals(password))
            return;

        SharedPreferences.Editor editor = mData.edit();
        editor.putString(HOST_KEY, host);
        editor.putString(LOGIN_KEY, login);
        editor.putString(PASSWORD_KEY, password);
        editor.commit();

        mHost = host;
        mLogin = login;
        mPassword = password;
    }

}
