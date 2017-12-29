/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.JDBC;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import SERVER.util.ClientReqRes;
import SocketConnect.Response;

/**
 *
 * @author Shub
 */
public class WriteThread extends Thread{

    @Override
    public void run() {
        
        System.out.println("write started");
        MainServer.l.setText(MainServer.l.getText().concat("Write started....\n"));

        
        for(;;){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(WriteThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(MainServer.crrs.isEmpty())
                continue;
            ClientReqRes crr = MainServer.crrs.remove();
           try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(crr.getSocket().getOutputStream());
                Response obj = (Response) Server.util.SreverUtilities.getDatabaseResponse(crr.getRequest().getType(), crr.getRequest().getRequestedObject());
                objectOutputStream.writeObject(obj);
                System.out.println("write   : - "  + crr.getSocket().getRemoteSocketAddress()+  "   Size  :" + MainServer.crrs.size());
                MainServer.l.setText(MainServer.l.getText().concat("\nwrite   : - "  + crr.getSocket().getRemoteSocketAddress()+  "   Size  :" + MainServer.crrs.size()));
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        }
    
    
    
}
