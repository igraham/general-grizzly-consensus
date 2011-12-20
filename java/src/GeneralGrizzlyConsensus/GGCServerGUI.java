package GeneralGrizzlyConsensus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * 
 * @author Stephen Kent
 * 
 *         This is a quick and dirty screen for showing that the network side of
 *         things is working properly for the server.
 * 
 */
@Deprecated
public class GGCServerGUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private GGCServer server;
	private JPanel panelMain;
	private JPanel panelSend;
	private JTextArea messages;
	private JTextField msgToSend;
	private JButton sendButton;

	GGCServerGUI()
	{
		setupGUI();
		setupCloseListener();
		setupServer();
	}

	private void setupCloseListener()
	{
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt)
			{
				// Do Socket clean-up here.
				server.stopListening();
				GGCGlobals.INSTANCE.dumpExceptionLog("ServerExceptionDump.txt");
				System.exit(0);
			}
		});
	}

	private void setupServer()
	{
		server = GGCServer.INSTANCE;
		server.setMessageSink(this);
		Thread t = new Thread(server);
		t.start();
	}

	private void setupGUI()
	{
		setTitle("Server Window");
		messages = new JTextArea();
		msgToSend = new JTextField();
		sendButton = new JButton("Send to Clients");
		sendButton.addActionListener(this);
		panelSend = new JPanel();
		panelSend.setLayout(new BorderLayout());
		panelSend.add(msgToSend, BorderLayout.CENTER);
		panelSend.add(sendButton, BorderLayout.LINE_END);
		panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		panelMain.add(messages, BorderLayout.CENTER);
		panelMain.add(panelSend, BorderLayout.PAGE_END);
		getContentPane().add(panelMain);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == sendButton)
		{
			server.sendMessageToAll(msgToSend.getText());
			msgToSend.setText(null);
		}
		else if (e.getID() == GGCGlobals.INSTANCE.MESSAGE_EVENT_ID)
		{
			if (messages.getText() != null && messages.getText().length() > 0)
				messages.setText(messages.getText()
						+ "\n"
						+ ((GGCConnection) e.getSource()).getRawSocket()
								.getRemoteSocketAddress() + ": "
						+ e.getActionCommand());
			else
				messages.setText(((GGCConnection) e.getSource()).getRawSocket()
						.getRemoteSocketAddress() + ": " + e.getActionCommand());
		}
	}
}