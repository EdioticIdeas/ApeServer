/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.JDBC;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import SERVER.util.ClientReqRes;
import SocketConnect.Request;

/**
 *
 * @author Shub
 */
class ReadThread extends Thread{

    @Override
    public void run() {

        try
		{
                    System.out.println("read started");
                    MainServer.l.setText(MainServer.l.getText().concat("Read started....\n"));

                    
			ServerSocket s = new ServerSocket(8189);
			for ( ; ; )
			{
                            try{
				Socket incoming = s.accept();
                                ObjectInputStream objectInputStream = new ObjectInputStream(incoming.getInputStream());
                                ClientReqRes crr = new ClientReqRes(incoming,(Request) objectInputStream.readObject());
                                MainServer.crrs.add(crr);
                                System.out.println("Read   :-" + crr.getRequest() + "    Size : "+ MainServer.crrs.size());
                                MainServer.l.setText(MainServer.l.getText().concat("\nRead   :-" + crr.getRequest() + "    Size : "+ MainServer.crrs.size()));
                            }
                            catch(Exception e){
                                System.out.println(e);
                            }
                            }
		}

		catch (Exception e)
		{
			System.out.println(e);
		}
    }
    
}    

