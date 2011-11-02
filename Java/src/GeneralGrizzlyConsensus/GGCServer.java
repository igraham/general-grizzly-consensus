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

   GGCServer()
   {
       keepListening = true;
       clients = new Vector<GGCConnection>();
   }
   
   public InetAddress getLikelyIpv4Address()
   {
	   InetAddress[] allIps = {};
	   ArrayList<InetAddress> likelyAddresses = new ArrayList<InetAddress>();
	   InetAddress fallbackAddress = null;

	   // Try and get any IPs available on the system.
       try
       {
    	   allIps = InetAddress.getAllByName(InetAddress.getLocalHost().getCanonicalHostName());
       }
       catch (UnknownHostException e)
       {
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
    			   // This block is for finding and excluding auto-configured and loop-back addresses
    			   if (allIps[i].isLinkLocalAddress())
    			   {
    				   // Assign auto-configured address as a fall-back only when a preferred global address is not available.
    				   fallbackAddress = allIps[i];
    			   }
    			   else if (!allIps[i].isLoopbackAddress())
    			   {
    				   // To get here we know that the address is not an auto-configured address or a local loop-back address.
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

   public InetAddress getLikelyIpv6Address()
   {
	   InetAddress[] allIps = {};
	   ArrayList<InetAddress> likelyAddresses = new ArrayList<InetAddress>();
	   InetAddress fallbackAddress = null;

	   // Try and get any IPs available on the system.
       try
       {
    	   allIps = InetAddress.getAllByName(InetAddress.getLocalHost().getCanonicalHostName());
       }
       catch (UnknownHostException e)
       {
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
    			   // This block is for finding and excluding auto-configured and loop-back addresses
    			   if (allIps[i].isLinkLocalAddress())
    			   {
    				   // Assign auto-configured address as a fall-back only when a preferred global address is not available.
    				   fallbackAddress = allIps[i];
    			   }
    			   else if (!allIps[i].isLoopbackAddress())
    			   {
    				   // To get here we know that the address is not an auto-configured address or a local loop-back address.
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
           }
           catch (IOException e)
           {
           }
       }

       // Done listening so we need to close up shop.
       try
       {
           synchronized (this)
           {
               for (GGCConnection client : clients)
               {
                   client.closeConnection();
               }
           }
           server.close();
       }
       catch (IOException e)
       {
       }
   }

   public synchronized void sendMessageToAll(String msg)
   {
       for (GGCConnection client : clients)
       {
           client.sendMessage(msg);
       }
   }

   public synchronized void setMessageSink(ActionListener listener)
   {
       messageSink = listener;
   }

   public synchronized void stopListening()
   {
       keepListening = false;
   }
}