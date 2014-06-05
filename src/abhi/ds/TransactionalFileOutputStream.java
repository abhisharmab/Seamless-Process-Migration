package abhi.ds;

/* This class keep track of an offset of where the file was last written onto and helps to provide that offset 
 * so the writing can begin exactly from the same location*/

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filename; 
	private boolean shouldAppend;
	//Toggle to know writing first time to when appending.


	public TransactionalFileOutputStream (String filename, boolean shouldAppend)
	{
		this.filename = filename; 
		this.shouldAppend = !shouldAppend;
	}
	
	  @Override
	  public synchronized void write(byte[] b, int off, int len) throws IOException {
		//Overall Structure 
		  //Write + Append
		  //Flush 
		  //Close
	    FileOutputStream ostream = new FileOutputStream(this.filename, !this.shouldAppend);
	    this.shouldAppend = false;

	    ostream.write(b, off, len);
	    ostream.flush();

	    ostream.close();
	  }

	  @Override
	  public synchronized void write(byte[] b) throws IOException {

	    FileOutputStream ostream = new FileOutputStream(this.filename, !this.shouldAppend);
	    this.shouldAppend = false;

	    ostream.write(b);
	    ostream.flush();

	    ostream.close();
	  }

	  @Override
	  public synchronized void write(int b) throws IOException {
	    FileOutputStream ostream = new FileOutputStream(this.filename, !this.shouldAppend);
	    this.shouldAppend = false;

	    ostream.write(b);
	    ostream.flush();

	    ostream.close();
	  }
}
