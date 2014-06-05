package abhi.ds;

import java.io.PrintStream;

//Factorial is a Migratable Process that calculates the Factorial of a Provided Number (Entered by the User)
//And output step by step results to an output file.

public class Factorial implements MigratableProcess {

		/**
	 * 
	 */
	 private static final long serialVersionUID = 2L;

		private String[] args;
		private int factorial=1;
		private int checkpoint;
		private int end;

		private volatile boolean suspending; 
		private TransactionalFileOutputStream outFile;

		
		
		public Factorial(String args[]) throws Exception
		{
			this.args = args;
			
			if (args.length != 2 || args.length > 2) {
				System.out.println("usage: Factorial <Number> <Output_Filename>");
				throw new Exception("Invalid Arguments");
			}
			
			
	        try 
	        {
	        	int test = Integer.parseInt(args[0]);
	        } catch(NumberFormatException e) {
	            System.out.println("<Number> should be some integer.");
	            throw new Exception("Invalid Arguments");
	        }
	        
	        outFile = new TransactionalFileOutputStream(args[1], false);
	        this.factorial = 1;
	        this.end = Integer.parseInt(this.args[0]);
	        this.checkpoint = 1;

		}

		public void run()
		{
			   PrintStream out = new PrintStream(outFile);

			   
				while (!suspending) {
					
					if(checkpoint != end)
					{
						for (int c = checkpoint ; c<= end ; c++ )
						{
				            this.factorial = this.factorial*c;
				            out.println(this.factorial);
				            checkpoint = c;
						}
					}
									
					// Make Factorial take longer so that we don't require extremely large files for interesting results
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						// ignore it
				    }
				}
				
			suspending = false;
		}

		public void suspend()
		{
			suspending = true;
			while (suspending);
		}
		
		  public String toString() 
		  {
			  String work = this.getClass().getName();
			  if (args == null)
			      return work;
			  for (String s : args) 
			  {
			      work = work + " " + s;
			  }
			  return work;
		  }

}


