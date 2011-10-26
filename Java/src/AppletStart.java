/**
 * Converted to Applet format, need input on how it will pull objects from on end or the other.
 * 
 * @author Marcus Michalske
 * TODO need to either import a Bar Graph library or create one from scratch.
 * TODO Add more functionality to the different panels.
 */
package GGCApplet;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.*;

public class AppletStart extends Applet
{
	//The container to hold the layout of the main frame of the Applet.
	private Container mfContainer;
	//The panel that will hold the selection between a responder or session manager.
	private JPanel pSelect;
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
	//The IP of the computer
	private String cIP = "000.000.0.0";
	//The type of queries to be polled upon.
	private String[] types = {"True/False", "A-C", "A-D", "A-E"};
	//This is to tell that all the IP numbers have been filled in.
	private boolean ip1Entered = false, ip2Entered = false, ip3Entered = false, ip4Entered = false;

	public void init()
	{
		JFrame mainFrame = new JFrame("Georgia Gwinnett College General Grizzly Consensus");
		mainFrame.setSize(300, 432);
		mainFrame.setResizable(false);
		mainFrame.setVisible(true);

		selectPane();
		showIP();
		connectIP();
		createResponder();
		createSessionManager();

		mfContainer = mainFrame.getContentPane();
		mfContainer.setLayout(new CardLayout());		
		mfContainer.add(pSelect, "Selection Panel");
		mfContainer.add(pShowIP, "IP Panel");
		mfContainer.add(pConnectIP, "IP Connection Panel");
		mfContainer.add(pResponder, "Responders Panel");
		mfContainer.add(pSessionM, "Session Managers Panel");

		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void selectPane()
	{
		pSelect = new JPanel();
		pSelect.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 200));
		pSelect.setVisible(true);

		JButton bResponder = new JButton("Responder");
		JButton bSManager = new JButton("Session Manager");

		ActionListener selectionCardR = new SelectionCardR();
		ActionListener selectionCardSM = new SelectionCardSM();
		bResponder.addActionListener(selectionCardR);
		bSManager.addActionListener(selectionCardSM);

