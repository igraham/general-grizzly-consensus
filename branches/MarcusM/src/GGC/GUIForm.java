package GGC;

/**
 * @author Marcus Michalske
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUIForm
{
	//Creation of the single frame
	private JFrame mainFrame;
	// bResponder and bSManager is located in the selectPanel, bConnect is located in the ipPanel
	private JButton bResponder, bSManager, bConnect;
	//Panels for the selection of user and IP connection panel
	private JPanel pSelect, pResponder,pIP, pSManager;
	// IP text fields in the IP panel
	private JTextField ip1,ip2,ip3;
	// used to set the session of the client
	private boolean sessionMUser = false;
	
	//The creation of the GUI form
	public GUIForm()
	{
	mainFrame = new JFrame("Georgia Gwinnett College General Grizzly Consensus");
	mainFrame.setSize(800, 632);
	//mainFrame.setResizable(false);
	mainFrame.setVisible(true);
	
	selectPanel();
	responderPanel();
	ipPanel();
	sManagerPanel();
	mainFrameLayout();

	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//GUI layout
	private void mainFrameLayout()
	{
		Container container = mainFrame.getContentPane();
		container.setLayout(new CardLayout());		
		container.add(pSelect, "Selection Panel");
		container.add(pResponder, "Responder Panel");
		container.add(pIP, "IP Panel");
		container.add(pSManager, "Session Manager Panel");
	}
	
	//Creates the selection panel object
	private JPanel selectPanel()
	{
		pSelect = new JPanel();
		pSelect.setVisible(true);
		pSelect.setLayout(new FlowLayout());
		
		bResponder = new JButton("Responder");
		bSManager = new JButton("Session Manager");
		bSManager.setName("pressed");
		pSelect.add(bResponder);
		pSelect.add(bSManager);
		
		ActionListener selectionCardR = new SelectionCardR();
		ActionListener selectionCardSM = new SelectionCardSM();
		bResponder.addActionListener(selectionCardR);
		bSManager.addActionListener(selectionCardSM);
		
		return pSelect;
	}
	
	//Creates the responder panel object
	private JPanel responderPanel()
	{
		pResponder = new JPanel();
		pResponder.setVisible(false);
		pResponder.setLayout(new FlowLayout());
		
		bResponder = new JButton("Responder");
		bSManager = new JButton("Session Manager");
		pResponder.add(bResponder);
		pResponder.add(bSManager);
		
		return pResponder;
	}
	
	//Creates the IP panel object
	private JPanel ipPanel()
	{
		pIP = new JPanel();
		pIP.setVisible(false);
		pIP.setLayout(new FlowLayout());

		JLabel ipLabel = new JLabel("Your IP is: 000.000.00");
		pIP.add(ipLabel);
		ip1 = new JTextField(3);
		ip2 = new JTextField(3);
		ip3 = new JTextField(2);
		JLabel dot1= new JLabel(".");
		JLabel dot2= new JLabel(".");
		//pIP.add(ip1);
		//pIP.add(dot1);
		//pIP.add(ip2);
		//pIP.add(dot2);
		//pIP.add(ip3);
		ipLabel.setVisible(sessionMUser);

		return pIP;
	}
	
	//Creates the session manager panel object
	private JPanel sManagerPanel()
	{
		pSManager = new JPanel();
		pSManager.setVisible(false);
		pSManager.setLayout(new FlowLayout());
		
		return pSManager;
	}
	
	//User selection listeners
	class SelectionCardR implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			pIP.setVisible(true);
			pSelect.setVisible(false);
		}
	}
	
	class SelectionCardSM implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			sessionMUser = true;
			mainFrame.getRootPane().revalidate();
			pIP.setVisible(true);
			pSelect.setVisible(false);
		}
	}
}
