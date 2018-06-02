/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 *
 * @author Rizi
 */
public class FXMLDocumentController implements Initializable {
    
    private boolean buttonsEnabled= false;
    
    
    @FXML
    private Label myAddress;
    
    @FXML
    private VBox vbox;
    
    @FXML
    private TextField remoteIp;
    
    @FXML
    private TextField remotePort;
    
    @FXML
    private Button buttonCall;
    
    @FXML
    private Button buttonSend;
    
    @FXML
    private Button buttonAttachFile;
    
    @FXML
    private TextArea textArea;
    
    @FXML
    private VBox messagebox;
    
    @FXML
    private void connectBtnClick(ActionEvent event) {
        System.out.println(remoteIp.getText() + ":"+ remotePort.getText());
        try{
            Socket socket = new Socket (remoteIp.getText(), Integer.parseInt(remotePort.getText()));
            Socket msocket = new Socket (socket.getInetAddress(), socket.getPort()-1);
            ControlInfoReader r = new ControlInfoReader(socket);
            SocketsHandler.getInstance().Update(socket, this, r, msocket);
            new Thread (r).start();
        }
        catch (NumberFormatException | IOException e)
        {
            //To do
            //could not connect show dialog box
            
        }
        
    }
    
    @FXML
    private void sendFile(ActionEvent event)
    {
        try {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(SocketsHandler.active.getOutputStream()));
            br.write("Recieve File\r\n");
            br.flush();
            
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        File file = fileChooser.showOpenDialog(null);
        
//        System.out.println(file.getName());
        if(file!=null)
            sendFile(file);
//        System.out.println("File selected "+ file.getName());
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendFile(File file)
    {
        try {
            int port = SocketsHandler.controlInfoReaders.get(SocketsHandler.sockets.indexOf(SocketsHandler.active)).port;
            System.out.println("Sending file on port " +port);
            transferfileClient t = new transferfileClient(new Socket(SocketsHandler.active.getInetAddress().getHostAddress(), port));
            new Thread (new Runnable() {

                @Override
                public void run() {
                    try {
                        t.SendFile(file);
                    } catch (Exception ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedWriter br = null;
        String message = "File: " + file.getName();
        message = message +"\r\n";
        br = SocketsHandler.activeWriter;
        Label l;
        try {
            br.write(message);
            br.flush();
            l = new Label(message);
            l.setStyle("-fx-background: #53f442;");
            messagebox.getChildren().add(l);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void call(ActionEvent event)
    {
        try {
            BufferedWriter br = new BufferedWriter (new OutputStreamWriter (SocketsHandler.active.getOutputStream()));
            br.write("call\r\n");
            br.flush();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void sendMessage(ActionEvent event)
    {
        BufferedWriter br = null;
        String message = textArea.getText();
        message = message +"\r\n";
        br = SocketsHandler.activeWriter;
        Label l;
        try {
            br.write(message);
            br.flush();
            l = new Label(message);
            l.setStyle("-fx-background: #53f442;");
            l.setAlignment(Pos.CENTER_RIGHT);
            messagebox.getChildren().add(l);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            MasterServer masterServer = MasterServer.getInstance();
            myAddress.setText("Server listening at: "+masterServer.getLocalIp()+":"+masterServer.getPort());
            //masterServer.setVboxToAddAcceptedConnections(vbox);
            masterServer.setController(this);
            Thread th = new Thread(masterServer);
            th.setDaemon(true);
            th.start();
            //VBox.setVgrow(scrollpane, Priority.ALWAYS);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void enableButtons()
    {
        this.buttonAttachFile.setDisable(false);
        this.buttonCall.setDisable(false);
        this.buttonSend.setDisable(false);
        this.buttonsEnabled = true;
    }
    
    public boolean isButtonsEnabled()
    {
        return this.buttonsEnabled;
    }
    
    public VBox geVBox ()
    {
        return this.vbox;
    }
    
    public VBox getMessageBox()
    {
        return this.messagebox;
    }
    
}