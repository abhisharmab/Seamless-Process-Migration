package abhi.ds;

import java.util.*;

/* This class defines the Worker Manager. The main guys that has its footprint on each Worker 
 * The primary tasks of a Worker Manager class are:
 * 1. Start sending HeartBeat to the Process Manager 
 * 2. Start a Listener that monitors any request coming in from Process Manager or the other workers to do some work*/

public class WorkerManager implements Runnable {
	
	//List of Processes Running on this Worker 
	Map<Thread, MigratableProcess> activeProcessesonWorker;
	
	private String ipAdress; 
	private int portNumber; 
	private String pmIpAddress; //IP address of Master (Process Manager)
	private int pmPortNumber; //Port Number of Master (Process Manager)
	
	
   public int getWorkerPortNumber()
   {
	  return this.portNumber;
   }
  
   public String getWorkerIpAddress()
   {
	 return this.ipAdress;
   }
   
   public String getProcessManagerIP()
   {
	   return this.pmIpAddress;
   }
   
   public int getProcessManagerPort()
   {
	   return this.pmPortNumber;
   }
	  
	public WorkerManager(String ipAddress, int port, String pmIPAddress, int pmPort)
	{
		this.ipAdress = ipAddress; //HelperUtility.getLocalIPAddress();
		this.portNumber = port;
		this.pmIpAddress = pmIPAddress;
		this.pmPortNumber = pmPort;
		this.activeProcessesonWorker =  Collections.synchronizedMap(new HashMap<Thread, MigratableProcess>());
	}

	@Override
	public void run() 
	{

		//Send HeartBeat every 3 seconds
		Timer heartBeatSenderTimer =  new Timer();
		HeartBeatSender hbSender = new HeartBeatSender(this);
		heartBeatSenderTimer.schedule(hbSender, 0, HelperUtility.HB_SEND_DURATION);
		
		
		//Fire up a server socket (a.k.a Startup a Listener on the worker) to cater to requests from other people. 
		Thread requestListener = new Thread(new WorkerRequestListener(this));
		requestListener.start();
		
		try 
		{
			requestListener.join();
			
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			heartBeatSenderTimer.cancel();
		}
		
	}
	
}
