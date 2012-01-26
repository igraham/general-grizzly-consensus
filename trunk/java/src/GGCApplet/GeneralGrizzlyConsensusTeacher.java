package GGCApplet;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
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

/**
 * This is the professor's view of the GGC^2 program. The graph, send answer panel, and control panel are all displayed
 * in the initial screen.
 * @author Ian Graham
 *
 */
public class GeneralGrizzlyConsensusTeacher extends Applet implements ActionListener
{
	/**
	 * I added this because the client/server GUI's had it.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The container to hold the layout of the main frame of the Applet.
	 */
	private Container mfContainer;
	/**
	 * The panel will display the current IP of the computer in the network.
	 */
	private JPanel pShowIP;
	/**
	 * The session managers query type and graph.
	 */
	private JPanel pSessionM;
	/**
	 * This is the button which shows and hides the graph.
	 */
	private JButton pShowHide;
	/**
	 * This button is the Session Manager's way of sending questions to the students.
	 */
	private JButton sendQuestion;
	/**
	 * The panel which contains the graph which displays the results of the question.
	 */
	private ChartPanel chartPanel;
	/**
	 * The IP of the computer
	 */
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
	/**
	 * This is the instance of the server that, should it need to be used, will be initialized.
	 */
	private GGCServer server;
	/**
	 * This button group is used by the session manager to get the selected button and 
	 * determine the type of question sent.
	 */
	private ArrayList<JToggleButton> managerButtons;
	/**
	 * Button groups which organize the buttons used by the session manager and responder.
	 */
	private ButtonGroup mGroup;
	/**
	 * This is the main frame for the whole GUI.
	 */
	private JFrame mainFrame;
	/**
	 * This is the bar graph which is used to change the colors on the graph.
	 */
	private JFreeChart graph;
	/**
	 * This is the actual object that holds the data.
	 */
	private static DefaultCategoryDataset barData;
	/**
	 * This is the object which will handle the updates of the JFreeChart data.
	 */
	private GraphUpdater updater;
	/**
	 * Displays the number of connections in the server.
	 */
	private JLabel connected;
	/**
	 * Displays the answers received.
	 */
	private JLabel ans;
	/**
	 * Counts the number of answers received for a question.
	 */
	private int numAnswered;
	/**
	 * This Point is used to keep track of where the JFrame Window is for dragging while in undecorated mode.
	 */
	private Point point = new Point();
	/**
	 * This boolean is used to tell when the mouse has left or entered the JFrame at least once.
	 */
	private boolean currentIn = true;
	/**
	 * Exit button
	 */
	private CustomJButton exit;

	/**
	 * This is the initialization method for the applet. It's essentially the constructor. The code is surrounded by an
	 * EventQueue runnable because swing has threading issues.
	 */
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
				//The color is GGC EverGreen Green
				mainFrame.getContentPane().setBackground(new Color(0, 125, 75));
				//AWTUtilities.setWindowShape(mainFrame, new RoundRectangle2D.Float(0, 0,mainFrame.getWidth(), mainFrame.getHeight(), 30, 30));
				mainFrame.setVisible(true);

				final CustomGlass glass = new CustomGlass(mainFrame);
				mainFrame.setGlassPane(glass);
				glass.setFocusable(false);
				glass.setEnabled(false);
				glass.setVisible(true);
				mfContainer = mainFrame.getContentPane();
				mfContainer.setLayout(new CardLayout());		

				setupManagerCloseListener();
				setupServer();
				createSessionManager();
				mfContainer.add(pSessionM, "Session Managers Panel");
				pSessionM.setVisible(true);
				
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

	/**
	 * This initializes all of the GUI elements, attaches listeners, and sets up a server waiting 
	 * to accept connections.
	 */
	private void createSessionManager()
	{
		System.out.println("check 1");
		numAnswered = 0;
		pSessionM = new JPanel();
		pSessionM.setVisible(true);
		pSessionM.setLayout(new BorderLayout());
		pSessionM.setBackground(new Color(0, 125, 75));
		pSessionM.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		System.out.println("check 2");
		exit = new CustomJButton("Exit");
		exit.addActionListener(this);

		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		cPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
		cPanel.setBackground(new Color(0, 125, 75));
		cPanel.setBorder(BorderFactory.createTitledBorder("Your Control Panel"));
		pShowHide = new CustomJButton("Show/Hide");
		pShowHide.addActionListener(new GGCGraphListener());
		cPanel.add(pShowHide);
		connected = new JLabel("Connected: 0");
		System.out.println("check 3");
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

		System.out.println("check 4");
		qPanel.setLayout(new GridLayout(3,1));
		tfPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
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
		System.out.println("check 5");

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

		chartPanel.setMinimumSize(new Dimension(350, 250));
		chartPanel.setMaximumSize(new Dimension(500, 450));
		System.out.println("check 6");

		JPanel exitPanel = new JPanel(new BorderLayout());
		exitPanel.setBackground(new Color(0, 125, 75));
		exitPanel.add(exit, BorderLayout.EAST);
		
		JPanel ipPanel = new JPanel();
		JPanel panel = new JPanel();
		ipPanel.setBackground(new Color(0, 125, 75));
		panel.setBackground(new Color(0, 125, 75));
		BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.add(chartPanel);

		System.out.println("check 7");
		ipPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel myIP = new JLabel("Your IP: ");
		// At this time people are more likely to be using IPv4 addresses instead of IPv6 addresses.
		cIP = GGCServer.getLikelyIpv4Address().getHostAddress();
		JLabel myIP1 = new JLabel(cIP);
		ipPanel.add(myIP);
		ipPanel.add(myIP1);
		exitPanel.add(ipPanel, BorderLayout.CENTER);

		System.out.println("check 8");
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(new Color(0, 125, 75));
		buttonPanel.add(qPanel, BorderLayout.WEST);
		buttonPanel.add(cPanel, BorderLayout.CENTER);

		pSessionM.add(buttonPanel, BorderLayout.SOUTH);
		pSessionM.add(exitPanel, BorderLayout.PAGE_START);
		pSessionM.add(panel, BorderLayout.CENTER);
		System.out.println("check 9");
	}

	/**
	 * Default JFreeChart dataset factory. This is what defines what appears inside the graph
	 * when the program starts up.
	 * @return Returns the CategoryDataset containing basic data series.
	 */
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
	/**
	 * This creates the JFreeChart and sets colors and a few other custom settings to make the graph
	 * look nice. This is what actually appears in the program window.
	 * @param dataset
	 * @return Returns the JFreeChart created from the DefaultCategoryDataset and CategoryDataset, modified
	 * to add custom colors.
	 */
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

	class ShowIPCard implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			pSessionM.setVisible(true);
			pShowIP.setVisible(false);
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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == exit)
		{
			mainFrame.setVisible(false);
			mainFrame.dispose();
			server.stopListening();
			this.stop();
			this.destroy();
		}
	}
}
