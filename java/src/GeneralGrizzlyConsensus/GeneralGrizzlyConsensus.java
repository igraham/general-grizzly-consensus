package GeneralGrizzlyConsensus;

/**
 * @author Stephen Kent
 * 
 *         This is intended to be the main executable class for the clicker.
 * 
 */
public class GeneralGrizzlyConsensus
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		GGCChooseGUI chooser = new GGCChooseGUI();
		chooser.pack();
		chooser.setVisible(true);
	}
}