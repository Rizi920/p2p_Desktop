/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

/**
 *
 * @author Rizi
 */
public class transferfile
{
	Socket ClientSoc;

	DataInputStream din;
	DataOutputStream dout;
	
	transferfile(Socket soc)
	{
		try
		{
			ClientSoc=soc;						
			din=new DataInputStream(ClientSoc.getInputStream());
			dout=new DataOutputStream(ClientSoc.getOutputStream());
			System.out.println("FTP Client Connected ...");
			run();
			
		}
		catch(Exception ex)
		{
		}		
	}
	void SendFile() throws Exception
	{		
		String filename=din.readUTF();
		File f=new File(filename);
		if(!f.exists())
		{
			dout.writeUTF("File Not Found");
			return;
		}
		else
		{
			dout.writeUTF("READY");
			FileInputStream fin=new FileInputStream(f);
			int ch;
			do
			{
				ch=fin.read();
				dout.writeUTF(String.valueOf(ch));
			}
			while(ch!=-1);	
			fin.close();	
			dout.writeUTF("File Receive Successfully");							
		}
	}
	
	void ReceiveFile() throws Exception
	{
		String filename=din.readUTF();
		if(filename.compareTo("File not found")==0)
		{
			return;
		}
		File f=new File("C:/Users/"+System.getProperty("user.name")+"/Downloads/"+filename);
		String option;
		
		if(f.exists())
		{
			dout.writeUTF("File Already Exists");
			option=din.readUTF();
		}
		else
		{
			dout.writeUTF("SendFile");
			option="Y";
		}
			
			if(option.compareTo("Y")==0)
			{
				FileOutputStream fout=new FileOutputStream(f);
				int ch;
				String temp;
				do
				{
					temp=din.readUTF();
					ch=Integer.parseInt(temp);
					if(ch!=-1)
					{
						fout.write(ch);					
					}
				}while(ch!=-1);
				fout.close();
				dout.writeUTF("File Send Successfully");
			}
			else
			{
				return;
			}
			
	}


	public void run()
	{
		
			try
			{
			//System.out.println("Waiting for Command ...");
			String Command=din.readUTF();
			if(Command.compareTo("GET")==0)
			{
				System.out.println("\tGET Command Received ...");
				SendFile();
				
			}
			else if(Command.compareTo("SEND")==0)
			{
				System.out.println("\tSEND Command Receiced ...");				
				ReceiveFile();
				//continue;
			}
			else if(Command.compareTo("DISCONNECT")==0)
			{
				System.out.println("\tDisconnect Command Received ...");
				System.exit(1);
			}
			}
			catch(Exception ex)
			{
			}
		
	}
}