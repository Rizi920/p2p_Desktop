/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rizi
 */
public class MessageReciever implements Runnable {
    
    Socket socket;
    int index;
    public MessageReciever(Socket socket)
    {
        this.socket = socket;
        index = SocketsHandler.msockets.indexOf(socket);
    }

    @Override
    public void run() {
        while(true)
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            SocketsHandler.unReadMessages.get(index).add(in.readLine());
            if(index==SocketsHandler.sockets.indexOf(SocketsHandler.active))
                SocketsHandler.getInstance().updateUi(index);
        } catch (IOException ex) {
            Logger.getLogger(MessageReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
