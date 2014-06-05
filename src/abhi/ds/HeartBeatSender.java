package abhi.ds;

/* This class runs as a separate thread on each Worker in the Distributed System
 * The main job of this class is to send a periodic heart-beat to the Process Manager to let it know that the worker it alive
 * Since I am using the heart-beat mechanism; I am piggy-backing on the heart-beat mechanism to send additional information as well. 
 * For instance in this case information about the number of processes running on the worker is also sent to the Process Manager.
 * */

import java.net.ConnectException;
import java.util.*;

import abhi.ds.Signal.SignalType;

public class HeartBeatSender extends TimerTask {

	private WorkerManager workerManager; 
	
	public HeartBeatSender(WorkerManager wm)
	{
		workerManager = wm;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//Scan all the Processes in the Process List 
		TreeMap<Integer, String> activeProcessList  = new TreeMap<Integer, String>();
		List<Thread> deadProcesses = new ArrayList<Thread>();
		
		//Check which ones are ALIVE and which ones are DEAD 
		synchronized(this.workerManager.activeProcessesonWorker)
		{
			for (Map.Entry<Thread, MigratableProcess> item : this.workerManager.activeProcessesonWorker.entrySet())
			{
				if(item.getKey().isAlive())
				{
					//Make a HASHMAP of things are alive for the HB signal
					activeProcessList.put(Integer.parseInt(item.getKey().getName().split(":")[0].toString()),
										 item.getKey().getName().split(":")[1].toString());
				}
				else
				{
					//Make a List of Processes that need to be Killed
					deadProcesses.add(item.getKey());
				}
			}
			
			//Remove the deadProcesses from the Internal Map
			for (Thread deadProcess : deadProcesses)
			{
				this.workerManager.activeProcessesonWorker.remove(deadProcess);
			}
		}

		//Produce a appropriate Signal from the Signal Factory 
		Signal heartbeatSignal = new Signal(SignalType.HB,this.workerManager.getWorkerIpAddress(), this.workerManager.getWorkerPortNumber(), "HeartBeat", activeProcessList);
		
        try 
        {
          //Send the HeartBeat Signal to the Process Manager
    
          HelperUtility.sendSignal(this.workerManager.getProcessManagerIP(), this.workerManager.getProcessManagerPort(), heartbeatSignal) ;
          
        } 
        catch (ConnectException e) 
        {
        e.printStackTrace();
          System.err.println("Error sending HeartBeat Signal");
        }
		
		
	}
	

}
