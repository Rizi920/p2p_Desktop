/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;





/**
 *
 * @author Rizi
 */
public class SocketsHandler{

    private static SocketsHandler instance = new SocketsHandler();
    protected static ArrayList<Socket> sockets;
    protected static ArrayList<Button> connectionButtons;
    public static Socket active;
    protected static ArrayList<ControlInfoReader> controlInfoReaders;
    protected static ArrayList<Socket> msockets;
    protected static ArrayList<ArrayList<String>> unReadMessages;
    
    protected static BufferedWriter activeWriter;
    protected static FXMLDocumentController controler;

    //private VBox vbox;
    
    private SocketsHandler()
    {
        sockets = new ArrayList();
        connectionButtons = new ArrayList();
        controlInfoReaders= new ArrayList();
        msockets = new ArrayList();
        unReadMessages = new ArrayList();
    }
    
    public static SocketsHandler getInstance()
    {
        return instance;
    }
    
    
    public void Update(Socket socket, FXMLDocumentController controller, ControlInfoReader  cIR, Socket msocket)
    {
        active = socket;
        sockets.add(socket);
        msockets.add(msocket);
        unReadMessages.add(new ArrayList<String> ());
        controlInfoReaders.add(cIR);
        Button button = new Button();
        button.setText(socket.getInetAddress().getHostName());
        connectionButtons.add(button);
        try {
            activeWriter = new BufferedWriter(new OutputStreamWriter(msocket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(SocketsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        controler = controller;
        new Thread(new MessageReciever(msocket)).start();
        button.setOnAction((ActionEvent e)->setActiveSocket(e));
        Platform.runLater(
            () -> {
                controller.geVBox().getChildren().add(button);
                if(!controller.isButtonsEnabled())
                {
                    controller.enableButtons();
                }
                }
            );
        
    }
    
    public void setActiveSocket(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        int index = connectionButtons.indexOf(button);
        if(sockets.indexOf(active)!= index)
        {
            active = sockets.get(index);
            try {
                activeWriter = new BufferedWriter (new OutputStreamWriter(msockets.get(index).getOutputStream()));
            } catch (IOException ex) {
                Logger.getLogger(SocketsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            updateUi(index);
            System.out.println("Active Socket Remote Ip = "+ active.getInetAddress().getHostAddress());
        }
    }
    
    public void updateUi(int index)
    {
        if(sockets.indexOf(active) == index)
        {
            Platform.runLater(
            () -> {
                ArrayList<String> messages=unReadMessages.get(index);
                for(int i =0; i< messages.size(); i++)
                {
                    Label l = new Label (messages.get(i));
                    l.setStyle("-fx-background: #DDDDDD;");
                    controler.getMessageBox().getChildren().add(l);
                }
                messages.clear();
            }
            );
        }
    }
    
}
