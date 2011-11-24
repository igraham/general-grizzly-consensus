package GeneralGrizzlyConsensus;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * 
 * @author Stephen Kent
 * 
 *         This is where all the globals we use should go.
 * 
 */
public enum GGCGlobals
{
	INSTANCE;
	public int COMMUNICATION_PORT = 10515;
	public int MESSAGE_EVENT_ID = 8722736; // Just a large random number so that
											// our id should not interfere with
											// other action ids.

	/**
	 * Note: exceptionLog should only be accessed via the appropriate functions
	 * because access to the log needs to be synchronized in a multi-threaded
	 * environment.
	 */
	private ArrayList<Exception> exceptionLog = null;

	public synchronized void addExceptionToLog(Exception e)
	{
		if (exceptionLog == null)
			exceptionLog = new ArrayList<Exception>();

		exceptionLog.add(e);
	}

	public synchronized void dumpExceptionLog(String strFile)
	{
		if (exceptionLog != null)
		{
			PrintWriter dumpTo = null;
			try
			{
				dumpTo = new PrintWriter(strFile);
			}
			catch (FileNotFoundException err)
			{
				System.out.println("Unable to create exception dump file: "
						+ strFile);
			}

			if (dumpTo != null)
			{
				for (int i = 0; i < exceptionLog.size(); i++)
				{
					Exception e = exceptionLog.get(i);
					dumpTo.println("Exception: " + e.getMessage());
					e.printStackTrace(dumpTo);
					dumpTo.println();
				}
			}

			dumpTo.flush();
			dumpTo.close();
			dumpTo = null;
		}
	}
}