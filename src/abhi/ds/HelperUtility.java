package abhi.ds;

/* Helper Utility Class hold Systemic constants and Common utility functions that will be used by all other things in the system
 * The sendSignal method is used both by the Process manager and the Worker to establish the Socket Connection with the server socket on the other end
 * and send its messages to them.
 * */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class HelperUtility {
	
	public final static long WORKER_HEALTH_CHECK_PERIOD = 5000;
	public final static long HB_THRESHOLD = 4000; 
	public final static long HB_SEND_DURATION = 2000;

	
	public static void sendSignal(String ipAddress, int portNumber, Signal signal) throws ConnectException
	{

		try
		{
			Socket s = new Socket(ipAddress, portNumber);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(signal);
			out.close();
			s.close();
		}
		catch(ConnectException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			//TODO Fix this code
			e.printStackTrace();
		}

	}

}
