import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import AppletStart.GGCConnectListener;
import AppletStart.ResponderListener;
import GeneralGrizzlyConsensus.GGCConnection;
import GeneralGrizzlyConsensus.GGCGlobals;
import GeneralGrizzlyConsensus.GGCServer;


public class GeneralGrizzlyConsensusStudent extends Applet
{
	//The panel will display the current IP of the computer in the network.
	private JPanel pShowIP;
	//Will allow the responder to connect to an IP of a session manager.
	private JPanel pConnectIP;
	//The responders response pad with "True/False" and "A-E" buttons.
	private JPanel pResponder;
	//The session managers query type and graph.
	private JPanel pSessionM;
	//This is the button for the responder to connect to the IP.
	private JButton rContorlP;
	//This is the button which shows and hides the graph.
	private JButton pShowHide;
	//This button is the Session Manager's way of sending questions to the students.
	private JButton sendQuestion;
	//This button is the Responder's way of sending an answer to the professor.
	private JButton sendAnswer;
	//The panel which contains the graph which displays the results of the question.
	private ChartPanel chartPanel;
	//The IP of the computer
	private String cIP = "000.000.0.0";
	//The type of queries to be polled upon.
	private String[] types = new String[26];
	//This is to tell that all the IP numbers have been filled in.
	private JTextField[] ip;
	//This is the instance of the server that, should it need to be used, will be initialized.
	private GGCServer server;
	//This is an instance of a connection which will essentially serve as the client. It will be initialized
	//upon clicking on responder.
	private GGCConnection client;
	//This list of buttons will be initialized if the responder button is pressed.
	private ArrayList<JButton> buttons;
	//Make an object of cloneable Radio Buttons for the responder
	private JRadioButton rButton;
	//This is the main frame for the whole GUI.
	private JFrame mainFrame;

	
	public void init()
	{
		Runnable runner = new Runnable() {
            public void run() {
            	mainFrame = new JFrame("Georgia Gwinnett College General Grizzly Consensus");
        		mainFrame.setSize(300, 432);
        		mainFrame.setVisible(true);
        		selectPane();
        		mfContainer = mainFrame.getContentPane();
        		mfContainer.setLayout(new CardLayout());		
        		mfContainer.add(pSelect, "Selection Panel");
        		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        };
        EventQueue.invokeLater(runner);
	}

	private void connectIP()
	{
		pConnectIP = new JPanel();
		pConnectIP.setLayout(new GridLayout(2,1));
		pConnectIP.setVisible(false);
		JPanel lP1 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.TRAILING));

		JPanel lP2 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));

		ip = new JTextField[4];
		JTextField ip1 = new JTextField();
		ip1.setColumns(3);
		ip[0] = ip1;
		JTextField ip2 = new JTextField();
		ip2.setColumns(3);
		ip[1] = ip2;
		JTextField ip3 = new JTextField();
		ip3.setColumns(3);
		ip[2] = ip3;
		JTextField ip4 = new JTextField();
		ip4.setColumns(3);
		ip[3] = ip4;

		JLabel dot1= new JLabel(".");
		JLabel dot2= new JLabel(".");
		JLabel dot3= new JLabel(".");

		rContorlP = new JButton("Connect");
		rContorlP.addActionListener(new GGCConnectListener());

		lP1.add(ip1);
		lP1.add(dot1);
		lP1.add(ip2);
		lP1.add(dot2);
		lP1.add(ip3);
		lP1.add(dot3);
		lP1.add(ip4);
		lP2.add(rContorlP);

		pConnectIP.add(lP1);
		pConnectIP.add(lP2);
	}

	private void createResponder()
	{
		pResponder = new JPanel();
		pResponder.setVisible(false);

		JPanel lP1 = new JPanel();
		lP1.setLayout(new GridLayout(3,1));
		
		String sPerm = "Test";
		
		//Whatever buttons end up here, add them to the ArrayList of buttons.
		ButtonGroup bg = new ButtonGroup();
		JRadioButton tRButton = new JRadioButton("True");
		JRadioButton fRButton = new JRadioButton("False");
		JRadioButton rButton = new JRadioButton(sPerm);

		bg.add(tRButton);
		bg.add(fRButton);
		bg.add(rButton);
		
		lP1.add(tRButton);
		lP1.add(fRButton);
		lP1.add(rButton);

		pResponder.add(lP1);
	}
	private void setupResponderCloseListener()
	{
	       mainFrame.addWindowListener(new java.awt.event.WindowAdapter() 
	       {
	           public void windowClosing(WindowEvent winEvt)
	           {
	               // Do Socket clean-up here.
	               if (client != null)
	                   client.closeConnection();
	               System.exit(0);
	           }
	       });
	}
	/**
	    * This method is essentially the same as the old setupConnection method but
	    * it takes in an IP address as a parameter and is public.
	    * @param IP
	    */
	   private void setupConnection(String IP)
	   {
		   try
	       {
	           client = new GGCConnection(new Socket(IP,
	                   GGCGlobals.INSTANCE.COMMUNICATION_PORT), new ResponderListener());
	           Thread t = new Thread(client);
	           t.start();
	       }
	       catch (UnknownHostException e)
	       {
	           JOptionPane.showMessageDialog(this, "Unknown host.");
	       }
	       catch (IOException e)
	       {
	           // JOptionPane.showMessageDialog(this, "Cannot connect to host.");
	           JOptionPane.showMessageDialog(this, e.getMessage());
	       }
	   }
		/**
		 * This method checks to see whether the given String input is a valid IP address.
		 * This method also pops up error boxes every time bad input is given when connect is pressed.
		 * As a result, that part can be deleted and it would be fine with me.
		 * @author Ian Graham
		 * @param IP
		 * @return Returns true if it is a valid IP address, and false if it has any deviations
		 * such as numbers greater than 255 or less than 0, and characters other than numbers.
		 */
		private boolean isValidIP(String[] IP)
		{
			for(int i = 0; i < IP.length; i++)
			{
				if(IP[i].length() == 0)
				{
					JOptionPane.showMessageDialog(null, "Please fill out all text boxes before attempting to connect.",
							"Invalid IP", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				try
				{
					int num = Integer.parseInt(IP[i]);
					if(i == 0 || i == 3)
					{
						if(num > 254 || num < 1)
						{
							showBadIPField(i);
							JOptionPane.showMessageDialog(null, "Please use numbers between 254 and 1 for the first and last box.",
									"Invalid IP", JOptionPane.ERROR_MESSAGE);
							return false;
						}

					}
					if(i == 1 || i == 2)
					{
						if(num > 254 || num < 0)
						{
							showBadIPField(i);
							JOptionPane.showMessageDialog(null, "Please use numbers between 254 and 0 for the second and third box.",
									"Invalid IP", JOptionPane.ERROR_MESSAGE);
							return false;
						}

					}
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, "Please use numbers when entering the IP.",
							"Invalid IP", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			return true;
		}
		/**
		 * When the user has a bad IP address in a particular field, this method will highlight which one.
		 * You may or may not wish to use this.
		 */
		private void showBadIPField(int i)
		{
			ip[i].requestFocus();
			ip[i].selectAll();
		}
		

		class GraphBox extends JComponent
		{
			public void paint(Graphics g)
			{
				g.setColor(Color.WHITE);
				g.fill3DRect(20, 20, 120, 120,true);
			}
		}

}
