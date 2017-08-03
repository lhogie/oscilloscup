package oscilloscup.data.rendering.figure;

import oscilloscup.data.rendering.DataElementRenderer;

/**
 * @author Luc Hogie
 */
public abstract class FigureRenderer extends DataElementRenderer
{
	private boolean shownPointReductionEnabled = false;

	public boolean isShownPointReductionEnabled()
	{
		return shownPointReductionEnabled;
	}

	public void setShownPointReductionEnabled(boolean b)
	{
		shownPointReductionEnabled = b;
	}

}
