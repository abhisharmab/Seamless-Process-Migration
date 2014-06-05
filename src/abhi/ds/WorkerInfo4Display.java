package abhi.ds;

/* This is class defines some limited information about a workers that will be maintained on the process maanger side
 * Think of this as meta-data about all the workers encapsulated in this class*/

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

public class WorkerInfo4Display  {
	
	private String workerIpAddress;
	private int workerPortNumber;
	private long lastHeartBeat;
	
	private Map<Integer, String> runningProcessList;
	// A Map Containing a List of Processes Running on Each Worker
	
	
	public WorkerInfo4Display(String ipAddress, int portNumber, long t) 
	{
		  this.runningProcessList = Collections.synchronizedSortedMap(new TreeMap<Integer, String>());
		  //Reference: http://docs.oracle.com/javase/6/docs/api/java/util/TreeMap.html
		  
		  this.workerIpAddress = ipAddress;
		  this.workerPortNumber = portNumber;
		  this.lastHeartBeat = t;
	}
	
	public Map<Integer, String> GetProcessList()
	{
		return runningProcessList;
	}

	//The process manager maintins the list of all process running on each worker. 
	//Each time a HeartBeat signal arrives with the information form the worker we need to update this list.
	public void syncLatestProcessList(TreeMap<Integer, String> workerProcessList) 
	{
		  TreeMap<Integer, String> aliveProcesses = new TreeMap<Integer, String>();
		  
		  synchronized (this.runningProcessList) //Reference: http://docs.oracle.com/javase/6/docs/api/java/util/Collections.html#synchronizedSortedMap(java.util.SortedMap)
		  {
		    
			for(Map.Entry<Integer,String> entry : workerProcessList.entrySet())
			{
			    aliveProcesses.put(entry.getKey(), entry.getValue().toString());
			}
			   
		   this.runningProcessList.clear();
		   this.runningProcessList.putAll(aliveProcesses);
		  }
	}
			

	public void addProcess(int processID, String processName) {
	  this.runningProcessList.put(processID, processName);
	}
	
	public String getIpAddress() {
	  return workerIpAddress;
	}
	
	public int getPortNumber() {
	  return workerPortNumber;
	}
	
	public long getLastHBTimeStamp()
	{
		return this.lastHeartBeat;
	}
		
	public void setHBTimeStamp(long heartBeat) 
	{
		this.lastHeartBeat = heartBeat;
	}
	
	public boolean isWorkerAlive(long latestHeartBeat) {
	  return (latestHeartBeat - this.lastHeartBeat <= HelperUtility.HB_THRESHOLD);
	}
	

	public static String getMapKey(String host, int port) 
	{
	  return host + ":" + port;
	}
	
	public static String getMapKey(WorkerInfo4Display wInfo) 
	{
	  return wInfo.getIpAddress() + ":" + wInfo.getPortNumber();
	}

}
