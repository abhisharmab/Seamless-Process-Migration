package abhi.ds;

/* This class specifies the Health Checker that runs on the Process Manager at a given timely interval 
 * The job is the find out how many workers did not send HeartBeat in the specified time limit and if they did not then remove them from the internal map
 * If the workers come back alive again then they will again be added to the list from another place.*/

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Map;
import java.util.List;

public class WorkerHealthChecker extends TimerTask {

	private ProcessManager processManager; 
	
	public WorkerHealthChecker(ProcessManager pm)
	{
		this.processManager = pm; 
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		List<String> deadWorkers = new ArrayList<String>();
		
		//Figure out which Worker are Dead 
		for (Map.Entry<String, WorkerInfo4Display> item: this.processManager.GetWorkerList().entrySet())
		{
			if(!item.getValue().isWorkerAlive(System.currentTimeMillis()))
			{
				deadWorkers.add(item.getKey()); //Make a list of all the Dead Worker (We did not hear HB from in the specified threashold
			}
				
		}
	
		this.processManager.RemoveDeadWorkers(deadWorkers);
	}
}
