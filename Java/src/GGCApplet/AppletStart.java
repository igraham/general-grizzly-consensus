/**
 * Converted to Applet format, need input on how it will pull objects from on end or the other.
 * 
 * @author Marcus Michalske
 * TODO Fix the GUI, everything is completely functional other than that.
 * TODO Implement the drop-down box in place of the text field on the manager panel.
 */
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

import com.sun.awt.AWTUtilities;

public class AppletStart extends Applet
{
	//I added this because the client/server GUI's had it.
	private static final long serialVersionUID = 1L;
	//The container to hold the layout of the main frame of the Applet.
	private Container mfContainer;
	//The panel that will hold the selection between a responder or session manager.
	private JPanel pSelect;
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
	/**
	 * This is the input for the JComboBox of multiple choice options.
	 */
	private String[] types = {"A-C", "A-D", "A-E", "A-F", "A-G", "A-H", "A-I", "A-J", "A-K", "A-L", "A-M", 
			"A-N", "A-O", "A-P", "A-Q", "A-R", "A-S", "A-T", "A-U", "A-V", "A-W", "A-X", "A-Y", "A-Z"};
	/**
	 * This is the replacement for the number text field in the previous GUI. It is a drop-down list which
	 * uses the array of strings "types" as input.
	 */
	private JComboBox multipleChoice;
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
	//This is the main frame for the whole GUI.
	private JFrame mainFrame;
	//This is the bar graph which is used to change the colors on the graph.
	private JFreeChart graph;
	//This is the actual object that holds the data.
	private static DefaultCategoryDataset barData;
	//This is the object which will handle the updates of the JFreeChart data.
	private GraphUpdater updater;
	//Displays the number of connections in the server.
	private JLabel connected;
	//Displays the answers received.
	private JLabel ans;
	//Counts the number of answers received for a question.
	private int numAnswered;
	//This boolean is a temporary fix to an issue when switching between multiple choice and T/F
	private boolean rtf;
	//This Point is used to keep track of where the JFrame Window is for dragging while in undecorated mode.
	private Point point = new Point();
	//This boolean is used to tell when the mouse has left or entered the JFrame at least once.
	private boolean currentIn = true;
	//JPanels
	private JPanel tfPanel, tfPanelBuff, ownPanel, numPanelBuff;

