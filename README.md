Seamless-Process-Migration
==========================

A distributed computation framework for Seamless Process Migration between machine to help work-load balancing


Process to build, deploy and run the project

1. Download the source code and cd into the directory 

2. Compile the system by running the following command
	> make
		
3. To start a Process Manager run the following command:
	> make processmanager IP="IPAddress" PORT="PortNumber"

4. To start a Worker run the command:
   > make worker IP="IPAddress" PORT="PortNumber" PMIP="IPAddress" PMPORT="PortNumber"

5. To start the GrepProcess follow the instructions on the Process Manager prompt. 
   > Example below:abhi.ds.GrepProcess "query" "inputfile" "outputfile" -location "IPAddress:PortNumber"

6. To start Factorial process follow the instructions on the Process Manager prompt. 
   > Example below: abhi.ds.Factorial <Integer> <outputfile> -location "IPAddress:PortNumer"

7. To print a List of all Workers; use the following command:
   > listallWorkers

8. To print a List of all running processes on a particular Worker; use the following command:
   > listallps "IPAddress:Port"

9. To Migrate a process from one machine to another; use the following command:
   > "ProcessID" -fromLocation "IPAddress:Port" -toLocation "IPAddress:Port"

Software Dependencies
The process migration framework has been build using the Java 1.7. There are no external dependencies or additional software requirements. The namespace of the package is abhi.ds.
