import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import AppletStart.GGCGraphListener;
import AppletStart.ManagerListener;
import AppletStart.ShowIPCard;
import GeneralGrizzlyConsensus.GGCConnection;
import GeneralGrizzlyConsensus.GGCGlobals;
import GeneralGrizzlyConsensus.GGCServer;


public class GeneralGrizzlyConsensusTeacher extends Applet 
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
		Runnable runner = new Runnable() 
		{
            public void run() 
            {
            	mainFrame = new JFrame("Georgia Gwinnett College General Grizzly Consensus");
        		mainFrame.setSize(300, 432);
        		mainFrame.setVisible(true);
        		
        		mfContainer = mainFrame.getContentPane();
        		mfContainer.setLayout(new CardLayout());		
        		//mfContainer.add();
        		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        };
        EventQueue.invokeLater(runner);
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

	private void createSessionManager()
	{
		pSessionM = new JPanel();
		pSessionM.setVisible(true);
		pShowHide = new JButton("Show/Hide");
		pShowHide.addActionListener(new GGCGraphListener());

		pSessionM.setLayout(new GridLayout(2,1));

		JPanel lP1 = new JPanel();
		JPanel lP2 = new JPanel();
		JPanel lP3 = new JPanel();
		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));
		lP2.setLayout(new GridLayout(2,1));

		lP1.setLayout(new FlowLayout(FlowLayout.CENTER));
		//lP2.setBackground(new Color(255,255,255));
		JComboBox qType = new JComboBox(types);
		JRadioButton tfButton = new JRadioButton("True/False");
		JRadioButton numButton = new JRadioButton("Number Responces: ");
		JTextField numField = new JTextField(5);
		ButtonGroup bg = new ButtonGroup();
		
		bg.add(tfButton);
		bg.add(numButton);
		
		sendQuestion = new JButton("Send Question");
		lP2.add(tfButton);
		lP3.add(numButton);
		lP3.add(numField);
		lP2.add(lP3);
		lP1.add(lP2);
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

}
