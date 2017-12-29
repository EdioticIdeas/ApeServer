/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.util;

/**
 *
 * @author Shub
 */
public interface SessionMap {
    
    public long Add(String Username);
    public boolean hasSession(String Username);
    public boolean isSession(long SessionId);
    public boolean exitSession(long SessionId);
    
    
}
