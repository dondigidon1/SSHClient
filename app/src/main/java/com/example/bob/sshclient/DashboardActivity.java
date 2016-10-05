package com.example.bob.sshclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class DashboardActivity extends AppCompatActivity {

    public static final String HOST_PARAM="host_param";
    public static final String LOGIN_PARAM="login_param";


    private Session mSession;

    private TextView mHost;
    private TextView mLogin;

    private ViewGroup mRootView;
    private View mBlocker;

    private CommandExecutionTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mRootView=(ViewGroup)findViewById(R.id.rootView);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSession=GlobalData.getSession();
        if(mSession==null)
            finish();

        mHost=(TextView) findViewById(R.id.hostText);
        mLogin=(TextView) findViewById(R.id.loginText);

        Intent intent=getIntent();
        if (intent != null) {
            String host = intent.getStringExtra(HOST_PARAM);
            String login = intent.getStringExtra(LOGIN_PARAM);

            mHost.setText(host);
            mLogin.setText(login);
        }

        LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mBlocker=inflater.inflate(R.layout.execution_blocker,null,false);
        ViewGroup.LayoutParams blockerParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBlocker.setLayoutParams(blockerParams);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                closeTask();
                finish();
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    /**
     * Обработчик первой кнопки
     */
    public void onScript1Click(View view){
        closeTask();
        showBlocker();
        mTask=new CommandExecutionTask(this,mSession);

        mTask.execute("ls");//выполнить комманду ls
    }

    /**
     * Обработчик второй кнопки
     */
    public void onScript2Click(View view){
        closeTask();
        showBlocker();
        mTask=new CommandExecutionTask(this,mSession);

        mTask.execute("echo ku ku epta > fsociety.txt");//выполнить команду echo ku ku epta > fsociety.txt
    }

    /**
     * Обработчик третьей кнопки
     */
    public void onScript3Click(View view){
        closeTask();
        showBlocker();
        mTask=new CommandExecutionTask(this,mSession);

        mTask.execute("date");//выполнить комманду date
    }

    /**
     * Обработчик четвертой кнопки
     */
    public void onScript4Click(View view){
        closeTask();
        showBlocker();
        mTask=new CommandExecutionTask(this,mSession);

        mTask.execute("test_script.sh");//выполнить скрипт test_script.sh
    }

    void onFinishCommand(String output){
        Toast.makeText(this,"Finish command with result:\n"+output,Toast.LENGTH_LONG).show();
        hideBlocker();
        mTask=null;
    }


    void onFailCommand(){
        Toast.makeText(this,"Session break",Toast.LENGTH_LONG).show();
        closeTask();
        finish();
    }

    private void showBlocker(){
        if(mBlocker.getParent()==null)
            mRootView.addView(mBlocker);
    }

    private void hideBlocker(){
        if(mBlocker.getParent()!=null)
            mRootView.removeView(mBlocker);
    }

    private void closeTask(){
        if(mTask!=null)
            mTask.cancel(false);
        mTask=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeTask();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeTask();
    }
}

class CommandExecutionTask extends AsyncTask<String,Void,String>{

    private final WeakReference<DashboardActivity> mActivityRef;
    private final Session mSession;

    CommandExecutionTask(DashboardActivity activity, Session session){
        mActivityRef=new WeakReference<>(activity);
        mSession=session;
    }

    @Override
    protected String doInBackground(String... params) {
        String command=params[0];

        try {
            String output=execCommand(mSession,command);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String output) {
        DashboardActivity activity=mActivityRef.get();

        if(activity!=null){
            if(output!=null)
                activity.onFinishCommand(output);
            else
                activity.onFailCommand();
        }
    }

    private static String execCommand(Session session, String command) throws JSchException, IOException {

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.connect();

        InputStream input = channel.getInputStream();
        int data = input.read();

        StringBuilder builder = new StringBuilder();
        while (data != -1) {
            builder.append((char) data);
            data = input.read();
        }

        channel.disconnect();

        return builder.toString();
    }
}