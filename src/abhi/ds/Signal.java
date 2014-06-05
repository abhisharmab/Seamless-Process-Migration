package abhi.ds;

import java.io.*;
import java.net.*;
import java.util.TreeMap;

public class Signal implements Serializable{
 
	//Used Factory to Manufacture Signals	
	//A Signal Factory that will produce the required signals based on request
	
	public enum SignalType 
	{
		HB, 
		LAUNCH,
		MIGRATE,
		STOP
	}
	
	private String destIpAddress;
	private int destPortNumber;
	private SignalType signalType; 
	private MigratableProcess mProcess; 
	private String lauchCommand;
	private int uniqueJobId;
	private String workerIp;
	private int workerPort;
	private TreeMap<Integer, String> processList;
	
	//Launch Signal
	public Signal(SignalType launchSignal, String launchCommand, int jobId)
	{
		this.signalType = launchSignal;
		this.lauchCommand = launchCommand;
		this.mProcess = null;
		this.uniqueJobId = jobId;
	}
	
	//Migrate Signal
	public Signal(SignalType migrateSignal, MigratableProcess mp, int jobId)
	{
		this.signalType = migrateSignal;
		this.lauchCommand = "";
		this.mProcess = mp;
		this.uniqueJobId = jobId;
	}
	
	//Stop/Suspend Signal
	public Signal(SignalType stopSignal, int jobId, String destIpAddress, int destPort, String stopCommand)
	{
		this.signalType = stopSignal;
		this.lauchCommand = stopCommand;
		this.uniqueJobId = jobId;
		this.destIpAddress = destIpAddress;
		this.destPortNumber = destPort;
	}
	
	
	//HeartBeat Signal
	public Signal(SignalType heartBeat, String workerIpAddress, int workerPort, String hbCommand, TreeMap<Integer, String> processList)
	{
		this.signalType = heartBeat;
		this.lauchCommand = hbCommand;
		this.uniqueJobId = 0;
		this.workerIp = workerIpAddress;
		this.workerPort = workerPort;
		this.processList = processList;
		
	}
	
	public String getWorkerIp()
	{
		return this.workerIp;
	}
	
	public int getWorkerPort()
	{
		return this.workerPort;
	}
	
	public TreeMap<Integer, String> getRunningProcessList()
	{
		return this.processList;
	}
	
	public SignalType getSignalType()
	{
		return this.signalType;
	}
	
	public MigratableProcess getMigratableProcess()
	{
		return this.mProcess;
	}
	
	public String getCommand()
	{
		return this.lauchCommand;
	}
	
	public int getUniqueJobID()
	{
		return this.uniqueJobId;
	}
	
	public int getDestPortNumber()
	{
		return this.destPortNumber;
	}
	
	public String getDestIpAddress()
	{
		return this.destIpAddress;
	}
}

