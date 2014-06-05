package abhi.ds;

import java.io.Serializable;

/* This interface defines an interface that will be implemented by all Migratables Processes in-order for them to be stopped and serialized 
 * and restarted at the same position */

public interface MigratableProcess extends Runnable, Serializable 
{
	void suspend(); //To suspend the process 
	  
	String toString(); //for Purpose of Serialization
}
