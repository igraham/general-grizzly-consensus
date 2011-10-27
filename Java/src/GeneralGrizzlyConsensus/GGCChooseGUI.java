package GeneralGrizzlyConsensus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
*
* @author Stephen Kent
*
*         This is just a quick chooser screen to allow the user to choose
*         whether they are the server or a client.
*
*/
public class GGCChooseGUI extends JFrame implements ActionListener
{
   private static final long serialVersionUID = 1L;
   private JPanel panel;
   private JButton serverButton;
   private JButton clientButton;

   GGCChooseGUI()
   {
       setupGUI();
       setupCloseListener();
   }

   private void setupCloseListener()
   {
       this.addWindowListener(new java.awt.event.WindowAdapter() {
           public void windowClosing(WindowEvent winEvt)
           {
               // Do any necessary clean-up here.
               System.exit(0);
           }
       });
   }

   private void setupGUI()
   {
       setTitle("Choose Type");
       serverButton = new JButton("Server");
       serverButton.addActionListener(this);
       clientButton = new JButton("Client");
       clientButton.addActionListener(this);
       panel = new JPanel();
       panel.setLayout(new FlowLayout());
       panel.add(serverButton);
       panel.add(clientButton);
       getContentPane().add(panel);
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
       if (e.getSource() == serverButton)
       {
           this.setVisible(false);
           GGCServerGUI serverWindow = new GGCServerGUI();
           serverWindow.pack();
           serverWindow.setVisible(true);
           this.dispose();
       }
       else if (e.getSource() == clientButton)
       {
           this.setVisible(false);
           GGCClientGUI clientWindow = new GGCClientGUI();
           clientWindow.pack();
           clientWindow.setVisible(true);
           this.dispose();
       }
   }
}