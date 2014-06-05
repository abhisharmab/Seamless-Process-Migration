package abhi.ds;

/* This class runs a server socket to exclusively listen for heartbeat on the process manager's side
 * This all the workers are sending HeartBeats to the Process it has to constantly listen for HeartBeats*/
import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class ProcessManagerHeartBeatListener implements Runnable  {

	private int portNumber; 
	private ProcessManager processManager;  // Reference to the ProcessManager the listener belongs to

	public ProcessManagerHeartBeatListener(ProcessManager pm)
	{
		this.portNumber = pm.getPMPortNumber();
		this.processManager = pm;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		ServerSocket hbListener = null;
		if(this.portNumber > 0)
		{
			try
			{
				hbListener = new ServerSocket(this.portNumber);
				while(true)
				{
					Socket hbSocket = hbListener.accept();
					
					//Start NEW THREAD FOR HBRequestHandler and Go Back to Listening to Requests
					//So there is no Blocking. Each HB is catered in its particular thread.
					Thread hbRequestHandler = new Thread(new HeartBeatRequestHandler(hbSocket,this.processManager));
					hbRequestHandler.start();
					
				}
			}
			catch(IOException e)
			{
				System.err.print(e.getStackTrace().toString());
			}
			
			finally
			{
				try {
					hbListener.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}


