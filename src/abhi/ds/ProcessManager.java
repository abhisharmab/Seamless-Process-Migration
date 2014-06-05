package abhi.ds;

/* The Process Manager is sort of the Master in the system. Its primary functios are as follows:
 * 1. Listen to HeartBeats from all the other Workers 
 * 2. Run a Periodic Health Checker that will examine the Threshold of the Heartbeat and decide whether a certain worker is alive or dead
 * 3. Cater to the USER Request in an unblocking fashion. Any time an user request is made it should be immediately ready to take another request */

import java.io.Console;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.TimerTask;
import abhi.ds.Signal.SignalType;

public class ProcessManager implements Runnable {

	private String ipAddress;
	private int portNumber;
	public Console console; 
	private AtomicInteger nextID = new AtomicInteger(0); 
	//Each process must get an unique ID
	//Java provides some inbuilt atomic variables
	
	public int getPMPortNumber()
	{
		return portNumber;
	}
	 
	private Map<String, WorkerInfo4Display> workerList; //Maintains a list if Workers(Machines) that this Manager is handling
	//When the Worker sends a heart-beat it will be added to this List
	
	public Map<String, WorkerInfo4Display> GetWorkerList()
	{
		return workerList;
	}
	
	public ProcessManager(String ipAddress, int portNumber)
	{
		this.ipAddress = ipAddress; //HelperUtility.getLocalIPAddress();
		this.portNumber = portNumber;
		this.workerList = Collections.synchronizedMap(new TreeMap<String, WorkerInfo4Display>()); 
		
		this.console = System.console();
	}
	
	public synchronized void RemoveDeadWorkers(List<String> deadWorkers)
	{
		for (String workerIP: deadWorkers)
		{
			this.workerList.remove(workerIP);
		}
	}
	
	@Override
	public void run() {
	
		//Monitor the Health of Workers every 2 seconds. (See if they are Alive or Dead)
		Timer workerHealthTimer =  new Timer();
		WorkerHealthChecker healthChecker = new WorkerHealthChecker(this);
		workerHealthTimer.schedule(healthChecker,0, HelperUtility.WORKER_HEALTH_CHECK_PERIOD);
		
		//Fire the Listener for HeartBeat Message
		Thread hbistener = new Thread(new ProcessManagerHeartBeatListener(this));
		hbistener.start();
		
		
		//Cater to User Requests and Needs 
		// Take user input until the user chooses to quit the program.
		
	    Scanner scanner = new Scanner(System.in);
	    // start the console to accept user commands. 
	    while (true) 
	    {
	      System.out.println("\n\t" + "Process Manager IP Address:" + this.ipAddress + "\n");
	      System.out.println("\n\t Following is a list of available commands:\n");
	      System.out.println("\t #.<ProcessName> <Parameters> -location <IPAddress:Port>");
	      System.out.println("\t   Example1: abhi.ds.GrepProcess abhi abhi/ds/1.txt abhi/ds/2.txt -location 127.0.0.1:7778");
	      System.out.println("\t   Example2: abhi.ds.Factorial 762 abhi/ds/3.txt -location 127.0.0.1:7778\n");
	      System.out.println("\t #.listallWorkers\n");
	      System.out.println("\t #.listallps <IPAddress:Port>\n");
	      System.out.println("\t #.<ProcessID> -fromLocation <IPAddress:Port> -toLocation <IPAddress:Port>\n");
	      System.out.println("\t #.Quit\n");
	      System.out.print("> ");
	      String command = scanner.nextLine().trim();
	      if (command.equals("")) 
	      {
	        continue;
	      }   
	      else if (command.equalsIgnoreCase("listallWorkers")) 
	      {
	    	//This Commands lists all the Workers in the Distributed System.
	    	  System.out.println("\nList of Workers in the Distributed System");
	    	  System.out.println("-------------------------------------------");
	    	  Object [] workers = this.workerList.keySet().toArray();
	    	  int size = this.workerList.keySet().size();
	    	  
	    	  for(int i=0; i<size; i++)
	    	  {
	    		 int j = i+1;
	    		 System.out.printf("[ %s ] - Worker%d\n" ,workers[i].toString(),j);
	    	  }
	    	  
	    	  System.out.println("\n\n");
	      } 
	      else if (command.startsWith("listallps"))
	      {
	    	//Split command to find the the IPAddress of the Worker of Interest  
	        //Some method to list all Process in a Worker
	    	 //This Commands lists all the processes running on a particular worker
	    	  String [] worker = command.split(" ");
	    	  if(this.GetWorkerList().containsKey(worker[1].toString()))
	    	  {
	    	  
		    	  System.out.printf("\nProcesses Running on [%s]\n",worker[1].toString());
		    	  System.out.println("-------------------------------------------");
		    	  System.out.println("\tID\tProcessName");
				  for(Map.Entry<Integer,String> entry : this.GetWorkerList().get(worker[1]).GetProcessList().entrySet())
				  {
					    System.out.printf("\t%s\t%s\n", entry.getKey().toString(),entry.getValue());
				  }
	    	  }
	    	  else
	    	  {
	    		  System.out.println("No such Worker exists. Check your command");
	    	  }
	    	  System.out.println("\n\n");
	      } 
	      else if(command.equalsIgnoreCase("quit"))
	      {
	    	  workerHealthTimer.cancel();
	    	  System.exit(0);
	      }
	      else 
	      {
	    	  
	        if (command.toLowerCase().contains("-location")) 
	        {
	          String[] cmd = command.split("-location");

	          //Manufacture the Launch Signal from the Signal Factory
	          Signal launchSignal = new Signal(SignalType.LAUNCH, cmd[0].toString(), nextID.incrementAndGet());
	          
	          try 
	          {
	        	//Send the Launch Signal to the Worker with the Command.
	            HelperUtility.sendSignal(cmd[1].split(":")[0].toString().trim(), Integer.parseInt(cmd[1].split(":")[1].toString().trim()), launchSignal) ; //Signal Info Serialized in Bytes
	          } 
	          catch (ConnectException e) 
	          {
	            System.err.println("Error sending Launch Signal");
	          }
	        }
	        else if(command.contains("-toLocation") && command.contains("-fromLocation"))
	        {
	        	String[] processID = command.split("-fromLocation");
	        	String uniqueprocessID = processID[0].trim().toString();
	        	String[] cmdFromTo = processID[1].split("-toLocation");
	        	
	        	//Before migrating the Process we need SUSPEND the existing one. 
	        	//So Manufacture a STOP signal along with the Final Destination Information packed into it.
	        	Signal migrateSignal = new Signal(SignalType.STOP, Integer.parseInt(uniqueprocessID), cmdFromTo[1].split(":")[0].trim().toString(), 
	        									  Integer.parseInt(cmdFromTo[1].split(":")[1].trim().toString()), "MigrateSignalInitiated" );
	        	
		          try 
		          {
		        	//Send the STOP signal to the worker running the process first. 
		            HelperUtility.sendSignal(cmdFromTo[0].split(":")[0].trim().toString(), Integer.parseInt(cmdFromTo[0].split(":")[1].trim().toString()), migrateSignal) ; 
		            
		            //After this that worker will suspend and take the initiated to migrate the process to Final Destination
		          } 
		          catch (ConnectException e) 
		          {
		            System.err.println("Error sending Migrate Signal");
		          }
	        }
	        else 
	        { 
	          System.err.println("In-valid Command. Retry.");
	        }
	      }
	    }
	}
}

