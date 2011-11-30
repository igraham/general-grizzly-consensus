package GGCApplet;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.*;

import com.sun.awt.AWTUtilities;

import GeneralGrizzlyConsensus.*;

public class GeneralGrizzlyConsensusStudent extends Applet implements ActionListener
{
	//I added this because the client/server GUI's had it.
	private static final long serialVersionUID = 1L;
	//The container to hold the layout of the main frame of the Applet.
	private Container mfContainer;
	//Will allow the responder to connect to an IP of a session manager.
	private JPanel pConnectIP;
	//The responders response pad with "True/False" and "A-E" buttons.
	private JPanel pResponder;
	//This is the button for the responder to connect to the IP.
	private JButton rContorlP;
	//This button is the Responder's way of sending an answer to the professor.
	private JButton sendAnswer;
	//This is to tell that all the IP numbers have been filled in.
	private JTextField[] ip;
	//This is an instance of a connection which will essentially serve as the client. It will be initialized
	//upon clicking on responder.
	private GGCConnection client;
	//Button groups which organize the buttons used by the session manager and responder.
	private ButtonGroup rGroup, tGroup;
	//This button group is used by the responder and will be used to both determine which
	//response buttons are on the screen and which response is sent to the session manager
	//when the sendAnswer button is clicked.
	private ArrayList<JToggleButton> responderButtons;
	//This button group is used specifically for the true and false buttons.
	private ArrayList<JToggleButton> trueFalseButtons;
	//This is the main frame for the whole GUI.
	private JFrame mainFrame;
	//This is a temporary fix to the error caused when switching from multiple choice to T/F.
	private boolean rtf;
	//This Point is used to keep track of where the JFrame Window is for dragging while in undecorated mode.
	private Point point = new Point();
	//This boolean is used to tell when the mouse has left or entered the JFrame at least once.
	private boolean currentIn = true;
	//JPanels
	private JPanel tfPanel, tfPanelBuff, ownPanel, numPanelBuff;
	//Exit buttons
	private CustomJButton exit1, exit2;

