/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rizi
 */
public class ControlInfoWriter {

    Socket socket;
    BufferedWriter br;
    BufferedReader re;
    int port;
    public ControlInfoWriter (Socket socket)
    {
        this.socket = socket;
        try {
            br = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            re = new BufferedReader (new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(ControlInfoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public int sendFile() throws InterruptedException
    {
        try{
            
            System.out.println(re.readLine());
        System.out.println("Into control info writer");
        br.write("Recieve File\r\n");
        br.flush();
        //port = re.read();
            System.out.println("port = "+port);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ControlInfoWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
//        Thread t =new Thread (new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    String temp;
//                    System.out.println("Sending command Recieve File");
//                    br.write("Recieve File\r\n");
//                    br.flush();
//                    temp = re.readLine();
//                    System.out.println(temp);
//                    port = Integer.parseInt(temp);
//                    System.out.println("port read " + port);
//                } catch (IOException ex) {
//                    Logger.getLogger(ControlInfoWriter.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//        t.start();
//        t.join();
        return port;
    }
    
}
