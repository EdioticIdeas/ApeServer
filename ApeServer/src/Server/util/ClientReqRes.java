/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SERVER.util;

import SocketConnect.Request;
import java.net.Socket;

/**
 *
 * @author Shub
 */
public class ClientReqRes {
    Socket socket;
    Request request;

    public ClientReqRes(Socket socket, Request request) {
        this.socket = socket;
        this.request = request;
    }

    public Socket getSocket() {
        return socket;
    }

    public Request getRequest() {
        return request;
    } 
}
