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

   // This function doesn't actually close the connection, but it does signal
   // that we are done with it so that the thread can exit safely.
   public synchronized void closeConnection()
   {
       keepConnected = false;
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
           catch (IOException e)
           {
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
       }
   }

   public synchronized void sendMessage(String msg)
   {
       if (out != null)
       {
           out.println(msg);
       }
   }
}