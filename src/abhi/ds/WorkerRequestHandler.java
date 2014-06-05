package abhi.ds;

/* Once a request (in my case: Signal) has been received by the worker the main funciton on this class is to 
 * de-serialize the signal -> extract the necessary information from Signal object based on TYPE of the Signal 
 * and then Run the necessary actions */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.List;
import java.util.Set;
import java.io.*;

import abhi.ds.Signal.SignalType;

public class WorkerRequestHandler implements Runnable {

	private WorkerManager workerManager;
	private Socket requestSocket;
	
	public WorkerRequestHandler(Socket requestSocket, WorkerManager workerManager)
	{
		this.workerManager = workerManager;
		this.requestSocket = requestSocket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(this.requestSocket == null){
			try {
				throw new Exception("Socket is invalid. Problem occured");
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
			
		Signal signal = null;
		
		try {
			ObjectInputStream objStream = new ObjectInputStream(this.requestSocket.getInputStream());
			signal = (Signal) objStream.readObject();
			SignalHandler(signal);
			objStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void SignalHandler(Signal signal) {
		// TODO Auto-generated method stub
		switch(signal.getSignalType())
		{
				
			case LAUNCH: //Request to Lauch a New Process
				String processName = signal.getCommand().substring(0, signal.getCommand().indexOf(" "));
				String [] arguments = signal.getCommand().substring(signal.getCommand().indexOf(' ') + 1).split(" ");
				Object[] args = {arguments};
				
				MigratableProcess mp;
				try {
						Class<?> classDef = Class.forName(processName);
						Constructor<?> conS = classDef.getConstructor(String [].class);
						mp = (MigratableProcess) conS.newInstance(args);
						Thread newThread = new Thread(mp);
						newThread.setName(signal.getUniqueJobID() +":"+ processName);
						synchronized(workerManager.activeProcessesonWorker)
						{
							workerManager.activeProcessesonWorker.put(newThread, mp);
						}
						System.out.println("Request to start " + String.valueOf(signal.getUniqueJobID())+ ":"+ processName + " received. Initiating it.");
						newThread.start();
						
					} 
				catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException
					| ClassNotFoundException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				break;
				
			case MIGRATE: //Request to Migrate the Process on the Final Destination Machine. BAsically once recieved start the Thread
				MigratableProcess p = signal.getMigratableProcess();
				
				Thread migratedProcess = new Thread(p);
				
				migratedProcess.setName(signal.getUniqueJobID() + ":" + p.getClass().getName().toString());
				
				System.out.println("Migrating request accepted. Starting Job ID: " + String.valueOf(signal.getUniqueJobID()));
				
				synchronized(this.workerManager.activeProcessesonWorker)
				{
					this.workerManager.activeProcessesonWorker.put(migratedProcess, p);
					migratedProcess.start();
				}
				break;
				
			case STOP: //Request to STOP a currently running process. 
				MigratableProcess process = null;
				Thread t = null;
				
				synchronized(this.workerManager.activeProcessesonWorker)
				{
					if(this.workerManager.activeProcessesonWorker.size() == 0)
						return; //Nothing to migrate really
					
					Set<Thread> List = this.workerManager.activeProcessesonWorker.keySet();
					for (Thread item : List)
					{
						if(signal.getUniqueJobID() == Integer.parseInt(item.getName().split(":")[0].toString()))
						{
							process = this.workerManager.activeProcessesonWorker.get(item);
							t = item;
							
							synchronized(this.workerManager.activeProcessesonWorker)
							{
								this.workerManager.activeProcessesonWorker.remove(item);
							}
						}
					}
				}
				
				if(t != null && t.isAlive()) //Make sure the thread is ALIVE before sending the object further.
				{
					
					System.out.println("Request to Stop & Migrate Job ID: " + String.valueOf(signal.getUniqueJobID())+  " received. Migrating it.");
					process.suspend();
					
					Signal migrateSignal = new Signal(SignalType.MIGRATE, process, signal.getUniqueJobID());
					
					try 
					{
						HelperUtility.sendSignal(signal.getDestIpAddress(), signal.getDestPortNumber(), migrateSignal);
					} 
					//If the Signal Failed to Send to the Destination Machine B then just loop-back and start it again on the same machine.
					catch (ConnectException e) 
					{
						// TODO Auto-generated catch block
						try 
						{
							HelperUtility.sendSignal(this.workerManager.getWorkerIpAddress(), this.workerManager.getWorkerPortNumber(), migrateSignal);
						} 
						catch (ConnectException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}
				else 
				{
					System.out.println("Thread has finished execution. There is no more work left to do. Not Migration It");
					synchronized(this.workerManager.activeProcessesonWorker)
					{
						this.workerManager.activeProcessesonWorker.remove(t);
					}
				}
				break;
				
			default:
				break; 	
		}
		
	}

}
