package GGCApplet;

import org.jfree.data.category.DefaultCategoryDataset;

/**
 * The purpose of this class is to be able to increment data and set up the graph for a new
 * question, I.E. a new data set.
 * @author Administrator
 *
 */
public class GraphUpdater {

	/**
	 * This is the actual object that holds the data.
	 */
	private DefaultCategoryDataset barData;
	
	/**
	 * Pass in the object reference to the data, and the methods of GraphUpdater will handle everything
	 * else, in theory. The data etc will be initialized in the class using the updater. Initialization
	 * is not the job of the updater. Its job is to set up and modify the data.
	 * @param chartData
	 */
	public GraphUpdater(DefaultCategoryDataset chartData)
	{
		barData = chartData;
	}
	
	/**
	 * This method increments the data of the "row" that was specified by barNumber.
	 * @param barNumber
	 */
	public void incrementData(int barNumber)
	{
		barData.incrementValue(1.0, barData.getRowKey(barNumber), barData.getColumnKey(barNumber));
	}
	
	/**
	 * This method clears all values from the bar graph data and then based on whether
	 * the new question specified was true/false or multiple choice, it will create
	 * a specified amount of new data values and add them to the bar data.
	 * @param trueFalse - true for true/false, false for multiple choice
	 * @param numBars - The number of bars to add (i.e. number of multiple choice questions). 
	 * Ignored if trueFalse is set to true.
	 */
	public void newQuestion(boolean trueFalse, int numBars)
	{
		barData.clear();
		if(trueFalse)
		{
			barData.addValue(0.0, "True", "T");
			barData.addValue(0.0, "False", "F");
		}
		else
		{
			for(int i = 0; i < numBars; i++)
			{
				char c = (char)('A' + i);
				String letter = "" + c;
				barData.addValue(0.0, letter, letter);
			}
		}
	}
}