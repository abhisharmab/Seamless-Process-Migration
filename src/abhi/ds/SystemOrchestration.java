package abhi.ds;

import java.io.Console;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/* The purpose of this is to act as the DS orchestrator
 * The user commands come here and based on the command a Process Manager or a Worker is started on a particular Machine
 * This is just acts as a clean layer to start the required services on the intended machine
 * The make commands call upon this class to fire up the necessary services*/

public class SystemOrchestration 
{

	public static void main(String[] args) 
	{
		// User might want to start the Process Manager or He might want to Fire Up some Workers 
		// Using a single Command he should be able to Orchestrate this Distributed System
		
	    Runnable requestedAction = launchAppropriateHandler(args);
	    
	    if (requestedAction == null)
	      return ;
	    
	    else 
	    {
	      Thread requestedActionHandler = new Thread(requestedAction);
	      requestedActionHandler.run(); 
	      //Called run since I can run this on the same main thread. No need to spawn another thread while the main thread sits there doing nothing.
	      
	      try 
	      {
	    	 requestedActionHandler.join();
	      } 
	      catch (Exception e) 
	      {

	      }
	    }
	    
	}
	
 public static Runnable launchAppropriateHandler(String[] args)
	{
	    if (args == null || args.length == '0') {
	        System.err.println("Invalid Input");
	        return null;
	      }
	    
	    try 
	    {
	    	if(args[0].toString().equalsIgnoreCase("PM") && args.length == 3) //Commands to start a Process Manager
	    	{
		        return new ProcessManager(args[1].toString().trim(), Integer.parseInt(args[2])); 
		        //This Process Manager will fire up and it will act as the Controller for USER INPUT
	    	}
	    		
	    	else if(args[0].toString().equalsIgnoreCase("W") && args.length == 5) //Commands to start a Worker
	    	{
		        
		       return new WorkerManager(args[1].toString().trim(),Integer.parseInt(args[2]), args[3].toString().trim(), Integer.parseInt(args[4]));  
		        //Workers returned here who will do some Work 
		        //Logically Workers Represent Some Entities Doing Work
	    	}
	    		
	    	else
	    	{
	    		System.err.println("Invalid use of Command");
	    		return null;
	    	}

	    }
	    catch (NumberFormatException e) 
	    	{
	    		System.err.println("Invalid port number.");
	    		return null;
	    	}
	}
}

