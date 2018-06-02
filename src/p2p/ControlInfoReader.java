/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

/**
 *
 * @author Rizi
 */
public class ControlInfoReader implements Runnable {
    
    BufferedWriter br;
    BufferedReader re;
    Socket socket;
    ServerSocket ssoc;
    int port;
    
    public ControlInfoReader(Socket socket)
    {
        this.socket = socket;
        try {
            br = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            re = new BufferedReader (new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(ControlInfoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while(true)
        {
        try {
            
            String command = re.readLine();
            if (command.compareTo("Recieve File")==0)
            {
                ssoc = new ServerSocket (0);
                System.out.println(ssoc.getLocalPort());
                br.write("Send File\r\n");
                br.flush();
                br.write(ssoc.getLocalPort());
                br.flush();
                startFileTransferServer();
            }
            
            else if (command.compareTo("Send File")==0)
            {
                System.out.println("Send file command recieved");
                port=re.read();
                System.out.println("port = " +port);
            }
            
            else if (command.compareTo("call")==0)
            {
                String caller=socket.getInetAddress().getHostName();
                Platform.runLater(
                        () ->{
                            Alert al = new Alert(AlertType.CONFIRMATION, caller+ " is calling, recieve call?", ButtonType.YES, ButtonType.NO);
                            al.showAndWait();
                            try{
                                if(al.getResult() == ButtonType.YES)
                                {
                                    br.write("Call Accepted\r\n");
                                    br.flush();
                                    
                                    new Thread (new SimpleVoiceReceiver(socket.getLocalAddress().getHostAddress(), 4999));
                                    new Thread (new SimpleVoiceTransmiter (socket.getInetAddress().getHostAddress(), 4999));
                                }
                                else if (al.getResult() == ButtonType.NO)
                                {
                                    br.write("Call Rejected\r\n");
                                    br.flush();
                                }
                            }
                            catch (IOException ex)
                            {
                                Logger.getLogger(ControlInfoReader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                );
            }
            else if (command.compareTo("Call Accepted")==0)
            {
                new Thread (new SimpleVoiceReceiver(socket.getLocalAddress().getHostAddress(), 4999));
                new Thread (new SimpleVoiceTransmiter (socket.getInetAddress().getHostAddress(), 4999));
            }
            
            else
                continue;
            
        } catch (IOException ex) {
            Logger.getLogger(ControlInfoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        }//end while
    }
    
    public void startFileTransferServer()
    {
        new Thread (new Runnable() {

            @Override
            public void run() {
                try {
                    new transferfile(ssoc.accept());
                } catch (IOException ex) {
                    Logger.getLogger(ControlInfoReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
    
}
