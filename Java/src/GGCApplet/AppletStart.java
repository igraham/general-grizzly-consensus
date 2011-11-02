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
	//This list of buttons will be initialized if the responder button is pressed.
	private ArrayList<JButton> buttons;
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
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel lP2 = new JPanel();
		lP1.setLayout(new GridLayout(3,2));
		//Whatever buttons end up here, add them to the ArrayList of buttons.
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

		pSessionM.setLayout(new GridLayout(2,1));

		JPanel lP1 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));

		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));
		//lP2.setBackground(new Color(255,255,255));
		JComboBox qType = new JComboBox(types);
		JRadioButton tfButton = new JRadioButton();
		JRadioButton numButton = new JRadioButton();
		JTextField numField = new JTextField();
		ButtonGroup bg = new ButtonGroup();
		
		bg.add(tfButton);
		bg.add(numButton);
		
		sendQuestion = new JButton("Send Question");
		lP1.add(tfButton);
		lP1.add(numButton);
		lP1.add(numField);
		lP1.add(sendQuestion);
		pSessionM.add(lP1);
		//TODO Make a UPDATABLE bar graph, maybe a new class that interfaces with it?
		CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
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
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, seriesT, categoryR);
        dataset.addValue(4.0, seriesF, categoryR);
        dataset.addValue(3.0, seriesA, categoryR);
        dataset.addValue(5.0, seriesB, categoryR);
        dataset.addValue(6.0, seriesC, categoryR);
        dataset.addValue(7.0, seriesD, categoryR);
        dataset.addValue(8.0, seriesE, categoryR);

        return dataset;

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
			buttons = new ArrayList<JButton>();
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
				client.sendMessage("T");
			}
			else if (e.getID() == GGCGlobals.INSTANCE.MESSAGE_EVENT_ID)
			{
				String message = e.getActionCommand();
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
				server.sendMessageToAll("T");
			}
			else if (e.getID() == GGCGlobals.INSTANCE.MESSAGE_EVENT_ID)
			{
				String message = e.getActionCommand();
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