	public void init()
	{
		Runnable runner = new Runnable() {
			public void run() {
				// ATWUtilities is a restricted API to Eclipes and need to be set up so it does not throw an error
				// Window -> Preferences -> Java -> Compiler -> Error/Warnings -> Depicted/Restricted API -> Forbidden Access -> Set to Warning
				if (!AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT))
				{
					System.out.print("You do not support AWTUtilities Translucency.");
				}

				mainFrame = new JFrame("Georgia Gwinnett College General Grizzly Consensus");
				mainFrame.setSize(400, 482);			
				mainFrame.setUndecorated(true);
				//Please keep setResizable to false till resize bug is fixed
				mainFrame.setResizable(false);
				mfContainer = mainFrame.getContentPane();
				//The color is GGC EverGreen Green
				mfContainer.setBackground(new Color(0, 125, 75));
				AWTUtilities.setWindowShape(mainFrame, new RoundRectangle2D.Float(0, 0,mainFrame.getWidth(), mainFrame.getHeight(), 30, 30));
				mainFrame.setVisible(true);

				final CustomGlass glass = new CustomGlass(mainFrame);
				mainFrame.setGlassPane(glass);
				glass.setFocusable(false);
				glass.setEnabled(false);
				glass.setVisible(true);
				
				connectIP();
				mfContainer.setLayout(new CardLayout());
				mfContainer.add(pConnectIP, "IP Connection Panel");
				setupResponderCloseListener();
				createResponder();
				mfContainer.add(pResponder, "Responders Panel");
				pConnectIP.setVisible(true);
				
				// This get the point location of where the mouse is pressed to save it location for moving.
				mainFrame.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						point.x = e.getX();
						point.y = e.getY();
					}
				});

				// This is what drags the JFrame when the frame is undecorated.
				mainFrame.addMouseMotionListener(new MouseMotionAdapter() {
					public void mouseDragged(MouseEvent e) {
						Point p = mainFrame.getLocation();
						mainFrame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
					}
				});

				// When the mouse leaves the JFrame it disposes of the frame and redraws the decorated boarder
				mainFrame.addMouseListener(new MouseAdapter() {
					public void mouseExited(MouseEvent e) {
						/*
						//This is the get the bounding box of the where the JFrame is on the screen.
						Dimension bD = new Dimension(mainFrame.getWidth(), mainFrame.getHeight());
						Rectangle bR = new Rectangle(mainFrame.getLocation(),bD);

						if(!bR.contains(e.getLocationOnScreen()))
						{
							//System.out.println(bR.contains(e.getLocationOnScreen()));
							//System.out.println("Out");
							mainFrame.dispose();
							mainFrame.setUndecorated(false);
							//frame.setBounds(frame.getGraphicsConfiguration().getBounds());
							mainFrame.setVisible(true);
							currentIn = false;
						}*/
					}
				});

				// When the mouse enters the JFrame it disposes of the frame and redraws the undecorated boarder
				mainFrame.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) {
						if(!currentIn)
						{
							/*//System.out.println("In");
							mainFrame.dispose();
							mainFrame.setUndecorated(true);
							AWTUtilities.setWindowShape(mainFrame, new RoundRectangle2D.Float(0, 0,mainFrame.getWidth(), mainFrame.getHeight(), 30, 30));
							currentIn = true;
							mainFrame.setVisible(true);*/
						}
					}
				});
				mfContainer.validate();
			}
		};
		EventQueue.invokeLater(runner);
	}

	private void connectIP()
	{
		pConnectIP = new JPanel();
		pConnectIP.setLayout(new BorderLayout());
		pConnectIP.setVisible(false);
		pConnectIP.setBackground(new Color(0, 125, 75));
		pConnectIP.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		
		exit1 = new CustomJButton("Exit");
		exit1.addActionListener(this);
		exit2 = new CustomJButton("Exit");
		exit2.addActionListener(this);
		
		JPanel lP1 = new JPanel();
		lP1.setBackground(new Color(0, 125, 75));
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel lP2 = new JPanel();
		lP2.setBackground(new Color(0, 125, 75));
		lP2.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JPanel lP3 = new JPanel();
		lP3.setBackground(new Color(0, 125, 75));
		lP3.setLayout(new GridLayout(2,1));
		
		//This is the pattern that captures an IP address. It can't be, "(?:25[0-4]|2[0-4][0-9]|[01]?[1-9][0-9]?)" because 
		//that discludes 100, and many other non-zero values with a zero in it, from the list of valid IP's.
		//_254_1 is for the outer two blocks.
		String _254_1 = "(?:25[0-4]|2[0-4][0-9]|[01]?[0-9][0-9]|[1-9])";
		RegexFormatter ipFormatter1 = new RegexFormatter( _254_1 );
		//_254_0 is for the inner two blocks.
		String _254_0 = "(?:25[0-4]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		RegexFormatter ipFormatter2 = new RegexFormatter( _254_0 );

		ip = new JFormattedTextField[4];
		JFormattedTextField ip1 = new JFormattedTextField(ipFormatter1);
		ip1.setColumns(3);
		ip[0] = ip1;
		JFormattedTextField ip2 = new JFormattedTextField(ipFormatter2);
		ip2.setColumns(3);
		ip[1] = ip2;
		JFormattedTextField ip3 = new JFormattedTextField(ipFormatter2);
		ip3.setColumns(3);
		ip[2] = ip3;
		JFormattedTextField ip4 = new JFormattedTextField(ipFormatter1);
		ip4.setColumns(3);
		ip[3] = ip4;

		JLabel dot1= new JLabel(".");
		JLabel dot2= new JLabel(".");
		JLabel dot3= new JLabel(".");

		rContorlP = new CustomJButton("Connect");
		rContorlP.addActionListener(new GGCConnectListener());
		
		JLabel ipText = new JLabel("Please enter your instructor's IP address Here: ");
		JPanel exitPanel = new JPanel(new BorderLayout());
		exitPanel.setBackground(new Color(0, 125, 75));
		exitPanel.add(exit1, BorderLayout.EAST);
		JPanel ipTextPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		ipTextPanel.setBackground(new Color(0, 125, 75));
		lP3.add(exitPanel);
		ipTextPanel.add(ipText);
		lP3.add(ipTextPanel);

		lP1.add(ip1);
		lP1.add(dot1);
		lP1.add(ip2);
		lP1.add(dot2);
		lP1.add(ip3);
		lP1.add(dot3);
		lP1.add(ip4);
		lP2.add(rContorlP);

		pConnectIP.add(lP1, BorderLayout.CENTER);
		pConnectIP.add(lP2, BorderLayout.PAGE_END);
		pConnectIP.add(lP3, BorderLayout.NORTH);
	}

	private void createResponder()
	{
		pResponder = new JPanel();
		pResponder.setLayout(new BorderLayout());
		pResponder.setVisible(false);
		pResponder.setBackground(new Color(0, 125, 75));
		pResponder.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		
		tfPanel = new JPanel();
		tfPanel.setBackground(new Color(0, 125, 75));
		tfPanelBuff = new JPanel();
		tfPanelBuff.setBackground(new Color(0, 125, 75));
		tfPanelBuff.setLayout(new BoxLayout(tfPanelBuff, BoxLayout.PAGE_AXIS));
		tfPanelBuff.add(Box.createVerticalGlue());
		tfPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		ownPanel = new JPanel();
		ownPanel.setBackground(new Color(0, 125, 75));
		numPanelBuff = new JPanel();
		numPanelBuff.setBackground(new Color(0, 125, 75));
		numPanelBuff.setLayout(new BoxLayout(numPanelBuff, BoxLayout.PAGE_AXIS));
		numPanelBuff.add(Box.createVerticalGlue());
		numPanelBuff.add(ownPanel);
		
		JPanel exitPanel = new JPanel(new BorderLayout());
		exitPanel.setBackground(new Color(0, 125, 75));
		exitPanel.add(exit2, BorderLayout.EAST);
		pResponder.add(exitPanel, BorderLayout.NORTH);
		
		pResponder.add(numPanelBuff);
		pResponder.add(tfPanelBuff);
		
		JPanel lP1 = new JPanel();
		lP1.setBackground(new Color(0, 125, 75));
		lP1.setLayout(new GridLayout(3,1));

		responderButtons = new ArrayList<JToggleButton>();
		trueFalseButtons = new ArrayList<JToggleButton>();
		rGroup = new ButtonGroup();
		tGroup = new ButtonGroup();
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
	 * it takes in an IP address as a parameter.
	 * @param IP
	 */
	private void setupConnection(String IP)
	{
		try
		{
			client = new GGCConnection(new Socket(IP,
					GGCGlobals.INSTANCE.COMMUNICATION_PORT), new ResponderListener());
			Thread t = new Thread(client);
			JPanel sa = new JPanel();
			sa.setBackground(new Color(0, 125, 75));
			t.start();
			pResponder.setVisible(true);
			pConnectIP.setVisible(false);
			sendAnswer = new CustomJButton("Send Answer");
			sendAnswer.addActionListener(new ResponderListener());
			sa.add(sendAnswer);
			pResponder.add(sa, BorderLayout.PAGE_END);
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
	 * This listener will be attached to the button in the student screen.
	 * This class can be used by GGCConnection to test the connection to the specified IP address.
	 * This class will handle user input for a student connecting to the professor
	 * AND CONNECT.
	 * @author Ian Graham
	 *
	 */
	public class GGCConnectListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String[] IP = new String[4];
			IP[0] = ip[0].getText();
			IP[1] = ip[1].getText();
			IP[2] = ip[2].getText();
			IP[3] = ip[3].getText();
			String ipAddress = "";
			ipAddress += IP[0] + ".";
			ipAddress += IP[1] + ".";
			ipAddress += IP[2] + ".";
			ipAddress += IP[3];
			setupConnection(ipAddress);
		}
	}

	/**
	 * This method will usually only generate/show 3-5 buttons (regularly multiple choice)
	 * or 2 buttons (regularly true/false), and hide the ones that are not relevant to the
	 * scope of the button range. It is *planned* to go up to 26, but technically unless
	 * it is coded out it can do up to 99 (tested, it will do up to 99). I have coded out
	 * trying to make a one or two button multiple choice or something ridiculous like that.
	 * @param num - The number of buttons. Two buttons represents true/false. Anything more than
	 * that is multiple choice.
	 */
	private void generateButtons(int num, boolean trueFalse)
	{		
		if(num < 2)
		{
			//throw an error, someone tried to make a one answer multiple choice,
			//or either entered a negative or zero, all of which are bad.
		}
		else if(trueFalse)
		{
			showHideButtons(responderButtons, false);
			pResponder.remove(numPanelBuff);
			pResponder.remove(tfPanelBuff);
			pResponder.add(tfPanelBuff);
			if(trueFalseButtons.size() == 2)
			{
				showHideButtons(trueFalseButtons, true);
			}
			else
			{
				JToggleButton b1 = new CustomJToggleButton("True");
				trueFalseButtons.add(b1);
				tGroup.add(b1);

				JToggleButton b2 = new CustomJToggleButton("False");
				trueFalseButtons.add(b2);
				tGroup.add(b2);

				tfPanel.add(b1);
				tfPanel.add(b2);
				
				tfPanelBuff.add(tfPanel);
				
				pResponder.validate();
			}
		}
		else
		{
			showHideButtons(trueFalseButtons, false);
			
			pResponder.remove(numPanelBuff);
			pResponder.remove(tfPanelBuff);
			pResponder.add(numPanelBuff);
			if(num > responderButtons.size())
			{
				showHideButtons(responderButtons,true);
				for(int i = responderButtons.size(); i < num; i++)
				{
					JToggleButton r = new CustomJToggleButton(""+(char)('A'+i));
					rGroup.add(r);
					responderButtons.add(r);
					ownPanel.add(r);
					pResponder.validate();
				}
			}
			else
			{
				for(int i = 0; i < responderButtons.size(); i++)
				{
					if(i < num)
					{
						responderButtons.get(i).setVisible(true);
					}
					else
					{
						responderButtons.get(i).setVisible(false);
					}
				}
			}
		}
	}
	/**
	 * Shows or hides the buttons in the specified group.
	 * @param buttons
	 * @param state
	 */
	private void showHideButtons(ArrayList<JToggleButton> buttons, boolean state)
	{
		for(JToggleButton b : buttons)
		{
			b.setVisible(state);
		}
	}
	/**
	 * Given one of the button lists, finds which one is selected.
	 * @param buttons
	 * @return returns the text of the selected button.
	 */
	private String findSelected(ArrayList<JToggleButton> buttons)
	{
		for(int i = 0; i < buttons.size(); i++)
		{
			if(buttons.get(i).isSelected())
			{
				return buttons.get(i).getText();
			}
		}
		return "";
	}

	/**
	 * This class listens for when the professor clicks the send question button.
	 * It will send some sort of text to the student/client (maybe just T for true
	 * or false and M# for multiple choice where # is a number between 3 and 5) and
	 * will check for valid output on multiple choice.
	 * @author Ian Graham
	 *
	 */
	public class ResponderListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == sendAnswer)
			{
				String messageR = "";
				for(int i = 0; i < responderButtons.size(); i++)
				{
					if(responderButtons.get(i).isSelected())
					{
						messageR = ""+i;
					}
				}
				String messageT = findSelected(trueFalseButtons);
				if(messageR.length() < 1 && messageT.length() < 1)
				{
					//throw an error, pretty much no button was selected.
				}
				else if(messageR.length() > 0 && rtf)
				{
					client.sendMessage(messageR);
					sendAnswer.setEnabled(false);
				}
				else
				{
					if(messageT.equals("True"))
					{
						client.sendMessage("T");
						sendAnswer.setEnabled(false);
					}
					else if(messageT.equals("False"))
					{
						client.sendMessage("F");
						sendAnswer.setEnabled(false);
					}
					else
					{
						//throw an arbitrary error. something went wrong.
					}
				}
			}
			else if (e.getID() == GGCGlobals.INSTANCE.MESSAGE_EVENT_ID)
			{
				String message = e.getActionCommand();
				//This if statement means that it will always be at least one character. 
				//Hopefully if it's one, it's a "T"
				//It also means if it's multiple choice, that we aren't going to go past double digit buttons.
				if(message.length() > 0 && message.length() < 4)
				{
					if(message.substring(0,1).equals("T") && message.length() == 1)
					{
						rtf = false;
						generateButtons(2, true);
						sendAnswer.setEnabled(true);
					}
					else if(message.substring(0,1).equals("M") && message.length() > 1)
					{
						try
						{
							rtf = true;
							int num = Integer.parseInt(message.substring(1, message.length()));
							if(num > 2 && num < 27)
							{
								generateButtons(num, false);
								sendAnswer.setEnabled(true);
							}
							else
							{
								//Someone did something ridiculous like 2 or less choices or more than 26 choices.
								//Two is defaulted to true/false, less than two is not a valid question, and more than 26 is unreasonable.
							}
						}
						catch(NumberFormatException exc)
						{
							//throw an error, this happens when the text after the "M" is not a number. Ex: MC5
						}
					}
					else
					{
						//throw an error, a T or M is not present in the first letter of the message. This is bad.
					}
				}
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == exit1 || e.getSource() == exit2)
		{
			mainFrame.setVisible(false);
			mainFrame.dispose();
			this.stop();
			this.destroy();
		}
	}
}
