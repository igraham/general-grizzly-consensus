package GeneralGrizzlyConsensus;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * 
 * @author Stephen Kent
 * 
 *         This handles listening for, accepting, and managing of multiple
 *         client connections.
 * 
 */
public enum GGCServer implements Runnable
{
	INSTANCE;
	private boolean keepListening;
	private ServerSocket server;
	private Vector<GGCConnection> clients;
	private ActionListener messageSink;
	private String lastSentMessage;
	private ClientCleanUpTask clientCleaner;

	protected class ClientCleanUpTask implements Runnable
	{
		private boolean continueTask;

		ClientCleanUpTask()
		{
			continueTask = true;
		}

		public synchronized void stopCleanup()
		{
			continueTask = false;
		}

		@Override
		public void run()
		{
			while (continueTask)
			{
				synchronized (clients)
				{
					for (int i = (clients.size() - 1); i >= 0; i--)
					{
						// Remove inactive connections so they don't screw up
						// any connection counts.
						if (clients.get(i).getRawSocket().isClosed())
							clients.remove(i);
					}
				}
				try
				{
					// This way we only try to cleanup our list once a second.
					this.wait(1000);
				}
				catch (InterruptedException e)
				{
					// The thread was interrupted, but this really doesn't
					// constitute a problem as we are just doing a wait, but
					// we'll log it anyway.
					GGCGlobals.INSTANCE.addExceptionToLog(e);
				}
			}
		}
	}

	GGCServer()
	{
		keepListening = true;
		clients = new Vector<GGCConnection>();
		lastSentMessage = null;
		clientCleaner = new ClientCleanUpTask();
		Thread t = new Thread(clientCleaner);
		t.start();
	}

	public static InetAddress getLikelyIpv4Address()
	{
		InetAddress[] allIps = {};
		ArrayList<InetAddress> likelyAddresses = new ArrayList<InetAddress>();
		InetAddress fallbackAddress = null;

		// Try and get any IPs available on the system.
		try
		{
			allIps = InetAddress.getAllByName(InetAddress.getLocalHost()
					.getCanonicalHostName());
		}
		catch (UnknownHostException e)
		{
			// This should only occur if the computer has no network cards in
			// which case running this program is pretty pointless.
			JOptionPane.showMessageDialog(null,
					"Unable to obtain this computer's IP addresses.");
		}

		if (allIps.length < 1)
			return null;
		else if (allIps.length == 1)
			return allIps[0];
		else
		{
			for (int i = 0; i < allIps.length; i++)
			{
				// Exclude IPv6 Addresses
				if (!allIps[i].getHostAddress().contains(":"))
				{
					// This block is for finding and excluding auto-configured
					// and loop-back addresses
					if (allIps[i].isLinkLocalAddress())
					{
						// Assign auto-configured address as a fall-back only
						// when a preferred global address is not available.
						fallbackAddress = allIps[i];
					}
					else if (!allIps[i].isLoopbackAddress())
					{
						// To get here we know that the address is not an
						// auto-configured address or a local loop-back address.
						likelyAddresses.add(allIps[i]);
					}
				}
			}
			if (likelyAddresses.size() < 1)
				return fallbackAddress;
			else
				return likelyAddresses.get(0);
		}
	}

	public static InetAddress getLikelyIpv6Address()
	{
		InetAddress[] allIps = {};
		ArrayList<InetAddress> likelyAddresses = new ArrayList<InetAddress>();
		InetAddress fallbackAddress = null;

		// Try and get any IPs available on the system.
		try
		{
			allIps = InetAddress.getAllByName(InetAddress.getLocalHost()
					.getCanonicalHostName());
		}
		catch (UnknownHostException e)
		{
			// This should only occur if the computer has no network cards in
			// which case running this program is pretty pointless.
			JOptionPane.showMessageDialog(null,
					"Unable to obtain this computer's IP addresses.");
		}

		if (allIps.length < 1)
			return null;
		else if (allIps.length == 1)
			return allIps[0];
		else
		{
			for (int i = 0; i < allIps.length; i++)
			{
				// Exclude IPv4 Addresses
				if (allIps[i].getHostAddress().contains(":"))
				{
					// This block is for finding and excluding auto-configured
					// and loop-back addresses
					if (allIps[i].isLinkLocalAddress())
					{
						// Assign auto-configured address as a fall-back only
						// when a preferred global address is not available.
						fallbackAddress = allIps[i];
					}
					else if (!allIps[i].isLoopbackAddress())
					{
						// To get here we know that the address is not an
						// auto-configured address or a local loop-back address.
						likelyAddresses.add(allIps[i]);
					}
				}
			}
			if (likelyAddresses.size() < 1)
				return fallbackAddress;
			else
				return likelyAddresses.get(0);
		}
	}

	public int getNumberOfConnectedClients()
	{
		return clients.size();
	}

	public ServerSocket getRawServerSocket()
	{
		return server;
	}

	@Override
	public void run()
	{
		// Set-up our port for listening.
		try
		{
			server = new ServerSocket(GGCGlobals.INSTANCE.COMMUNICATION_PORT);
		}
		catch (IOException e)
		{
			JOptionPane
					.showMessageDialog(null, "Unable to create server port.");
		}

		// Listen and accept connections for as long as we are allowed.
		while (keepListening)
		{
			GGCConnection conn;
			try
			{
				conn = new GGCConnection(server.accept(), messageSink);
				clients.add(conn);
				Thread t = new Thread(conn);
				t.start();
				if (lastSentMessage != null)
					conn.sendMessage(lastSentMessage);
			}
			catch (IOException e)
			{
				// Really this should never occur, but we'll log it if it does
				// occur.
				GGCGlobals.INSTANCE.addExceptionToLog(e);
			}
		}

		// Done listening so we need to close up shop.
		try
		{
			synchronized (clients)
			{
				for (GGCConnection client : clients)
				{
					client.closeConnection();
				}
				clients.clear();
			}
			server.close();
		}
		catch (IOException e)
		{
			// This should never occur as closing a ServerSocket is akin to
			// simply saying stop listening for connections, but we'll log it if
			// it occurs.
			GGCGlobals.INSTANCE.addExceptionToLog(e);
		}
	}

	public void sendMessageToAll(String msg)
	{
		lastSentMessage = msg;
		synchronized (clients)
		{
			for (GGCConnection client : clients)
			{
				client.sendMessage(msg);
			}
		}
	}

	public synchronized void setMessageSink(ActionListener listener)
	{
		messageSink = listener;
	}

	public synchronized void stopListening()
	{
		keepListening = false;
		// This allows our cleanup thread to exit gracefully.
		clientCleaner.stopCleanup();
	}
}