/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

/**
 *
 * @author Rizi
 */
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;



public class SimpleVoiceReceiver implements Runnable
{

    
    String ip;
    int port;
    public SimpleVoiceReceiver(String localIP, int port)
    {
        this.ip= localIP;
        this.port = port;
    }
   
    
    public void run(){
        
    
	
	
            // ip and port declaration for rtp session
		String url = "rtp://"+ip+":"+port+"/audio/16";

		MediaLocator mrl = new MediaLocator(url);

		// Create a player for this rtp session
		Player player = null;
		try
		{
			player = Manager.createPlayer(mrl);
		} catch (Exception e)
		{
                    System.out.println("player creation exception");
                }

		if (player != null)
		{
			System.out.println("Player created: ");
			player.realize();
			// wait for realizing (construction of media player)
			while (player.getState() != Player.Realized)
			{
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("Starting player: ");
			player.start();
		} else
		{
			System.err.println("Player doesn't created: ");
			System.exit(-1);
		}
		
		
	}

}
   

