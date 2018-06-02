/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;


import java.util.Vector;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NotRealizedError;
import javax.media.Processor;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;


/**
 * 
 * @author Rizi
 *
 */

public class SimpleVoiceTransmiter implements Runnable
{
    String ip;
    int port;
    

    public SimpleVoiceTransmiter(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }
    
	public void run()
	{
                CaptureDeviceInfo di = null;
		Format format;
		format = new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
			// First find a capture device that will capture linear audio
			// data at 8bit 8Khz
		AudioFormat captureFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
	
		Vector devices = CaptureDeviceManager.getDeviceList(captureFormat);
	
			
	
		if (devices.size() > 0)
			{
				di = (CaptureDeviceInfo) devices.elementAt(0);
			} else
			{
				System.err.println("No capture devices");
				// exit if we could not find the relevant capturedevice.
				System.exit(-1);
			}
		

		// Create a processor for this capturedevice & exit if we
		// cannot create it
		Processor processor = null;
		try
		{
               		processor = Manager.createProcessor(di.getLocator());
		} catch (Exception e)
		{
                    System.out.println("Procesor Exception: ");
                }

		// configure the processor
		processor.configure();

		while (processor.getState() != Processor.Configured)
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				 System.out.println("interupted processor exception: ");
			}
		}

		processor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW_RTP));

		TrackControl track[] = processor.getTrackControls();

		boolean encodingOk = false;

		// Go through the tracks and try to program one of them to
		// output gsm data.

		for (int i = 0; i < track.length; i++)
		{
			if (!encodingOk && track[i] instanceof FormatControl)
			{
				if (((FormatControl) track[i]).setFormat(format) == null)
				{

					track[i].setEnabled(false);
				} else
				{
					encodingOk = true;
				}
			} else
			{
				// we could not set this track to gsm, so disable it
				track[i].setEnabled(false);
			}
		}

		// At this point, we have determined where we can send out
		// gsm data or not.
		// realize the processor
		if (encodingOk)
		{
			if (!new net.sf.fmj.ejmf.toolkit.util.StateWaiter(processor).blockingRealize())
			{
				System.err.println("Failed to realize");
				return;
			}

			// get the output datasource of the processor and exit
			// if we fail
			DataSource ds = null;

			try
			{
				ds = processor.getDataOutput();
			} catch (NotRealizedError e)
			{
				e.printStackTrace();
				System.exit(-1);
			}

			// hand this datasource to manager for creating an RTP
			// datasink our RTP datasink will multicast the audio
			try
			{
				String url = "rtp://"+ip+":"+port+"/audio/16";

				MediaLocator m = new MediaLocator(url);

				DataSink d = Manager.createDataSink(ds, m);
				d.open();
				d.start();
				
				System.out.println("Starting processor");
				processor.start();
				Thread.sleep(30000);
			} catch (Exception e)
			{
				            System.out.println("RTP connection exception: ");
				System.exit(-1);
			}
		}

	}

}