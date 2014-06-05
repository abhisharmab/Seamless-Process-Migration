package abhi.ds;

/* This class defines a HeartBeat Request Handler which gets spawned as a thread by the HeartBeat Request Listener.
 Since multiple workers could simultaneously hit the Process Manager by sending their heart beat. The handler takes that signal 
 and caters the heartbeat request. A Map of all the workers that are alive in maintained. If heartbeat from a new worker is recieved then its added 
 to the internal map of workers. 
 */
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.Map;
import java.util.TreeMap;
import java.io.*;

public class HeartBeatRequestHandler implements Runnable {

	private ProcessManager processManager;
	private Socket hbSocket;
	
	public HeartBeatRequestHandler(Socket hbSocket, ProcessManager processManager)
	{
		this.processManager = processManager;
		this.hbSocket = hbSocket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(this.hbSocket == null){
			try {
				throw new Exception("Socket is invalid. Problem occured");
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
			
		Signal signal = null;
		
		try {
			ObjectInputStream objStream = new ObjectInputStream(this.hbSocket.getInputStream());
			signal = (Signal) objStream.readObject(); //De-serialze the Signal Object sent over the wire
			HeartBeatSignalHandler(signal);
			objStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void HeartBeatSignalHandler(Signal signal) {
		// TODO Auto-generated method stub
		switch(signal.getSignalType())
		{
			case HB:
				if(this.processManager.GetWorkerList() != null)
				{
					synchronized(this.processManager.GetWorkerList()) //Only one thread should mutate the Map at a given instance of time.
					{
					    if (this.processManager.GetWorkerList().containsKey(signal.getWorkerIp() +":" + String.valueOf(signal.getWorkerPort()))) 
					    {
					    	//If worker already present just recorded the latest timestamp.
					    	this.processManager.GetWorkerList().get(signal.getWorkerIp() +":" + String.valueOf(signal.getWorkerPort())).setHBTimeStamp(System.currentTimeMillis());
					     } 
					    else 
					    {	//If new worker then add this worker to the internal map that maintains the list of workers
					        WorkerInfo4Display newWorker = new WorkerInfo4Display(signal.getWorkerIp(), signal.getWorkerPort(), System.currentTimeMillis());
	 
					        this.processManager.GetWorkerList().put(signal.getWorkerIp() +":" + String.valueOf(signal.getWorkerPort()), newWorker);
					      }
					      
					    // Once this is done, Also update the the list of processes running on each worker into a internal map. 
					    // Since the last heartbeat things might have changed so we need to have the latest information about processes on each worker.
					    this.processManager.GetWorkerList().get(signal.getWorkerIp() +":" + String.valueOf(signal.getWorkerPort())).syncLatestProcessList(signal.getRunningProcessList());
					}
					
				}
				
				break;
				
			default:
				System.out.println("Invalid Signal sent to Process Manager");
			break; 	
		}
		
	}

}
