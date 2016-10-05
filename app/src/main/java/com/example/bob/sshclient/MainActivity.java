package com.example.bob.sshclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private EditText mLogin;
    private EditText mPassword;
    private EditText mHost;

    private ViewGroup mRootView;
    private View mBlocker;

    private Storage mStorage;

    private StartSessionTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView=(ViewGroup)findViewById(R.id.rootView);

        mLogin = (EditText) findViewById(R.id.loginText);
        mPassword = (EditText) findViewById(R.id.passwordText);
        mHost = (EditText) findViewById(R.id.hostText);

        mStorage=new Storage(this);
        mLogin.setText(mStorage.getLogin());
        mPassword.setText(mStorage.getPassword());
        mHost.setText(mStorage.getHost());

        LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mBlocker=inflater.inflate(R.layout.connect_blocker,null,false);
        ViewGroup.LayoutParams blockerParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBlocker.setLayoutParams(blockerParams);

        Log.i("DEBUG","onCreate");
    }

    public void onStartClick(View view){
        Log.i("DEBUG","onStartClick");
        showBlocker();

        String login = mLogin.getText().toString();
        String password = mPassword.getText().toString();
        String host = mHost.getText().toString();

        if(GlobalData.getSession()!=null)
            GlobalData.getSession().disconnect();

        cancelTask();
        mTask=new StartSessionTask(this,login,password,host);
        mTask.execute();

    }

    private void showBlocker(){
        if(mBlocker.getParent()==null)
            mRootView.addView(mBlocker);
    }

    private void hideBlocker(){
        if(mBlocker.getParent()!=null)
            mRootView.removeView(mBlocker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTask();
    }

    void onConnect(Session session){

        String login = mLogin.getText().toString();
        String password = mPassword.getText().toString();
        String host = mHost.getText().toString();
        mStorage.update(host,login,password);

        hideBlocker();
        Log.i("DEBUG","onConnect");
        Toast.makeText(this,"Success",Toast.LENGTH_LONG).show();
        GlobalData.setSession(session);

        Intent intent=new Intent(this,DashboardActivity.class);
        intent.putExtra(DashboardActivity.LOGIN_PARAM,mLogin.getText().toString());
        intent.putExtra(DashboardActivity.HOST_PARAM,mHost.getText().toString());

        startActivity(intent);
    }

    void onFailConnect(){
        hideBlocker();
        Log.i("DEBUG","onFailConnect");
        Toast.makeText(this,"Fail",Toast.LENGTH_LONG).show();
    }

    private void cancelTask() {
        if (mTask != null)
            mTask.cancel(false);
        mTask = null;
    }
}

class StartSessionTask extends AsyncTask<Void, Void, Session> {

    private final WeakReference<MainActivity> mActivityRef;

    private final String mLogin;
    private final String mPassword;
    private final String mHost;

    StartSessionTask(MainActivity activity,String login, String password, String host) {
        mActivityRef=new WeakReference<>(activity);

        mLogin = login;
        mPassword = password;
        mHost = host;
    }

    @Override
    protected Session doInBackground(Void... params) {

        try {
            JSch sshChannel = new JSch();

            Session session = sshChannel.getSession(mLogin, mHost, 22);
            session.setPassword(mPassword);

            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "password");

            session.connect(30000);

            return session;
        } catch (JSchException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Session session) {
        MainActivity activity = mActivityRef.get();
        Log.i("DEBUG","onPostExecute "+activity);
        if (activity != null) {
            if (session != null)
                activity.onConnect(session);
            else
                activity.onFailConnect();
        }
    }
}
