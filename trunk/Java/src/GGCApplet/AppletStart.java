/**
 * Converted to Applet format, need input on how it will pull objects from on end or the other.
 * 
 * @author Marcus Michalske
 * TODO Add more functionality to the different panels.
 * TODO Fix the radio buttons to have functionality along with text field.
 * TODO Add the "Update question button" along with a default "Questions" button with the last number of buttons.
 * TODO Axis on bar graph need to be updated to reflect correct layout.
 * TODO add functionality to bar graph so that updates after it is hidden.
 */
package GGCApplet;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.*;

import GeneralGrizzlyConsensus.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class AppletStart extends Applet
{
	//I added this because the client/server GUI's had it.
	private static final long serialVersionUID = 1L;
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
	private String[] types = {"True/False", "A-C", "A-D", "A-E"};
	//This is to tell that all the IP numbers have been filled in.
	private JTextField[] ip;
	//This is the instance of the server that, should it need to be used, will be initialized.
	private GGCServer server;
	//This is an instance of a connection which will essentially serve as the client. It will be initialized
	//upon clicking on responder.
	private GGCConnection client;
	//This button group is used by the session manager to get the selected button and 
	//determine the type of question sent.
	private ArrayList<JToggleButton> managerButtons;
	//Button groups which organize the buttons used by the session manager and responder.
	private ButtonGroup mGroup, rGroup, tGroup;
	//This button group is used by the responder and will be used to both determine which
	//response buttons are on the screen and which response is sent to the session manager
	//when the sendAnswer button is clicked.
	private ArrayList<JToggleButton> responderButtons;
	//This button group is used specifically for the true and false buttons.
	private ArrayList<JToggleButton> trueFalseButtons;
	//This is used for 
	private JTextField numField;
	//This is the main frame for the whole GUI.
	private JFrame mainFrame;
	//This is the actual object that holds the data.
	private static DefaultCategoryDataset barData;
	//This is the object which will handle the updates of the JFreeChart data.
	private GraphUpdater updater;

	public void init()
	{
		Runnable runner = new Runnable() {
            public void run() {
            	mainFrame = new JFrame("Georgia Gwinnett College General Grizzly Consensus");
        		mainFrame.setSize(500, 432);
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

		/*
		 * Finds the current machine's IP address. Faulty code, replace so we can always get the right
		 * IP address.
		 * -Ian Graham
		 */
		/*try
		{
			InetAddress iP = InetAddress.getLocalHost();
			cIP = iP.getHostAddress();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}//*/

		// At this time people are more likely to be using IPv4 addresses instead of IPv6 addresses.
		cIP = GGCServer.getLikelyIpv4Address().getHostAddress();

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

		//Whatever buttons end up here, add them to the Button group.
		responderButtons = new ArrayList<JToggleButton>();
		trueFalseButtons = new ArrayList<JToggleButton>();
		rGroup = new ButtonGroup();
		tGroup = new ButtonGroup();

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

	private void createSessionManager()
	{
		pSessionM = new JPanel();
		pSessionM.setVisible(true);
		pShowHide = new JButton("Show/Hide");
		pShowHide.addActionListener(new GGCGraphListener());
		managerButtons = new ArrayList<JToggleButton>();
		mGroup = new ButtonGroup();
		pSessionM.setLayout(new GridLayout(2,1));
		JPanel lP1 = new JPanel();
		JPanel lP2 = new JPanel();
		JPanel lP3 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));
		lP2.setLayout(new GridLayout(2,1));
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));
		//lP2.setBackground(new Color(255,255,255));
		//JComboBox qType = new JComboBox(types);
		JToggleButton tfButton = new JToggleButton("True/False");
		JToggleButton numButton = new JToggleButton("Number Responses: ");
		managerButtons.add(tfButton);
		managerButtons.add(numButton);
		mGroup.add(tfButton);
		mGroup.add(numButton);
		numField = new JTextField(5);
		ButtonGroup bg = new ButtonGroup();
		
		bg.add(tfButton);
		bg.add(numButton);
		
		sendQuestion = new JButton("Send Question");
		sendQuestion.addActionListener(new ManagerListener());
		lP2.add(tfButton);
		lP3.add(numButton);
		lP3.add(numField);
		lP2.add(lP3);
		lP1.add(lP2);
		lP1.add(sendQuestion);
		pSessionM.add(lP1);
		//TODO Make a UPDATABLE bar graph, maybe a new class that interfaces with it?
		//Ian - This is probably the best way to go about it, since we're already splitting this class.
		CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        updater = new GraphUpdater(barData);
        chartPanel = new ChartPanel(chart);
        pSessionM.add(new JLabel(cIP));
        pSessionM.add(pShowHide);
		pSessionM.add(chartPanel);
	}
	
	private static CategoryDataset createDataset()
	{

        // row keys...
        String seriesT = "True";
        String seriesF = "False";
        String seriesA = "A";
        String seriesB = "B";
        String seriesC = "C";
        String seriesD = "D";
        String seriesE = "E";

        // column keys...
        String categoryR = "Responses";

        // create the dataset...
        barData = new DefaultCategoryDataset();

        barData.addValue(1.0, seriesT, categoryR);
        barData.addValue(4.0, seriesF, categoryR);
        barData.addValue(3.0, seriesA, categoryR);
        barData.addValue(5.0, seriesB, categoryR);
        barData.addValue(6.0, seriesC, categoryR);
        barData.addValue(7.0, seriesD, categoryR);
        barData.addValue(8.0, seriesE, categoryR);

        return barData;

    }
	
    private static JFreeChart createChart(CategoryDataset dataset)
    {

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            "Number of Responses",       // chart title
            "Response type",               // domain axis label
            "Number of Responses",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        
        /*
         * Code supplied from BarChartDemo1 at:
         * http://www.jfree.org/jfreechart
         * 
         * code edited by Marcus Michalske 
         */
        
        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);
        
        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, new Color(0, 125, 75),
                0.0f, 0.0f, new Color(0, 0, 0));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.white,
                0.0f, 0.0f, Color.darkGray);
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, new Color(0, 125, 75),
                0.0f, 0.0f, new Color(0, 0, 0));
        GradientPaint gp3 = new GradientPaint(0.0f, 0.0f, Color.white,
                0.0f, 0.0f, Color.darkGray);
        GradientPaint gp4 = new GradientPaint(0.0f, 0.0f, new Color(0, 125, 75),
                0.0f, 0.0f, new Color(0, 0, 0));
        GradientPaint gp5 = new GradientPaint(0.0f, 0.0f, Color.white,
                0.0f, 0.0f, Color.darkGray);
        GradientPaint gp6 = new GradientPaint(0.0f, 0.0f, new Color(0, 125, 75),
                0.0f, 0.0f, new Color(0, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        renderer.setSeriesPaint(3, gp3);
        renderer.setSeriesPaint(4, gp4);
        renderer.setSeriesPaint(5, gp5);
        renderer.setSeriesPaint(6, gp6);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }
    /**
     * Code from Stephen's server GUI.
     */
    private void setupManagerCloseListener()
    {
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt)
            {
                // Do Socket clean-up here.
                server.stopListening();
                System.exit(0);
            }
        });
    }
    /**
     * Code from Stephen's server GUI.
     */
    private void setupServer()
    {
        server = GGCServer.INSTANCE;
        server.setMessageSink(new ManagerListener());
        Thread t = new Thread(server);
        t.start();
    }

	class SelectionCardR implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			connectIP();
			mfContainer.add(pConnectIP, "IP Connection Panel");
			setupResponderCloseListener();
			createResponder();
			mfContainer.add(pResponder, "Responders Panel");
			pConnectIP.setVisible(true);
			pSelect.setVisible(false);
		}
	}

	class SelectionCardSM implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			showIP();
			setupManagerCloseListener();
			setupServer();
			mfContainer.add(pShowIP, "IP Panel");
			createSessionManager();
			mfContainer.add(pSessionM, "Session Managers Panel");
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
			if(isValidIP(IP))
			{
				String ipAddress = "";
				ipAddress += IP[0] + ".";
				ipAddress += IP[1] + ".";
				ipAddress += IP[2] + ".";
				ipAddress += IP[3];
				setupConnection(ipAddress);
				pResponder.setVisible(true);
				pConnectIP.setVisible(false);
				sendAnswer = new JButton("Send Answer");
				sendAnswer.addActionListener(new ResponderListener());
				pResponder.add(sendAnswer);
			}
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
				JOptionPane.showMessageDialog(null, "Please fill out all text boxes before " +
						"attempting to connect.",
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
						JOptionPane.showMessageDialog(null, "Please use numbers between " +
								"254 and 1 for the first and last box.",
								"Invalid IP", JOptionPane.ERROR_MESSAGE);
						return false;
					}

				}
				if(i == 1 || i == 2)
				{
					if(num > 254 || num < 0)
					{
						showBadIPField(i);
						JOptionPane.showMessageDialog(null, "Please use numbers between 254 " +
								"and 0 for the second and third box.",
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
	
	@Deprecated
	private void changePanel(JPanel jp)
    {
        Container container = mainFrame.getContentPane();        
        jp.setVisible(true);
        container.add(jp);
    }

	/**
	 * This class listens for the graph show/hide button. If it is clicked, it shows or hides the graph.
	 * @author Ian Graham
	 *
	 */
	public class GGCGraphListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			chartPanel.setVisible(!chartPanel.isVisible());
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
		ImageIcon defaultButton = new ImageIcon("DefaultButton.png");
		//Image tempImg = defaultButtonImage.getImage(); 
		
		if(num < 2)
		{
			//throw an error, someone tried to make a one answer multiple choice,
			//or either entered a negative or zero, all of which are bad.
		}
		else if(trueFalse)
		{
			showHideButtons(responderButtons, false);
			if(trueFalseButtons.size() == 2)
			{
				showHideButtons(trueFalseButtons, true);
			}
			else
			{
				JToggleButton b1 = new JToggleButton("True");
				//TODO Get image to scale to button.
				//Image newimg = tempImg.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);  
				//ImageIcon defaultButton = new ImageIcon(newimg);
				//System.out.print("W: "+b1.getWidth() + "H: " + b1.getHeight());
				b1.setIcon(defaultButton);
				b1.setHorizontalTextPosition(SwingConstants.CENTER);
				b1.setBorderPainted(false);
				b1.setContentAreaFilled(false); 
				b1.setFocusPainted(false); 
				b1.setOpaque(false);
				trueFalseButtons.add(b1);
				tGroup.add(b1);
				
				JToggleButton b2 = new JToggleButton("False");
				trueFalseButtons.add(b2);
				tGroup.add(b2);
				pResponder.add(b1);
				pResponder.add(b2);
				pResponder.validate();
			}
		}
		else
		{
			showHideButtons(trueFalseButtons, false);
			if(num > responderButtons.size())
			{
            	for(int i = 0; i < num; i++)
				{
					JToggleButton r = new JToggleButton(""+i);
					rGroup.add(r);
					responderButtons.add(r);
					pResponder.add(r);
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
				String messageR = findSelected(responderButtons);
				String messageT = findSelected(trueFalseButtons);
				if(messageR.length() < 1 && messageT.length() < 1)
				{
					//throw an error, pretty much no button was selected.
				}
				else if(messageR.length() > 0)
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
						generateButtons(2, true);
						sendAnswer.setEnabled(true);
					}
					else if(message.substring(0,1).equals("M") && message.length() > 1)
					{
						try
						{
							int num = Integer.parseInt(message.substring(1, message.length()));
							generateButtons(num, false);
							sendAnswer.setEnabled(true);
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

	/**
	 * This class is used to gather input from the student's answer frame.
	 * The listener will send output in the form of an INT (answer number) to the server. The
	 * server will interpret the output.
	 * @author Ian Graham
	 *
	 */
	public class ManagerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == sendQuestion)
			{
				String message = findSelected(managerButtons);
				if(message == "True/False")
				{
					server.sendMessageToAll("T");
					//two is a filler, it doesn't do anything, because the boolean is all
					//that the program needs to determine what to do.
					updater.newQuestion(true, 2);
				}
				else if(message == "Number Responses: ")
				{
					try
					{
						int num = Integer.parseInt(numField.getText());
						server.sendMessageToAll("M"+num);
						updater.newQuestion(false, num);
					}
					catch(NumberFormatException err)
					{
						//throw an error, the number is bad.
					}
				}
				else
				{
					//throw an error, the number is bad or there is no
					//selected input.
				}
			}
			else if (e.getID() == GGCGlobals.INSTANCE.MESSAGE_EVENT_ID)
			{
				String message = e.getActionCommand();
				if(Character.isDigit(message.charAt(0)))
				{
					try
					{
						int i = Integer.parseInt(message);
						updater.incrementData(i);
					}
					catch(NumberFormatException err)
					{
						//throw an error, maybe the first digit was a zero but the second wasn't
						//TODO The number of buttons can currently go over 26, make sure it can't.
					}
				}
				else if(message.equals("T"))
				{
					updater.incrementData(0);
				}
				else
				{
					updater.incrementData(1);
				}
			}
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
