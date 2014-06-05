package abhi.ds;

/* This class specifies the Listener running on each Worker that cater to requests.*/
import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class WorkerRequestListener implements Runnable  {

	private int portNumber; 
	private WorkerManager workerManager;  // Reference to the WorkManager it belongs to

	public WorkerRequestListener(WorkerManager wm)
	{
		this.portNumber = wm.getWorkerPortNumber();
		this.workerManager = wm;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		ServerSocket workerListener = null;
		if(this.portNumber > 0)
		{
			try
			{
				workerListener = new ServerSocket(this.portNumber);

				while(true)
				{
					Socket requestSocket = workerListener.accept();
					
					//Start NEW THREAD FOR RequestHandler and Go Back to Listening to Requests
					//Cater to requests in an unblocking way.
					Thread workerRequestHandler = new Thread(new WorkerRequestHandler(requestSocket,this.workerManager));
					workerRequestHandler.start();
					
				}
			}
			catch(IOException e)
			{
				System.err.print(e.getStackTrace().toString());
			}
			
			finally
			{
				try {
					workerListener.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}