	public void init()
	{
		// Get our parameter from the HTML tag.
		int loadProfessor = -1;
		String param = this.getParameter("loadProfessor");
		if (param != null)
		{
			// Parameter exists so parse to int (if properly set in HTML should only be 1 or 0).
			loadProfessor = Integer.parseInt(param);
		}
		Runnable runner = new Runnable() {
			public void run()
			{
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
				//The color is GGC EverGreen Green
				mainFrame.getContentPane().setBackground(new Color(0, 125, 75));
				AWTUtilities.setWindowShape(mainFrame, new RoundRectangle2D.Float(0, 0,mainFrame.getWidth(), mainFrame.getHeight(), 30, 30));
				mainFrame.setVisible(true);

				final CustomGlass glass = new CustomGlass(mainFrame);
				mainFrame.setGlassPane(glass);
				glass.setFocusable(false);
				glass.setEnabled(false);
				glass.setVisible(true);

				selectPane();
				mfContainer = mainFrame.getContentPane();
				mfContainer.setLayout(new CardLayout());		
				mfContainer.add(pSelect, "Selection Panel");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
						}
					}
				});

				// When the mouse enters the JFrame it disposes of the frame and redraws the undecorated boarder
				mainFrame.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) {
						if(!currentIn)
						{
							//System.out.println("In");
							mainFrame.dispose();
							mainFrame.setUndecorated(true);
							AWTUtilities.setWindowShape(mainFrame, new RoundRectangle2D.Float(0, 0,mainFrame.getWidth(), mainFrame.getHeight(), 30, 30));
							currentIn = true;
							mainFrame.setVisible(true);
						}
					}
				});
			}
		};

		EventQueue.invokeLater(runner);
	}

	private void selectPane()
	{
		pSelect = new JPanel();
		pSelect.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 200));
		pSelect.setVisible(true);
		pSelect.setBackground(new Color(0, 125, 75));
		pSelect.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

		JButton bResponder = new CustomJButton("Responder");
		JButton bSManager = new CustomJButton("Session Manager");

		ActionListener selectionCardR = new SelectionCardR();
		ActionListener selectionCardSM = new SelectionCardSM();
		bResponder.addActionListener(selectionCardR);
		bSManager.addActionListener(selectionCardSM);

		pSelect.add(bResponder);
		pSelect.add(bSManager);
	}

	private void connectIP()
	{
		pConnectIP = new JPanel();
		pConnectIP.setLayout(new BorderLayout());
		pConnectIP.setVisible(false);
		pConnectIP.setBackground(new Color(0, 125, 75));
		pConnectIP.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		
		JPanel lP1 = new JPanel();
		lP1.setBackground(new Color(0, 125, 75));
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel lP2 = new JPanel();
		lP2.setBackground(new Color(0, 125, 75));
		lP2.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JPanel lP3 = new JPanel();
		lP3.setBackground(new Color(0, 125, 75));
		lP3.setLayout(new FlowLayout(FlowLayout.CENTER));
		
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
		
		JLabel ipText = new JLabel("Please enter your instructors IP Here: ");
		lP3.add(ipText);

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
		//ownPanel.setLayout(new GridLayout(10,3));
		numPanelBuff.add(ownPanel);
		
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

	private void createSessionManager()
	{
		numAnswered = 0;
		pSessionM = new JPanel();
		pSessionM.setVisible(true);
		pSessionM.setLayout(new BorderLayout());
		pSessionM.setBackground(new Color(0, 125, 75));
		pSessionM.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		cPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
		cPanel.setBackground(new Color(0, 125, 75));
		cPanel.setBorder(BorderFactory.createTitledBorder("Your Control Panel"));
		pShowHide = new CustomJButton("Show/Hide");
		pShowHide.addActionListener(new GGCGraphListener());
		cPanel.add(pShowHide);
		connected = new JLabel("Connected: 0");
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					connected.setText("Connected: "+server.getNumberOfConnectedClients());
				}
			}

		});
		t.start();
		ans = new JLabel("Answered: 0");
		cPanel.add(connected);
		cPanel.add(ans);
		connected.setText("Connected: "+server.getNumberOfConnectedClients());
		JPanel qPanel = new JPanel();
		JPanel tfPanel = new JPanel();
		JPanel numPanel = new JPanel();
		qPanel.setBackground(new Color(0, 125, 75));
		tfPanel.setBackground(new Color(0, 125, 75));
		numPanel.setBackground(new Color(0, 125, 75));

		qPanel.setLayout(new GridLayout(3,1));
		//qPanel.add(Box.createVerticalGlue());
		tfPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		//numPanel.setLayout(new GridLayout(1,2));

		qPanel.setBorder(BorderFactory.createTitledBorder("Question Type"));

		managerButtons = new ArrayList<JToggleButton>();
		mGroup = new ButtonGroup();

		JToggleButton tfButton = new CustomJToggleButton("True/False");
		JToggleButton numButton = new CustomJToggleButton("Number Responses: ");
		managerButtons.add(tfButton);
		managerButtons.add(numButton);
		mGroup.add(tfButton);
		mGroup.add(numButton);
		multipleChoice = new JComboBox(types);
		multipleChoice.setMaximumRowCount(5);
		ButtonGroup bg = new ButtonGroup();

		bg.add(tfButton);
		bg.add(numButton);

		sendQuestion = new CustomJButton("Send Question");
		sendQuestion.addActionListener(new ManagerListener());
		tfPanel.add(tfButton);
		numPanel.add(numButton);
		numPanel.add(multipleChoice);

		qPanel.add(tfButton);
		qPanel.add(numPanel);
		qPanel.add(sendQuestion);

		CategoryDataset dataset = createDataset();
		graph = createChart(dataset);
		updater = new GraphUpdater(barData, graph);
		chartPanel = new ChartPanel(graph);

		//JScrollPane scrollChart = new JScrollPane(chartPanel);
		chartPanel.setMinimumSize(new Dimension(350, 250));
		chartPanel.setMaximumSize(new Dimension(500, 450));

		JPanel ipPanel = new JPanel();
		JPanel panel = new JPanel();
		ipPanel.setBackground(new Color(0, 125, 75));
		panel.setBackground(new Color(0, 125, 75));
		BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.add(chartPanel);

		ipPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel myIP = new JLabel("Your IP: ");
		// At this time people are more likely to be using IPv4 addresses instead of IPv6 addresses.
		cIP = GGCServer.getLikelyIpv4Address().getHostAddress();
		JLabel myIP1 = new JLabel(cIP);
		ipPanel.add(myIP);
		ipPanel.add(myIP1);


		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(new Color(0, 125, 75));
		buttonPanel.add(qPanel, BorderLayout.WEST);
		buttonPanel.add(cPanel, BorderLayout.CENTER);

		pSessionM.add(buttonPanel, BorderLayout.SOUTH);
		pSessionM.add(ipPanel, BorderLayout.PAGE_START);
		pSessionM.add(panel, BorderLayout.CENTER);
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
			setupManagerCloseListener();
			setupServer();
			createSessionManager();
			mfContainer.add(pSessionM, "Session Managers Panel");
			pSessionM.setVisible(true);
			pSelect.setVisible(false);
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
					JToggleButton r = new CustomJToggleButton(""+i);
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
					responderButtons.get(i).setSelected(false);
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
				numAnswered = 0;
				ans.setText("Answered: 0");
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
					label0:
						try
				{
							int num = multipleChoice.getSelectedIndex()+3;
							if(num > 2 && num < 27)
							{
								server.sendMessageToAll("M"+num);
								updater.newQuestion(false, num);
								break label0;
							}
							throw new NumberFormatException();
				}
				catch(NumberFormatException err)
				{
					//throw an error, the number is bad.
					//Zomg, labels!
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
						numAnswered++;
						ans.setText("Answered: "+numAnswered);
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
					numAnswered++;
					ans.setText("Answered: "+numAnswered);
					updater.incrementData(0);
				}
				else
				{
					numAnswered++;
					ans.setText("Answered: "+numAnswered);
					updater.incrementData(1);
				}
			}
		}
	}
}