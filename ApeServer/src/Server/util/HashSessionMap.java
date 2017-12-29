/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.util;

import java.util.Hashtable;
import java.util.Random;
import java.util.SortedMap;

/**
 *
 * @author Shub
 */
public class HashSessionMap implements SessionMap{

    
    private Hashtable<Long ,String> sessionTable;
    private static HashSessionMap map = new HashSessionMap();

    private HashSessionMap() {
    sessionTable = new Hashtable<>();
    }
    
    public static HashSessionMap getMap(){
        return map;
    }
    
    @Override
    public long Add(String Username) {
        Long session = Long.valueOf(-1);
        do{
            Random random = new Random();
            session = random.nextLong();
        }while(isSession(session) && (session != 0));
        sessionTable.put(session, Username);
        return session;
    }

    @Override
    public boolean hasSession(String Username) {
        return sessionTable.contains(Username);
    }

    @Override
    public boolean isSession(long SessionId) {
        return  sessionTable.containsKey(SessionId);
    }

    @Override
    public boolean exitSession(long SessionId) {
        if(isSession(SessionId));
        {
            sessionTable.remove(SessionId);
        }
        return true;
    }
}
