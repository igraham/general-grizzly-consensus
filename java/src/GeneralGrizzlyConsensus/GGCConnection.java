package GeneralGrizzlyConsensus;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

/**
 * 
 * @author Stephen Kent
 * 
 *         This is the connection object that will manage any single connection
 *         on either the server or client side.
 * 
 */
public class GGCConnection implements Runnable
{
	private boolean keepConnected;
	private Socket socket;
	private ActionListener messageSink;
	private BufferedReader in;
	private PrintWriter out;

	/**
	 * The connection class is used by the client programs in order to establish a connection
	 * to the server. A connection is made using a socket as a parameter so that a connection 
	 * can be made using server.accept(). The action listener is used as the method of reading 
	 * and writing between GUI elements. A boolean parameter is used to allow graceful exiting 
	 * of threads.
	 * @param sock
	 * @param listener
	 */
	public GGCConnection(Socket sock, ActionListener listener)
	{
		keepConnected = true;
		socket = sock;
		messageSink = listener;

		// Set-up our input and output streams so we can read and send commands
		// over our connection.
		try
		{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,
					"Unable to map input and output streams.");
		}
	}

	/**
	 * This function doesn't actually close the connection, but it does signal 
	 * that we are done with it so that the thread can exit safely.
	 */
	public synchronized void closeConnection()
	{
		keepConnected = false;
		try {
			socket.close();
			in.close();
		} catch (IOException e) {
			//Most likely every time the socket closing will throw an IOException but it should not cause
			//any issues.
		}
	}

	public Socket getRawSocket()
	{
		return socket;
	}

	@Override
	public void run()
	{
		String line;

		while (keepConnected)
		{
			try
			{
				while(in == null) {
					System.out.println("in was null; sleeping....");
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try
				{
					line = in.readLine().trim();
					if (line != null && line.length() > 0)
					{
						ActionEvent msg = new ActionEvent(this,
								GGCGlobals.INSTANCE.MESSAGE_EVENT_ID, line);
						synchronized (messageSink)
						{
							messageSink.actionPerformed(msg);
						}
					}
				}
				catch(NullPointerException e)
				{
					break;
					//Most likely if an exception occurs, it was purposefully induced.
				}
				
			}
			catch (IOException e)
			{
				// Connection has been terminated so we need to stop trying to
				// listen to it.
				keepConnected = false;
			}
		}

		// We're no longer interested in what comes over the connection so clean
		// it up and exit the thread.
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			// This should never be reached as the close() method only really
			// works locally, but we'll log any exceptions thrown here.
			GGCGlobals.INSTANCE.addExceptionToLog(e);
		}
	}
	/**
	 * Sends a message to the server in the form of a string.
	 * @param msg
	 */
	public synchronized void sendMessage(String msg)
	{
		if (out != null)
		{
			out.println(msg);
		}
	}
}