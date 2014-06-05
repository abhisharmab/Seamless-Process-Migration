package abhi.ds;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

/* This class keep track of an offset of where the file was last read and helps to provide that offset 
 * so the reading can begin exactly from the same location*/

public class TransactionalFileInputStream extends InputStream  implements Serializable{

// Reference Tutorial: http://qdaolong.blogspot.com/2013/02/offset-of-file-in-c-and-java.html
// Reference Oracle Documentation:http://docs.oracle.com/javase/7/docs/api/java/io/RandomAccessFile.html 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String filename; 
	private long currOffset; 
	
	public TransactionalFileInputStream(String fileName)
	{
		this.filename = fileName;
		this.currOffset = 0;
	}
	
	
	public synchronized void reset() throws IOException 
	{
		super.reset();
		this.currOffset = 0;
	}
	
	public synchronized String readLine() throws IOException
	{
		RandomAccessFile seeker = new RandomAccessFile(this.filename, "r");

		seeker.seek(this.currOffset + 1); //Start reading from the nextByte up-til where you finished previously
		
		String readLine = seeker.readLine();
	    if(readLine != null)
	    {
	    	this.currOffset += readLine.length();
	    }
	    seeker.close();
	    
		return readLine;
	}
	
	
	@Override
	public synchronized int read() throws IOException 
	{
		RandomAccessFile seeker = new RandomAccessFile(this.filename, "r");

		seeker.seek(this.currOffset + 1); //Start reading from the nextByte up-til where you finished previously
		
		int readResult = 0;
		readResult = seeker.read();
		
		this.currOffset ++;
	   
		seeker.close();
		
		return readResult;
	}
	

	public synchronized int read(byte b[], int off, int len) throws IOException
	{
		if (b == null) 
		{
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) 
        {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) 
        {
            return 0;
        }
		
		RandomAccessFile seeker = new RandomAccessFile(this.filename, "r");
		
		int readBytes = seeker.read(b, off, len);
		if(readBytes > 0)
			this.currOffset += readBytes;
		
		return readBytes; 
	}
	
	public synchronized int read(byte b[]) throws IOException
	{
		RandomAccessFile seeker = new RandomAccessFile(this.filename, "r");
		
		int readBytes = seeker.read(b);
		if(readBytes > 0)
			this.currOffset += readBytes;
		return readBytes; 
	}

}