		pSelect.add(bResponder);
		pSelect.add(bSManager);
	}

	private void showIP()
	{
		pShowIP = new JPanel();
		pShowIP.setLayout(new GridLayout(4,1));
		pShowIP.setVisible(false);
    
    //Finds the current machine's IP address.
		try
    {
	     InetAddress iP = InetAddress.getLocalHost();
	     cIP = iP.getHostAddress();
    }
    catch(Exception e) 
    {
     e.printStackTrace();
    }

		JLabel ipLabel1 = new JLabel("Please have your responders ");
		JLabel ipLabel2 = new JLabel("connect to this IP: ");
		JLabel ipLabel3 = new JLabel(cIP);
		JButton smContorlP = new JButton("Contune");

		ActionListener showIPCard = new ShowIPCard();
		smContorlP.addActionListener(showIPCard);

		pShowIP.add(ipLabel1);
		pShowIP.add(ipLabel2);
		pShowIP.add(ipLabel3);
		pShowIP.add(smContorlP);
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

		try
		{
			MaskFormatter formatter = new MaskFormatter("###"); 
			JFormattedTextField ip1 = new JFormattedTextField(formatter);
			ip1.setColumns(2);
			JFormattedTextField ip2 = new JFormattedTextField(formatter);
			ip2.setColumns(2);
			JFormattedTextField ip3 = new JFormattedTextField(formatter);
			ip3.setColumns(2);
			JFormattedTextField ip4 = new JFormattedTextField(formatter);
			ip4.setColumns(2);

			JLabel dot1= new JLabel(".");
			JLabel dot2= new JLabel(".");
			JLabel dot3= new JLabel(".");

			rContorlP = new JButton("Connect");
			rContorlP.setEnabled(false);
			
			InputMethodListener checkFilled1 = new CheckFilled1();
			ip1.addInputMethodListener(checkFilled1);
			
			InputMethodListener checkFilled2 = new CheckFilled2();
			ip1.addInputMethodListener(checkFilled2);
			
			InputMethodListener checkFilled3 = new CheckFilled3();
			ip1.addInputMethodListener(checkFilled3);
			
			InputMethodListener checkFilled4 = new CheckFilled4();
			ip1.addInputMethodListener(checkFilled4);
			
			ActionListener connectIPCard = new ConnectIPCard();
			rContorlP.addActionListener(connectIPCard);

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
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}

	private void createResponder()
	{
		pResponder = new JPanel();
		pResponder.setVisible(false);

		JPanel lP1 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel lP2 = new JPanel();
		lP1.setLayout(new GridLayout(3,2));

		JButton tButton = new JButton("True");
		JButton fButton = new JButton("False");
		JButton aButton = new JButton("A");
		JButton bButton = new JButton("B");
		JButton cButton = new JButton("C");
		JButton dButton = new JButton("D");
		JButton eButton = new JButton("E");

		lP1.add(tButton);
		lP1.add(fButton);

		lP2.add(aButton);
		lP2.add(bButton);
		lP2.add(cButton);
		lP2.add(dButton);
		lP2.add(eButton);

		pResponder.add(lP1);
		pResponder.add(lP2);
	}

	private void createSessionManager()
	{
		pSessionM = new JPanel();
		pSessionM.setVisible(true);

		pSessionM.setLayout(new GridLayout(1,2));

		JPanel lP1 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel lP2 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));
		//lP2.setBackground(new Color(255,255,255));

		JComboBox qType = new JComboBox(types);
		JLabel bGraph = new JLabel("Bar Graph here");

		lP1.add(qType);

		pSessionM.add(lP1);
		//TODO Make a UPDATABLE bar graph, maybe a new class that interfaces with it?
		pSessionM.add(new GraphBox());
	}

	public void stop()
	{

	}

	class SelectionCardR implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			pConnectIP.setVisible(true);
			pSelect.setVisible(false);
		}
	}

	class SelectionCardSM implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			pShowIP.setVisible(true);
			pSelect.setVisible(false);
		}
	}

	class ShowIPCard implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			pSessionM.setVisible(true);
			pShowIP.setVisible(false);
		}
	}

	class ConnectIPCard implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			pResponder.setVisible(true);
			pConnectIP.setVisible(false);
		}
	}
	
	public class CheckFilled1 implements InputMethodListener
	{

		@Override
		public void caretPositionChanged(InputMethodEvent arg0)
		{
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
			
			System.out.println("Test");
		}

		@Override
		public void inputMethodTextChanged(InputMethodEvent arg0)
		{
			ip1Entered = true;
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
			System.out.println("Test");
		}

	}
	
	public class CheckFilled2 implements InputMethodListener
	{

		@Override
		public void caretPositionChanged(InputMethodEvent arg0)
		{
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
		}

		@Override
		public void inputMethodTextChanged(InputMethodEvent arg0)
		{
			ip2Entered = true;
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
		}

	}
	
	public class CheckFilled3 implements InputMethodListener
	{

		@Override
		public void caretPositionChanged(InputMethodEvent arg0)
		{
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
		}

		@Override
		public void inputMethodTextChanged(InputMethodEvent arg0)
		{
			ip3Entered = true;
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
		}

	}
	
	public class CheckFilled4 implements InputMethodListener
	{

		@Override
		public void caretPositionChanged(InputMethodEvent arg0)
		{
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
		}

		@Override
		public void inputMethodTextChanged(InputMethodEvent arg0)
		{
			ip4Entered = true;
			if (ip1Entered && ip2Entered && !ip3Entered && ip4Entered)
				rContorlP.setEnabled(true);
			else
				rContorlP.setEnabled(false);
		}

	}

	/**
	 * This class listens for the graph show/hide button. If it is clicked, it shows or hides the graph.
	 * @author Ian Graham
	 *
	 */

	public class GGCGraphListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * The listener will show or hide the graph, depending on its current state.
			 * As suggested, the code should be something like this:
			 *
			 * graphPanel.setVisible(!graphPanel.isVisible())
			 *
			 */

		}

	}

	/**
	 * This class listens for when the professor clicks the send question button.
	 * It will send some sort of text to the student/client (maybe just T for true
	 * or false and M# for multiple choice where # is a number between 3 and 5) and
	 * will check for valid output on multiple choice.
	 * @author Ian Graham
	 *
	 */

	public class GGCQuestionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * Checks for multiple choice. If it is, then it checks for a valid number (3-5).
			 * If it is not it just passes the output for true/false.
			 */

		}

	}

	/**
	 * This class is used to gather input from the student's answer frame.
	 * The listener will send output in the form of an INT (answer number) to the server. The
	 * server will interpret the output.
	 * @author Ian Graham
	 *
	 */

	public class GGCAnswerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e)

		{
			/*
			 * Looks at the selected radio button's INDEX and sends that as output.
			 */

		}

	}

	// OLD CODE! Saved for possible use later.
	/*class LimitJTextField extends PlainDocument
	{
		private int limit;

		public LimitJTextField(int limit)
		{
			super();
			this.limit = limit;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
		{
			if (limit == 0 || getLength() + str.length() <= limit)
			{
				for (int i = 0; i < str.length(); i++)
				{					 
					if (!Character.isDigit(str.charAt(i)))
						super.insertString(offs, "", a);
				}
				super.insertString(offs, str, a);
			}
		}
	}*/

	class GraphBox extends JComponent
	{
		public void paint(Graphics g)
		{
			g.setColor(Color.WHITE);
			g.fill3DRect(20, 20, 120, 120,true);
		}
	}
}
