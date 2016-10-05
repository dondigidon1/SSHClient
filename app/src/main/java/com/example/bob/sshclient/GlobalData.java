package com.example.bob.sshclient;

import com.jcraft.jsch.Session;

public class GlobalData {
    private static Session sSession;

    public static void setSession(Session session){
        if(sSession!=null)
            sSession.disconnect();
        sSession=session;
    }

    public static Session getSession(){
        return sSession;
    }

}
