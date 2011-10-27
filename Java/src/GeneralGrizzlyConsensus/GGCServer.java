package GeneralGrizzlyConsensus;

import java.awt.event.*;
import java.io.*;
import java.net.*;
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