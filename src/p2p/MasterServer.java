/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
//import javafx.scene.layout.VBox;

/**
 *
 * @author Rizi
 */
public class MasterServer implements Runnable {
    
    private static MasterServer obj;
    private static int port;
    private static ServerSocket ssoc;
    private Random rand;
    private ArrayList<Socket> sockets;
    private VBox vbox;
    private FXMLDocumentController controller;

    private MasterServer() throws IOException
    {
        rand = new Random();
        port = rand.nextInt(2001)+5000;
        ssoc = new ServerSocket(port);
        sockets = new ArrayList();
        
    }
    
    public static MasterServer getInstance() throws IOException
    {
        if (obj == null)
            obj = new MasterServer();
        return obj;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public String getLocalIp ()
    {
        return ssoc.getInetAddress().getHostAddress();
    }
    
    public void setVboxToAddAcceptedConnections(VBox vbox)
    {
        this.vbox = vbox;
    }
    
    public void setController(FXMLDocumentController controler)
    {
        this.controller = controler;
    }

    @Override
    public void run() {
        while(!Thread.interrupted())
        {
            try {
                Socket socket=ssoc.accept();
                Socket msocket = new ServerSocket(socket.getLocalPort()-1).accept();
                ControlInfoReader r = new ControlInfoReader(socket);
                //Socket msocket = new ServerSocket(socket.getLocalPort()-1).accept();
                SocketsHandler.getInstance().Update(socket, this.controller, r, msocket);
                new Thread (r).start();
                
            } catch (IOException ex) {
                Logger.getLogger(MasterServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
